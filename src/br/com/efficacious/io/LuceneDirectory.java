package br.com.efficacious.io;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class LuceneDirectory {
	
	private static Directory INDEX_DIRECTORY; 
	
	private static CountDownLatch latch = new CountDownLatch(1);
	
	public static void setDirectory(String directory) {
		
		try {
			INDEX_DIRECTORY = FSDirectory.open(Paths.get(directory));
		} catch (IOException e) {
			Logger.getGlobal().log(Level.INFO, "Cannot open Index directory, using working directory", e);
			try {
				INDEX_DIRECTORY = FSDirectory.open(Paths.get("/index/"));
			} catch (IOException e1) {
				Logger.getGlobal().log(Level.SEVERE, "Error openning index directory, exiting", e1);
				System.exit(0);
			}
		}
		
		latch.countDown();
	}
	
	
	
	
	public static CountDownLatch getLatch() {
		return latch;
	}
	
	public static Directory getDirectory() {
		return INDEX_DIRECTORY;
	}
	
}
