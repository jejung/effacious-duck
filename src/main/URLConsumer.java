/**
 * 
 */
package main;

import java.util.ArrayDeque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
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

	private Object lock = new Object();

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
	}

	public void addDocument(Document doc) {
		
		// TODO: Trocar por ReentrantLock
		
		synchronized (lock) {
			while (docs.size() > MAX_DOCUMENTS) {
				try {
					lock.wait();
				} catch (InterruptedException e) {
					Logger.getGlobal().log(Level.SEVERE, "Thread interrupted", e);
				}
			}
			
			docs.add(doc);
			
			lock.notifyAll();
		}
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
				synchronized (lock) {
					while (docs.isEmpty()) {
						lock.wait();
					}
					semaphore.acquire();

					executor.submit(new URLExtractor(this.semaphore, this.lock, this.queue, 
							this::mapped, this.docs.poll()));
				}
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
