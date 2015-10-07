/**
 * 
 */
package main;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.Semaphore;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Class that handle extraction of links in web pages
 * 
 * @author Jean Jung
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

	private volatile Semaphore sem = new Semaphore(MAX_CONNECTIONS);

	@Deprecated
	private class FuturePoolController extends Thread {

		private final int DEFAULT_WAIT_MILISSECONDS = 30;
		private ArrayList<Future<Void>> connections;
		private final int DEFAULT_CAPACITY;

		public FuturePoolController(final int DEFAULT_CAPACITY) {
			this.DEFAULT_CAPACITY = DEFAULT_CAPACITY;
			connections = new ArrayList<>(DEFAULT_CAPACITY);
		}

		public void add(Future<Void> connection) {

			try {

				sem.acquire();

				connections.add(connection);

			} catch (InterruptedException e) {

			}

		}

		public void release() {

		}

		@Override
		public void run() {

			int dynamicProcessorAdjustingTime = 0;

			while (true) {

				try {

					synchronized (this) {

						while (connections.isEmpty())
							wait();

						if (connections.removeIf(p -> p.isDone())) {
							dynamicProcessorAdjustingTime = dynamicProcessorAdjustingTime < 20 ? 0 : dynamicProcessorAdjustingTime - 20;
							notifyAll();
						} else {
							dynamicProcessorAdjustingTime += 10;
						}

						wait(DEFAULT_WAIT_MILISSECONDS + dynamicProcessorAdjustingTime);
					}

				} catch (InterruptedException e) {

				}
			}
		}
	}

	public URLExtractor(URLList urlList) {
		docs = new ArrayDeque<>();
		this.urlList = urlList;
		executor = Executors.newFixedThreadPool(MAX_CONNECTIONS);

	}

	public void addDocument(Document doc) {

		synchronized (lock) {

			while (docs.size() > 40)
				try {
					lock.wait();
				} catch (InterruptedException e) {

					e.printStackTrace();
				}

			docs.add(doc);

			lock.notifyAll();

		}
	}

	private void extract(Element el) {

		Elements links = el.getElementsByTag("a");
		for (Element link : links) {
			try {
				String href = link.absUrl("href");
				if (!(href.endsWith(".pdf") || href.endsWith(".jpg") || href.endsWith(".avi"))) {

					// TODO create an machanism to configure permission of https
					// pages
					// could be used to remove https pages, this would be
					// configurable in the application
					if (href.startsWith("https")) {
						href = href.replaceFirst("s", "");
					}
					urlList.add(new URL(href));

				} else {
					// TODO maybe store images on database
					System.out.println("Midia ignored: " + href);
				}
			} catch (MalformedURLException e) {

			}
		}

		System.out.println(mapped++ + " pages indexed so far.");

	}

	@Override
	public void run() {

		// FuturePoolController futurePoolControler = new
		// FuturePoolController(MAX_CONNECTIONS);
		// futurePoolControler.start();

		while (true) {
			try {
				synchronized (lock) {
					while (docs.isEmpty()) {
						lock.wait();
					}
					sem.acquire();

					executor.submit(new Callable<Void>() {
						@Override
						public Void call() throws Exception {

							try {
								extract(docs.poll());
								return null;
							} finally {
								sem.release();
								lock.notifyAll();
							}
						}
					});
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
				break;
			}
		}
	}
}
