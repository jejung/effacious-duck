/**
 * 
 */
package br.com.efficacious.services;

import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;

/**
 * Base class for initial check of resources.
 * 
 * @author Jean Jung
 */
public abstract class BaseServiceChecker implements Callable<ServiceTestResponse> {
	
	protected CountDownLatch latch;
	
	/**
	 * Forces all services to use a {@link CountDownLatch}.
	 */
	public BaseServiceChecker(CountDownLatch latch) {
		this.latch = latch;
	}
	
	/**
	 * The default behavior of a {@link BaseServiceChecker} must be check the service, 
	 * count down the {@link CountDownLatch} and return the {@link ServiceStatus}. 
	 * @return OK
	 */
	@Override
	public ServiceTestResponse call() throws Exception {
		this.latch.countDown();
		return ServiceTestResponse.builder().ok().noMessage(); 
	}
}
