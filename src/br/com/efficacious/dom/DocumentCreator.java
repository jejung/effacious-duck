/**
 * 
 */
package br.com.efficacious.dom;

import java.io.InputStream;
import java.net.URLConnection;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import br.com.efficacious.config.CrawlerConfig;

/**
 * @author Jean Jung
 *
 */
public class DocumentCreator implements Callable<URLDocument> {
	
	private URLConnection connection;
	private CrawlerConfig config;

	/**
	 * 
	 */
	public DocumentCreator(CrawlerConfig config, URLConnection connection) {
		this.config = config;
		this.connection = connection;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public URLDocument call() throws Exception {

		try (InputStream inputStream = this.connection.getInputStream();) {
			Document document = Jsoup.parse(inputStream, this.connection.getContentEncoding(), this.connection.getURL().toString());
			URLDocument urlDocument = new URLDocument(this.connection.getURL(), document);
			return urlDocument;
		} catch (Exception e) {
			this.config.getLogger().log(Level.SEVERE, "Error parsing HTML", e);
		}
		return null;
	}

}
