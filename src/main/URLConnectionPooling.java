package main;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;

/**
 * Class that must provide connection for given URLs
 * 
 * @author johnny w. g. g.
 *
 */
public class URLConnectionPooling {

    private static final int MAX_CONNECTIONS = 50;

    private ArrayList<URLConnection> connections = new ArrayList<>();
    
    private Semaphore sem = new Semaphore(MAX_CONNECTIONS);

    /**
     * 
     * @param url url that a connection will be opened to
     * @return a connection if possible to open, null otherwise 
     * @throws InterruptedException if this thread was interrupted
     */
    public URLConnection addUrl(final URL url) throws InterruptedException {

	sem.acquire();

	URLConnection connection = null;

	try {

	    connection = url.openConnection();

	} catch (IOException e) {
	    System.out.println(e.getMessage());
	    return null;
	} finally {
	    sem.release();
	}
	
	connections.add(connection);
	return connection;
    }

}
