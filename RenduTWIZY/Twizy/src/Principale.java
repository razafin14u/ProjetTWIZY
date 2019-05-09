import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.features2d.DescriptorExtractor;
import org.opencv.features2d.FeatureDetector;
import org.opencv.highgui.Highgui;
import org.opencv.highgui.VideoCapture;
//import org.opencv.imgcodecs.Imgcodecs;
//import org.opencv.highgui.Highgui;
//import org.opencv.highgui.VideoCapture;
import org.opencv.imgproc.Imgproc;
//import org.opencv.videoio.VideoCapture;

import java.io.File;

import utilitaireAgreg.MaBibliothequeTraitementImage;

public class Principale {

	static Fenetre fen;
	static Fenetre2 fen2;
	static Image img_principale=null;
	static Image img_extraite=null;
	static Image img_trouve=null;
	static Image img_hsv=null;
	static Image img_seuillage=null;

	static boolean hsv=false;
	static boolean tableau=false;
	static boolean seuillage=false;
	static String chemin_video;
	static String chemin_image;
	static String fonctionnement;

	static boolean detecte=false;
	static boolean debut=false;
	static boolean continu=true;

	static List<MatOfKeyPoint> bdds= new ArrayList<>();
	static List<Mat> bddd= new ArrayList<>();
	static int nb=5;
	static int []tab=new int[7];
	
	static Mat panref;

	static {
		//System.load("C:\\Users\\Adrien\\eclipse-workspace\\Twizi\\opencv_ffmpeg2413_64.dll");
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
	}

	public static void main(String[] args) throws InterruptedException, IOException
	{
		//File file=new File("opencv_ffmpeg2413_64.dll");
		//System.load(file.getAbsolutePath());
		
		fen=new Fenetre();
		fen2=new Fenetre2();
		
		bddcreator();

		while(debut==false) {
			try{
				Thread.sleep(500);
			}catch(InterruptedException e){}
		}
		if (fonctionnement=="Vidéo") 
			LectureVideo();
		else if (fonctionnement=="Image")
			LectureImage();
		else if (fonctionnement=="Live")
			LectureLive();

	}

	public static void LectureVideo() throws IOException {

		Mat frame = new Mat();
		VideoCapture camera= new VideoCapture(chemin_video);

		//if(camera.isOpened()) System.out.println("ouvert");

		while (camera.read(frame) && continu==true) {


			img_principale = Mat2bufferedImage(frame);
			nb++;
			matching(frame);

			if (debut==true) {
				fen2.setVisible(true);
				debut=false;
			}

			fen2.repaint();

		}

	}
	public static void LectureLive() throws IOException {

		Mat frame = new Mat();
		VideoCapture camera= new VideoCapture(0);

		//if(camera.isOpened()) System.out.println("ouvert");

		while (camera.read(frame) && continu==true) {


			img_principale = Mat2bufferedImage(frame);
			nb++;
			matching(frame);

			if (debut==true) {
				fen2.setVisible(true);
				debut=false;
			}

			fen2.repaint();

		}

	}

	public static void LectureImage() throws IOException {
		Mat img = Highgui.imread(chemin_image);
		img_principale = Mat2bufferedImage(img);
		matching(img);
		fen2.setVisible(true);
		fen2.repaint();
	}

	
	
