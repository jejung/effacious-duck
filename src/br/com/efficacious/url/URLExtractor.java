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

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import br.com.efficacious.config.CrawlerConfig;
import br.com.efficacious.crawler.WebCrawler;

/**
 * This is the task responsible for the "spider" work. These workers find the urls
 * on the pages and put it in the lifecycle of the {@link WebCrawler}.
 * 
 * @author Jean Jung
 */
public class URLExtractor implements Callable<Void> {

	private Semaphore realeseWhenDone;
	private URLQueue queue;
	private IntSupplier incrementCallback;
	private Document document;
	private CrawlerConfig config;

	/**
	 * Create a new {@link URLExtractor}.
	 */
	public URLExtractor(CrawlerConfig config, Semaphore realeseWhenDone, URLQueue queue, IntSupplier incrementCallback, Document document) {
		this.config = config;
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
			this.config.getLogger().info("#" + this.incrementCallback.getAsInt() + " -> Processing page with title: " + document.getElementsByTag("title").get(0).text().trim());
			this.extract(document);
		} finally {
			this.realeseWhenDone.release();
		}
		return null;
	}

	/**
	 * Extract all the urls from the element.
	 * @param el
	 */
	private void extract(Element el) {

		el.getAllElements().parallelStream()
		.filter((e) -> {
			return "a".equals(e.tagName());
		}).map((e) -> {
			return e.absUrl("href");
		}) .filter((href) -> href != null && !href.isEmpty())
		.forEach((href) -> {
			try {
				this.queue.add(new URL(href), el.baseUri());
			} catch (MalformedURLException e) {
				this.config.getLogger().log(Level.INFO, "Invalid URL: " + href, e);
			}
		});
	}

}
