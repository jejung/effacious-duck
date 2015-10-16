/**
 * 
 */
package br.com.effacious.url;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.Callable;
import java.util.concurrent.Semaphore;
import java.util.function.IntSupplier;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

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

		Elements links = el.getElementsByTag("a");
		
		links.parallelStream().forEach(
			(link) -> {
				String href = link.absUrl("href");
				if (!href.isEmpty()) {
					try {
						if (!(href.endsWith(".pdf") || href.endsWith(".jpg") || href.endsWith(".avi"))) {
							// TODO create an machanism to configure permission of https
							// pages
							// could be used to remove https pages, this would be
							// configurable in the application
//							if (href.startsWith("https")) {
//								href = href.replaceFirst("s", "");
//							}
							
							this.queue.add(new URL(href));
						} else {
							// TODO maybe store images on database
							System.err.println("Midia ignored: " + href);
						}
					} catch (MalformedURLException e) {
						System.err.println("Invalid URL: " + e.getMessage() + href);
					}	
				}	
				
			});
		System.out.println(this.incrementCallback.getAsInt() + " pages indexed so far.");
		//Logger.getGlobal().log(Level.INFO, );
	}

}
