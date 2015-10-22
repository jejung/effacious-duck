/**
 * 
 */
package br.com.efficacious.crawler;

import br.com.efficacious.config.CrawlerConfig;
import br.com.efficacious.connection.ConnectionList;
import br.com.efficacious.connection.ConnectionProducer;
import br.com.efficacious.dom.HTMLList;
import br.com.efficacious.dom.HTMLPull;
import br.com.efficacious.dom.HTMLSpliterator;
import br.com.efficacious.url.URLConsumer;
import br.com.efficacious.url.URLExtractor;
import br.com.efficacious.url.URLQueue;

/**
 * This class is composed of all EfficaciouDuck components. It's a handy class for starting to crawl the web,
 * it's the entry point to all operations in the efficacious API. You must configure it, choose the operations
 * you need and start them. 
 * 
 * @author Jean Jung
 */
public class WebCrawler {
	
	private CrawlerConfig config;
	
	private volatile ConnectionList connectionList;
	private volatile URLQueue urlQueue;
	private volatile HTMLList htmlList;
	
	private Thread connectionProducer;
	private Thread htmlPull;
	private Thread urlExtractor;
	private Thread htmlSpliterator;
	
	private Thread domainRanking;
	
	/**
	 * Creates a basic {@link WebCrawler} with the default {@link CrawlerConfig} instance.
	 * This constructors create and prepare all the basic components that maintain the {@link WebCrawler} alive
	 * till the end of the {@link URLQueue} was reached. 
	 */
	public WebCrawler() {
		this.config = new CrawlerConfig();
		
		this.connectionList = new ConnectionList();
		this.urlQueue = new URLQueue();
		this.htmlList = new HTMLList();
		
		this.connectionProducer = new Thread(new ConnectionProducer(this.urlQueue, this.connectionList));
		this.htmlPull = new Thread(new HTMLPull(this.connectionList, this.htmlList));
		URLConsumer urlConsumer = new URLConsumer(this.urlQueue);
		this.urlExtractor  = new Thread(urlConsumer);
		this.htmlSpliterator = new Thread(new HTMLSpliterator(this.htmlList, urlConsumer));
	}
	
	
}
