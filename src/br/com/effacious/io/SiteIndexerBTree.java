/**
 * 
 */
package br.com.effacious.io;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * @author Jean Jung
 *
 */
public class SiteIndexerBTree {
	private Node root;
	private int order;
	private int maxKeys;
	private IOBlockManager ioManager;

	/**
	 * @throws IOException
	 * 
	 */
	public SiteIndexerBTree(File file) throws IOException {
		this.ioManager = new IOBlockManager(file);
		this.order = this.ioManager.getTreeOrder();
		this.maxKeys = this.order * 2;
		this.root = this.ioManager.read(IOBlockManager.HEADER_SIZE);
	}

	/**
	 * @throws IOException
	 * 
	 */
	public SiteIndexerBTree(int order, File indexFile, File dataFile) throws IOException {
		this.order = order;
		this.maxKeys = order * 2;
		this.ioManager = new IOBlockManager(indexFile, dataFile, this.maxKeys);
		this.root = this.ioManager.read(IOBlockManager.HEADER_SIZE);
	}

	public long search(String w) throws IOException {
		Key key = new Key();
		key.word = w;
		return this.search(this.root, key);
	}

	/**
	 * @param x
	 * @param e
	 * @return
	 * @throws IOException
	 */
	private long search(Node x, Key e) throws IOException {
		if (x != null && x.size > 0) {
			int j = 0;
			int c = Integer.MAX_VALUE;
			while (j < x.size) {
				c = e.compareTo(x.elements[j]);
				if (c <= 0)
					break;
				j++;
			}
			if (c == 0) {
				return x.elements[j].dataPos;
			}
			return this.search(x.getChild(this.ioManager, j), e);
		}

		return -1;
	}

	public boolean insert(String w, String url) throws IOException {
		Key key = new Key();
		key.word = w;
		long l = this.search(this.root, key);
		long dPos = this.ioManager.storeNewURL(l, url);
		if (l == -1) {
			key.dataPos = dPos;
			this.insert(key);
			return true;
		}
		return false;
	}

	private void insert(Key key) throws IOException {
		if (this.root == null)
			this.root = new Node(this.maxKeys);

		Node r = this.root;

		if (r.leaf && r.size == this.maxKeys) {

			this.splitRoot(r, key);
		} else {
			this.insert(r, key);
			if (r.size > this.maxKeys)
				this.splitRoot(r, null);
		}
	}

	private void splitRoot(Node r, Key e) throws IOException {
		Node s = new Node(this.maxKeys);
		this.root = s;
		s.leaf = false;
		this.ioManager.write(r, false);
		s.childRefs[0] = r.id;
		s.childs[0] = r;
		if (e != null)
			this.insert(r, e);
		this.split(s, 0);
	}

	private void insert(Node x, Key e) throws IOException {
		int i = x.size - 1;
		if (x.leaf) {
			while (i >= 0 && (x.elements[i] != null && e.compareTo(x.elements[i]) < 0)) {
				x.elements[i + 1] = x.elements[i];
				i--;
			}
			x.elements[i + 1] = e;
			x.size++;
			this.ioManager.write(x, x == this.root);
		} else {
			while (i >= 0 && (x.elements[i] != null && e.compareTo(x.elements[i]) < 0))
				i--;
			i++;
			Node child = x.getChild(this.ioManager, i);
			if (child.size == this.maxKeys && child.leaf) {
				this.insert(child, e);
				this.split(x, i);
			} else {
				this.insert(child, e);
				if (child.size > this.maxKeys)
					this.split(x, i);
			}
		}
	}

	private void split(Node x, int i) throws IOException {
		int t = this.order;
		Node z = new Node(this.maxKeys);
		Node y = x.getChild(this.ioManager, i);
		z.leaf = y.leaf;
		z.size = t;
		y.size = z.size;
		for (int j = 0; j <= z.size; j++) {
			if (j < z.size)
				z.elements[j] = y.elements[j + t + 1];
			z.childRefs[j] = y.childRefs[j + t + 1];
			z.childs[j] = y.childs[j + t + 1];
		}
		for (int j = x.size; j >= i; j--) {
			if (j < x.size)
				x.elements[j + 1] = x.elements[j];

			if (j >= i + 1) {

				if (j < x.childRefs.length - 1) {
					x.childRefs[j + 1] = x.childRefs[j];
					x.childs[j + 1] = x.childs[j];
				}
			}
		}
		x.elements[i] = y.elements[t];
		x.size++;
		this.ioManager.write(y, y == this.root);
		this.ioManager.write(z, z == this.root);
		x.childRefs[i + 1] = z.id;
		x.childs[i + 1] = z;
		this.ioManager.write(x, x == this.root);
	}

