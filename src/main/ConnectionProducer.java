/**
 * 
 */
package main;

import java.net.URL;
import java.net.URLConnection;
import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.Semaphore;

/**
 * @author Jean Jung
 * @author Johnny W. G. G.
 *
 */
public class ConnectionProducer implements Runnable {

	private static final int MAX_CONNECTIONS = 50;

	private ExecutorService executor;

	//private Queue<Future<URLConnection>> connections;

	//private Object pollLock;
	// private Object addLock;

	//private Semaphore sem = new Semaphore(MAX_CONNECTIONS);

	// private BlockingQueue<Runnable> runnables;

	private URLList urlList;
	private ConnectionList connectionList;

	@Override
	public void run() {

		while (true) {

			synchronized (urlList) {
				try {
					while (urlList.isEmpty())
						urlList.wait();
				} catch (InterruptedException e) {
					break;
				}
				connectionList.add(this.executor.submit(new ConnectionCreator(urlList.get())));
			}
		}
	}

	public ConnectionProducer(URLList urlList, ConnectionList connectionList) {

		this.urlList = urlList;
		this.connectionList = connectionList;

		// this.runnables = new LinkedBlockingQueue<>();
		//this.pollLock = new Object();
		// this.addLock = new Object();
		this.executor = Executors.newFixedThreadPool(MAX_CONNECTIONS);

	}

//	/**
//	 * 
//	 * @param uri
//	 * @return
//	 * @throws InterruptedException
//	 */
//	@Deprecated
//	public Future<URLConnection> add(URL uri) throws InterruptedException {
//
//		sem.acquire();
//
//		try {
//
//			this.urlList.add(uri);
//
//			ConnectionCreator creator = new ConnectionCreator(uri);
//
//			Future<URLConnection> future = this.executor.submit(creator);
//
//			return future;
//
//		} finally {
//			sem.release();
//		}
//	}

//	@Deprecated
//	public URLConnection poll() {
//		synchronized (this.pollLock) {
//			// TODO: Fazer await.
//			return this.connections.poll();
//		}
//	}

	private class ConnectionCreator implements Callable<URLConnection> {

		private URL uri;

		public ConnectionCreator(URL uri) {
			this.uri = uri;
		}

		@Override
		public URLConnection call() throws Exception {
			return uri.openConnection();
		}
	}

}
