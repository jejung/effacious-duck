package br.com.efficacious.main;

import java.io.IOException;
import java.net.URL;
import java.util.logging.ConsoleHandler;
import java.util.logging.Logger;

import br.com.efficacious.connection.ConnectionList;
import br.com.efficacious.connection.ConnectionProducer;
import br.com.efficacious.dom.HTMLList;
import br.com.efficacious.dom.HTMLPull;
import br.com.efficacious.dom.HTMLSpliterator;
import br.com.efficacious.url.URLConsumer;
import br.com.efficacious.url.URLQueue;

/**
 * @author johnny w. g. g.
 * @author Jean Jung
 */
public class Main {

	static volatile ConnectionList connectionList = ConnectionList.getInstance();
	static volatile URLQueue urlList = URLQueue.getInstance();
	static volatile HTMLList htmlList = HTMLList.getInstance();

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		
		Logger.getGlobal().addHandler(new ConsoleHandler());
		
		urlList.add(new URL("http://www.furb.br/web/10/portugues"));
		urlList.add(new URL("http://g1.globo.com"));
		urlList.add(new URL("http://www.tecmundo.com.br"));
		urlList.add(new URL("http://docs.oracle.com"));
		
		ConnectionProducer connectionProducer = new ConnectionProducer(urlList, connectionList);
		HTMLPull htmlPull = new HTMLPull(connectionList, htmlList);
		URLConsumer urlExtractor = new URLConsumer(urlList);
		HTMLSpliterator htmlSpliterator = new HTMLSpliterator(htmlList, urlExtractor);

		Thread connectionProducerThread = new Thread(connectionProducer);
		Thread htmlPullThread = new Thread(htmlPull);
		Thread urlExtractorThread = new Thread(urlExtractor);
		Thread htmlSpliteratorThread = new Thread(htmlSpliterator);
		
		urlExtractorThread.setPriority(Thread.MAX_PRIORITY);
		
		connectionProducerThread.setPriority(Thread.MIN_PRIORITY);
		htmlPullThread.setPriority(Thread.MIN_PRIORITY);
		htmlSpliteratorThread.setPriority(Thread.MIN_PRIORITY);
		
		connectionProducerThread.start();
		htmlPullThread.start();
		urlExtractorThread.start();
		htmlSpliteratorThread.start();
	}
}
