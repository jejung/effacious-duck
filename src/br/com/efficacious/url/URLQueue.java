package br.com.efficacious.url;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;

import br.com.efficacious.config.CrawlerConfig;

/**
 * Class that handle and URL queue to be consumed by Connection Producer.
 * 
 * @author johnny w. g. g.
 *
 */
public class URLQueue {

	private static final int MAX_URLS = 100;

	private Queue<URL> queue;

	private Lock writeLock;
	private Lock readLock;

	private Object isEmpty;
	
	private CrawlerConfig config;

	public int size() {
		return queue.size();
	}

	public URLQueue(CrawlerConfig config) {
		this.config = config;
		this.queue = new ArrayDeque<>();
		this.readLock = new ReentrantLock();
		this.writeLock = new ReentrantLock();
		this.isEmpty = new Object();
	}

	public boolean isEmpty() {
		return queue.isEmpty();
	}

	public boolean isFull() {
		return queue.size() > MAX_URLS;
	}
	
	public void add(URL url) {
		this.add(url, null);
	}

	public void add(URL url, String from) {

		writeLock.lock();
		try {

			// TODO here we will to make some machanism to store the urls on the
			// database because if wait() is called could have an deadlock in
			// some
			// specific cases
			if (isFull()) {
				return;
			}

			// TODO we will use isFull in the future, after fix TODO described
			// above
			/*
			 * while (isFull()) { wait(); }
			 */

			if (queue.add(url)) {
				 this.config.getLogger().log(Level.INFO, "URL enqueued: " + url);
			}

			if (from != null && !getHostName(from).equals(url.getHost())) {

				DomainRepository.getInstance().addDomain(url.getHost());

			}

			synchronized (isEmpty) {
				isEmpty.notifyAll();
			}

		} finally {
			writeLock.unlock();
		}

	}

	private String getHostName(String urlInput) {
		urlInput = urlInput.toLowerCase();
		String hostName = urlInput;
		
		if (!"".equals(urlInput)) {
			if (urlInput.startsWith("http") || urlInput.startsWith("https")) {
				try {
					URL netUrl = new URL(urlInput);
					String host = netUrl.getHost();
					hostName = host;
				} catch (MalformedURLException e) {
					hostName = urlInput;
				}
			}
			return hostName;
		}
		
		return urlInput;
	}

	public URL pop() throws InterruptedException {

		readLock.lock();

		while (isEmpty()) {

			synchronized (isEmpty) {
				isEmpty.wait();
			}

		}

		try {
			URL url = queue.poll();

			return url;
		} finally {
			readLock.unlock();
		}
	}
}
