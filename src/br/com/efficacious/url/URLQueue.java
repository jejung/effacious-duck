package br.com.efficacious.url;

import java.net.URL;
import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
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

	private Lock writeLock;
	private Lock readLock;

	private Object isEmpty;

	public int size() {
		return queue.size();
	}

	private URLQueue() {
		this.queue = new ArrayDeque<URL>();
		this.readLock = new ReentrantLock();
		this.writeLock = new ReentrantLock();
		this.isEmpty = new Object();
	}

	public static URLQueue getInstance() {
		return instance;
	}

	public boolean isEmpty() {
		return queue.isEmpty();
	}

	public boolean isFull() {
		return queue.size() > MAX_URLS;
	}

	public void add(URL url) {

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
				Logger.getGlobal().log(Level.INFO, "URL enqueued: " + url);
			}

			synchronized (isEmpty) {
				isEmpty.notifyAll();
			}

		} finally {
			writeLock.unlock();
		}

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
