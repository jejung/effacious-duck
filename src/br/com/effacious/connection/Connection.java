package br.com.effacious.connection;

import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.Callable;

public class Connection implements Callable<URLConnection> {
	private URL url;
	
	public Connection(URL url) {
		this.url = url;
	}
	
	@Override
	public URLConnection call() throws Exception {
		return url.openConnection();
	}
}
