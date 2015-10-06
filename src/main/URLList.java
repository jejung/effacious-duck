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

    private URLList() {
	this.queue = new ArrayDeque<URL>();
    }

    public static URLList getInstance() {
	return instance;
    }

    public synchronized boolean isEmpty() {
	return queue.isEmpty();
    }

    public synchronized void add(URL url) {
	queue.add(url);
	this.notifyAll();
    }

    public synchronized URL get() {
	return queue.poll();
    }
}
