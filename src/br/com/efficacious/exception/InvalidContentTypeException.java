/**
 * 
 */
package br.com.efficacious.exception;

import java.net.URLConnection;

import br.com.efficacious.http.ContentType;

/**
 * Thrown when a {@link URLConnection} have an invalid content-type or it's not
 * present.
 * 
 * @author Jean Jung
 */
public class InvalidContentTypeException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4444702711801269247L;
	
	/**
	 * Default constructor 
	 */
	public InvalidContentTypeException() {
	}
	
	/**
	 * Creates a {@link InvalidContentTypeException} with a default message.
	 * @param contentType
	 */
	public InvalidContentTypeException(ContentType contentType) {
		super(String.format("The content-type %s is invalid for connection at %s", contentType, contentType.getConnection()));
	}
}
