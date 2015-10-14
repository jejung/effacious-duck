package main;

import java.io.IOException;
import java.net.URL;

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

		urlList.add(new URL("http://g1.globo.com"));
		urlList.add(new URL("http://www.tecmundo.com.br"));
		urlList.add(new URL("http://docs.oracle.com"));
		
		ConnectionProducer connectionProducer = new ConnectionProducer(urlList, connectionList);
		HTMLPull htmlPull = new HTMLPull(connectionList, htmlList);
		URLExtractor urlExtractor = new URLExtractor(urlList);
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