	/**
	 * @throws IOException
	 * 
	 */
	public void print() throws IOException {
		if (this.root != null)
			this.root.print(ioManager, "");
	}

	public static class IOBlockManager {

		public static int blockReads;
		public static int blockWrites;
		public static final long HEADER_SIZE = Integer.BYTES + Long.BYTES;
		private File file;
		private File dataFile;
		private int blockSize;
		private int keyCount;

		/**
		 * @param file
		 * @throws IOException
		 */
		public IOBlockManager(File file) throws IOException {
			super();
			this.file = file;
			int order = this.getTreeOrder();
			this.keyCount = order * 2;
			this.blockSize = Integer.BYTES + // tamanho
					((Key.WORD_BYTES + Long.BYTES) * keyCount) + // elementos
					(Long.BYTES * (keyCount + 1)) + // referencias
					Long.BYTES; // próximo bloco livre.
		}

		/**
		 * @param file
		 * @param dataFile
		 */
		public IOBlockManager(File file, File dataFile, int keyCount) {
			super();
			this.file = file;
			this.dataFile = dataFile;
			this.keyCount = keyCount;
			this.blockSize = Integer.BYTES + // tamanho
					((Key.WORD_BYTES + Long.BYTES) * keyCount) + // elementos
					(Long.BYTES * (keyCount + 1)) + // referencias
					Long.BYTES; // próximo bloco livre
		}

		public int getTreeOrder() throws IOException {
			int order = 0;

			try (RandomAccessFile raf = new RandomAccessFile(file, "r")) {

				if (raf.length() > 0)
					order = raf.readInt();
			}

			return order;
		}

		private long getNewId(boolean root) throws IOException {
			try (RandomAccessFile raf = new RandomAccessFile(file, "rw")) {
				if (raf.length() == 0) {
					raf.setLength(HEADER_SIZE);
					raf.seek(0);
				}

				long free = 0;

				if (root) {
					free = HEADER_SIZE;
				} else {
					// ignora a ordem da árvore
					raf.seek(Integer.BYTES);
					free = raf.readLong();
					// se não tem livre vai ser gravado no final
					if (free == 0) {
						free = raf.length();
					} else {
						// se não tem que atualizar o primeiro endereço livre.
						raf.seek(raf.getFilePointer() + (this.blockSize - Long.BYTES));
						long nextFree = raf.readLong();
						// volta pro começo e atualiza o endereço livre
						raf.seek(Integer.BYTES);
						raf.writeLong(nextFree);
					}
				}
				return free;
			}
		}

		public Node read(long pos) throws IOException {
			Node n = null;
			try (RandomAccessFile raf = new RandomAccessFile(file, "rw")) {
				if (raf.length() > pos && pos > 0) {
					n = new Node(this.keyCount);
					n.id = pos;
					raf.seek(pos);
					byte[] b = new byte[this.blockSize];
					raf.read(b);
					ByteBuffer bb = ByteBuffer.wrap(b);
					n.size = bb.getInt();
					for (int i = 0; i < this.keyCount; i++)
						n.elements[i] = this.readKey(bb);

					for (int i = 0; i < this.keyCount + 1; i++) {
						n.childRefs[i] = bb.getLong();
						if (n.leaf && n.childRefs[i] != 0L)
							n.leaf = false;
					}
					IOBlockManager.blockReads++;
				}
			}
			return n;
		}

		private Key readKey(ByteBuffer bb) {
			Key key = new Key();
			byte[] b = new byte[Key.WORD_BYTES];
			bb.get(b);
			key.word = new String(b).trim();
			key.dataPos = bb.getLong();
			return key;
		}

