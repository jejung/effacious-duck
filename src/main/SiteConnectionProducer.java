/**
 * 
 */
package main;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;
import java.util.Stack;

/**
 * @author Jean Jung
 *
 */
public class SiteConnectionProducer extends Thread 
{
	private Map<URL, URLConnection> opened;
	private Stack<URLConnection> connections;
	private Stack<URL> urls;
	
	/**
	 * 
	 */
	public SiteConnectionProducer() 
	{	
		this.connections = new Stack<>();
		this.urls = new Stack<>();
	}
	
	/**
	 * Add a URL to process
	 * @param url
	 */
	public void pushURL(URL url, boolean start)
	{
		this.urls.push(url);
		
		if(start && !this.isAlive())
		{
			this.start();
		}
	}
	
	/**
	 * Busy wait till a a connection is opened or no URL is to be processed.  
	 */
	private void waitForConnections()
	{
		while (this.connections.isEmpty() && !this.urls.isEmpty());
	}
	
	/**
	 * Returns the last opened connection pushed on the stack.
	 * @return
	 */
	public URLConnection popConnection()
	{
		this.waitForConnections();
		
		if (!this.connections.isEmpty())
			return this.connections.pop();
		return null;
	}
	
	/**
	 * Returns a InputStream from a URL.
	 * @return
	 */
	public InputStream popInputStream()
	{
		this.waitForConnections();
		
		if (!this.connections.isEmpty())
		{
			try{
				return this.connections.pop().getInputStream();
			}catch(Exception e){e.printStackTrace();}
		}
		
		return null;
	}
	
	/**
	 * Start to open the {@link URLConnection}s 
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run() 
	{
		while(!this.urls.isEmpty())
		{
			try {
				URL url = this.urls.pop(); 
				if (!this.opened.containsKey(url))
				{
					URLConnection connection = this.urls.pop().openConnection();
					connection.connect();
					this.opened.put(url, connection);
					this.connections.push(connection);
				}
			} catch (IOException e) { e.printStackTrace(); }
		}
	}
}
