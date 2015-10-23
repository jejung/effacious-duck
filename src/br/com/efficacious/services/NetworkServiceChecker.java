/**
 * 
 */
package br.com.efficacious.services;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Socket;
import java.util.concurrent.CountDownLatch;

/**
 * This is a Network board check. This class just try to open a connection to a host configured to be the  
 * test host.
 * 
 * @author Jean Jung
 */
public class NetworkServiceChecker extends BaseServiceChecker {

	private InetSocketAddress address;
	private Proxy proxy;
	
	/**
	 * Creates a checker on the given host through the proxy.
	 * 
	 * @param latch The latch to coutDown when the test is done.
	 * @param address The address to try to connect.
	 * @param proxy The proxy to go through
	 */
	public NetworkServiceChecker(CountDownLatch latch, InetSocketAddress address, Proxy proxy) {
		this(latch, address);
		this.proxy = proxy;
	}
	
	/**
	 * Creates a checker on the given host that try to connect to it.
	 * @param latch The latch to coutDown when the test is done.
	 * @param address The address to try to connect.
	 */
	public NetworkServiceChecker(CountDownLatch latch, InetSocketAddress address) {
		super(latch);
		this.address = address;
	}

	/**
	 * Makes the test when the service is called.
	 */
	@Override
	public ServiceTestResponse call() throws Exception {
		ServiceTestResponse response = null;
		Socket socket = null;
		try {
			if (this.proxy != null) 
				socket = new Socket(this.proxy);
			else
				socket = new Socket();
			
			socket.connect(address);
			response =  ServiceTestResponse.builder().ok().message("Successfully test connection to: " + address.toString());
		} catch (IOException e) {
			response = ServiceTestResponse.builder().failed().because(e).message("Failed test connection to:" + address.toString());	
		} finally {
			if (socket != null)
				socket.close();
		}
		return response;
	}
}
