package br.com.efficacious.test;

import java.net.MalformedURLException;
import java.net.URL;
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

	public static void main(String[] args) throws InterruptedException, MalformedURLException {

		Logger crawlerLogger = Logger.getLogger("CrawlerLogger");
		crawlerLogger.setUseParentHandlers(false);
		crawlerLogger.addHandler(new CrawlerLogHandler());
		crawlerLogger.setLevel(Level.ALL);
		
		CrawlerConfig config = CrawlerConfig
								.builder()
								.initFromArgs(args)
								.logOn(crawlerLogger)
								.build();
		WebCrawler crawler = new WebCrawler(config);
		crawler.addBaseURL(new URL("http://www.globo.com"));
		crawler.addBaseURL(new URL("http://www.furb.br"));
		crawler.start();
//		crawler.stop();
	}
}
