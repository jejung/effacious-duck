/**
 * 
 */
package br.com.efficacious.config;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.logging.Logger;

import org.jsoup.helper.HttpConnection;

import br.com.efficacious.io.EfficaciousLogHandler;
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
	private InetSocketAddress testAddress;
	private Logger logger;
	
	/**
	 * Create a proxy based config.
	 * 
	 * @param proxy
	 */
	public CrawlerConfig(Proxy proxy) {
		this();
		this.proxy = proxy;
	}
	/**
	 * Create a directory based config.
	 * 
	 * @param luceneDirectory
	 */
	public CrawlerConfig(String luceneDirectory) {
		this();
		this.luceneDirectory = luceneDirectory;
	}
	/**
	 * Create a config with given directory and proxy.
	 * 
	 * @param proxy
	 * @param luceneDirectory
	 */
	public CrawlerConfig(Proxy proxy, String luceneDirectory) {
		this();
		this.proxy = proxy;
		this.luceneDirectory = luceneDirectory;
	}
	/**
	 * Default constructor. 
	 */
	public CrawlerConfig() {
		this.luceneDirectory = DEFAULT_DIRECTORY;
		this.proxy = null;
		this.testAddress = new InetSocketAddress("http://www.google.com", 80);
		this.logger = Logger.getGlobal();
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
	 * @return the testAddress
	 */
	public InetSocketAddress getTestAddress() {
		return testAddress;
	}
	/**
	 * @param testAddress the testAddress to set
	 */
	public void setTestAddress(InetSocketAddress testAddress) {
		this.testAddress = testAddress;
	}
	
	/**
	 * @param logger the logger to set
	 */
	public void setLogger(Logger logger) {
		this.logger = logger;
	}
	
	/**
	 * @return the logger
	 */
	public Logger getLogger() {
		return logger;
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
		 * Define the initial connection test must be used.
		 * In the initialization of the Crawler it check for all needed services
		 * and the network is one of them. With this configuration you can choose if
		 * the test will be local or remote, by giving this kind of address. To improve the 
		 * test performance the address must be a very accessed service, and should respond 
		 * fast too. By default we are using <a href="http://www.google.com:80">http://www.google.com:80</a>
		 * because it's the most obvious option.
		 *  
		 * @param at The address where you want to connect to verify the services.
		 * @return {@code this} instance
		 */
		public Builder makeConnectionTest(InetSocketAddress at) {
			this.instance.setTestAddress(at);
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
		 *   <tr>
		 *   	<td> -l logger.name </td>
		 *   	<td> The name of the {@link Logger} that will be used to log all the messages. </td>
		 *   </tr>
		 * </table>
		 * 
		 * @param args The main program args.
		 * @return {@code this} instance
		 */
		public Builder initFromArgs(String[] args) {
			Builder result = this;
			for (int i = 0; i < args.length; i++) {
				if ("-p".equals(args[i])) {
					i++;
					String[] split = args[i].split(":");
					String host = split[0];
					int port = 0;
					if (split.length > 1)
						port = Integer.parseInt(split[1]);
					result = result.useProxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress(host, port)));
				} else if ("-d".equals(args[i])) {
					result = result.storeIndexOn(args[++i]);
				} else if ("-l".equals(args[i])) {
					result = result.logOn(args[++i]);
				}
			}
			return result;
		}
		
		/**
		 * Log the information on the given logger name. By default Efficacious add
		 * all the informations on a specific console handler called {@link EfficaciousLogHandler},
		 * by now this option cannot be ignored.
		 * 
		 * @param name 
		 * @return
		 */
		private Builder logOn(String name) {
			this.instance.setLogger(Logger.getLogger(name));
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
