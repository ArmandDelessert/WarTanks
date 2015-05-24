package startMenu;
import game.Game;

import java.awt.*;// pour la gestion de la fenetre
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.util.Observable;
import java.util.Observer;
import role.*;
import startMenu.Model.ScreenObservers;
import startMenu.Model.ServerIpObservers;
import startMenu.Model.StartObservers;

/**
 * La vue en SWING qui représente le Model
 * @author Julien Bignens
 * @author Bruno Carvalho
 * @author Gaëtan Djomnang Yaze
 * @author Marcel Sinniger
 *
 */
public class View extends JFrame implements Observer {
	 
	
   private static final long serialVersionUID = 1L;
   private ComboPanel comboPanel = new ComboPanel();
	private JComboBox<String> comboPanel2 = new JComboBox<String>();
	private JButton buttonClient = new JButton("Rejoindre");
	private JButton buttonServerCreate = new JButton("Créer");
	private JButton buttonServerStart = new JButton("Lancer");
	private JLabel title = new JLabel("BOMBERMAN");
	private JLabel ipAddress = new JLabel("IP: xxx.xxx.xxx");
	private JLabel ipServer = new JLabel("IP Serveur:");
	private JLabel portServer = new JLabel("Port:");
	private Font titleFont = new Font ("Serif", Font.BOLD | Font.ITALIC,64);
	private Font ipFont = new Font ("Serif", Font.BOLD | Font.ITALIC,40);
   private String filename = new String("res/Bomberman2.jpg");
   private JTextField ipText = new JTextField(10);
   private JTextField portText = new JTextField(10);
   private JPanel[] panel = new JPanel[5];
   
   private Model model;