	public static BufferedImage Mat2bufferedImage(Mat image) {
		MatOfByte bytemat = new MatOfByte();
		Highgui.imencode(".jpg", image, bytemat);
		byte[] bytes = bytemat.toArray();
		InputStream in = new ByteArrayInputStream(bytes);
		BufferedImage img = null;
		try {
			img = ImageIO.read(in);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return img;
	}




	public static void matching(Mat frame) throws IOException {
		//Ouverture le l'image et saturation des rouges
		//	Mat m=Highgui.imread("p8.jpg",Highgui.CV_LOAD_IMAGE_COLOR);
		//MaBibliothequeTraitementImageEtendue.afficheImage("Imagetestee", frame);
		Mat transformee=MaBibliothequeTraitementImageEtendue.transformeBGRversHSV(frame);
		img_hsv=Mat2bufferedImage(transformee);
		//la methode seuillage est ici extraite de l'archivage jar du meme nom 
		Mat saturee=MaBibliothequeTraitementImage.seuillage(transformee, 6, 170, 110);
		img_seuillage=Mat2bufferedImage(saturee);
		//Mat saturee=MaBibliothequeTraitementImageEtendue.PlusieursSeuils(transformee);
		Mat objetrond = null;

		//Création d'une liste des contours à partir de l'image saturée
		List<MatOfPoint> ListeContours= MaBibliothequeTraitementImageEtendue .ExtractContours(saturee);
		int i=0;
		double [] scores=new double [6];
		for(int j=0;j<scores.length;j++){scores[j]=-1;}
		//Pour tous les contours de la liste
		for (MatOfPoint contour: ListeContours  ){
			i++;
			objetrond=MaBibliothequeTraitementImageEtendue.DetectForm(frame,contour);
			if (objetrond ==null)
				detecte=false;
			else {
				detecte=true;

				//fen2.repaint();
				if(nb>4) {tab=new int [7];}
				nb=0;
				//MaBibliothequeTraitementImage.afficheImage("Objet rond detécté", objetrond);
				//MaBibliothequeTraitementImageEtendue.afficheImage("panneau", objetrond);

				scores[0]=MaBibliothequeTraitementImageEtendue.Similitude(objetrond,0);

				scores[1]=MaBibliothequeTraitementImageEtendue.Similitude(objetrond,1);

				scores[2]=MaBibliothequeTraitementImageEtendue.Similitude(objetrond,2);

				scores[3]=MaBibliothequeTraitementImageEtendue.Similitude(objetrond,3);

				scores[4]=MaBibliothequeTraitementImageEtendue.Similitude(objetrond,4);
				scores[5]=MaBibliothequeTraitementImageEtendue.Similitude(objetrond,5);


				//recherche de l'index du maximum et affichage du panneau detecté
				double scoremax=-1;
				int indexmax=0;
				for(int j=0;j<scores.length;j++){
					if (scores[j]>scoremax){scoremax=scores[j];indexmax=j;}}	
				if(scoremax<30){/*System.out.println("Aucun Panneau reconnu")*/tab[0]++;}

				if(scoremax>=30){switch(indexmax){
				case -1:;break;
				case 0:/*System.out.println("Panneau 30 détécté")*/tab[1]++;break;
				case 1:/*System.out.println("Panneau 50 détécté")*/tab[2]++;;break;
				case 2:/*System.out.println("Panneau 70 détécté")*/tab[3]++;break;
				case 3:/*System.out.println("Panneau 90 détécté")*/tab[4]++;break;
				case 4:/*System.out.println("Panneau 110 détécté")*/tab[5]++;break;
				case 5:/*System.out.println("Panneau interdiction de dépasser détécté")*/tab[6]++;break;
				}
				for(int j=0;j<scores.length;j++){scores[j]=-1;}
				/*
				for(int j=0;j<tab.length;j++){System.out.print(tab[j]+" " );}
				System.out.println("");*/

				double score=-1;
				int indexm = 0;
				for(int j=0;j<tab.length;j++){
					if (tab[j]>score){score=tab[j];indexm=j;}}
				String img_name=null;
				switch(indexm){
				case -1:;break;
				case 0:/* System.out.println("Aucun Panneau reconnu");*/img_name="aucun";break;
				case 1:/*System.out.println("Panneau 30 détécté");*/img_name="ref30";break;
				case 2:/*System.out.println("Panneau 50 détécté");*/img_name="ref50";break;
				case 3:/*System.out.println("Panneau 70 détécté");*/img_name="ref70";break;
				case 4:/*System.out.println("Panneau 90 détécté");*/img_name="ref90";break;
				case 5:/*System.out.println("Panneau 110 détécté");*/img_name="ref110";break;
				case 6:/*System.out.println("Panneau interdiction de dépasser détécté");*/img_name="refdouble";break;
				}
				img_trouve=ImageIO.read(new File(img_name+".jpg"));

				}

			}
		}	

	}

	public static void bddcreator(){
		Mat panneauref = Highgui.imread("ref30.jpg");
		Mat graySign = new Mat(panneauref.rows(), panneauref.cols(), panneauref.type());
		Imgproc.cvtColor(panneauref, graySign, Imgproc.COLOR_BGRA2GRAY);
		Core.normalize(graySign, graySign, 0, 255, Core.NORM_MINMAX);
		FeatureDetector orbDetector = FeatureDetector.create(FeatureDetector.ORB);
		MatOfKeyPoint signKeypoints = new MatOfKeyPoint();
		orbDetector.detect(graySign, signKeypoints);
		bdds.add(signKeypoints);
		DescriptorExtractor orbExtractor = DescriptorExtractor.create(DescriptorExtractor.ORB);
		Mat signDescriptor = new Mat(panneauref.rows(), panneauref.cols(), panneauref.type());
		orbExtractor.compute(graySign, signKeypoints, signDescriptor);
		bddd.add(signDescriptor);

		panneauref = Highgui.imread("ref50.jpg");
		graySign = new Mat(panneauref.rows(), panneauref.cols(), panneauref.type());
		Imgproc.cvtColor(panneauref, graySign, Imgproc.COLOR_BGRA2GRAY);
		Core.normalize(graySign, graySign, 0, 255, Core.NORM_MINMAX);
		orbDetector = FeatureDetector.create(FeatureDetector.ORB);
		signKeypoints = new MatOfKeyPoint();
		orbDetector.detect(graySign, signKeypoints);
		bdds.add(signKeypoints);
		orbExtractor = DescriptorExtractor.create(DescriptorExtractor.ORB);
		signDescriptor = new Mat(panneauref.rows(), panneauref.cols(), panneauref.type());
		orbExtractor.compute(graySign, signKeypoints, signDescriptor);
		bddd.add(signDescriptor);

		panneauref = Highgui.imread("ref70.jpg");
		graySign = new Mat(panneauref.rows(), panneauref.cols(), panneauref.type());
		Imgproc.cvtColor(panneauref, graySign, Imgproc.COLOR_BGRA2GRAY);
		Core.normalize(graySign, graySign, 0, 255, Core.NORM_MINMAX);
		orbDetector = FeatureDetector.create(FeatureDetector.ORB);
		signKeypoints = new MatOfKeyPoint();
		orbDetector.detect(graySign, signKeypoints);
		bdds.add(signKeypoints);
		orbExtractor = DescriptorExtractor.create(DescriptorExtractor.ORB);
		signDescriptor = new Mat(panneauref.rows(), panneauref.cols(), panneauref.type());
		orbExtractor.compute(graySign, signKeypoints, signDescriptor);
		bddd.add(signDescriptor);
		
		panneauref = Highgui.imread("ref90.jpg");
		graySign = new Mat(panneauref.rows(), panneauref.cols(), panneauref.type());
		Imgproc.cvtColor(panneauref, graySign, Imgproc.COLOR_BGRA2GRAY);
		Core.normalize(graySign, graySign, 0, 255, Core.NORM_MINMAX);
		orbDetector = FeatureDetector.create(FeatureDetector.ORB);
		signKeypoints = new MatOfKeyPoint();
		orbDetector.detect(graySign, signKeypoints);
		bdds.add(signKeypoints);
		orbExtractor = DescriptorExtractor.create(DescriptorExtractor.ORB);
		signDescriptor = new Mat(panneauref.rows(), panneauref.cols(), panneauref.type());
		orbExtractor.compute(graySign, signKeypoints, signDescriptor);
		bddd.add(signDescriptor);
		
		panneauref = Highgui.imread("ref110.jpg");
		graySign = new Mat(panneauref.rows(), panneauref.cols(), panneauref.type());
		Imgproc.cvtColor(panneauref, graySign, Imgproc.COLOR_BGRA2GRAY);
		Core.normalize(graySign, graySign, 0, 255, Core.NORM_MINMAX);
		orbDetector = FeatureDetector.create(FeatureDetector.ORB);
		signKeypoints = new MatOfKeyPoint();
		orbDetector.detect(graySign, signKeypoints);
		bdds.add(signKeypoints);
		orbExtractor = DescriptorExtractor.create(DescriptorExtractor.ORB);
		signDescriptor = new Mat(panneauref.rows(), panneauref.cols(), panneauref.type());
		orbExtractor.compute(graySign, signKeypoints, signDescriptor);
		bddd.add(signDescriptor);
		
		panneauref = Highgui.imread("refdouble.jpg");
		graySign = new Mat(panneauref.rows(), panneauref.cols(), panneauref.type());
		Imgproc.cvtColor(panneauref, graySign, Imgproc.COLOR_BGRA2GRAY);
		Core.normalize(graySign, graySign, 0, 255, Core.NORM_MINMAX);
		orbDetector = FeatureDetector.create(FeatureDetector.ORB);
		signKeypoints = new MatOfKeyPoint();
		orbDetector.detect(graySign, signKeypoints);
		bdds.add(signKeypoints);
		orbExtractor = DescriptorExtractor.create(DescriptorExtractor.ORB);
		signDescriptor = new Mat(panneauref.rows(), panneauref.cols(), panneauref.type());
		orbExtractor.compute(graySign, signKeypoints, signDescriptor);
		bddd.add(signDescriptor);
		
		panref=panneauref;
		
	}
}