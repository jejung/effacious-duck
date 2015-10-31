/**
 * 
 */
package br.com.efficacious.services;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.Objects;

/**
 * This is a Network board check. This class just try to open a connection to a host configured to be the  
 * test host.
 * 
 * @author Jean Jung
 */
public class NetworkServiceTester extends BaseServiceTester {

	private static final String TESTER_NAME = NetworkServiceTester.class.getName();
	
	private InetSocketAddress address;
	private SocketAddress proxyAddress;
	
	/**
	 * Creates a checker on the given host through the proxy.
	 * 
	 * @param address The address to try to connect.
	 * @param socketAddress The proxy to go through
	 */
	public NetworkServiceTester(InetSocketAddress address, SocketAddress socketAddress) {
		this(address);
		this.proxyAddress = socketAddress;
	}
	
	/**
	 * Creates a checker on the given host that try to connect to it.
	 * @param address The address to try to connect.
	 */
	public NetworkServiceTester(InetSocketAddress address) {
		super(TESTER_NAME);
		this.address = address;
	}

	/**
	 * Makes the test when the service is called.
	 * @throws IOException 
	 */
	@Override
	public ServiceTestResponse test() throws IOException {
		Objects.requireNonNull(this.address, "The address must not be null");
		Socket socket = null;
		try {
			if (this.proxyAddress != null) 
				socket = new Socket(new Proxy(Proxy.Type.SOCKS, proxyAddress));
			else
				socket = new Socket();
			socket.connect(address);
		} finally {
			if (socket != null)
				socket.close();
		}
		
		return ServiceTestResponse
				.builder(this)
				.ok()
				.message("Successfully test connection to: " + address.toString());
	}
}
