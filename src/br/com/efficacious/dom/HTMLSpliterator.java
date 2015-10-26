package br.com.efficacious.dom;

import java.util.concurrent.ExecutionException;
import java.util.logging.Level;

import br.com.efficacious.config.CrawlerConfig;
import br.com.efficacious.url.URLConsumer;

/**
 * Class that split an web page into tasks to send to anothers modules
 * 
 * @author johnny w. g. g.
 *
 */
public class HTMLSpliterator implements Runnable {

	private HTMLList htmlList;
	private URLConsumer urlConsumer;
	private boolean alive;
	private CrawlerConfig config;

	public HTMLSpliterator(CrawlerConfig config, HTMLList htmlList, URLConsumer urlExtractor) {
		this.config = config;
		this.htmlList = htmlList;
		this.urlConsumer = urlExtractor;
		this.setAlive(true);
	}

	@Override
	public void run() {
		this.splitForEver();
	}
	
	private void splitForEver() {
		while (this.isAlive()) {
			URLDocument urlDocument;
			try {
				urlDocument = this.htmlList.getAsFuture().get();
				// TODO fix in some way that don't need this if clause
				// could be happen in some cases of timeout connection
				if (urlDocument != null) {
					this.urlConsumer.addDocument(urlDocument);
				}
			} catch (InterruptedException | ExecutionException e) {
				this.config.getLogger().log(Level.SEVERE, "Thread interrupted", e);
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
}
