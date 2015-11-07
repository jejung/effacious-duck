/**
 * 
 */
package br.com.efficacious.crawler;

import java.net.URL;
import java.util.Objects;

import br.com.efficacious.config.CrawlerConfig;
import br.com.efficacious.config.MediaStorage;
import br.com.efficacious.connection.ConnectionList;
import br.com.efficacious.connection.ConnectionProducer;
import br.com.efficacious.dom.DocumentList;
import br.com.efficacious.dom.DocumentPull;
import br.com.efficacious.dom.DocumentSpliterator;
import br.com.efficacious.media.MediaList;
import br.com.efficacious.services.BaseServiceTester;
import br.com.efficacious.services.NetworkServiceTester;
import br.com.efficacious.url.DocumentConsumer;
import br.com.efficacious.url.URLQueue;

/**
 * This class is composed of all EfficaciouDuck components. It's a handy class for starting to crawl the web,
 * it's the entry point to all operations in the EfiicaciousAPI. You must configure it, choose the operations
 * you need and start them.
 * The {@link WebCrawler} is self fed and don't need nothing more than the initial list of web sites to be visited. 
 * You can add sites by calling {@link #addBaseURL(URL)} method.
 * 
 * @author Jean Jung
 */
public class WebCrawler {
	
	private CrawlerConfig config;
	private CrawlerStartUp startUp;
	
	private volatile ConnectionList connectionList;
	private volatile URLQueue urlQueue;
	private volatile DocumentList htmlList;
	private volatile MediaList mediaList;
	
	private ConnectionProducer connectionProducer;
	private DocumentPull htmlPull;
	private DocumentConsumer urlConsumer;
	private DocumentSpliterator htmlSpliterator;
	
	/**
	 * Creates a basic {@link WebCrawler} with the default {@link CrawlerConfig}.
	 */
	public WebCrawler() {
		this.config = new CrawlerConfig();
		this.startUp = new CrawlerStartUp(this.config);
		this.urlQueue = new URLQueue(this.config);
	}
	
	/**
	 * Creates a basic {@link WebCrawler} with the given {@link CrawlerConfig}. 
	 * 
	 * @param config
	 */
	public WebCrawler(CrawlerConfig config) {
		this.config = config;
		this.startUp = new CrawlerStartUp(config);
		this.urlQueue = new URLQueue(config);
	}
	
	/**
	 * Add a {@link URL} to the start point of the crawler.
	 * Call this method before calling {@link #start()} to configure what {@link URL}s will be 
	 * touched first and will produce more {@link URL}s to the {@link URLQueue} to maintain the
	 * {@link WebCrawler} alive and producing.
	 * @param url
	 */
	public void addBaseURL(URL url) {
		this.urlQueue.add(url, null);
	}
	
	/**
	 * Create the basic components to maintain the {@link WebCrawler} alive and running.
	 * Additional can be used by the user, this choice depends on the user needs.
	 */
	private void createComponents() {
		this.connectionList = new ConnectionList(this.config);
		this.htmlList = new DocumentList(this.config);
		
		if (this.config.getMediaStorage() != MediaStorage.NONE)
			this.mediaList = new MediaList();
		
		this.connectionProducer = new ConnectionProducer(this.config, this.urlQueue, this.connectionList);
		this.htmlPull = new DocumentPull(this.config, this.connectionList, this.htmlList, this.mediaList);
		this.urlConsumer = new DocumentConsumer(this.config, this.urlQueue);
		this.htmlSpliterator = new DocumentSpliterator(this.config, this.htmlList, urlConsumer);
	}
	
	/**
	 * Create a basic {@link NetworkServiceTester} that verify if there is an Internet connection,
	 * the {@link CrawlerConfig} define some of the properties of the new tester.
	 * @return the {@link NetworkServiceTester} 
	 */
	private NetworkServiceTester createNetworkTest() {
		return new NetworkServiceTester(this.config.getTestAddress(), this.config.getProxy());
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
		this.urlConsumer.stop();
		this.connectionProducer.stop();
		this.htmlPull.stop();
		this.htmlSpliterator.stop();
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
