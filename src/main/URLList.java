package main;

import java.net.URL;
import java.util.ArrayDeque;
import java.util.Queue;

/**
 * Class that handle and URL queue to be consumed by Connection Producer.
 * 
 * @author johnny w. g. g.
 *
 */
public class URLList {

	private Queue<URL> queue;
	private static URLList instance = new URLList();
	
	public int size() {
		return queue.size();
	}
	
	private URLList() {
		this.queue = new ArrayDeque<>();
	}

	public static URLList getInstance() {
		return instance;
	}

	public synchronized boolean isEmpty() {
		return queue.isEmpty();
	}

	public synchronized boolean isFull() {
		return queue.size() > 2000;
	}

	public synchronized void add(URL url) {
		try {
			while (isFull())
				wait();
			queue.add(url);
			//System.err.println("ADDED " + url);
			this.notifyAll();
		} catch (InterruptedException e) {

		}
	}

	public synchronized URL get() {
		try {
			while (isEmpty())
				wait();
			URL url = queue.poll();
			notifyAll();
			return url;
		} catch (InterruptedException e) {
			return null;
		}
	}
}
