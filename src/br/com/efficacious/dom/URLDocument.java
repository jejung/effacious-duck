/**
 * 
 */
package br.com.efficacious.dom;

import java.net.URL;

import org.jsoup.nodes.Document;

/**
 * @author Jean Jung
 *
 */
public class URLDocument {
	
	private URL url;
	private Document document;
	
	/**
	 * @param url
	 * @param document
	 */
	public URLDocument(URL url, Document document) {
		super();
		this.url = url;
		this.document = document;
	}

	/**
	 * @return the document
	 */
	public Document getDocument() {
		return this.document;
	}

	/**
	 * @param document the document to set
	 */
	public void setDocument(Document document) {
		this.document = document;
	}

	/**
	 * @return the url
	 */
	public URL getUrl() {
		return this.url;
	}

	/**
	 * @param url the url to set
	 */
	public void setUrl(URL url) {
		this.url = url;
	}
}
