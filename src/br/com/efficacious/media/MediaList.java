/**
 * 
 */
package br.com.efficacious.media;

import java.net.URLConnection;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

import br.com.efficacious.crawler.WebCrawler;

/**
 * A media {@link URLConnection} list to save the medias that will be stored on the index 
 * given by {@link WebCrawler} config.
 * 
 * @author Jean Jung
 */
public class MediaList {
	
	private Queue<URLConnection> medias;
	
	/**
	 * Default constructor
	 */
	public MediaList() {
		this.medias = new ArrayBlockingQueue<>(100);
	}
	
	/**
	 * Add a {@link URLConnection} to the list.
	 * @param media
	 */
	public synchronized void add(URLConnection media) {
		this.medias.add(media);
	}
}
