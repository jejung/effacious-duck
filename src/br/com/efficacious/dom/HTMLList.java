package br.com.efficacious.dom;

import java.net.URLConnection;
import java.util.ArrayDeque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 
 * @author Jean Jung
 * @author johnny w. g. g.
 */
public class HTMLList {

	private static final int MAX_CONNECTIONS = 5;
	private static final int MAX_DOCS = 40;

	private ArrayDeque<Future<URLDocument>> documents;
	private ExecutorService executor;

	public HTMLList() {
		documents = new ArrayDeque<>();
		executor = Executors.newFixedThreadPool(MAX_CONNECTIONS);
	}

	public synchronized Future<URLDocument> getAsFuture() {
		try {
			while (isEmpty())
				wait();

			Future<URLDocument> future = documents.poll();
			notifyAll();

			return future;

		} catch (InterruptedException e) {
			return null;
		}
	}

	/**
	 * 
	 * @param connection
	 */
	public synchronized void add(URLConnection connection) {

		while (isFull()) {
			try {
				wait();
			} catch (InterruptedException e) {
				Logger.getGlobal().log(Level.SEVERE, "Thread interrupted", e);
			}
		}

		documents.add(executor.submit(new DocumentCreator(connection)));
		notifyAll();
	}

	public synchronized boolean isEmpty() {
		return documents.isEmpty();
	}

	public synchronized boolean isFull() {
		return documents.size() > MAX_DOCS;
	}
}