		public void write(Node n, boolean root) throws IOException {
			try (RandomAccessFile raf = new RandomAccessFile(file, "rw")) {

				if (raf.length() == 0) {
					raf.writeInt(this.keyCount / 2);// TODO: mudar
					raf.writeLong(0L);// free address;
				}

				if (n.id == 0 || (n.id == HEADER_SIZE && !root) || (n.id != HEADER_SIZE && root)) {
					n.id = this.getNewId(root);
				}

				raf.seek(n.id);
				ByteBuffer bb = ByteBuffer.allocate(this.blockSize);
				bb.putInt(n.size);
				for (int i = 0; i < this.keyCount; i++) {
					if (n.elements[i] != null) {
						byte[] b = n.elements[i].word.getBytes();
						bb.put(Arrays.copyOfRange(b, 0, Key.WORD_BYTES));
						bb.putLong(n.elements[i].dataPos);
					} else {
						bb.put(new byte[Key.WORD_BYTES]);
						bb.putLong(-1L);
					}
				}

				for (int i = 0; i < this.keyCount + 1; i++) {
					if (n.childs[i] != null)
						bb.putLong(n.childs[i].id);
					else
						bb.putLong(0L);
				}
				raf.write(bb.array());
				IOBlockManager.blockWrites++;
			}
		}

		private void checkExists(File aFile) throws IOException {
			if (!aFile.exists())
				aFile.createNewFile();
		}

		public long storeNewURL(long pos, String url) throws IOException {
			this.checkExists(this.dataFile);

			long newPos = -1;
			try (RandomAccessFile raf = new RandomAccessFile(this.dataFile, "rw")) {
				if (raf.length() == 0) {
					raf.writeUTF(url);
					raf.writeLong(-1L);
					newPos = 0;
				} else {
					if (pos == -1) {
						newPos = raf.length();
						raf.seek(raf.length());
						raf.writeUTF(url);
						raf.writeLong(-1L);
					} else {
						raf.seek(pos);
						String lUtf = raf.readUTF();
						long nPos = raf.readLong();
						while (nPos != -1L && !lUtf.equals(url)) {
							raf.seek(nPos);
							lUtf = raf.readUTF();
							nPos = raf.readLong();
						}

						if (lUtf.equals(url))
							return pos;
						newPos = raf.length();
						raf.seek(raf.getFilePointer() - Long.BYTES);
						raf.writeLong(newPos);
						raf.seek(raf.length());
						raf.writeUTF(url);
						raf.writeLong(-1L);
					}
				}
			}
			return newPos;
		}
	}

	private static class Key implements Comparable<Key> {
		public static final int WORD_BYTES = 40;
		private String word;
		private long dataPos;

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return this.word + "(" + dataPos + ")";
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Comparable#compareTo(java.lang.Object)
		 */
		@Override
		public int compareTo(Key o) {
			return this.word.compareTo(o.word);
		}
	}

	private static class Node {
		private long id;
		private int size;
		private boolean leaf;
		private Key[] elements;
		private long[] childRefs;
		private Node[] childs;

		/**
		 * 
		 */
		public Node(int size) {
			super();
			this.id = 0;
			this.size = 0;
			this.leaf = true;
			this.elements = new Key[size + 1];
			this.childRefs = new long[size + 2];
			this.childs = new Node[childRefs.length];
		}

		public void print(IOBlockManager reader, String prefix) throws IOException {

			System.out.print(prefix + "+ [");
			for (int i = 0; i < this.size; i++) {
				if (i > 0)
					System.out.print(",");
				System.out.print(this.elements[i]);
			}
			System.out.println("]");

			for (int i = 0; i < this.size + 1; i++) {
				Node c = this.getChild(reader, i);
				if (c != null && c.id != 0)
					c.print(reader, prefix + i + "   ");
			}
		}

		public Node getChild(IOBlockManager reader, int i) throws IOException {
			if (i < 0 || i >= this.childRefs.length)
				return null;

			if (this.childs[i] == null || this.childs[i].id != this.childRefs[i])
				this.childs[i] = reader.read(this.childRefs[i]);

			return this.childs[i];
		}
	}
}
