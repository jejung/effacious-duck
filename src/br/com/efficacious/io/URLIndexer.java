/**
 * 
 */
package br.com.efficacious.io;

import java.io.StringReader;
import java.nio.file.Paths;
import java.util.concurrent.Callable;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import br.com.efficacious.config.CrawlerConfig;
import br.com.efficacious.crawler.WebCrawler;
import br.com.efficacious.dom.URLDocument;

/**
 * The {@link URLIndexer} is a {@link WebCrawler} component that given a {@link URLDocument} it 
 * stores it contents on a {@link LuceneDirectory} previously configured on the {@link CrawlerConfig} interface.
 * This operation enable us to execute text searchs on the indexed pages.
 * 
 * @author Jean Jung
 */
public class URLIndexer implements Callable<Void> {

	private static final Analyzer DEFAULT_ANALYZER = new StandardAnalyzer();
	private static final IndexWriterConfig DEFAULT_CONFIG = new IndexWriterConfig(DEFAULT_ANALYZER);

	static {
		DEFAULT_CONFIG.setOpenMode(OpenMode.CREATE_OR_APPEND);
	}
	
	private CrawlerConfig config; 
	private URLDocument document;
	private Runnable runWhenDone;

	/**
	 * Default constructor.
	 */
	private URLIndexer() { }

	/**
	 * @see java.util.concurrent.Callable#call()
	 */
	@Override
	public Void call() throws Exception {
		if (this.document != null) {
			Directory indexDirectory = null; 
			try {
				indexDirectory = FSDirectory.open(Paths.get(this.config.getLuceneDirectory()));
				IndexWriter writer = new IndexWriter(indexDirectory, DEFAULT_CONFIG);
				writer.updateDocument(new Term("url", this.document.getUrl().toString()),
						URLIndexBuilder.create(this.document.getUrl(), new StringReader(this.document.getDocument().html())));
				writer.close();
			} finally {
				if (this.runWhenDone != null)
					this.runWhenDone.run();
				if (indexDirectory != null)
					indexDirectory.close();
			}
		}
		return null;
	}
	
	/**
	 * Start to build a new {@link URLIndexer}.
	 * @param config The {@link WebCrawler} config to use.
	 * @return {@code new Builder(config);}
	 */
	public static Builder builder(CrawlerConfig config) {
		return new Builder(config); 
	}
	
	/**
	 * Builder class for {@link URLIndexer}
	 * @author Jean Jung
	 *
	 */
	public static class Builder {
		
		private CrawlerConfig config;
		private URLDocument document;
		private Runnable runWhenDone;
		
		/**
		 * Default constructor.
		 */
		private Builder(CrawlerConfig config) {
			this.config = config; 
		}
		
		/**
		 * Set the document to be indexed.
		 * 
		 * @param document The {@link URLDocument} to index.
		 * @return {@code this} instance.
		 */
		public Builder index(URLDocument document) {
			this.document = document;
			return this;
		}
		
		/**
		 * Sets a operation to be executed when the document is indexed.
		 * 
		 * @return {@code this} instance.
		 */
		public Builder andThen(Runnable run) {
			this.runWhenDone = run;
			return this;
		}
		
		/**
		 * Build the return with the given values.
		 * @return a new instance of the {@link URLIndexer}
		 */
		public URLIndexer build() {
			URLIndexer instance = new URLIndexer();
			instance.config = this.config;
			instance.document = this.document;
			instance.runWhenDone = this.runWhenDone;
			return instance;
		}
	}
}
