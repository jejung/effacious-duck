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

import br.com.efficacious.config.CrawlerConfig;
import br.com.efficacious.dom.URLDocument;
import br.com.efficacious.io.URLIndexer;

/**
 * Class that handle extraction of links in web pages
 * 
 * @author Jean Jung
 * @author johnny w. g. g.
 */
public class URLConsumer implements Runnable {

	private static final int MAX_EXTRACTORS = 10;
	private static final int MAX_DOCUMENTS = 300;
	private static final int MAX_INDEXERS = 10;

	private Lock lock;
	private Condition full;
	private Condition empty;
	private URLQueue queue;
	
	private volatile ArrayDeque<URLDocument> docs;
	private volatile int mapped = 0;
	private boolean alive;

	private ExecutorService extractorExecutor;
	private ExecutorService indexerExecutor;

	private volatile Semaphore extractorsSemaphore = new Semaphore(MAX_EXTRACTORS);
	private Semaphore indexersSemaphore = new Semaphore(MAX_INDEXERS);
	private CrawlerConfig config;


	public URLConsumer(CrawlerConfig config, URLQueue urlList) {
		this.config = config; 
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
				this.config.getLogger().log(Level.SEVERE, "Thread interrupted", e);
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
		aliveLoop: while (isAlive()) {
			lock.lock();
			try {
				while (docs.isEmpty()) {
					empty.await();
					if (!this.isAlive())
						break aliveLoop;
				}

				URLDocument document = this.docs.poll();
				
				extractorsSemaphore.acquire();
				extractorExecutor.submit(new URLExtractor(this.extractorsSemaphore, this.queue, this::mapped, document.getDocument()));

				indexersSemaphore.acquire();
				indexerExecutor.submit(new URLIndexer(this.indexersSemaphore, document));

				full.signalAll();
			} catch (InterruptedException e) {
				this.config.getLogger().log(Level.SEVERE, "Thread interrupted", e);
				break;
			} finally {
				lock.unlock();
			}
		}
	}

	/**
	 * @return the alive
	 */
	public synchronized boolean isAlive() {
		return alive;
	}

	/**
	 * @param alive the alive to set
	 */
	public synchronized void setAlive(boolean alive) {
		this.alive = alive;
	}
}
