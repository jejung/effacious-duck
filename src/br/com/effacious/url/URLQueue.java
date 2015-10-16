package br.com.effacious.url;

import java.net.URL;
import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class that handle and URL queue to be consumed by Connection Producer.
 * 
 * @author johnny w. g. g.
 *
 */
public class URLQueue {

	private static final int MAX_URLS = 100;
	
	private static URLQueue instance = new URLQueue();
	
	private Queue<URL> queue;
	private ReadWriteLock lock;

	public int size() {
		return queue.size();
	}

	private URLQueue() {
		this.queue = new ArrayDeque<URL>();
		this.lock = new ReentrantReadWriteLock();
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
			System.err.println("URL enqueued: " + url);
			Logger.getGlobal().log(Level.INFO, "URL enqueued: " + url);
		}
		notifyAll();
	}

	public synchronized URL pop() throws InterruptedException {
		while (isEmpty()) {
			wait();
		}
		
		lock.readLock().lock();
		
		try {
			URL url = queue.poll();
			
			notifyAll();
			
			return url;
		} finally {
			lock.readLock().unlock();
		}
	}
}
