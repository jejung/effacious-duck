package main;

import java.io.IOException;
import java.net.URLConnection;
import java.util.concurrent.ExecutionException;

/**
 * 
 * @author johnny w. g. g.
 *
 */
public class HTMLPull implements Runnable {

    private ConnectionList connectionList;
    private HTMLList htmlList;

    public HTMLPull(ConnectionList connectionList, HTMLList htmlList) {

	this.connectionList = connectionList;
	this.htmlList = htmlList;
    }

    @Override
    public void run() {

	while (true) {

	    synchronized (connectionList) {

		try {
		    
		    while (connectionList.isEmpty())
			connectionList.wait();

		    URLConnection conn;

		    connectionList.wait(35);
		    
		  //  System.out.println("html pull");
		    
		    conn = connectionList.getAsFuture().get();
		    htmlList.add(conn.getInputStream(), conn.getContentEncoding(), conn.getURL().getPath());
		    
		} catch (InterruptedException | ExecutionException | IOException e) {
		    System.out.println(e.getMessage());
		    break;
		}

	    }
	}

    }
}
