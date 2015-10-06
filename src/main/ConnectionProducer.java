/**
 * 
 */
package main;

import java.net.URL;
import java.net.URLConnection;
import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.Semaphore;

/**
 * @author Jean Jung
 * @author Johnny W. G. G.
 *
 */
public class ConnectionProducer implements Runnable {

    private static final int MAX_CONNECTIONS = 5;

    private ExecutorService executor;

    private URLList urlList;
    private ConnectionList connectionList;

    @Override
    public void run() {

	while (true) {

	    synchronized (urlList) {
		
		try {
		
		    while (urlList.isEmpty())
		    	urlList.wait();
		    
		  urlList.wait(35);
		
		} catch (InterruptedException e) {
		    break;
		}
		
		
		
		//System.out.println("ConnecionProducer");
		connectionList.add(this.executor.submit(new ConnectionCreator(urlList.get())));
		//connectionList.notifyAll();
	    }
	}
    }

    public ConnectionProducer(URLList urlList, ConnectionList connectionList) {

	this.urlList = urlList;
	this.connectionList = connectionList;

	this.executor = Executors.newFixedThreadPool(MAX_CONNECTIONS);

    }

    private class ConnectionCreator implements Callable<URLConnection> {

	private URL uri;

	public ConnectionCreator(URL uri) {
	    this.uri = uri;
	}

	@Override
	public URLConnection call() throws Exception {
	    return uri.openConnection();
	}
    }

}
