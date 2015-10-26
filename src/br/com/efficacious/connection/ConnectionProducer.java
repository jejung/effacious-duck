/**
 * 
 */
package br.com.efficacious.connection;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;

import br.com.efficacious.config.CrawlerConfig;
import br.com.efficacious.url.URLQueue;

/**
 * @author Jean Jung
 * @author Johnny W. G. G.
 *
 */
public class ConnectionProducer implements Runnable {

	private static final int MAX_CONNECTIONS = 5;

	private ExecutorService executor;

	private URLQueue urlList;
	private ConnectionList connectionList;
	private boolean alive;
	
	private CrawlerConfig config;
	
	public ConnectionProducer(CrawlerConfig config, URLQueue urlList, ConnectionList connectionList) {
		this.config = config;
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
		while (isAlive()) {
			try {
				connectionList.add(executor.submit(ConnectionCreator.create(this.config, urlList.pop())));
			} catch (InterruptedException e) {
				this.config.getLogger().log(Level.SEVERE, "Thread interrupted", e);
			}
		}
	}

	/**
	 * @return the alive
	 */
	public synchronized boolean isAlive() {
		return this.alive;
	}

	/**
	 * @param alive the alive to set
	 */
	public synchronized void setAlive(boolean alive) {
		this.alive = alive;
	}

}
