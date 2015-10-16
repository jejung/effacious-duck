/**
 * 
 */
package br.com.efficacious.io;

import java.io.StringReader;
import java.util.concurrent.Callable;
import java.util.concurrent.Semaphore;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;

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

			// waits for the lucene directory configuration completes
			LuceneDirectory.getLatch().await();

			INDEX_DIRECTORY = LuceneDirectory.getDirectory();

			IndexWriter writer = new IndexWriter(INDEX_DIRECTORY, DEFAULT_CONFIG);
			writer.updateDocument(new Term("url", this.document.getUrl().toString()),
					URLIndexBuilder.create(this.document.getUrl(), new StringReader(this.document.getDocument().html())));
			writer.close();
		} finally {
			this.releaseWhenDone.release();
		}
		return null;
	}

}
