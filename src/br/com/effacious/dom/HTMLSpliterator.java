package br.com.effacious.dom;

import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jsoup.nodes.Document;

import br.com.effacious.url.URLConsumer;

/**
 * Class that split an web page into tasks to send to anothers modules
 * 
 * @author johnny w. g. g.
 *
 */
public class HTMLSpliterator implements Runnable {

	private HTMLList htmlList;
	private URLConsumer urlExtractor;
	private boolean alive;

	public HTMLSpliterator(HTMLList htmlList, URLConsumer urlExtractor) {
		this.htmlList = htmlList;
		this.urlExtractor = urlExtractor;
		this.setAlive(true);
	}

	@Override
	public void run() {
		this.splitForEver();
	}
	
	private void splitForEver() {
		while (this.isAlive()) {
			Document doc;
			try {
				doc = this.htmlList.getAsFuture().get();
				// TODO fix in some way that don't need this if clause
				// could be happen in some cases of timeout connection
				if (doc != null) {
					this.urlExtractor.addDocument(doc);
				}
			} catch (InterruptedException | ExecutionException e) {
				Logger.getGlobal().log(Level.SEVERE, "Thread interrupted", e);
			}
		}
	}

	/**
	 * @return the alive
	 */
	public boolean isAlive() {
		return alive;
	}

	/**
	 * @param alive the alive to set
	 */
	public void setAlive(boolean alive) {
		this.alive = alive;
	}
}
