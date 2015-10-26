package br.com.efficacious.connection;

import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.Callable;

import br.com.efficacious.config.CrawlerConfig;

public class ConnectionCreator implements Callable<URLConnection> {
	
	private URL url;
	private CrawlerConfig config;
	
	private ConnectionCreator(CrawlerConfig config, URL url) {
		this.config = config;
		this.url = url;
	}
	
	@Override
	public URLConnection call() throws Exception {
		
		if (this.config.getProxy() == null)
			return url.openConnection();
		else
			return url.openConnection(this.config.getProxy());
	}

	/**
	 * Builder for the connection creator. 
	 * @param url
	 * @return
	 */
	public static ConnectionCreator create(CrawlerConfig config, URL url) {
		return new ConnectionCreator(config, url);
	}
}
