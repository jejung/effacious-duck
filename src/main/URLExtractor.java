/**
 * 
 */
package main;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

/**
 * @author jean
 *
 */
public class URLExtractor {

	private SiteConnectionProducer producer;
	private ThreadPoolExecutor threads;
	private BlockingQueue<Runnable> threadQueue;
	private Queue<Document> queue;
	
	/**
	 * 
	 */
	public URLExtractor(SiteConnectionProducer producer) {
		this.producer = producer;
		this.threadQueue = new LinkedBlockingQueue<>(); 
		this.queue = new ConcurrentLinkedQueue<>();
		this.threads = new ThreadPoolExecutor(100, 200, 150000, TimeUnit.MILLISECONDS, threadQueue); 
	}
	
	public synchronized void collect(Document doc) {
		this.queue.add(doc);
		this.threads.submit(new Extractor());
	}
	
	private class Extractor implements Runnable {
		
		public void extractURLs(Element el) {
			
			if (el.hasAttr("href")) {
				try {
					if (!el.absUrl("href").isEmpty())
						producer.add(new URL(el.absUrl("href")));
				} catch (MalformedURLException | InterruptedException e) {
					// dont worry
					e.printStackTrace();
				}
			}
			for (Element ch: el.children())
				extractURLs(ch);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void run() {
			this.extractURLs(queue.poll());
		}
	}
}
