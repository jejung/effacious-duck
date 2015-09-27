/**
 * 
 */
package main;

/**
 * @author Johnny
 *
 */
public interface IndexerStatusListener {
	
	void statusChanged(String newStatus);
	void searchReturned(String url);
	void wordIndexed(String word, int qtIndexada);
	void indexListTerminated(long totalTime);
}
