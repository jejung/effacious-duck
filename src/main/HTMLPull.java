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

			URLConnection conn;

			try {
				
				conn = connectionList.getAsFuture().get();
				htmlList.add(conn.getInputStream(), conn.getContentEncoding(), conn.getURL().getPath());
			
			} catch (IOException | InterruptedException | ExecutionException e) {
				e.printStackTrace();
			}

		}

	}
}
