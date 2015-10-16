/**
 * 
 */
package br.com.efficacious.io;

import java.io.Reader;
import java.net.URL;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;

/**
 * @author Jean Jung
 *
 */
public class URLIndexBuilder  {
	
	/**
	 *  
	 */
	private URLIndexBuilder() {
	} 
	
	/**
	 * Create a {@link Document} to be stored on the URL index.
	 * @return a {@link Document} instance
	 */
	public static Document create(URL url, Reader contentReader) {
		
		Document document = new Document();
		document.add(new StringField("url", url.toString(), Store.YES));
		document.add(new StringField("domain", url.getHost(), Store.YES));
		document.add(new TextField("content", contentReader));
		return document;
	}
	
}
