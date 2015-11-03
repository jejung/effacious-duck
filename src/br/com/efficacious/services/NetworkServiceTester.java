/**
 * 
 */
package br.com.efficacious.services;

import java.io.IOException;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.util.Objects;

/**
 * This is a Network board check. This class just try to open a connection to a host configured to be the  
 * test host.
 * 
 * @author Jean Jung
 */
public class NetworkServiceTester extends BaseServiceTester {

	private static final String TESTER_NAME = NetworkServiceTester.class.getSimpleName();
	
	private URL url;
	private Proxy proxyAddress;
	
	/**
	 * Creates a checker on the given host through the proxy.
	 * 
	 * @param address The address to try to connect.
	 * @param proxyAddress The proxy to go through
	 */
	public NetworkServiceTester(URL address, Proxy proxyAddress) {
		this(address);
		this.proxyAddress = proxyAddress;
	}
	
	/**
	 * Creates a checker on the given host that try to connect to it.
	 * @param url The address to try to connect.
	 */
	public NetworkServiceTester(URL url) {
		super(TESTER_NAME);
		this.url = url;
	}

	/**
	 * Makes the test when the service is called.
	 * @throws IOException 
	 */
	@Override
	public ServiceTestResponse test() throws IOException {
		Objects.requireNonNull(this.url, "The address should not be null");
		URLConnection connection = null;
		try {
			if (proxyAddress != null)
				connection = this.url.openConnection(proxyAddress);
			else
				connection = this.url.openConnection();
			
			connection.connect(); 
		} finally {
			if (connection != null)
				connection.getInputStream().close();
		}
		
		return ServiceTestResponse
				.builder(this)
				.ok()
				.message("Successfully test connection to: " + url.toString());
	}
}
