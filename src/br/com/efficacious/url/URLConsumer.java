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

/**
 * Class that handle extraction of links in web pages
 * 
 * @author Jean Jung
 * @author johnny w. g. g.
 */
public class URLConsumer implements Runnable {

	volatile ArrayDeque<Document> docs;

	private Lock lock;
	
	private Condition isFull;
	
	private Condition isEmpty;
	
	private URLQueue queue;

	private static final int MAX_EXTRACTORS = 10;
	
	private static final int MAX_DOCUMENTS = 300;

	private volatile int mapped = 0;

	private ExecutorService executor;

	private volatile Semaphore semaphore = new Semaphore(MAX_EXTRACTORS);
	
	private boolean alive;

	public URLConsumer(URLQueue urlList) {
		this.docs = new ArrayDeque<>();
		this.queue = urlList;
		this.executor = Executors.newFixedThreadPool(MAX_EXTRACTORS);
		this.alive = true;
		this.lock = new ReentrantLock();
		this.isFull = this.lock.newCondition();
		this.isEmpty = this.lock.newCondition();
	}

	public void addDocument(Document doc) {
		
		this.lock.lock();
		while (docs.size() > MAX_DOCUMENTS) {
			try {
				this.isFull.await();
			} catch (InterruptedException e) {
				Logger.getGlobal().log(Level.SEVERE, "Thread interrupted", e);
			}
		}
		docs.add(doc);
		this.isEmpty.signalAll();
		this.lock.unlock();
	}

	@Override
	public void run() {
		this.collectForever();
	}
	
	private synchronized int mapped() {
		return ++this.mapped;
	}
	
	private void collectForever(){
		while (this.isAlive()) {
			try {
				this.lock.lock();
				
				while (this.docs.isEmpty()) {
					this.isEmpty.await();
				}
				
				semaphore.acquire();
				executor.submit(
						new URLExtractor(this.semaphore, this.queue, 
								this::mapped, this.docs.poll()));
				this.isFull.signalAll();
				this.lock.unlock();
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
