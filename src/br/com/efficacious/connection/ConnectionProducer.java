/**
 * 
 */
package br.com.efficacious.connection;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
	
	public ConnectionProducer(URLQueue urlList, ConnectionList connectionList) {
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
				connectionList.add(executor.submit(ConnectionCreator.create(urlList.pop())));
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
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
