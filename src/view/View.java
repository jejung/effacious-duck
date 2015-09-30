/**
 * 
 */
package view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;

import main.IndexerStatusListener;

/**
 * @author Jean Jung
 *
 */
public class View extends JFrame implements ActionListener, IndexerStatusListener 
{

	/* (non-Javadoc)
	 * @see main.IndexerStatusListener#statusChanged(java.lang.String)
	 */
	@Override
	public void statusChanged(String newStatus) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see main.IndexerStatusListener#searchReturned(java.lang.String)
	 */
	@Override
	public void searchReturned(String url) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see main.IndexerStatusListener#wordIndexed(java.lang.String, int)
	 */
	@Override
	public void wordIndexed(String word, int qtIndexada) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see main.IndexerStatusListener#indexListTerminated(long)
	 */
	@Override
	public void indexListTerminated(long totalTime) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		
	}
//	/**
//	 * 
//	 */
//	private static final long serialVersionUID = 4083085192632510988L;
//	private static final String NAME_BTN_INDEXAR = "BTN_INDEXAR";
//	private static final String NAME_BTN_CONSULTAR = "BTN_CONSULTAR";
//	private SiteIndexer indexer;
//	private File indexFile;
//	private JTextPane txtSite;
//	private JLabel lblNotificacoes;
//	private JTextField txtConsulta;
//	private JTextPane lstSites;
//	private JLabel lblIndexadas;
//	/**
//	 * @throws HeadlessException
//	 * @throws IOException 
//	 */
//	public View() 
//		throws HeadlessException, IOException 
//	{
//		this.setTitle("Indexador de sites v0.1");
//		this.indexFile = new File("SITE_INDEX.dat");
//		this.indexer = new SiteIndexer(this, this.indexFile);
//		this.setLayout(new BorderLayout());
//		JPanel pnlIdx = new JPanel(new BorderLayout());
//		JLabel lblInformation = new JLabel("Adicione os sites a serem indexados. Adicione um site por linha.");
//		pnlIdx.add(lblInformation, BorderLayout.NORTH);
//		this.txtSite = new JTextPane();
//		this.txtSite.setPreferredSize(new Dimension(100,100));
//		this.txtSite.setBorder(new LineBorder(Color.BLACK));
//		JScrollPane jscpSites = new JScrollPane(this.txtSite);
//		jscpSites.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
//		jscpSites.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
//		pnlIdx.add(jscpSites, BorderLayout.CENTER);
//		JButton btnIdx = new JButton("Indexar sites");
//		btnIdx.setName(NAME_BTN_INDEXAR);
//		btnIdx.addActionListener(this); 
//		pnlIdx.add(btnIdx, BorderLayout.SOUTH);
//		
//		JPanel pnlTop = new JPanel(new BorderLayout());
//		pnlTop.add(pnlIdx, BorderLayout.CENTER);
//		JPanel pnlNotificacoes = new JPanel(new BorderLayout());
//		this.lblNotificacoes = new JLabel("Aguardando...");
//		this.lblNotificacoes.setVerticalAlignment(SwingConstants.CENTER);
//		pnlNotificacoes.add(this.lblNotificacoes, BorderLayout.CENTER);
//		this.lblIndexadas = new JLabel("0");
//		this.lblIndexadas.setAlignmentX(Component.RIGHT_ALIGNMENT);
//		pnlNotificacoes.add(this.lblIndexadas, BorderLayout.EAST);
//		pnlTop.add(pnlNotificacoes, BorderLayout.SOUTH);
//		
//		this.add(pnlTop, BorderLayout.NORTH); 
//		
//		JPanel pnlConsulta = new JPanel(new BorderLayout());
//		JPanel pnlFilter = new JPanel(new FlowLayout());
//		this.txtConsulta = new JTextField(40);
//		pnlFilter.add(this.txtConsulta);
//		JButton btnConsulta = new JButton("Consultar sites");
//		btnConsulta.addActionListener(this);
//		btnConsulta.setName(NAME_BTN_CONSULTAR);
//		pnlFilter.add(btnConsulta);
//		pnlConsulta.add(pnlFilter, BorderLayout.NORTH);
//		this.lstSites = new JTextPane();
//		this.lstSites.setEditable(false); 
//		this.lstSites.setBorder(new LineBorder(Color.BLACK));
//		this.lstSites.setPreferredSize(new Dimension(100, 100));
//		JScrollPane jscp = new JScrollPane(this.lstSites);
//		jscp.setAutoscrolls(true);
//		jscp.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
//		jscp.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
//		pnlConsulta.add(jscp, BorderLayout.CENTER);
//		this.add(pnlConsulta, BorderLayout.CENTER);
//		this.setResizable(true);
//	}
//
//	/* (non-Javadoc)
//	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
//	 */
//	@Override
//	public void actionPerformed(ActionEvent e) {
//		
//		if (e.getSource() instanceof JButton) 
//		{
//			JButton btn = (JButton) e.getSource();
//			switch (btn.getName()) {
//			case NAME_BTN_INDEXAR:
//				this.lblIndexadas.setText("0");
//				new Thread(
//					new Runnable() {
//						@Override
//						public void run() {
//							try {
//								indexer.index(txtSite.getText().split("\n"));
//							} catch (IOException e) {
//								JOptionPane.showMessageDialog(null, "Problemas ao gravar arquivos: " + "\n" + 
//										e.getMessage());
//								e.printStackTrace();
//							}
//						}
//					}).start();
//				break;
//			case NAME_BTN_CONSULTAR:
//				this.lstSites.setText("");
//				new Thread(new Runnable() {
//					
//					@Override
//					public void run() {
//						Set<String> urlsConsultadas;
//						try {
//							urlsConsultadas = indexer.listByToken(txtConsulta.getText());
//							String txt = "";
//							for (String url : urlsConsultadas) {
//								
//								txt = txt.concat(url.concat("\n"));
//							}
//							lstSites.setText(txt);
//						} catch (IOException e) {
//							e.printStackTrace();
//							JOptionPane.showMessageDialog(null, "Problemas com arquivos:\n"
//									+ e.getMessage());
//						}
//					}
//				}).start();
//				break;
//			default:
//				break;
//			}
//		}
//	}
//
//	/* (non-Javadoc)
//	 * @see main.IndexerStatusListener#statusChanged(java.lang.String)
//	 */
//	@Override
//	public void statusChanged(String newStatus) {
//		this.lblNotificacoes.setText(newStatus);
//	}
//
//	/* (non-Javadoc)
//	 * @see main.IndexerStatusListener#searchReturned(java.lang.String)
//	 */
//	@Override
//	public void searchReturned(String url) 
//	{
//	}
//
//	/* (non-Javadoc)
//	 * @see main.IndexerStatusListener#wordIndexed(java.lang.String)
//	 */
//	@Override
//	public void wordIndexed(String word, int qtIndexada) 
//	{
//		this.lblIndexadas.setText(String.valueOf(qtIndexada));
//	}
//
//	/* (non-Javadoc)
//	 * @see main.IndexerStatusListener#indexListTerminated(long)
//	 */
//	@Override
//	public void indexListTerminated(long totalTime) 
//	{
//		DecimalFormat df = new DecimalFormat("##################.######");
//		
//		this.lblNotificacoes.setText("Pronto! Tempo total: " + df.format(totalTime/1000) + " s");
//	}
}
