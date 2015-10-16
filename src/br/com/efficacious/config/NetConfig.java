/**
 * 
 */
package br.com.efficacious.config;

import java.net.Proxy;
import java.util.concurrent.CountDownLatch;

/**
 * @author jean
 *
 */
public class NetConfig {
	
	private static NetConfig instance;
	
	private Proxy proxy;
	
	private CountDownLatch latch = new CountDownLatch(1);
	
	private NetConfig() {
		
	}
	
	public CountDownLatch getLatch() {
		return latch;
	}
	
	/**
	 * Singletton provider
	 * @return
	 */
	public static synchronized NetConfig instance() {
		if (instance == null)
			instance = new NetConfig();
		return instance;
	}

	/**
	 * @return the proxy
	 */
	public Proxy getProxy() {
		return proxy;
	}

	/**
	 * @param proxy the proxy to set
	 */
	public void setProxy(Proxy proxy) {
		this.proxy = proxy;
	}
}
