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

/**
 * @author Jean Jung
 *
 */
public class DocumentCreator implements Callable<URLDocument> {
	
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
	public URLDocument call() throws Exception {
		
		try (InputStream inputStream = this.connection.getInputStream();) {
			
			Document document = Jsoup.parse(inputStream, this.connection.getContentEncoding(), this.connection.getURL().toString());
			URLDocument urlDocument = new URLDocument(this.connection.getURL(), document);
			return urlDocument;
		} catch (Exception e) {
			Logger.getGlobal().log(Level.SEVERE, "Error parsing HTML", e);
		}
		return null;
	}

}
