package main;

import java.io.InputStream;
import java.util.ArrayDeque;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

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
	return documents.poll();
    }

    public synchronized void add(InputStream input, String charSet, String baseUri) {

	documents.add(executor.submit(new Callable<Document>() {
	    @Override
	    public Document call() throws Exception {
		try {
		return Jsoup.parse(input, charSet, baseUri);
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
