/**
 * 
 */
package br.com.efficacious.connection;

import java.net.URLConnection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;

import br.com.efficacious.config.CrawlerConfig;
import br.com.efficacious.dom.DocumentList;
import br.com.efficacious.exception.InvalidContentTypeException;
import br.com.efficacious.http.ContentType;
import br.com.efficacious.media.MediaList;

/**
 * This class is a fork way to determine what type of treatement we have to 
 * do with any {@link URLConnection}. 
 * 
 * @author Jean Jung
 */
public class ContentTypeResolver {

	private ExecutorService connectionCloser;
	private CrawlerConfig config;
	
	/**
	 * Default constructor
	 */
	public ContentTypeResolver(CrawlerConfig config) {
		this.config = config;
		this.connectionCloser = Executors.newWorkStealingPool();
	}
	
	/**
	 * Resolve what to do with the {@link URLConnection}.
	 * @param connection
	 */
	public void forwardConnection(URLConnection connection, DocumentList documents, MediaList medias) {
		ContentType contentType = new ContentType(connection);
		try {
			if (contentType.isMedia(this.config)) {
				medias.add(connection);
				return;
			}else if (contentType.isDocument(this.config)) {
				documents.add(connection);
				return;
			} else {
				this.config.getLogger().info(String.format("URL ignored due to content-type %s: %s ", connection.getContentType(), connection.getURL()));
			}
		} catch (InvalidContentTypeException e) {
		}
		
		this.connectionCloser.execute(()-> { 
			try {
				connection.getInputStream().close();
			} catch (Exception e) {
				this.config.getLogger().log(Level.SEVERE, "The connection could not be closed.", e);
			}
		});
	}
}
