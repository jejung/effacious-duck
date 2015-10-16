/**
 * 
 */
package br.com.efficacious.url;

import java.util.ArrayDeque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jsoup.nodes.Document;

import br.com.efficacious.dom.URLDocument;
import br.com.efficacious.io.URLIndexBuilder;
import br.com.efficacious.io.URLIndexer;

/**
 * Class that handle extraction of links in web pages
 * 
 * @author Jean Jung
 * @author johnny w. g. g.
 */
public class URLConsumer implements Runnable {

	volatile ArrayDeque<URLDocument> docs;

	private static final int MAX_EXTRACTORS = 10;

	private static final int MAX_DOCUMENTS = 300;

	private static final int MAX_INDEXERS = 10;

	private Lock lock;
	private Condition full;
	private Condition empty;
	private URLQueue queue;

	private volatile int mapped = 0;

	private ExecutorService extractorExecutor;

	private ExecutorService indexerExecutor;

	private volatile Semaphore extractorsSemaphore = new Semaphore(MAX_EXTRACTORS);

	private Semaphore indexersSemaphore = new Semaphore(MAX_INDEXERS);

	private boolean alive;

	public URLConsumer(URLQueue urlList) {
		this.docs = new ArrayDeque<>();
		this.queue = urlList;
		this.extractorExecutor = Executors.newFixedThreadPool(MAX_EXTRACTORS);
		this.indexerExecutor = Executors.newWorkStealingPool(MAX_INDEXERS);
		this.alive = true;
		this.lock = new ReentrantLock();
		this.full = this.lock.newCondition();
		this.empty = this.lock.newCondition();
	}

	public void addDocument(URLDocument doc) {
		this.lock.lock();

		while (docs.size() > MAX_DOCUMENTS) {
			try {
				full.await();
			} catch (InterruptedException e) {
				Logger.getGlobal().log(Level.SEVERE, "Thread interrupted", e);
			}
		}

		docs.add(doc);
		empty.signalAll();
		lock.unlock();
	}

	@Override
	public void run() {
		collectForever();
	}

	private synchronized int mapped() {
		return ++mapped;
	}

	private void collectForever() {
		while (isAlive()) {
			lock.lock();
			try {

				while (docs.isEmpty()) {
					empty.await();
				}

				extractorsSemaphore.acquire();
				URLDocument document = this.docs.poll();

				extractorExecutor.submit(new URLExtractor(this.extractorsSemaphore, this.queue, this::mapped, document.getDocument()));

				indexersSemaphore.acquire();
				indexerExecutor.submit(new URLIndexer(this.indexersSemaphore, document));

				full.signalAll();
			} catch (InterruptedException e) {
				Logger.getGlobal().log(Level.SEVERE, "Thread interrupted", e);
				break;
			} finally {
				lock.unlock();
			}
		}
	}

	/**
	 * @return the alive
	 */
	public boolean isAlive() {
		return alive;
	}

	/**
	 * @param alive
	 *            the alive to set
	 */
	public void setAlive(boolean alive) {
		this.alive = alive;
	}
}
