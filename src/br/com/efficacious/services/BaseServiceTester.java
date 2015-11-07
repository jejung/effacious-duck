/**
 * 
 */
package br.com.efficacious.services;

/**
 * Base class for initial check of resources.
 * 
 * @author Jean Jung
 */
public abstract class BaseServiceTester {
	
	private String name;
	
	/**
	 * Creates a named service tester.
	 */
	public BaseServiceTester(String name) {
		this.name = name;
	}

	/**
	 * The default behavior of a {@link BaseServiceTester} must be check the service
	 * and return the {@link ServiceStatus}. 
	 * @return The status of the test.
	 */
	public abstract ServiceTestResponse test() throws Exception;

	/**
	 * @return The name
	 */
	public String getName() {		
		return this.name;
	}
	
	/**
	 * {@inheritDoc Object}
	 */
	@Override
	public String toString() {
		return this.getName();
	}
}
