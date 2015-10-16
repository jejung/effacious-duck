/**
 * 
 */
package br.com.effacious.url;

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

/**
 * Class that handle extraction of links in web pages
 * 
 * @author Jean Jung
 * @author johnny w. g. g.
 */
public class URLConsumer implements Runnable {

	private static final int MAX_EXTRACTORS = 10;
	private static final int MAX_DOCUMENTS = 300;
	
	volatile ArrayDeque<Document> docs;

	private Lock lock;
	private Condition full;
	private Condition empty;
	private URLQueue queue;
	private ExecutorService executor;
	private boolean alive;
	
	private volatile int mapped = 0;
	private volatile Semaphore semaphore = new Semaphore(MAX_EXTRACTORS);

	public URLConsumer(URLQueue urlList) {
		this.docs = new ArrayDeque<>();
		this.queue = urlList;
		this.executor = Executors.newFixedThreadPool(MAX_EXTRACTORS);
		this.alive = true;
		this.lock = new ReentrantLock();
		this.full = this.lock.newCondition();
		this.empty = this.lock.newCondition();
	}

	public void addDocument(Document doc) {
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
			try {
				lock.lock();
				
				while (docs.isEmpty()) {
					empty.await();
				}
				
				semaphore.acquire();
				executor.submit(new URLExtractor(semaphore, queue, this::mapped, docs.poll()));
				full.signalAll();
				lock.unlock();
			} catch (InterruptedException e) {
				Logger.getGlobal().log(Level.SEVERE, "Thread interrupted", e);
				break;
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
	 * @param alive the alive to set
	 */
	public void setAlive(boolean alive) {
		this.alive = alive;
	}
}
