/**
 * 
 */
package main;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayDeque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.print.Doc;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

/**
 * Class that handle extraction of links in web pages
 * 
 * @author jean
 * @author johnny w. g. g.
 */
public class URLExtractor implements Runnable {

    volatile ArrayDeque<Document> docs;

    private Object lock = new Object();

    private URLList urlList;

    private static final int MAX_CONNECTIONS = 50;

    private ExecutorService executor;

    public URLExtractor(URLList urlList) {
	docs = new ArrayDeque<>();
	this.urlList = urlList;

	// condition = lockDocs.newCondition();

	executor = Executors.newFixedThreadPool(MAX_CONNECTIONS);

    }

    public void addDocument(Document doc) {
	//System.out.println("BEORE CRITICAL SESSION");
	synchronized (lock) {
	    // lockDocs.lock();
	//    System.out.println("addDocument");
	    // try {

	    docs.add(doc);
	    // condition.signalAll();
	    lock.notifyAll();
	 //   System.out.println("CALLED NOTIFY");
	    // } finally {
	    // lockDocs.unlock();
	    // }
	}
    }

    private void extract(Element el) {

	//System.out.println("extracting");

	if (el.hasAttr("href")) {

	    try {

		if (!el.absUrl("href").isEmpty()) {
		    System.out.println(el.absUrl("href"));
		    urlList.add(new URL(el.absUrl("href")));
		    // urlList.notifyAll();
		}

	    } catch (MalformedURLException e) {
		// dont worry
		// e.printStackTrace();
	    }
	}
	for (Element ch : el.children())
	    extract(ch);
    }

    @Override
    public void run() {

	while (true) {

	    try {

		synchronized (lock) {
		    while (docs.isEmpty()) {
			//System.out.println("SLEEPING");
			lock.wait();
		    }

		   // System.out.println("url extractor");
		}

		executor.submit(new Runnable() {
		    public void run() {
			extract(docs.poll());
		    }
		});

	    } catch (InterruptedException e) {
		break;
	    }

	}

    }
}
