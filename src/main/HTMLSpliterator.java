package main;

import java.util.concurrent.ExecutionException;

import org.jsoup.nodes.Document;

/**
 * Class that split an web page into tasks to send to anothers modules
 * 
 * @author johnny w. g. g.
 *
 */
public class HTMLSpliterator implements Runnable {

	private HTMLList htmlList;
	private URLExtractor urlExtractor;
	private boolean alive;

	public HTMLSpliterator(HTMLList htmlList, URLExtractor urlExtractor) {
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
				doc = htmlList.getAsFuture().get();
				// TODO fix in some way that don't need this if clause
				// could be happen in some cases of timeout connection
				if (doc != null) {
					urlExtractor.addDocument(doc);
				}
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
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
