package br.com.effacious.main;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import br.com.effacious.connection.ConnectionList;
import br.com.effacious.connection.ConnectionProducer;
import br.com.effacious.dom.HTMLList;
import br.com.effacious.dom.HTMLPull;
import br.com.effacious.dom.HTMLSpliterator;
import br.com.effacious.io.FileBTree;
import br.com.effacious.url.URLConsumer;
import br.com.effacious.url.URLQueue;

/**
 * @author johnny w. g. g.
 * @author Jean Jung
 */
public class Main {

	static volatile ConnectionList connectionList = ConnectionList.getInstance();
	static volatile URLQueue urlList = URLQueue.getInstance();
	static volatile HTMLList htmlList = HTMLList.getInstance();

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		
		FileBTree btree = new FileBTree(2, new File("Data.bin"));
		btree.insert("hola");
		btree.insert("abola");
		btree.insert("maestro");
		btree.insert("morango");
		btree.insert("abacaxi");
		btree.insert("cachecol");
		btree.insert("abelha");
		btree.insert("mamangava");
		btree.insert("bilbo");
		btree.insert("manganes");
		btree.insert("besouto");
		btree.insert("banco");
		btree.insert("navio");
		btree.insert("lagoa");
		btree.insert("lago");
		btree.insert("camarão");
		btree.insert("bagre");
		btree.insert("notificacao");
		btree.insert("limao");
		btree.insert("medalha");
		btree.insert("pictografico");
		btree.print();

		/* urlList.add(new URL("http://www.furb.br/web/10/portugues"));
		urlList.add(new URL("http://g1.globo.com"));
		urlList.add(new URL("http://www.tecmundo.com.br"));
		urlList.add(new URL("http://docs.oracle.com"));
		
		ConnectionProducer connectionProducer = new ConnectionProducer(urlList, connectionList);
		HTMLPull htmlPull = new HTMLPull(connectionList, htmlList);
		URLConsumer urlExtractor = new URLConsumer(urlList);
		HTMLSpliterator htmlSpliterator = new HTMLSpliterator(htmlList, urlExtractor);

		Thread connectionProducerThread = new Thread(connectionProducer);
		Thread htmlPullThread = new Thread(htmlPull);
		Thread urlExtractorThread = new Thread(urlExtractor);
		Thread htmlSpliteratorThread = new Thread(htmlSpliterator);
		
		urlExtractorThread.setPriority(Thread.MAX_PRIORITY);
		
		connectionProducerThread.setPriority(Thread.MIN_PRIORITY);
		htmlPullThread.setPriority(Thread.MIN_PRIORITY);
		htmlSpliteratorThread.setPriority(Thread.MIN_PRIORITY);
		
		connectionProducerThread.start();
		htmlPullThread.start();
		urlExtractorThread.start();
		htmlSpliteratorThread.start();*/
	}
}
