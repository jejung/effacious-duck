/**
 * 
 */
package main;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayDeque;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Class that handle extraction of links in web pages
 * 
 * @author Jean Jung
 * @author johnny w. g. g.
 */
public class URLExtractor implements Runnable {

	volatile ArrayDeque<Document> docs;

	private Object lock = new Object();

	private URLQueue urlList;

	private static final int MAX_EXTRACTORS = 10;
	
	private static final int MAX_DOCUMENTS = 300;

	private volatile int mapped = 0;

	private ExecutorService executor;

	private volatile Semaphore semaphore = new Semaphore(MAX_EXTRACTORS);
	
	private boolean alive;

	public URLExtractor(URLQueue urlList) {
		this.docs = new ArrayDeque<>();
		this.urlList = urlList;
		this.executor = Executors.newFixedThreadPool(MAX_EXTRACTORS);
		this.alive = true;
	}

	public void addDocument(Document doc) {

		synchronized (lock) {
			while (docs.size() > MAX_DOCUMENTS) {
				try {
					lock.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
					break;
				}
			}
			
			docs.add(doc);
			
			lock.notifyAll();
		}
	}
	
	private synchronized int mapped() {
		return ++mapped;
	}
	
	private void extract(Element el) {

		Elements links = el.getElementsByTag("a");
		
		for (Element link : links) {
			String href = link.absUrl("href");
			if (!href.isEmpty()) {
				try {
					if (!(href.endsWith(".pdf") || href.endsWith(".jpg") || href.endsWith(".avi"))) {
						// TODO create an machanism to configure permission of https
						// pages
						// could be used to remove https pages, this would be
						// configurable in the application
//						if (href.startsWith("https")) {
//							href = href.replaceFirst("s", "");
//						}
						
						urlList.add(new URL(href));
					} else {
						// TODO maybe store images on database
						System.err.println("Midia ignored: " + href);
					}
				} catch (MalformedURLException e) {
					System.err.println("Invalid URL: " + e.getMessage() + href);
				}	
			}			
		}

		System.out.println(mapped() + " pages indexed so far.");

	}

	@Override
	public void run() {
		this.collectForever();
	}
	
	private void collectForever(){
		while (this.isAlive()) {
			try {
				synchronized (lock) {
					while (docs.isEmpty()) {
						lock.wait();
					}
					semaphore.acquire();

					executor.submit(new Callable<Void>() {
						@Override
						public Void call() throws Exception {

							try {
								extract(docs.poll());
								return null;
							} finally {
								semaphore.release();
								//System.out.println("releasing semaphore: " + sem.availablePermits());
								lock.notifyAll();
							}
						}
					});
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
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
