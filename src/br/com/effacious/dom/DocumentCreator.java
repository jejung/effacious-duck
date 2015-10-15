/**
 * 
 */
package br.com.effacious.dom;

import java.io.InputStream;
import java.net.URLConnection;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/**
 * @author Jean Jung
 *
 */
public class DocumentCreator implements Callable<Document> {
	
	private URLConnection connection;

	/**
	 * 
	 */
	public DocumentCreator(URLConnection connection) {
		this.connection = connection;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Document call() throws Exception {
		
		try (InputStream inputStream = this.connection.getInputStream();) {
			Document doc = Jsoup.parse(inputStream, this.connection.getContentEncoding(), this.connection.getURL().toString());
			return doc;
		} catch (Exception e) {
			Logger.getGlobal().log(Level.SEVERE, "Error parsing HTML", e);
		}

		return null;
	}

}
