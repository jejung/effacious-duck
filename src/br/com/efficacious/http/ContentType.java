/**
 * 
 */
package br.com.efficacious.http;

import java.net.URLConnection;
import java.util.Objects;

import br.com.efficacious.config.CrawlerConfig;
import br.com.efficacious.config.MediaStorage;
import br.com.efficacious.crawler.WebCrawler;
import br.com.efficacious.exception.InvalidContentTypeException;

/**
 * This class is a handy class to store informations about {@link URLConnection}s that will
 * be used on {@link WebCrawler}.
 * 
 * @author Jean Jung
 */
public class ContentType {
	
	private URLConnection connection;
	private String contentType;
	
	/**
	 * Default constructor 
	 */
	public ContentType(URLConnection connection) {
		this.connection = connection;
	}
	
	/**
	 * Verify and store the content-type of the connection.
	 * 
	 * @throws InvalidContentTypeException
	 */
	public void loadContentType() throws InvalidContentTypeException {
		if (this.contentType == null)
			this.contentType = this.connection.getContentType();
		
		if (this.contentType == null)
			throw new InvalidContentTypeException(this);
	}
	
	/**
	 * Verify if this content-type is a media.
	 * @return {@code true} if it's a media.
	 * @throws InvalidContentTypeException if this is an invalid content-type 
	 */
	public boolean isMedia(CrawlerConfig config) throws InvalidContentTypeException {
		this.loadContentType();
		return config.getMediaStorage() != MediaStorage.NONE &&
				(config.getMediaStorage() == MediaStorage.ANY || 
				config
					.getAcceptedMedias()
					.parallelStream()
					.anyMatch((contentType) -> contentType.equalsIgnoreCase(this.contentType)));
	}
	
	/**
	 * Verify if this content-type is a document.
	 * @return {@code true} if it's a document.
	 * @throws InvalidContentTypeException if this is a invalid content-type.
	 */
	public boolean isDocument(CrawlerConfig config) throws InvalidContentTypeException {
		this.loadContentType();
		return !Objects.isNull(this.contentType) && 
				this.contentType.toLowerCase().contains(ContentTypeRepository.HTML) &&
				config.getConnectionFilters()
					.parallelStream()
					.allMatch((predicate) -> predicate.accept(connection));
	}

	/**
	 * @return the connection
	 */
	public URLConnection getConnection() {
		return connection;
	}
}
