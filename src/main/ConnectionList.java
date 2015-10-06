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

	private volatile Queue<Future<URLConnection>> queue;
	private static ConnectionList instance = new ConnectionList();
	
	private volatile static int size = 0;
	
	private ConnectionList() {
		this.queue = new ArrayDeque<>();

	}

	public static ConnectionList getInstance() {
		return instance;
	}

	public synchronized void add(Future<URLConnection> connection) {
		
		System.out.println("SIZE CONNECTION LIST " + queue.size());
		
		if (queue.size() < 2000) {
			//stem.out.println("ENTROU");
			queue.add(connection);
		//	size++;
		} 
			
		
		this.notifyAll();
	}

	public synchronized boolean isFull() {
		return queue.size() > 1000;
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
	 */
	public synchronized Future<URLConnection> getAsFuture() {
		size--;
		return queue.poll();
	}

	/**
	 * This method should be avoid, since it's block the list until the
	 * connection is completed. Use {@link #getAsFuture()} instead
	 * 
	 * @see #getAsFuture()
	 * @return
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	public synchronized URLConnection get() throws InterruptedException, ExecutionException {
		return queue.poll().get();
	}

}
