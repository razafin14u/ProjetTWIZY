import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;

public class Fenetre extends JFrame implements ActionListener{

	private JButton jButton1;
	private JButton jButton2;
	private JTextField jtf1;
	private JTextField jtf2;
	private JRadioButton jr1;
	private JRadioButton jr2;
	private JRadioButton jr3;
	private JComboBox<String> combo;



	public Fenetre() throws InterruptedException {


		this.setTitle("Menu");

		//taille ecran
		//Toolkit outil = getToolkit();
		//this.setSize(outil.getScreenSize());

		this.setSize(500, 500);
		this.setResizable(true);

		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		//this.setContentPane(Pan);

		this.setLocationRelativeTo(null);

		//setDefaultLookAndFeelDecorated(true);
		setExtendedState(MAXIMIZED_BOTH);



		jButton1 = new JButton();
		ImageIcon icon1 = new ImageIcon(new ImageIcon("voit_verte.jpg").getImage().getScaledInstance(110, 70, Image.SCALE_DEFAULT));
		jButton1.setIcon(icon1);
		jButton1.setText("   Lancer");
		jButton1.addActionListener(this);

		jButton2 = new JButton();
		ImageIcon icon2 = new ImageIcon(new ImageIcon("voit_rouge.jpg").getImage().getScaledInstance(110, 70, Image.SCALE_DEFAULT));
		jButton2.setIcon(icon2);
		jButton2.setText("   Quitter");
		jButton2.addActionListener(this);

		String[] tab = {"Vidéo", "Live","Image"};
		combo = new JComboBox<String>(tab);
		combo.setBackground(Color.yellow);
		//combo.setForeground(Color.blue);

		jtf1 = new JTextField("video1.avi");
		jtf1.setBackground(Color.yellow);
		jtf2 = new JTextField("p1.jpg");
		jtf2.setBackground(Color.yellow);


		jr1 = new JRadioButton("HSV");
		jr2 = new JRadioButton("Seuillage");
		jr3 = new JRadioButton("Tableau");
		jr1.setBackground(Color.yellow);
		jr2.setBackground(Color.yellow);
		jr3.setBackground(Color.yellow);

		

		JPanel arriere = new JPanel()
		{
			protected void paintComponent(Graphics g) 
			{
				super.paintComponent(g);

				//Pour une image de fond
				Image img = null;
				try {
					img = ImageIO.read(new File("fond_ecran.png"));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				float prop=(float)img.getWidth(null)/((float)img.getHeight(null));
				//System.out.println(prop+" "+img.getHeight(null)+" "+img.getWidth(null));
				if(super.getHeight()>super.getWidth()/prop) {

					g.drawImage(img, super.getWidth()-(int)(super.getHeight()*prop), 0, (int)(super.getHeight()*prop), super.getHeight(), this);
				}
				else {
					g.drawImage(img, 0, super.getHeight()-(int)(super.getWidth()/prop), super.getWidth(), (int)(super.getWidth()/prop), this);
				}

				int decalage = (super.getHeight()/10);
				int centre_gauche = (super.getWidth()/4)-50;
				int centre_droit = (super.getWidth()*3/4)-50;
				int centre_haut1 = (super.getHeight()/2)-3*decalage;
				int centre_haut2 = (super.getHeight()/2)-2*decalage;
				int centre_haut3 = (super.getHeight()/2)-decalage;
				int centre_haut4 = (super.getHeight()/2)+decalage+50;

				jButton1.setBounds(super.getWidth()/2-50,super.getHeight()/2,100,50);
				jButton2.setBounds(super.getWidth()/2-50,centre_haut4,100,50);
				jtf1.setBounds(centre_gauche, centre_haut2, 100, 30);
				combo.setBounds(centre_gauche, centre_haut1, 100, 30);
				jtf1.setBounds(centre_gauche, centre_haut2, 100, 30);
				jtf2.setBounds(centre_gauche, centre_haut3, 100, 30);
				jr1.setBounds(centre_droit, centre_haut1, 100, 30);
				jr2.setBounds(centre_droit, centre_haut2, 100, 30);
				jr3.setBounds(centre_droit, centre_haut3, 100, 30);

				//label.setText(Integer.toString(test));
				


			}
		};

		arriere.setLayout(null);


		arriere.add(jr1);
		arriere.add(jr2);
		arriere.add(jr3);

		arriere.add(jButton1);
		arriere.add(jButton2);
		arriere.add(jtf1);
		arriere.add(jtf2);
		arriere.add(combo);


		//ajouter le panneau à la fenétre.
		this.getContentPane().add(arriere);
		//this.getContentPane().add(jButton1);


		this.setVisible(true);

	}


	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub

		if(arg0.getSource() == jButton2) {
			System.out.println("b2");
			Principale.fen2.dispose();
			Principale.fen.dispose();
		}
		if(arg0.getSource() == jButton1 ) {
			
			Principale.chemin_video=jtf1.getText();
			Principale.chemin_image=jtf2.getText();
			Principale.hsv=jr1.isSelected();
			Principale.seuillage=jr2.isSelected();
			Principale.tableau=jr3.isSelected();
			Principale.fonctionnement=(String) combo.getSelectedItem();
			
			Principale.debut=true;
			
			this.setVisible(false);
			
		}
		
	}

}
