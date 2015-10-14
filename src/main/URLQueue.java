package main;

import java.net.URL;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Class that handle and URL queue to be consumed by Connection Producer.
 * 
 * @author johnny w. g. g.
 *
 */
public class URLQueue {

	private Set<URL> queue;
	private static URLQueue instance = new URLQueue();

	private static final int MAX_URLS = 100;

	public int size() {
		return queue.size();
	}

	private URLQueue() {
		this.queue = new LinkedHashSet<URL>();
	}

	public static URLQueue getInstance() {
		return instance;
	}

	public synchronized boolean isEmpty() {
		return queue.isEmpty();
	}

	public synchronized boolean isFull() {
		return queue.size() > MAX_URLS;
	}

	public synchronized void add(URL url) {

		// TODO here we will to make some machanism to store the urls on the
		// database because if wait() is called could have an deadlock in some
		// specific cases
		if (isFull()) {
			return;
		}

		// TODO we will use isFull in the future, after fix TODO described above
		/*
		 * while (isFull()) { wait(); }
		 */
		
		if (queue.add(url)) {
			System.out.println("URL enqueued: " + url);
		}
		
		notifyAll();
	}

	synchronized public URL pop() {
		try {
			while (isEmpty()) {
				wait();
			}
			
			Iterator<URL> iterator = queue.iterator();
			URL url = iterator.next();
			iterator.remove();
			
			notifyAll();
			
			return url;
		} catch (InterruptedException e) {
			return null;
		}
	}
}
