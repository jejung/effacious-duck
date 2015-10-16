/**
 * 
 */
package br.com.efficacious.io;

import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Paths;
import java.util.concurrent.Callable;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import br.com.efficacious.dom.URLDocument;

/**
 * @author Jean Jung
 *
 */
public class URLIndexer implements Callable<Void> {
	
	private static Directory INDEX_DIRECTORY; 
	private static final Analyzer DEFAULT_ANALYZER = new StandardAnalyzer();
	private static final IndexWriterConfig DEFAULT_CONFIG = new IndexWriterConfig(DEFAULT_ANALYZER);
	
	static {
		try {
			INDEX_DIRECTORY = FSDirectory.open(Paths.get(System.getProperty("user.dir") + "/index"));
		} catch (IOException e) {
			Logger.getGlobal().log(Level.INFO, "Cannot open Index directory, using working directory", e);
			try {
				INDEX_DIRECTORY = FSDirectory.open(Paths.get("/index/"));
			} catch (IOException e1) {
				Logger.getGlobal().log(Level.SEVERE, "Error openning index directory, exiting", e1);
				System.exit(0);
			}
		}
		DEFAULT_CONFIG.setOpenMode(OpenMode.CREATE_OR_APPEND);
	}
	
	private URLDocument document;
	private Semaphore releaseWhenDone;
	
	/**
	 * 
	 */
	public URLIndexer(Semaphore releaseWhenDone, URLDocument document) {
		this.releaseWhenDone = releaseWhenDone;
		this.document = document;
	}
	
	/**
	 * @see java.util.concurrent.Callable#call()
	 */
	@Override
	public Void call() throws Exception {
		try {
			IndexWriter writer = new IndexWriter(INDEX_DIRECTORY, DEFAULT_CONFIG);
			writer.updateDocument(new Term("url", this.document.getUrl().toString()), 
					URLIndexBuilder.create(this.document.getUrl(), 
							new StringReader(this.document.getDocument().html())));
			writer.close();
		} finally {
			this.releaseWhenDone.release();
		}
		return null;
	}

}
