/**
 * 
 */
package br.com.efficacious.services;

/**
 * The class referent to a response of a service check. This is being used to grant us the chance to 
 * check more details of a test execution, like the exception who caused the fail or whatever.
 * 
 * @author Jean Jung
 */
public class ServiceTestResponse {

	private BaseServiceTester tester;
	private ServiceStatus status;
	private Exception causeFailed;
	private String message;
	
	/**
	 * Don't allow to call this method from outside the response builder. 
	 * @param tester 
	 */
	private ServiceTestResponse(BaseServiceTester tester) {
		this.tester = tester;
	}
	
	/**
	 * Initializes a builder that offers any option to build a consistent response.
	 *  
	 * @return
	 */
	public static ResponseBuilder builder(BaseServiceTester tester) {
		return new ResponseBuilder(tester);
	} 
	
	/**
	 * @return the status
	 */
	public ServiceStatus getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(ServiceStatus status) {
		this.status = status;
	}

	/**
	 * @return the causeFailed
	 */
	public Exception getCauseFailed() {
		return causeFailed;
	}

	/**
	 * @param causeFailed the causeFailed to set
	 */
	public void setCauseFailed(Exception causeFailed) {
		this.causeFailed = causeFailed;
	}

	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * @param message the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * @return the tester
	 */
	public BaseServiceTester getTester() {
		return tester;
	}

	/**
	 * @param tester the tester to set
	 */
	public void setTester(BaseServiceTester tester) {
		this.tester = tester;
	}

	/**
	 * The response builder class.
	 * 
	 * @author Jean Jung
	 */
	public static class ResponseBuilder {
		
		private ServiceTestResponse instance;
		
		/**
		 * Private constructor, the right way is to {@link ServiceTestResponse} provide the builder access to
		 * avoid sub-class access like {@code A.B b = A.B();}  
		 * @param tester 
		 */
		private ResponseBuilder(BaseServiceTester tester) {
			this.instance = new ServiceTestResponse(tester);
		}
		
		/**
		 * Build a {@link ServiceStatus#OK} instance. 
		 * @return {@code this} instance with the status updated.
		 */
		public MessageBuilder ok() {
			this.instance.setStatus(ServiceStatus.OK);
			return new MessageBuilder(instance);
		}
		
		/**
		 * Build a {@link ServiceStatus#FAILED} instance. 
		 * @return {@code this} instance with the status updated.
		 */
		public ExceptionBuilder failed() {
			this.instance.setStatus(ServiceStatus.FAILED);
			return new ExceptionBuilder(instance);
		}
	}
	
	/**
	 * The message phase builder
	 * 
	 * @author Jean Jung
	 */
	public static class MessageBuilder {
		
		private ServiceTestResponse instance;
		
		/**
		 * Private constructor, the right way is to {@link ResponseBuilder} or {@link ExceptionBuilder} provide the 
		 * builder access to avoid sub-class access like {@code A.B b = A.B();}  
		 */
		private MessageBuilder(ServiceTestResponse instance) {
			this.instance = instance;
		}
		
		/**
		 * Build a no message response.
		 * @return The {@link ServiceTestResponse} builded.
		 */
		public ServiceTestResponse noMessage() {
			this.instance.setMessage("");
			return instance;
		}
		
		/**
		 * Build a messaged response.
		 * @param message The response to set.
		 * @return The {@link ServiceTestResponse} builded. 
		 */
		public ServiceTestResponse message(String message) {
			this.instance.setMessage(message);
			return instance;
		}
	}
	
	/**
	 * The failed responses way builder.
	 * 
	 * @author Jean Jung
	 */
	public static class ExceptionBuilder {
		
		private ServiceTestResponse instance;

		/**
		 * Private constructor, the right way is to {@link ServiceTestResponse} provide the builder access to
		 * avoid sub-class access like {@code A.B b = A.B();}  
		 */
		private ExceptionBuilder(ServiceTestResponse instance) {
			this.instance = instance;
		}
		
		/**
		 * Build a no cause failed response.
		 * @return The next phase builder.
		 */
		public MessageBuilder noEspecificCause() {
			return new MessageBuilder(instance);
		}
		
		/**
		 * Build a failed response with a cause pointed.
		 * @param causeFailed The cause of the fail
		 * @return The next phase builder.
		 */
		public MessageBuilder because(Exception causeFailed) {
			this.instance.setCauseFailed(causeFailed);
			return new MessageBuilder(instance);
		}
	}
}
