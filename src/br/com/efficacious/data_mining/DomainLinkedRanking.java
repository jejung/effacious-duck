package br.com.efficacious.data_mining;

import java.util.Collection;

import br.com.efficacious.url.DomainRepository;

public class DomainLinkedRanking implements Runnable {

	@Override
	public void run() {
		showRanking();
	}

	private synchronized void showRanking() {

		while (true) {

			Collection<DomainRepository.DomainRanking> ranking = DomainRepository.getInstance().getDomainsRanking();

			int maxQuantity = 0;

			String domain = null;

			for (DomainRepository.DomainRanking rank : ranking) {

				if (rank.getQuantity() > maxQuantity) {
					maxQuantity = rank.getQuantity();
					domain = rank.getDomain();
				}

			}
			if (domain != null)
				System.err.println("DOMÍNIO QUE MAIS FOI LINKADO --> " + domain + " . LINKADO " + maxQuantity + " VEZES");

			try {
				wait(2000);
			} catch (InterruptedException e) {

			}
		}

	}

}
