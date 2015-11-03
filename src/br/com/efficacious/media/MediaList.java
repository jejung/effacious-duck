/**
 * 
 */
package br.com.efficacious.media;

import java.net.URLConnection;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * @author Jean Jung
 *
 */
public class MediaList {
	
	private Queue<URLConnection> medias;
	
	/**
	 * 
	 */
	public MediaList() {
		this.medias = new ArrayBlockingQueue<>(100);
	}
	
	public synchronized void add(URLConnection media) {
		this.medias.add(media);
	}
}
