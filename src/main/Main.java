package main;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/**
 * @author johnny w. g. g.
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
    public static void main(String[] args) throws IOException {
	
	ConnectionList connList = ConnectionList.getInstance();
	URLList urlList = URLList.getInstance();
	HTMLList htmlList = HTMLList.getInstance();
	
	urlList.add(new URL("http://g1.globo.com"));
	urlList.add(new URL("http://www.tecmundo.com.br"));
	urlList.add(new URL("http://docs.oracle.com"));
	
	
	ConnectionProducer connProducer = new ConnectionProducer(urlList, connList);
	HTMLPull htmlPull = new HTMLPull(connList, htmlList);
	URLExtractor urlExtractor = new URLExtractor(urlList);
	HTMLSpliterator htmlSplit = new HTMLSpliterator(htmlList, urlExtractor);
	
	
	Thread t1 = new Thread(connProducer);
	Thread t2 = new Thread(htmlPull);
	Thread t3 = new Thread(urlExtractor);
	Thread t4 = new Thread(htmlSplit);
	
	
	t1.start();
	t2.start();
	t3.start();
	t4.start();
	
    }
}
