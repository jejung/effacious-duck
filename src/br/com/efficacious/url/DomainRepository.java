package br.com.efficacious.url;

import java.util.Collection;
import java.util.HashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class DomainRepository {

	private HashMap<String, DomainRanking> domains;

	private static DomainRepository instance = new DomainRepository();

	private ReadWriteLock readWriteLock;

	private DomainRepository() {
		domains = new HashMap<String, DomainRanking>();
		readWriteLock = new ReentrantReadWriteLock();
	}

	public static DomainRepository getInstance() {
		return instance;

	}

	public Collection<DomainRanking> getDomainsRanking() {

		readWriteLock.readLock().lock();

		try {

			return domains.values();
			
		} finally {

			readWriteLock.readLock().unlock();

		}

	}

	public void addDomain(String domain) {

		readWriteLock.writeLock().lock();

		try {

			if (domains.containsKey(domain)) {
				domains.get(domain).increase();
			} else {
				domains.put(domain, new DomainRanking(domain));
			}

		} finally {
			readWriteLock.writeLock().unlock();
		}

	}

	public class DomainRanking {

		private String domain;
		private Integer quantity;

		public DomainRanking(String domain) {
			this.domain = domain;
			this.quantity = 1;
		}

		public String getDomain() {
			return domain;
		}

		public void setDomain(String domain) {
			this.domain = domain;
		}

		public Integer getQuantity() {
			return quantity;
		}

		public void setQuantity(Integer quantity) {
			this.quantity = quantity;
		}

		public synchronized void increase() {
			quantity++;
		}

	}

}
