/**
 * 
 */
package main;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayDeque;
import java.util.Stack;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.print.Doc;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

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

	private static final int MAX_CONNECTIONS = 5;
	
	// TODO tem que ser synchronized
	int mapped = 0;
	
	private ExecutorService executor;

	public URLExtractor(URLList urlList) {
		docs = new ArrayDeque<>();
		this.urlList = urlList;

		// condition = lockDocs.newCondition();

		executor = Executors.newFixedThreadPool(MAX_CONNECTIONS);

	}

	public void addDocument(Document doc) {
		// System.out.println("BEORE CRITICAL SESSION");
		synchronized (lock) {
			// lockDocs.lock();
			// System.out.println("addDocument");
			// try {

			docs.add(doc);
			// condition.signalAll();
			lock.notifyAll();
			// System.out.println("CALLED NOTIFY");
			// } finally {
			// lockDocs.unlock();
			// }
		}
	}

	private void extract(Element el) {

		//Element content = el.getElementById("content");
		Elements links = el.getElementsByTag("a");
		for (Element link : links) {
			
		//	System.out.println(link);
			try {
				urlList.add(new URL(link.attr("href")));
				System.out.println("Mapped = " + mapped++);
			} catch (MalformedURLException e) {
				//System.err.println(e.getMessage());
			}
		}

		// String linkHref = link.attr("href");
		// String linkText = link.text();

		// Stack<Element> st = new Stack<Element>();
		//
		// st.add(el);
		//
		// int max = 0;
		//
		// while (!st.isEmpty()) {
		//
		// Element child = st.pop();
		//
		// if (child.hasAttr("href")) {
		//
		// try {
		//
		// if (!child.absUrl("href").isEmpty()) {
		// System.out.println(child.absUrl("href"));
		// urlList.add(new URL(child.absUrl("href")));
		// // urlList.notifyAll();
		// }
		//
		// } catch (MalformedURLException e) {
		// // dont worry
		// // e.printStackTrace();
		// }
		// } else {
		// //System.out.println(child);
		// }
		//
		// if (max <= 1000) {
		// for (Element e : child.children()) {
		// st.add(e);
		// max++;
		// }
		// }
		// }

	}

	@Override
	public void run() {

		while (true) {

			try {

				synchronized (lock) {
					while (docs.isEmpty()) {
						// System.out.println("SLEEPING");
						lock.wait();
					}

			lock.wait(35);
					
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
