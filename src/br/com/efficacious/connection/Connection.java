package br.com.efficacious.connection;

import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.Callable;

import br.com.efficacious.config.NetConfig;

public class Connection implements Callable<URLConnection> {
	private URL url;
	
	public Connection(URL url) {
		this.url = url;
	}
	
	@Override
	public URLConnection call() throws Exception {
		
		if (NetConfig.instance().getProxy() == null)
			return url.openConnection();
		else
			return url.openConnection(NetConfig.instance().getProxy());
	}
}
