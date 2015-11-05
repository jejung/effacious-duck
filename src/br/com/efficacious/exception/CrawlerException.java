/**
 * 
 */
package br.com.efficacious.exception;

import br.com.efficacious.crawler.WebCrawler;

/**
 * Exception class to wrap and define common errors 
 * on {@link WebCrawler} operations.
 * 
 * @author Jean Jung
 */
public class CrawlerException extends RuntimeException {

	/**
	 * JDK1.1 default serialVersionUID
	 */
	private static final long serialVersionUID = -7912430439197689766L;
	
	/**
	 * 
	 */
	public CrawlerException() {
	}
	
	public CrawlerException(String message) {
		super(message);
	}
}
