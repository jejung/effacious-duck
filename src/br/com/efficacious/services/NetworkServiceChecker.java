/**
 * 
 */
package br.com.efficacious.services;

import java.util.concurrent.CountDownLatch;

/**
 * This is a Network board check. This 
 * 
 * @author Jean Jung
 */
public class NetworkServiceChecker extends BaseServiceChecker {

	/**
	 * Creates a 
	 * @param latch
	 */
	public NetworkServiceChecker(CountDownLatch latch) {
		super(latch);
	}

	/**
	 * 
	 */
	public ServiceStatus call() throws Exception {
		return null;	
	}
}
