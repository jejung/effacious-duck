package main;

import java.net.URLConnection;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jsoup.helper.HttpConnection;

/**
 * Pull connections from a {@link ConnectionList} and serve the {@link HTMLList} with a open {@link HttpConnection} 
 * 
 * @author johnny w. g. g.
 * @author Jean Jung
 */
public class HTMLPull implements Runnable {

	private ConnectionList connectionList;
	private HTMLList htmlList;
	private boolean alive;

	public HTMLPull(ConnectionList connectionList, HTMLList htmlList) {
		this.connectionList = connectionList;
		this.htmlList = htmlList;
		this.alive = true;
	}

	@Override
	public void run() {
		this.produceForEver();
	}
	/**
	 * Produces until {@code this.isAlive() == false} 
	 */
	private void produceForEver() {
		while (this.isAlive()) {
			try {
				URLConnection con = this.connectionList.getAsFuture().get();
				htmlList.add(con);
			} catch (InterruptedException | ExecutionException e) {
				Logger.getGlobal().log(Level.SEVERE, "Error producing connections", e);
			}
		}
	}

	/**
	 * @return the alive
	 */
	public boolean isAlive() {
		return alive;
	}

	/**
	 * @param alive the alive to set
	 */
	public void setAlive(boolean alive) {
		this.alive = alive;
	}
}
