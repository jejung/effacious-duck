package main;

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
			try {
				URLConnection con = this.connectionList.getAsFuture().get();
				htmlList.add(con);
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
			}
		}
	}
}
