/**
 * 
 */
package br.com.efficacious.config;

import java.net.Proxy;

/**
 * @author jean
 *
 */
public class NetConfig {
	
	private static NetConfig instance;
	
	private Proxy proxy;
	
	private NetConfig() {
		
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
