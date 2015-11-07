package br.com.efficacious.dom;

import java.net.URLConnection;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;

import org.jsoup.helper.HttpConnection;

import br.com.efficacious.config.CrawlerConfig;
import br.com.efficacious.connection.ConnectionList;
import br.com.efficacious.connection.ContentTypeResolver;
import br.com.efficacious.crawler.CrawlerComponent;
import br.com.efficacious.media.MediaList;

/**
 * Pull connections from a {@link ConnectionList} and serve the {@link DocumentList} with a open {@link HttpConnection} 
 * 
 * @author johnny w. g. g.
 * @author Jean Jung
 */
public class DocumentPull extends CrawlerComponent {

	private volatile ConnectionList connectionList;
	private volatile DocumentList htmlList;
	private MediaList mediaList;
	private boolean alive;
	private CrawlerConfig config;
	private ContentTypeResolver contentTypeResolver;

	public DocumentPull(CrawlerConfig config, ConnectionList connectionList, DocumentList htmlList, MediaList mediaList) {
		this.config = config;
		this.connectionList = connectionList;
		this.htmlList = htmlList;
		this.mediaList = mediaList;
		this.alive = true;
		this.contentTypeResolver = new ContentTypeResolver(config);
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
					this.contentTypeResolver.forwardConnection(con, htmlList, mediaList);
				}
			} catch (InterruptedException | ExecutionException e) {
				this.config.getLogger().log(Level.SEVERE, "Error producing connections", e);
			}
		}
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

	/* (non-Javadoc)
	 * @see br.com.efficacious.crawler.CrawlerComponent#stop()
	 */
	@Override
	public void stop() {
		if (this.alive) {
			this.alive = false;
		}
	}
}
