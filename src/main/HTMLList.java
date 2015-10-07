package main;

import java.io.InputStream;
import java.net.URLConnection;
import java.util.ArrayDeque;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/**
 * 
 * @author Jean Jung
 * @author johnny w. g. g.
 */
public class HTMLList {

	private static final int MAX_CONNECTIONS = 5;

	private static HTMLList instance = new HTMLList();

	private ArrayDeque<Future<Document>> documents;

	// TODO create an .ini file or store these values in a DB
	private static final int MAX_DOCS = 40;

	private ExecutorService executor;

	private HTMLList() {
		documents = new ArrayDeque<>();
		executor = Executors.newFixedThreadPool(MAX_CONNECTIONS);
	}

	public static HTMLList getInstance() {
		return instance;
	}

	public synchronized Future<Document> getAsFuture() {

		try {

			while (isEmpty())
				wait();

			Future<Document> future = documents.poll();

			notifyAll();

			return future;

		} catch (InterruptedException e) {
			return null;
		}

	}

	public synchronized void add(URLConnection con) {

		while (isFull())

			try {
				wait();
			} catch (InterruptedException e1) {

			}

		documents.add(executor.submit(new Callable<Document>() {
			@Override
			public Document call() throws Exception {

				try (InputStream inputStream = con.getInputStream();) {

					Document doc = Jsoup.parse(inputStream, con.getContentEncoding(), con.getURL().toString());
					return doc;
				} catch (Exception e) {
					System.out.println(e.getMessage());
				}

				return null;
			}
		}));

		notifyAll();

	}

	public synchronized boolean isEmpty() {
		return documents.isEmpty();
	}

	public synchronized boolean isFull() {
		return documents.size() > MAX_DOCS;
	}
}
