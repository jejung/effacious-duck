/**
 * 
 */
package main;

import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Jean Jung
 * @author Johnny W. G. G.
 *
 */
public class ConnectionProducer implements Runnable {

	private static final int MAX_CONNECTIONS = 5;

	private ExecutorService executor;

	private URLList urlList;
	private ConnectionList connectionList;

	@Override
	public void run() {

		while (true) {

			connectionList.add(this.executor.submit(new ConnectionCreator(urlList.get())));

		}
	}

	public ConnectionProducer(URLList urlList, ConnectionList connectionList) {
		this.urlList = urlList;
		this.connectionList = connectionList;
		this.executor = Executors.newFixedThreadPool(MAX_CONNECTIONS);

	}

	private class ConnectionCreator implements Callable<URLConnection> {

		private URL uri;

		public ConnectionCreator(URL uri) {
			this.uri = uri;
		}

		@Override
		public URLConnection call() throws Exception {
			return uri.openConnection();
		}
	}

}
