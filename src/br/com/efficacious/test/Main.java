package br.com.efficacious.test;

import java.util.logging.Level;
import java.util.logging.Logger;

import br.com.efficacious.config.CrawlerConfig;
import br.com.efficacious.crawler.WebCrawler;
import br.com.efficacious.io.CrawlerLogHandler;

/**
 * Test implementation.
 * 
 * @author johnny w. g. g.
 * @author Jean Jung
 */
public class Main {

	public static void main(String[] args) throws InterruptedException {

		Logger crawlerLogger = Logger.getLogger("CrawlerLogger");
		
		crawlerLogger.addHandler(new CrawlerLogHandler());
		crawlerLogger.setLevel(Level.ALL);
		
		CrawlerConfig config = CrawlerConfig
								.builder()
								.initFromArgs(args)
								.logOn(crawlerLogger.getName())
								.build();
		WebCrawler crawler = new WebCrawler(config);
		
		crawler.start();
	}
}
