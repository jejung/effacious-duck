package main;

import java.util.concurrent.Future;

/**
 * 
 * @author johnny w. g. g.
 *
 */
public class HTMLPull implements Runnable {

	private ConnectionList connectionList;

	public HTMLPull(ConnectionList connectionList) {

		this.connectionList = connectionList;

	}

	@Override
	public void run() {

		while (true) {

			synchronized (connectionList) {
				try {
					while (connectionList.isEmpty())
						connectionList.wait();
				} catch (InterruptedException e) {
					break;
				}

				
				
				
			}
		}

	}
}
