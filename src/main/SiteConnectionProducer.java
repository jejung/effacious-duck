/**
 * 
 */
package main;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author Jean Jung
 *
 */
public class SiteConnectionProducer {
	
	ThreadPoolExecutor threads;
	private Queue<URL> queue;
	private Queue<URLConnection> connections;
	private Object connectionsLock;
	private BlockingQueue<Runnable> runnables;
	
	/**
	 * 
	 */
	public SiteConnectionProducer() {
		this.queue = new ConcurrentLinkedQueue<>();
		this.connections = new ConcurrentLinkedQueue<>();
		this.runnables = new LinkedBlockingQueue<>();
		this.connectionsLock = new Object();
		this.threads = new ThreadPoolExecutor(20, 100, 150000, TimeUnit.MILLISECONDS, this.runnables);
	}
	
	public synchronized void add(URL uri) {
		this.queue.add(uri);
		this.threads.submit(new ConnectionCreator());
	}
	
	public URLConnection poll() {
		synchronized (this.connectionsLock) {
			// TODO: Fazer await.
			return this.connections.poll();
		}
	}
	
	private class ConnectionCreator implements Runnable {

		/* (non-Javadoc)
		 * @see java.lang.Runnable#run()
		 */
		@Override
		public void run() {
			synchronized (connectionsLock) {
				try {
					connections.add(queue.poll().openConnection());
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
