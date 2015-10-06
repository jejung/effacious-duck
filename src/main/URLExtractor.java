/**
 * 
 */
package main;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Stack;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
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
		// System.out.println("BEORE CRITICAL SESSION");
		synchronized (lock) {

			docs.add(doc);

			lock.notifyAll();

		}
	}

	private void extract(Element el) {

		// Element content = el.getElementById("content");
		Elements links = el.getElementsByTag("a");
		for (Element link : links) {

			// System.out.println(link);
			try {
				urlList.add(new URL(link.attr("href")));
				System.out.println("Mapped = " + mapped++);
			} catch (MalformedURLException e) {
				// System.err.println(e.getMessage());
			}
		}

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

							}
						}

					});

				}

			} catch (InterruptedException e) {
				break;
			}

		}

	}
}
