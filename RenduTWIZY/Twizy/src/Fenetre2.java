import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.sound.sampled.Clip;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class Fenetre2 extends JFrame implements ActionListener{


	//Panneau P_Pan = new Panneau();
	static boolean hsv=false;
	static boolean tableau=false;
	static boolean seuillage=false;
	private JButton b;


	private JLabel label=new JLabel("",SwingConstants.CENTER);

	public Fenetre2() throws InterruptedException, IOException {


		this.setTitle("Principal");

		//taille ecran
		//Toolkit outil = getToolkit();
		//this.setSize(outil.getScreenSize());

		this.setSize(500, 500);
		this.setResizable(true);

		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		this.setLocationRelativeTo(null);

		//setDefaultLookAndFeelDecorated(true);
		setExtendedState(MAXIMIZED_BOTH);


		b=new JButton("Quitter");
		b.addActionListener(this);
		b.setForeground(Color.red);


		JPanel Pan = new JPanel()
		{
			protected void paintComponent(Graphics g) 
			{
				super.paintComponent(g);

				Image img_principale = Principale.img_principale;
				Image img_extraite = Principale.img_extraite;
				Image img_trouve = Principale.img_trouve;
				Image img_hsv=Principale.img_hsv;
				Image img_seuillage=Principale.img_seuillage;

				int centre1 = (super.getWidth()/2)-(img_principale.getWidth(null)/2);


				if(img_principale != null)
					g.drawImage(img_principale, centre1, 0, img_principale.getWidth(null), img_principale.getHeight(null), this);
				if (img_extraite != null)
					g.drawImage(img_extraite, centre1, img_principale.getHeight(null)+25, img_extraite.getWidth(null), img_extraite.getHeight(null), this);
				if(img_trouve != null) {
					int centre2 = (super.getWidth()/2)+(img_principale.getWidth(null)/2)-img_trouve.getWidth(null);
					g.drawImage(img_trouve, centre2, img_principale.getHeight(null)+25, img_trouve.getWidth(null), img_trouve.getHeight(null), this);

					if (Principale.tableau==true) {
						label.setText("<html>"+
								"Aucun Panneau: "+Integer.toString(Principale.tab[0])+" <br>"+
								"	 30:       "+Integer.toString(Principale.tab[1])+" <br>"+
								"	 50:       "+Integer.toString(Principale.tab[2])+" <br>"+
								"	 70:       "+Integer.toString(Principale.tab[3])+" <br>"+
								"	 90:       "+Integer.toString(Principale.tab[4])+" <br>"+
								"	110:	   "+Integer.toString(Principale.tab[5])+" <br>"+
								"Depassement:  "+Integer.toString(Principale.tab[6])+" <br>"+
								"</html>");
						label.setOpaque(true);
						label.setForeground(Color.black);
						label.setBackground(Color.green);
						Font font = new Font("Arial",Font.BOLD,18);
						label.setFont(font);
					}
				}

				label.setBounds(super.getWidth()/2-100, img_principale.getHeight(null)+25, 200, 175);


				int taille=200;
				if(img_hsv!=null && Principale.hsv==true) {
					float prop=(float)img_hsv.getWidth(null)/((float)img_hsv.getHeight(null));
					g.drawImage(img_hsv, centre1-taille,img_principale.getHeight(null)/2 , (int)prop*taille, taille, this);
				}
				if(img_seuillage!=null && Principale.seuillage==true) {
					float prop=(float)img_seuillage.getWidth(null)/((float)img_seuillage.getHeight(null));
					g.drawImage(img_seuillage, (super.getWidth()/2)+(img_principale.getWidth(null)/2),img_principale.getHeight(null)/2 , (int)prop*taille, taille, this);
				}

				if(Principale.detecte==true) {
					int xdeb=centre1+MaBibliothequeTraitementImageEtendue.rect.x;
					int ydeb=MaBibliothequeTraitementImageEtendue.rect.y;
					int rWidth=MaBibliothequeTraitementImageEtendue.rect.width;
					int rHeight=MaBibliothequeTraitementImageEtendue.rect.height;

					g.setColor(Color.green);
					g.drawRect(xdeb, ydeb, rWidth, rHeight);
					//g.fillRect(xdeb, ydeb, rWidth, rHeight);
					//g.clearRect(xdeb+5, ydeb+5, rWidth-10, rHeight-10);

					/*Graphics2D g2 = (Graphics2D) g;
					BasicStroke line = new BasicStroke(4.0f);
					g2.setStroke(line);
					g2.drawRect(xdeb, ydeb, rWidth, rHeight);*/
				}

			}
		};

		Pan.setLayout(null);

		Pan.setBackground(Color.gray);
		Pan.add(label);

		//this.setLayout(new FlowLayout()); //mettre le panel au centre
		this.setLayout(new BorderLayout());
		this.getContentPane().add(Pan);
		this.getContentPane().add(b, BorderLayout.SOUTH);


		this.setVisible(false);

	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		if(arg0.getSource() == b) {
			this.dispose();
			Principale.fen.dispose();
			Principale.continu=false;
		}

	}
}
