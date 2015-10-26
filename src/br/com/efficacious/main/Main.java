package br.com.efficacious.main;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import br.com.efficacious.config.CrawlerConfig;
import br.com.efficacious.crawler.WebCrawler;
import br.com.efficacious.io.EfficaciousLogHandler;

/**
 * @author johnny w. g. g.
 * @author Jean Jung
 */
public class Main {

//	static volatile ConnectionList connectionList = new ConnectionList();
//	static volatile URLQueue urlList = new URLQueue();
//	static volatile HTMLList htmlList = new HTMLList();

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {

		Logger.getGlobal().addHandler(new EfficaciousLogHandler());
		Logger.getGlobal().setLevel(Level.ALL);
		
		CrawlerConfig config = CrawlerConfig.builder().initFromArgs(args).build();
		WebCrawler crawler = new WebCrawler(config);
		
		try {
			crawler.start();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		// initiates the resources configuration
//		new LoadArgumentsResources(args).start();
//
//		Logger.getGlobal().addHandler(new ConsoleHandler());
//
//		urlList.add(new URL("http://www.furb.br/web/10/portugues"), null);
//		urlList.add(new URL("http://g1.globo.com"), null);
//		urlList.add(new URL("http://www.tecmundo.com.br"), null);
//		urlList.add(new URL("http://docs.oracle.com"), null);
//		urlList.add(new URL("http://www.globo.com"), null);
//		urlList.add(new URL("http://www.gruponzn.com"), null);
//		urlList.add(new URL("http://olhardigital.uol.com.br"), null);
//		urlList.add(new URL("http://www.uol.com.br"), null);
//		urlList.add(new URL("http://www.pcmag.com"), null);
//		urlList.add(new URL("http://thehackernews.com"), null);
//		
//		
//		ConnectionProducer connectionProducer = new ConnectionProducer(urlList, connectionList);
//		HTMLPull htmlPull = new HTMLPull(connectionList, htmlList);
//		URLConsumer urlExtractor = new URLConsumer(urlList);
//		HTMLSpliterator htmlSpliterator = new HTMLSpliterator(htmlList, urlExtractor);
//		DomainLinkedRanking domainRanking = new DomainLinkedRanking();
//		
//		Thread domainRankingThread = new Thread(domainRanking);
//		Thread connectionProducerThread = new Thread(connectionProducer);
//		Thread htmlPullThread = new Thread(htmlPull);
//		Thread urlExtractorThread = new Thread(urlExtractor);
//		Thread htmlSpliteratorThread = new Thread(htmlSpliterator);
//
//		urlExtractorThread.setPriority(Thread.MAX_PRIORITY);
//
//		connectionProducerThread.setPriority(Thread.MIN_PRIORITY);
//		htmlPullThread.setPriority(Thread.MIN_PRIORITY);
//		htmlSpliteratorThread.setPriority(Thread.MIN_PRIORITY);
//
//		domainRankingThread.start();
//		connectionProducerThread.start();
//		htmlPullThread.start();
//		urlExtractorThread.start();
//		htmlSpliteratorThread.start();
	}
}
