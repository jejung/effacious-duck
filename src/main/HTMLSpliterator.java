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

	private void sendToURLExtractor(Document doc) {

	}

	@Override
	public void run() {

		while (true) {

			Document doc;
		
			try {
				
				doc = htmlList.getAsFuture().get();
			
				urlExtractor.addDocument(doc);
		
			} catch (InterruptedException | ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			// System.out.println("spliterator = " + doc.baseUri());

		}
	}

}
