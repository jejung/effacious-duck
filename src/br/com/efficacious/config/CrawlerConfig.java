/**
 * 
 */
package br.com.efficacious.config;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URLConnection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import org.jsoup.helper.HttpConnection;

import br.com.efficacious.connection.ConnectionPredicate;
import br.com.efficacious.crawler.WebCrawler;
import br.com.efficacious.http.ContentTypeRepository;
import br.com.efficacious.io.CrawlerLogHandler;
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
	private List<String> acceptedMedias;
	private MediaStorage mediaStorage;
	private List<ConnectionPredicate> connectionFilters;
	
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
		this.testAddress = new InetSocketAddress("www.google.com", 80);
		this.logger = Logger.getGlobal();
		this.acceptedMedias = new LinkedList<>();
		this.connectionFilters = new LinkedList<>();
		this.mediaStorage = MediaStorage.NONE;
	}
	
	/**
	 * Add a content-type string to the accepted medias list that the crawler will store, if the
	 * {@link CrawlerConfig#mediaStorage} field is of type {@link MediaStorage#FILTERED}, any other 
	 * value will ignore these values. You can create the string yourself or use the {@link ContentTypeRepository}
	 * interface to get a list of the most used content-type parameters.
	 * @param contentType The content-type string HTTP header parameter.
	 */
	public void addAcceptedMedia(String contentType) {
		this.acceptedMedias.add(contentType);
	}
	
	/**
	 * Add a {@link ConnectionPredicate} to allow the filter of the accepted connections that the 
	 * {@link WebCrawler} will use. The {@link URLConnection} will be openned when this predicate 
	 * is called and will keep open if the return is {@code true} and immediatly closed and ignored if the
	 * return is {@code false}.
	 * @param predicate The predicate that filter the {@link URLConnection}.
	 */
	public void addConnectionFilter(ConnectionPredicate predicate) {
		this.connectionFilters.add(predicate);
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
	
	/**
	 * @return the acceptedMedias
	 */
	public List<String> getAcceptedMedias() {
		return Collections.unmodifiableList(this.acceptedMedias);
	}

	/**
	 * @return the mediaStorage
	 */
	public MediaStorage getMediaStorage() {
		return mediaStorage;
	}
	/**
	 * @param mediaStorage the mediaStorage to set
	 */
	public void setMediaStorage(MediaStorage mediaStorage) {
		this.mediaStorage = mediaStorage;
	}
	
	/**
	 * @return the connectionFilters
	 */
	public List<ConnectionPredicate> getConnectionFilters() {
		return Collections.unmodifiableList(this.connectionFilters);
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
		 * all the informations on a specific console handler called {@link CrawlerLogHandler},
		 * by now this option cannot be ignored.
		 * 
		 * @param name 
		 * @return
		 */
		public Builder logOn(String name) {
			this.instance.setLogger(Logger.getLogger(name));
			return this;
		}
		
		/**
		 * Ensure that the {@link WebCrawler} will store medias of the given format.
		 * @param contentType The content type to accpet
		 * @return {@code this} instance.
		 * @see CrawlerConfig#addAcceptedMedia(String)
		 * @see CrawlerConfig#setMediaStorage(MediaStorage)
		 */
		public Builder acceptMedia(String contentType) {
			this.instance.setMediaStorage(MediaStorage.FILTERED);
			this.instance.addAcceptedMedia(contentType);
			return this;
		}
		
		/**
		 * Ensure that any format of media will be stored. 
		 * @return {@code this} instance;
		 * @see CrawlerConfig#addAcceptedMedia(String)
		 * @see CrawlerConfig#setMediaStorage(MediaStorage)
		 */
		public Builder acceptAnyMedia() {
			this.instance.setMediaStorage(MediaStorage.ANY);
			return this;
		}
		
		/**
		 * Filter the {@link URLConnection}s that the {@link WebCrawler} can use.
		 * @param predicate The predicate to filter.
		 * @return {@code this} instance. 
		 * @see CrawlerConfig#addConnectionFilter(ConnectionPredicate)
		 */
		public Builder filterConnections(ConnectionPredicate predicate) {
			this.instance.addConnectionFilter(predicate);
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
