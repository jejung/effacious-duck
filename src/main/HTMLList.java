package main;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayDeque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class HTMLList {

    private static final int MAX_CONNECTIONS = 50;

    private static HTMLList instance = new HTMLList();

    private ArrayDeque<Document> documents;

    private ExecutorService executor;

    private HTMLList() {
	documents = new ArrayDeque<>();
	executor = Executors.newFixedThreadPool(MAX_CONNECTIONS);
    }

    public static HTMLList getInstance() {
	return instance;
    }
    
    public synchronized Document get() {
	return documents.poll();
    }
    
    public synchronized void add(InputStream input, String charSet, String baseUri) {

	executor.submit(new Runnable() {
	    @Override
	    public void run() {
		try {
		    documents.add(Jsoup.parse(input, charSet, baseUri));
		} catch (IOException e) {
		    // TODO maybe do some treatment in parse errors or connections problem or just
		    // don't get mad
		}
	    }
	});

    }

    public synchronized boolean isEmpty() {
	return documents.isEmpty();
    }

}
