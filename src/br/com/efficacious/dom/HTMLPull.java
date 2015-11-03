package br.com.efficacious.dom;

import java.io.IOException;
import java.net.URLConnection;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;

import org.jsoup.helper.HttpConnection;

import br.com.efficacious.config.CrawlerConfig;
import br.com.efficacious.config.MediaStorage;
import br.com.efficacious.connection.ConnectionList;
import br.com.efficacious.http.ContentTypeRepository;
import br.com.efficacious.media.MediaList;

/**
 * Pull connections from a {@link ConnectionList} and serve the {@link HTMLList} with a open {@link HttpConnection} 
 * 
 * @author johnny w. g. g.
 * @author Jean Jung
 */
public class HTMLPull implements Runnable {

	private ConnectionList connectionList;
	private HTMLList htmlList;
	private MediaList mediaList;
	private boolean alive;
	private CrawlerConfig config;

	public HTMLPull(CrawlerConfig config, ConnectionList connectionList, HTMLList htmlList) {
		this.config = config;
		this.connectionList = connectionList;
		this.htmlList = htmlList;
		this.alive = true;
	}

	@Override
	public void run() {
		this.produceForEver();
	}
	
	/**
	 * Produces until {@code this.isAlive() == false} 
	 */
	private void produceForEver() {
		while (this.isAlive()) {
			try {
				URLConnection con = this.connectionList.getAsFuture().get();
				if (con != null) {
					if (this.filterConnection(con)) {
						if (this.isMedia(con)) {
							if (this.filterMedia(con)) {
								this.mediaList.add(con);
								continue;
							}
						}
						else {
							htmlList.add(con);
							continue;
						}
					}
					
					try {
						con.getInputStream().close();
					} catch (IOException e) {
						this.config.getLogger().log(Level.SEVERE, "Error closing an unsued connection", e);
					} 
				}
			} catch (InterruptedException | ExecutionException e) {
				this.config.getLogger().log(Level.SEVERE, "Error producing connections", e);
			}
		}
	}
	/**
	 * Verify if the connection contains a media that must be stored.
	 * @param connection
	 * @return
	 */
	private boolean filterMedia(URLConnection connection) {
		return this.config.getMediaStorage() == MediaStorage.ANY || 
			(this.config.getMediaStorage() != MediaStorage.NONE &&
				this.config
					.getAcceptedMedias()
					.parallelStream()
					.anyMatch((contentType) -> contentType.equals(connection)));  
	}
	
	/**
	 * Verify if the content-type of the connection is a Media or a HTML document.
	 * @param connection
	 * @return
	 */
	private boolean isMedia(URLConnection connection) {
		return !ContentTypeRepository.HTML.equalsIgnoreCase(connection.getContentType());
	}
	
	/**
	 * Filter the connections that can be used.
	 * @param connection
	 * @return
	 */
	private boolean filterConnection(URLConnection connection) {
		return this.config.getConnectionFilters()
			.parallelStream()
			.allMatch((predicate) -> predicate.accept(connection));
	}

	/**
	 * @return the alive
	 */
	public synchronized boolean isAlive() {
		return alive;
	}

	/**
	 * @param alive the alive to set
	 */
	public synchronized void setAlive(boolean alive) {
		this.alive = alive;
	}
}
