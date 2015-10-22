/**
 * 
 */
package br.com.efficacious.config;

import java.net.InetSocketAddress;
import java.net.Proxy;

import org.jsoup.helper.HttpConnection;

import br.com.efficacious.io.LuceneDirectory;

/**
 * The basic configurations of a Efficacious Duck Crawler instance.
 * 
 * @author Jean Jung
 *
 */
public class CrawlerConfig {
	
	private static final String DEFAULT_DIRECTORY = "/index/";
	
	private Proxy proxy;
	private String luceneDirectory;
	
	/**
	 * Create a proxy based config.
	 * 
	 * @param proxy
	 */
	public CrawlerConfig(Proxy proxy) {
		super();
		this.proxy = proxy;
	}
	/**
	 * Create a directory based config.
	 * 
	 * @param luceneDirectory
	 */
	public CrawlerConfig(String luceneDirectory) {
		super();
		this.luceneDirectory = luceneDirectory;
	}
	/**
	 * Create a config with given directory and proxy.
	 * 
	 * @param proxy
	 * @param luceneDirectory
	 */
	public CrawlerConfig(Proxy proxy, String luceneDirectory) {
		super();
		this.proxy = proxy;
		this.luceneDirectory = luceneDirectory;
	}
	/**
	 * Default constructor. 
	 */
	public CrawlerConfig() {
		this.luceneDirectory = DEFAULT_DIRECTORY;
	}
	/**
	 * @return the proxy
	 */
	public Proxy getProxy() {
		return this.proxy;
	}
	/**
	 * @param proxy the proxy to set
	 */
	public void setProxy(Proxy proxy) {
		this.proxy = proxy;
	}
	/**
	 * @return the luceneDirectory
	 */
	public String getLuceneDirectory() {
		return this.luceneDirectory;
	}
	/**
	 * @param luceneDirectory the luceneDirectory to set
	 */
	public void setLuceneDirectory(String luceneDirectory) {
		this.luceneDirectory = luceneDirectory;
	}
	
	/**
	 * Initializes a builder that can configure and create an instance
	 * of this configuration.
	 * 
	 * @return the {@code new Builder();}
	 */
	public static Builder builder() {
		return new Builder();
	}
	
	public static class Builder {
		
		private CrawlerConfig instance;
		
		/**
		 * Create a {@link CrawlerConfig} builder to help you configure the valid
		 * options on the {@link CrawlerConfig} instance. 
		 */
		private Builder() {
			this.instance = new CrawlerConfig();
		}
		
		/**
		 * Adds the proxy option to enable the proxy based {@link HttpConnection}s
		 *  
		 * @param proxy The proxy to use on each connection created.
		 * @return {@code this} instance
		 */
		public Builder useProxy(Proxy proxy) {
			this.instance.setProxy(proxy);
			return this;
		}
		
		/**
		 * Define the {@link LuceneDirectory} to use on the index storage. 
		 * 
		 * @param directory The directory to store the indexes
		 * @return {@code this} instance
		 */
		public Builder storeIndexOn(String directory) {
			this.instance.setLuceneDirectory(directory);
			return this;
		}
		
		/**
		 * Parse and create the parameters for this {@link CrawlerConfig} instance.
		 * The valid parameters are described below:
		 * 
		 * <table>
		 *   <tr>
		 *   	<td> Parameter </td>
		 *   	<td> Description </td>
		 *   </tr>
		 *   <tr>
		 *   	<td> -p proxy.domain:port </td>
		 *   	<td> The proxy address to use on the creation of each {@link HttpConnection}. </td>
		 *   </tr>
		 *   <tr>
		 *   	<td> -d /path/to/index </td>
		 *   	<td> The {@link LuceneDirectory} to store the HTML data. </td>
		 *   </tr>
		 * </table>
		 * 
		 * @param args The main program args.
		 * @return {@code this} instance
		 */
		public Builder initFromArgs(String[] args) {
			
			for (int i = 0; i < args.length; i++) {
				if ("-p".equals(args[i])) {
					i++;
					String[] split = args[i].split(":");
					String host = split[0];
					int port = 0;
					if (split.length > 1)
						port = Integer.parseInt(split[1]);
					this.useProxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress(host, port)));
				} else if ("-d".equals(args[i])) {
					this.storeIndexOn(args[++i]);
				}
			}
			return this;
		}
		
		/**
		 * Build and return the parametrized instance of the {@link CrawlerConfig}.
		 * 
		 * @return the parametrized instance of the {@link CrawlerConfig}.
		 */
		public CrawlerConfig build() {
			return this.instance;
		}
	}
}
