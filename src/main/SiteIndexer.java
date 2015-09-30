/**
 * 
 */
package main;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.URL;
import java.net.URLConnection;
import java.util.Set;
import java.util.TreeSet;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

/**
 * @author Jean Jung
 * @author Johnny
 *
 */
public class SiteIndexer 
{
//	private IoManager ioManager;
//	private String status;
//	private IndexerStatusListener listener;
//	private int words;
//	private SiteConnectionProducer producer;
//	
//	/**
//	 * @throws IOException
//	 */
//	public SiteIndexer(IndexerStatusListener listener, File indexFile) 
//		throws IOException 
//	{
//		this.listener = listener;
//		this.ioManager = new IoManager(indexFile);
//		this.producer = new SiteConnectionProducer();
//	}
//	
//	/**
//	 * Index the site list.
//	 * @param sites
//	 * @return
//	 * @throws IOException
//	 */
//	public int index(String[] sites) 
//		throws IOException 
//	{
//		long initialTime = System.currentTimeMillis();
//		this.words = 0;
//		for (final String url : sites)
//		{	
//			this.setStatus("Conectando a " + url);
//			producer.pushURL(new URL(url), true);
//		}
//		this.indexAll();
//		long finalTime = System.currentTimeMillis();
//		this.listener.indexListTerminated(finalTime - initialTime); 
//		return this.words;
//	}
//	
//	/**
//	 * 
//	 */
//	private void indexAll() 
//	{
//		URLConnection uc = this.producer.popConnection();
//		String url = uc.getURL().toString();
//		while (uc != null)
//		{
//			try {
//				this.setStatus("Processando: " + url);
//				this.index(url, uc);
//				this.setStatus("Pronto!");
//			} catch (IOException e) { e.printStackTrace(); }
//			
//			uc = this.producer.popConnection();
//		}
//	}
//
//	private void index(String url, URLConnection uc) 
//		throws IOException
//	{
//		Document doc = Jsoup.parse(uc.getInputStream(), null, url);
//		this.processElement(doc, url);
//	}
//	
//	private void processElement(Element root, String url) 
//		throws IOException 
//	{	
//		for (Element emt : root.children()) 
//		{
//			if (emt.isBlock() && !emt.html().equals(root.html())) 
//			{
//				this.processElement(emt, url);
//			}else if (emt.hasText())
//			{
//				for (String token : this.processText(emt.text().toLowerCase())) 
//				{
//					if (token.length() > 3)
//					{
//						this.setStatus("Indexando palavra ".concat(token).concat("..."));
//						if (this.ioManager.index(token, url))
//						{
//							this.words++;
//							this.listener.wordIndexed(token, this.words);
//						}
//					}
//				}
//			}
//			
//			if (emt.hasAttr("href"))
//			{
//				String pUrl = emt.absUrl("href");
//				
//				// ignores the resources links.
//				if (!(pUrl.endsWith(".jpg") || pUrl.endsWith(".jpeg")
//					|| pUrl.endsWith(".bmp") || pUrl.endsWith(".avi") 
//					|| pUrl.endsWith(".gif") || pUrl.endsWith(".png")))
//				{
//					this.setStatus("Redirecionando para ".concat(pUrl));
//					this.producer.pushURL(new URL(pUrl), true);
//				}
//			} 
//		}
//	}
//	
//	private String[] processText(String txt)
//	{
//		return this.removeIllegalCharacters(txt)
//				.replaceAll("[\\-_/~^!@#$%¬&*ºª§¹²³,;+-|?€®ŧ\"\'<>]]", "")
//				.replaceAll("\\W", " ")
//				.replaceAll("[0-9]", " ")
//				.split("\\s"); 
//	}
//	
//	public Set<String> listByToken(String token) 
//		throws IOException
//	{
//		return this.ioManager.listByToken(
//			this.removeIllegalCharacters(token.trim().toLowerCase())
//			 .replaceAll("[\\-_/~^!@#$%¬&*ºª§¹²³,;+-|?€®ŧ\"\'<>]]", ""), this.listener);
//	}
//	
//	/**
//	 * Replace the illegal characters on the string
//	 * @param string
//	 * @return
//	 */
//	private String removeIllegalCharacters(String string) 
//	{
//		String s = string;  
//		
//		char[] illegal = new char[]{'ã','á', 'à', 'â', 'ç', 'ü', 'û', 'ù', 'ú', 'õ', 'ô', 'ò', 'ó', 'î', 'ì', 'í', 'ê', 'è', 'é'};
//		char[] legal = new char[]{'a','a', 'a', 'a', 'c', 'u', 'u', 'u', 'u', 'o', 'o', 'o', 'o', 'i', 'i','i', 'e', 'e', 'e'};
//		
//		for (int i = 0; i < illegal.length; i++) 
//			s = s.replace(illegal[i], legal[i]);
//		
//		return s;
//	}
//	
//	/**
//	 * @return the words
//	 */
//	public int getWords() 
//	{
//		return words;
//	}
//
//	/**
//	 * @return the status
//	 */
//	public String getStatus() 
//	{
//		return status;
//	}
//
//	/**
//	 * @param status the status to set
//	 */
//	private void setStatus(String status) 
//	{
//		this.listener.statusChanged(status);
//		this.status = status;
//	}
//
//	private static class IoManager 
//	{
//		private File dataFile;
//		private SiteIndexerBTree bTree;
//		/**
//		 * @param indexFile
//		 * @throws IOException 
//		 */
//		public IoManager(File indexFile) 
//			throws IOException 
//		{
//			super();
//			this.setIndexFile(indexFile);
//			this.bTree = new SiteIndexerBTree(20, indexFile, this.dataFile); 
//		}
//		
//		private void checkExists(File aFile) 
//			throws IOException
//		{
//			if (!aFile.exists())
//				aFile.createNewFile();
//		}
//		
//		public Set<String> listByToken(String token, IndexerStatusListener listener) 
//			throws IOException
//		{
//			Set<String> set = new TreeSet<String>();
//			long pos = this.bTree.search(token);
//			if (pos > -1) 
//			{
//				this.checkExists(this.dataFile);
//				try (RandomAccessFile raf = new RandomAccessFile(this.dataFile, "r"))
//				{
//					if (raf.length() > 0) 
//					{
//						do 
//						{
//							raf.seek(pos);
//							String url = raf.readUTF(); 
//							pos = raf.readLong();
//							set.add(url);
//							listener.searchReturned(url);
//						} while (pos != -1);
//					}
//				}
//			}
//			return set;
//		}
//
//		public synchronized boolean index(String token, String url) 
//			throws IOException 
//		{
//			return this.bTree.insert(token, url);
//		}
//		
//		/**
//		 * @param indexFile the indexFile to set
//		 */
//		public void setIndexFile(File indexFile) 
//		{
//			this.dataFile = new File(indexFile.getAbsolutePath().concat("_DATA.dat"));;
//		}
//	}
}
