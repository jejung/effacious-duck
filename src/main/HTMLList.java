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

	private static final int MAX_CONNECTIONS = 50;

	private static HTMLList instance = new HTMLList();

	private ArrayDeque<Future<Document>> documents;

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

		//System.out.println("SIZE HTML LIST " + documents.size());

		documents.add(executor.submit(new Callable<Document>() {
			@Override
			public Document call() throws Exception {
				System.out.println("Getting inputStream from: " + con.getURL());
				try (InputStream inputStream = con.getInputStream();){
					
					Document doc = Jsoup.parse(inputStream, con.getContentEncoding(), con.getURL().toString());
					return doc;
				} catch (Exception e) {
					System.out.println(e.getMessage());
				}
				
				return null;
			}
		}));

		this.notifyAll();

	}

	public synchronized boolean isEmpty() {
		return documents.isEmpty();
	}

}
