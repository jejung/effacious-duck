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
	private boolean alive;
	
	public ConnectionProducer(URLList urlList, ConnectionList connectionList) {
		this.urlList = urlList;
		this.connectionList = connectionList;
		this.executor = Executors.newFixedThreadPool(MAX_CONNECTIONS);
		this.alive = true;
	}

	@Override
	public void run() {
		this.produceForever();
	}
	
	private void produceForever() {
		
		while (this.isAlive()) {
			this.connectionList.add(this.executor.submit(new ConnectionCreator(urlList.get())));
		}
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
	
	/**
	 * @return the alive
	 */
	public boolean isAlive() {
		return this.alive;
	}

	/**
	 * @param alive the alive to set
	 */
	public void setAlive(boolean alive) {
		this.alive = alive;
	}

}
