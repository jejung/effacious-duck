/**
 * 
 */
package br.com.efficacious.url;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.Callable;
import java.util.concurrent.Semaphore;
import java.util.function.IntSupplier;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

/**
 * @author jean
 *
 */
public class URLExtractor implements Callable<Void> {
	
	private Semaphore realeseWhenDone;
	private URLQueue queue;
	private IntSupplier incrementCallback;
	private Document document; 
	
	/**
	 * 
	 */
	public URLExtractor(Semaphore realeseWhenDone, URLQueue queue, IntSupplier incrementCallback, Document document) {
		this.realeseWhenDone = realeseWhenDone;
		this.queue = queue;
		this.incrementCallback = incrementCallback;
		this.document = document;
	}

	/**
	 * {@inheritDoc Callable#call()}
	 */
	@Override
	public Void call() throws Exception {
		try {
			System.out.println("Processing page with title: " + document.getElementsByTag("title").get(0).text().trim());
			this.extract(document);			
		} finally {
			this.realeseWhenDone.release();
		}
		return null;
	}
	
	private void extract(Element el) {

		el.getAllElements()
			.parallelStream()
			.filter((e) -> {
				return "a".equals(e.tagName());  
			})
			.map((e) -> { 
				return e.absUrl("href"); 
			})
			.filter(
				(href) -> { 
					// TODO create an machanism to configure permission of https
					// pages
					// could be used to remove https pages, this would be
					// configurable in the application
//					if (href.startsWith("https")) {
//						href = href.replaceFirst("s", "");
//					}
					return href != null && !href.isEmpty() && 
							!(href.endsWith(".pdf") || href.endsWith(".jpg") || href.endsWith(".avi"));
				})
			.forEach((href) -> { 
				try {
					this.queue.add(new URL(href));
				} catch (MalformedURLException e) {
					System.err.println("Invalid URL: " + e.getMessage() + href);
				}
			});
		Logger.getGlobal().log(Level.INFO, this.incrementCallback.getAsInt() + " pages indexed so far.");
	}

}
