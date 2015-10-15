package br.com.efficacious.connection;

import java.net.URLConnection;
import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Thread-Safe list of connections that holds all the open and not used
 * connections. Internally it uses a {@link Queue} to hold the connection and
 * return the first open.
 * 
 * @author johnny w. g. g.
 *
 */
public class ConnectionList {

	private Queue<Future<URLConnection>> queue;
	private static ConnectionList instance = new ConnectionList();

	private static final int MAX_CONNECTIONS = 40;

	private ConnectionList() {
		this.queue = new ArrayDeque<>();
	}

	public static ConnectionList getInstance() {
		return instance;
	}

	public synchronized void add(Future<URLConnection> connection) {
		try {
			while (this.isFull()) {
				wait();
			}
			queue.add(connection);
		} catch (InterruptedException e) {
			Logger.getGlobal().log(Level.SEVERE, "Thread interrupted", e);
		} 
		
		notifyAll();
	}

	public synchronized boolean isFull() {
		return queue.size() > MAX_CONNECTIONS;
	}

	public synchronized boolean isEmpty() {
		return queue.isEmpty();
	}

	/**
	 * Return an Future of URLConnection that could be completed or not, to get
	 * an URLConnection directly use {@link #get()}, but be careful
	 * 
	 * @see #get()
	 * @return
	 * @throws ExecutionException
	 */
	public synchronized Future<URLConnection> getAsFuture() throws ExecutionException {
		try {
			while (this.isEmpty()) {
				wait();
			}

			Future<URLConnection> future = queue.poll();
			notifyAll();
			return future;
		} catch (InterruptedException e) {
			return null;
		}
	}

	/**
	 * This method should be avoid, since it's block the list until the
	 * connection is completed. Use {@link #getAsFuture()} instead
	 * 
	 * @see #getAsFuture()
	 * @return the connection if exists, null otherwise
	 * @throws InterruptedException
	 *             if this thread was interruped
	 * 
	 */
	public synchronized URLConnection get() throws InterruptedException, ExecutionException {
		return queue.poll().get();
	}

}
