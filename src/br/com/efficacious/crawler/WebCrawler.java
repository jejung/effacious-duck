/**
 * 
 */
package br.com.efficacious.crawler;

import java.net.Proxy;
import java.net.URL;
import java.util.Objects;

import br.com.efficacious.config.CrawlerConfig;
import br.com.efficacious.connection.ConnectionList;
import br.com.efficacious.connection.ConnectionProducer;
import br.com.efficacious.dom.HTMLList;
import br.com.efficacious.dom.HTMLPull;
import br.com.efficacious.dom.HTMLSpliterator;
import br.com.efficacious.services.BaseServiceTester;
import br.com.efficacious.services.NetworkServiceTester;
import br.com.efficacious.url.URLConsumer;
import br.com.efficacious.url.URLQueue;

/**
 * This class is composed of all EfficaciouDuck components. It's a handy class for starting to crawl the web,
 * it's the entry point to all operations in the EfiicaciousAPI. You must configure it, choose the operations
 * you need and start them.
 * The {@link WebCrawler} is self fed and don't need nothing more than the initial list of web sites to be visited. 
 * You can add sites by calling {@link #addURL(URL)} method.
 * 
 * @author Jean Jung
 */
public class WebCrawler {
	
	private CrawlerConfig config;
	private CrawlerStartUp startUp;
	
	private volatile ConnectionList connectionList;
	private volatile URLQueue urlQueue;
	private volatile HTMLList htmlList;
	
	private ConnectionProducer connectionProducer;
	private HTMLPull htmlPull;
	private URLConsumer urlConsumer;
	private HTMLSpliterator htmlSpliterator;
	
	/**
	 * Creates a basic {@link WebCrawler} with the default {@link CrawlerConfig}.
	 */
	public WebCrawler() {
		this.config = new CrawlerConfig();
		this.startUp = new CrawlerStartUp(this.config);
		this.urlQueue = new URLQueue();
	}
	
	/**
	 * Creates a basic {@link WebCrawler} with the given {@link CrawlerConfig}. 
	 * 
	 * @param config
	 */
	public WebCrawler(CrawlerConfig config) {
		this.config = config;
		this.startUp = new CrawlerStartUp(config);
		this.urlQueue = new URLQueue();
	}
	
	/**
	 * Add a {@link URL} to the start point of the crawler.
	 * Call this method before calling {@link #start()} to configure what {@link URL}s will be 
	 * touched first and will produce more {@link URL}s to the {@link URLQueue} to maintain the
	 * {@link WebCrawler} alive and producing.
	 * @param url
	 */
	public void addURL(URL url) {
		this.urlQueue.add(url, null);
	}
	
	/**
	 * Create the basic components to maintain the {@link WebCrawler} alive and running.
	 * Additional can be used by the user, this choice depends on the user needs.
	 */
	private void createComponents() {
		this.connectionList = new ConnectionList(this.config);
		this.htmlList = new HTMLList(this.config);
		
		this.connectionProducer = new ConnectionProducer(this.config, this.urlQueue, this.connectionList);
		this.htmlPull = new HTMLPull(this.config, this.connectionList, this.htmlList);
		this.urlConsumer = new URLConsumer(this.config, this.urlQueue);
		this.htmlSpliterator = new HTMLSpliterator(this.config, this.htmlList, urlConsumer);
	}
	
	/**
	 * Create a basic {@link NetworkServiceTester} that verify if there is an Internet connection,
	 * the {@link CrawlerConfig} define some of the properties of the new tester.
	 * @return the {@link NetworkServiceTester} 
	 */
	private NetworkServiceTester createNetworkTest() {
		
		Proxy proxy = this.config.getProxy();
		NetworkServiceTester netTester;
		if (proxy != null)
			netTester = new NetworkServiceTester(this.config.getTestAddress(), proxy.address());
		else
			netTester = new NetworkServiceTester(this.config.getTestAddress());
		
		return netTester;
	}
	
	/**
	 * Create and add the basic {@link BaseServiceTester}  to the {@link CrawlerStartUp}
	 * to verify if the minimal  necessary services are up and running.
	 */
	private void prepareStartUp() {
		Objects.requireNonNull(this.config, "The config cannot be null");
		
		this.startUp.addServiceTest(this.createNetworkTest());
	}
	
	/**
	 * Start all the components in the optimal order.
	 */
	private void startComponents() {
		Thread connectionProducerThread = new Thread(this.connectionProducer);
		Thread htmlPullThread = new Thread(this.htmlPull);
		Thread htmlSpliteratorThread = new Thread(this.htmlSpliterator);
		Thread urlConsumerThread = new Thread(this.urlConsumer);
		
		urlConsumerThread.setPriority(Thread.MAX_PRIORITY);

		connectionProducerThread.setPriority(Thread.MIN_PRIORITY);
		htmlPullThread.setPriority(Thread.MIN_PRIORITY);
		htmlSpliteratorThread.setPriority(Thread.MIN_PRIORITY);
		
		connectionProducerThread.start();
		htmlPullThread.start();
		urlConsumerThread.start();
		htmlSpliteratorThread.start();
	}
	
	/**
	 * Safely stop the {@link WebCrawler} by gradually making them without resources.
	 */
	public void stop() {
		this.urlConsumer.setAlive(false);		
		this.htmlSpliterator.setAlive(false);
		this.htmlPull.setAlive(false);
		this.connectionProducer.setAlive(false);
	}
	
	/**
	 * Make this {@link WebCrawler} up and running.
	 *
	 * @throws IllegalStateException If any necessary service is not running or accessible, like network.
	 * @throws InterruptedException If there was an {@link Thread} interruption.
	 */
	public void start() throws InterruptedException {
		
		this.prepareStartUp();
		this.startUp.testAll();
		if (this.startUp.hasFailed())
			throw new IllegalStateException("Some services can not be started, please check the log for more details.");
		
		this.createComponents();
		this.startComponents();
	}
}
