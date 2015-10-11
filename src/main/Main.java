package main;

import java.io.IOException;
import java.net.URL;

/**
 * @author johnny w. g. g.
 * @author Jean Jung
 */
public class Main {

	static volatile ConnectionList connList = ConnectionList.getInstance();
	static volatile URLList urlList = URLList.getInstance();
	static volatile HTMLList htmlList = HTMLList.getInstance();

	public Main() {
	}

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {

		urlList.add(new URL("http://g1.globo.com"));
		urlList.add(new URL("http://www.tecmundo.com.br"));
		urlList.add(new URL("http://docs.oracle.com"));
		
		ConnectionProducer connProducer = new ConnectionProducer(urlList, connList);
		HTMLPull htmlPull = new HTMLPull(connList, htmlList);
		URLExtractor urlExtractor = new URLExtractor(urlList);
		HTMLSpliterator htmlSplit = new HTMLSpliterator(htmlList, urlExtractor);

		Thread t1 = new Thread(connProducer);
		Thread t2 = new Thread(htmlPull);
		Thread t3 = new Thread(urlExtractor);
		Thread t4 = new Thread(htmlSplit);
		
		t3.setPriority(10);
		
		t1.setPriority(1);
		t2.setPriority(1);
		t4.setPriority(1);
		
		t1.start();
		t2.start();
		t3.start();
		t4.start();
	}
}
