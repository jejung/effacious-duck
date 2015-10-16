package br.com.efficacious.main;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import br.com.efficacious.config.NetConfig;
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
		
		Proxy proxy = null;
		
		for (int i = 0; i < args.length; i++) {
			try {
				if ("-p".equals(args[i])) {
					i++;
					String host = args[i].split(":")[0];
					int port = Integer.parseInt(args[i].split(":")[1]);
					
					proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(host, port));
				}
			} catch (Exception e) {
				Logger.getGlobal().log(Level.SEVERE, String.format("Invalid argument[%d]: %s", i, e.getMessage(), e));
				throw e;
			}
		}
		
		NetConfig.instance().setProxy(proxy);
		
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
