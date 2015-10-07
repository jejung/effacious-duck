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

	public HTMLSpliterator(HTMLList htmlList, URLExtractor urlExtractor) {
		this.htmlList = htmlList;
		this.urlExtractor = urlExtractor;
	}

	@Override
	public void run() {

		while (true) {

			Document doc;

			try {

				doc = htmlList.getAsFuture().get();

				// TODO fix in some way that don't need this if clause
				// could be happen in some cases of timeout connection
				if (doc != null)
					urlExtractor.addDocument(doc);

			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
			}

		}
	}
}
