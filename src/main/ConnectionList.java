package main;

import java.net.URLConnection;
import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Classe que gerencia a lista de conexoes usadas pela aplicação
 * 
 * @author johnny w. g. g.
 *
 */
public class ConnectionList {

	private Queue<Future<URLConnection>> queue;
	private static ConnectionList instance = new ConnectionList();

	private ConnectionList() {
		this.queue = new ArrayDeque<>();

	}

	public static ConnectionList getInstance() {
		return instance;
	}

	public synchronized void add(Future<URLConnection> connection) {
		queue.add(connection);
	}
	
	/**
	 * Return an Future of URLConnection that could be completed or not, to get an URLConnection directly use {@link #get()}, but be careful
	 * @see #get() 
	 * @return
	 */
	public synchronized Future<URLConnection> getAsFuture() {
		return queue.poll();
	}

	/**
	 * This method should be avoid, since it's block the list until the connection is completed. Use {@link #getAsFuture()} instead
	 * @see #getAsFuture() 
	 * @return 
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	public synchronized URLConnection get() throws InterruptedException, ExecutionException {
		return queue.poll().get();
	}

}
