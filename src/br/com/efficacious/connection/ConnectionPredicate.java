/**
 * 
 */
package br.com.efficacious.connection;

import java.net.URLConnection;

/**
 * A functional interface to possible create 
 * 
 * @author Jean Jung
 */
@FunctionalInterface
public interface ConnectionPredicate {
	
	/**
	 * 
	 * @param connection
	 * @return
	 */
	boolean accept(URLConnection connection);
}
