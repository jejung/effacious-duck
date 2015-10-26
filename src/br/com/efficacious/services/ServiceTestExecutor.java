/**
 * 
 */
package br.com.efficacious.services;

import java.util.Objects;
import java.util.concurrent.CountDownLatch;

/**
 * It's a handy guarantee that the latch will be counted down. 
 * 
 * @author Jean Jung
 */
public final class ServiceTestExecutor implements Runnable {
	
	private BaseServiceTester serviceTester;
	private CountDownLatch latch;
	private ServiceTestResponse result;

	/**
	 * Creates a executor that starts the service test and get it's results.
	 * 
	 * @param serviceTester
	 * @param latch
	 */
	public ServiceTestExecutor(BaseServiceTester serviceTester, CountDownLatch latch) {
		super();
		this.serviceTester = serviceTester;
		this.latch = latch;
	}

	/**
	 * @see Runnable#run()
	 */
	@Override
	public void run() {
		Objects.requireNonNull(this.serviceTester, "The service checker must be set on the ServiceCheckExecutor");
		Objects.requireNonNull(this.latch, "The latch must be set on the ServiceCheckExecutor");
		
		try {
			this.result = this.serviceTester.test();
		} catch (Exception e) {
			this.result = ServiceTestResponse.builder(this.serviceTester).failed().because(e).message("Failed on test service: " + this.serviceTester.getName());
		}
	}

	/**
	 * @return the result
	 */
	public ServiceTestResponse getResult() {
		return result;
	}
}