	public View(final Model model) {
	   this.model = model;
		Toolkit tk = Toolkit.getDefaultToolkit();
		Dimension dimEcran = tk.getScreenSize(); 
		//permet de definir la taille et la position de la fenetre
		setBounds((dimEcran.width -500)/2, (dimEcran.height - 600)/2, 500, 600);
		setVisible(true);//permet de rendre visible la fenetre
		setResizable(false);
		
		setTitle("BomberMan");// definir le title
		title.setHorizontalAlignment(JLabel.RIGHT);
		title.setFont(titleFont);
	    title.setForeground(Color.magenta);
		ipAddress.setFont(ipFont);
		ipAddress.setForeground(Color.magenta);
		ipServer.setForeground(Color.magenta);
		portServer.setForeground(Color.magenta);
			try {
				title.setIcon(new ImageIcon(ImageIO.read( new File("res/logo.gif") ) ));
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		
	

		buttonClient.setPreferredSize(new Dimension(100, 40));
		buttonClient.setEnabled(true);
		buttonClient.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
            if (!model.getRole().getConnected()) {
               ((ClientRole)model.getRole()).connectServer(ipText.getText(), Integer.valueOf(portText.getText()));
               while (!model.getRole().getConnected()) { } // attendre jusqu'à ce que la connexion soit établie.
               model.getRole().send("clientConnected");
               buttonClient.setEnabled(false);
            }
         }
		});
		buttonServerCreate.setPreferredSize(new Dimension(100, 40));
		buttonServerCreate.setEnabled(true);
		buttonServerCreate.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent arg0) {
            if (!model.getRole().getConnected()) {
               ((ServerRole)model.getRole()).startServer(Integer.valueOf(portText.getText()));
               buttonServerCreate.setEnabled(false);
            }
         }
		});
		buttonServerStart.setPreferredSize(new Dimension(100, 40));
		buttonServerStart.setEnabled(false);
		buttonServerStart.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
            ((ServerRole)model.getRole()).send("startGame");
            buttonServerStart.setEnabled(false);
			   model.StartGame(Game.gameName);
			}
		});
		ImagePanel backPanel = new ImagePanel(filename);
		backPanel.setLayout(new GridLayout(6, 1));//position du buttonClient
	   
		comboPanel2.addItem("--");
		comboPanel2.setEditable(true);
		comboPanel2.setMaximumRowCount(4);
		comboPanel2.setVisible(false); // sélection de la carte prévue, mais pas encore implémentée
		
		panel[0] = new JPanel();
		panel[1] = new JPanel();
		panel[2] = new JPanel();
		panel[3] = new JPanel();
		panel[4] = new JPanel();
        
		panel[0].setOpaque(false);
		panel[1].setOpaque(false);
		panel[2].setOpaque(false);
		panel[3].setOpaque(false);
		panel[4].setOpaque(false);
		
		panel[3].setVisible(false);
		panel[4].setVisible(false);
		
		panel[0].add(title);
		panel[1].add(ipAddress);
		panel[2].add(comboPanel);

		backPanel.add(panel[0]);
		backPanel.add(panel[1]);
		backPanel.add(panel[2]);
		backPanel.add(panel[3]);
		backPanel.add(panel[4]);
       
		this.setContentPane(backPanel);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		this.setVisible(true);
		this.repaint();
		
	}

	class ImagePanel extends JPanel {

		  /**
       * 
       */
      private static final long serialVersionUID = 1L;
      private Image img;

		  public ImagePanel(String img) {
		    this(new ImageIcon(img).getImage());
		  }

		  public ImagePanel(Image img) {
		    this.img = img;
		    Dimension size = new Dimension(img.getWidth(null), img.getHeight(null));
		    setPreferredSize(size);
		    setMinimumSize(size);
		    setMaximumSize(size);
		    setSize(size);
		    setLayout(null);
		  }

		  public void paintComponent(Graphics g) {
		    g.drawImage(img, 0, 0, null);
		  }

		}
	 

	public class ComboPanel extends JPanel {
		/**
       * 
       */
      private static final long serialVersionUID = 1L;
      private String choices[] = { "--","Client", "Serveur"};
		private JComboBox<String> combo = new JComboBox<String>();
		public ComboPanel(){
			
			for(int i = 0; i < choices.length; i++)
				combo.addItem(choices[i]);
			combo.setEditable(false);
			add(combo);
			combo.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if(combo.getSelectedItem().toString() == choices[1]){
						panel[3].setVisible(true);
						panel[4].setVisible(true);
						
						panel[3].remove(buttonServerCreate);
						panel[3].remove(buttonServerStart);
						panel[4].remove(portServer);
						panel[4].remove(portText);
						panel[4].remove(comboPanel2);
						
						panel[3].add(buttonClient);
						panel[4].add(ipServer);
						panel[4].add(ipText);
						panel[4].add(portServer);
						panel[4].add(portText);
						panel[4].validate();
						panel[4].repaint();
						panel[3].validate();
						
						model.setRoleToClient();
						
						panel[3].repaint();
					}
					
					if(combo.getSelectedItem().toString() == choices[2]){
						panel[3].setVisible(true);
						panel[4].setVisible(true);
						
						panel[3].remove(buttonClient);
						panel[4].remove(ipServer);
					    panel[4].remove(ipText);
						
					    panel[3].add(buttonServerCreate);
						panel[3].add(buttonServerStart);
						panel[4].add(portServer);
						panel[4].add(portText);
						panel[4].add(comboPanel2);
						panel[4].validate();
						panel[4].repaint();
						panel[3].validate();
						
						model.setRoleToServer();
						
						panel[3].repaint();
						
					}
					if(combo.getSelectedItem().toString() == choices[0]){
						panel[3].setVisible(false);
						panel[4].setVisible(false);
						panel[3].remove(buttonClient);
						panel[4].remove(ipServer);
						panel[4].remove(ipText);
						panel[3].remove(buttonServerCreate);
						panel[3].remove(buttonServerStart);
						panel[4].remove(portServer);
						panel[4].remove(portText);
						panel[4].remove(comboPanel2);
						panel[4].validate();
						panel[4].repaint();
						panel[3].validate();
						panel[3].repaint();
					}
				}
			});
		}
		
		
	}

   @Override
   /**
    * Les différents mis à jour de la vue
    */
   public void update(Observable obs, Object arg) {
      
      if (obs instanceof ServerIpObservers){
         ipAddress.setText(model.getRole().getIpAddress());
         repaint();
      }
      
      if(obs instanceof ScreenObservers) {
         buttonClient.setText((String)arg);
         buttonServerCreate.setText((String)arg);
         repaint();
      }

      if (obs instanceof StartObservers) {
         buttonServerStart.setEnabled(true);
      }
      
   }

}