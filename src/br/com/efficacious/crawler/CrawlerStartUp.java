/**
 * 
 */
package br.com.efficacious.crawler;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;

import br.com.efficacious.config.CrawlerConfig;
import br.com.efficacious.services.BaseServiceTester;
import br.com.efficacious.services.ServiceStatus;
import br.com.efficacious.services.ServiceTestExecutor;
import br.com.efficacious.services.ServiceTestResponse;

/**
 * Define the basic behaviors of a Crawler StartUP procedure. The 
 * main operation is to check if all necessary services are up and 
 * running. It can start some services if they are down, but some 
 * other services will cause an error because they cannot be started 
 * here.
 * 
 * @author Jean Jung
 */
public class CrawlerStartUp {
	
	private CrawlerConfig config;
	private Set<BaseServiceTester> serviceTesters;
	private boolean failed;
	
	/**
	 * Constructs a new StartUp.
	 */
	public CrawlerStartUp(CrawlerConfig config) {
		this.config = config;
		this.serviceTesters = new HashSet<>();
		this.failed = false;
	}
	
	/**
	 * Add a service checker to run on the start validation routine. 
	 * It's not possible to add the same service checker more than 
	 * one time, if the checker already exists on this startup then
	 * nothing will be done.
	 * @param serviceChecker The checker to add.
	 * @return The status of the set of checkers, {@code true} if it 
	 * was modified, {@code false} otherwise.
	 */
	public boolean addServiceTest(BaseServiceTester serviceChecker) {
		return this.serviceTesters.add(serviceChecker);
	}
	
	/**
	 * Start all the service validations. 
	 * @throws InterruptedException 
	 */
	public void testAll() throws InterruptedException {
		ExecutorService executor = Executors.newFixedThreadPool(this.serviceTesters.size());
		CountDownLatch latch = new CountDownLatch(this.serviceTesters.size());
		ServiceTestExecutor[] testExecutors = new ServiceTestExecutor[this.serviceTesters.size()];
		int i = 0;
		for (BaseServiceTester baseServiceTester : serviceTesters) {
			testExecutors[i] = new ServiceTestExecutor(baseServiceTester, latch);
			executor.execute(testExecutors[i]);
			i++;
		}
		latch.await();
		executor.shutdown();
		for (ServiceTestExecutor serviceTestExecutor : testExecutors) {
			ServiceTestResponse result = serviceTestExecutor.getResult();
			if (result.getStatus() != ServiceStatus.OK) {
				logFailed(result);
				this.failed = true;
			} else
				logOK(result);
		}
	}
	
	private void logFailed(ServiceTestResponse result) {
		this.config.getLogger()
			.log(Level.SEVERE, String.format("SERVICE TEST %s: %s", result.getTester(), result.getMessage()), result.getCauseFailed());
	}
	
	private void logOK(ServiceTestResponse result) {
		this.config.getLogger()
			.log(Level.INFO, String.format("SERVICE TEST %s: %s", result.getTester(), result.getMessage()));
	}

	/**
	 * @return if any service failed
	 */
	public boolean hasFailed() {
		return failed;
	}
}
