/**
 * 
 */
package br.com.efficacious.crawler;

/**
 * This is the base class for all {@link WebCrawler} attachable or implicity components.
 * This class defines the default behaviours and imply some restrictions to the 
 * life philosophy of any component.
 * 
 * @author Jean Jung
 */
public abstract class CrawlerComponent implements Runnable {

	/**
	 * Stop the process being runned.
	 */
	public abstract void stop(); 
}
