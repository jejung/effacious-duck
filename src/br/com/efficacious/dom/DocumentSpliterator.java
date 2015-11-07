package br.com.efficacious.dom;

import java.util.concurrent.ExecutionException;
import java.util.logging.Level;

import br.com.efficacious.config.CrawlerConfig;
import br.com.efficacious.crawler.CrawlerComponent;
import br.com.efficacious.url.DocumentConsumer;

/**
 * Class that split an web page into tasks to send to anothers modules
 * 
 * @author johnny w. g. g.
 * @author Jean Jung
 */
public class DocumentSpliterator extends CrawlerComponent {

	private DocumentList htmlList;
	private DocumentConsumer urlConsumer;
	private boolean alive;
	private CrawlerConfig config;

	public DocumentSpliterator(CrawlerConfig config, DocumentList htmlList, DocumentConsumer urlExtractor) {
		this.config = config;
		this.htmlList = htmlList;
		this.urlConsumer = urlExtractor;
		this.setAlive(true);
	}

	@Override
	public void run() {
		this.splitForever();
	}
	
	private void splitForever() {
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

	/**
	 * {@inheritDoc CrawlerComponent}
	 */
	@Override
	public void stop() {
		if (this.alive)
			this.alive = false;
	}
}
