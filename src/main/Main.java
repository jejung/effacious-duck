package main;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/**
 * @author Jean Jung
 */
public class Main {

	/**
	 * 
	 */
	public Main() {
	}

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args)

	throws IOException {

		
		//ConnectionList.getInstance().get
		
		
		// ConnectionProducer producer = new ConnectionProducer();
		// URLExtractor extractor = new URLExtractor(producer);
		// try {
		// producer.add(new URL("http://www.globo.com/"));
		// } catch (InterruptedException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// URLConnection connection = null;
		// while ((connection = producer.poll()) != null)
		// {
		// System.out.format("Lendo connexão: %s\n", connection.getURL());
		// Document doc = Jsoup.parse(connection.getInputStream(),
		// connection.getContentEncoding(),connection.getURL().getPath());
		// extractor.collect(doc);
		// }

		// View view = new View();
		// view.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		// view.pack();
		// view.setLocationRelativeTo(null);
		// view.setVisible(true);
	}
}
