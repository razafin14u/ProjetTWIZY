import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfDMatch;
import org.opencv.core.MatOfInt4;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.features2d.DMatch;
import org.opencv.features2d.DescriptorExtractor;
import org.opencv.features2d.DescriptorMatcher;
import org.opencv.features2d.FeatureDetector;
import org.opencv.features2d.Features2d;
import org.opencv.highgui.Highgui;

import org.opencv.imgproc.Imgproc;

public class MaBibliothequeTraitementImageEtendue {
	
	static Rect rect;
	//Contient toutes les méthodes necessaires à la transformation des images
	static {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
	}

	//Methode qui permet de transformer une matrice intialement au  format BGR au format HSV
	public static Mat transformeBGRversHSV(Mat matriceBGR){
		Mat matriceHSV=new Mat(matriceBGR.height(),matriceBGR.cols(),matriceBGR.type());
		Imgproc.cvtColor(matriceBGR,matriceHSV,Imgproc.COLOR_BGR2HSV);
		return matriceHSV;

	}

	//Methode qui convertit une matrice avec 3 canaux en un vecteur de 3 matrices monocanal (un canal par couleur)
	public static Vector<Mat> splitHSVChannels(Mat input) {
		Vector<Mat> channels = new Vector<Mat>(); 
		Core.split(input, channels);
		return channels;
	}

	//Methode qui permet d'afficher une image sur un panel
	public static void afficheImage(String title, Mat img){
		
		MatOfByte matOfByte=new MatOfByte();
		Highgui.imencode(".png",img,matOfByte);
		byte[] byteArray=matOfByte.toArray();
		BufferedImage bufImage=null;
		try{
			InputStream in=new ByteArrayInputStream(byteArray);
			bufImage=ImageIO.read(in);
			JFrame frame=new JFrame();
			frame.setTitle(title);
			frame.getContentPane().add(new JLabel(new ImageIcon(bufImage)));
			frame.pack();
			frame.setVisible(true);

		}
		catch(Exception e){
			e.printStackTrace();
		}


	}




	//Methode qui permet d'extraire les contours d'une image donnee
	public static List<MatOfPoint> ExtractContours(Mat input) {
		//System.load("opencv-249");
		// Detecter les contours des formes trouvées
		
		int thresh = 100;
		Mat canny_output = new Mat();
		List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
		MatOfInt4 hierarchy = new MatOfInt4();
		Imgproc.Canny( input, canny_output, thresh, thresh*2);


		/// Find extreme outer contours
		Imgproc.findContours( canny_output, contours, hierarchy,Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

		Mat drawing = Mat.zeros( canny_output.size(), CvType.CV_8UC3 );
		Random rand = new Random();
		for( int i = 0; i< contours.size(); i++ )
		{
			Scalar color = new Scalar( rand.nextInt(255 - 0 + 1) , rand.nextInt(255 - 0 + 1),rand.nextInt(255 - 0 + 1) );
			Imgproc.drawContours( drawing, contours, i, color, 1, 8, hierarchy, 0, new Point() );
		}
		//afficheImage("Contours",drawing);

		return contours;
	}

	//Methode qui permet de decouper et identifier les contours carrés, triangulaires ou rectangulaires. 
	//Renvoie null si aucun contour rond n'a été trouvé.	
	//Renvoie une matrice carrée englobant un contour rond si un contour rond a été trouvé


	public static double angle(Point a, Point b, Point c) {
		Point ab = new Point( b.x - a.x, b.y - a.y );
		Point cb = new Point( b.x - c.x, b.y - c.y );
		double dot = (ab.x * cb.x + ab.y * cb.y); // dot product
		double cross = (ab.x * cb.y - ab.y * cb.x); // cross product
		double alpha = Math.atan2(cross, dot);
		return Math.floor(alpha * 180. / Math.PI + 0.5);
	}

	public static Mat DetectForm(Mat img,MatOfPoint contour) {

		MatOfPoint2f matOfPoint2f = new MatOfPoint2f();
		MatOfPoint2f approxCurve = new MatOfPoint2f();
		float[] radius = new float[1];

		Point center = new Point();
		rect = Imgproc.boundingRect(contour);
		double contourArea = Imgproc.contourArea(contour);


		matOfPoint2f.fromList(contour.toList());
		// Cherche le plus petit cercle entourant le contour
		Imgproc.minEnclosingCircle(matOfPoint2f, center, radius);

		//System.out.println(contourArea+" "+Math.PI*radius[0]*radius[0]);
		//on dit que c'est un cercle si l'aire occupé par le contour est à supérieure à  80% de l'aire occupée par un cercle parfait
		if ((contourArea / (Math.PI*radius[0]*radius[0])) >=0.82 && radius[0]>15 ) {
		//	System.out.println("Cercle");
		//	System.out.println(radius[0]);
			

			
			Core.circle(img, center, (int)radius[0], new Scalar(255, 0, 0), 2);
			Core.rectangle(img, new Point(rect.x,rect.y), new Point(rect.x+rect.width,rect.y+rect.height), new Scalar (0, 255, 0), 2);
			Mat tmp = img.submat(rect.y,rect.y+rect.height,rect.x,rect.x+rect.width);
			Mat sign = Mat.zeros(tmp.size(),tmp.type());
			tmp.copyTo(sign);
			return sign;
		}else {

			Imgproc.approxPolyDP(matOfPoint2f, approxCurve, Imgproc.arcLength(matOfPoint2f, true) * 0.02, true);
			long total = approxCurve.total();
			if (total == 3 ) { // is triangle
				//System.out.println("Triangle");
				Point [] pt = approxCurve.toArray();
				Core.line(img, pt[0], pt[1], new Scalar(255,0,0),2);
				Core.line(img, pt[1], pt[2], new Scalar(255,0,0),2);
				Core.line(img, pt[2], pt[0], new Scalar(255,0,0),2);
				Core.rectangle(img, new Point(rect.x,rect.y), new Point(rect.x+rect.width,rect.y+rect.height), new Scalar (0, 255, 0), 2);
				Mat tmp = img.submat(rect.y,rect.y+rect.height,rect.x,rect.x+rect.width);
				Mat sign = Mat.zeros(tmp.size(),tmp.type());
				tmp.copyTo(sign);
				return null;
			}
			if (total >= 4 && total <= 6) {
				List<Double> cos = new ArrayList<>();
				Point[] points = approxCurve.toArray();
				for (int j = 2; j < total + 1; j++) {
					cos.add(angle(points[(int) (j % total)], points[j - 2], points[j - 1]));
				}
				Collections.sort(cos);
				Double minCos = cos.get(0);
				Double maxCos = cos.get(cos.size() - 1);
				boolean isRect = total == 4 && minCos >= -0.1 && maxCos <= 0.3;
				boolean isPolygon = (total == 5 && minCos >= -0.34 && maxCos <= -0.27) || (total == 6 && minCos >= -0.55 && maxCos <= -0.45);
				if (isRect) {
					double ratio = Math.abs(1 - (double) rect.width / rect.height);
					//drawText(rect.tl(), ratio <= 0.02 ? "SQU" : "RECT");
					//System.out.println("Rectangle");
					Core.rectangle(img, new Point(rect.x,rect.y), new Point(rect.x+rect.width,rect.y+rect.height), new Scalar (0, 255, 0), 2);
					Mat tmp = img.submat(rect.y,rect.y+rect.height,rect.x,rect.x+rect.width);
					Mat sign = Mat.zeros(tmp.size(),tmp.type());
					tmp.copyTo(sign);
					return null;
				}
				if (isPolygon) {
					//System.out.println("Polygon");
					//drawText(rect.tl(), "Polygon");
				}
			}			
		}
		return null;

	}



	public static double Similitude(Mat object,int index) {

		// Conversion du signe de reference en niveaux de gris et normalisation
		Mat panneauref = Principale.panref;
		Mat graySign = new Mat(panneauref.rows(), panneauref.cols(), panneauref.type());
		


		//Conversion du panneau extrait de l'image en gris et normalisation et redimensionnement � la taille du panneau de r�ference
		Mat grayObject = new Mat(panneauref.rows(), panneauref.cols(), panneauref.type());
		Imgproc.resize(object, object, graySign.size());
		
		/*test match NB*/
		/*Mat objectNB =Match.convertNB(object);
		Biblio.afficheImage("Objet rond NB", objectNB);*/
		
		//afficheImage("Panneau extrait de l'image",object);
		
		Principale.img_extraite=Principale.Mat2bufferedImage(object); //on récupère l'image extraite resize
		
		Imgproc.cvtColor(object, grayObject, Imgproc.COLOR_BGRA2GRAY);
		Core.normalize(grayObject, grayObject, 0, 255, Core.NORM_MINMAX);
		Imgproc.resize(grayObject, grayObject, graySign.size());	
		
		
		/*Extraction des caract�eristiques*/
		FeatureDetector orbDetector = FeatureDetector.create(FeatureDetector.ORB);
		DescriptorExtractor orbExtractor = DescriptorExtractor.create(DescriptorExtractor.ORB);

		MatOfKeyPoint objectKeypoints = new MatOfKeyPoint();
		orbDetector.detect(grayObject, objectKeypoints);

		MatOfKeyPoint signKeypoints = Principale.bdds.get(index);

		Mat objectDescriptor = new Mat(graySign.rows(), graySign.cols(), graySign.type());
		orbExtractor.compute(grayObject, objectKeypoints, objectDescriptor); //COMPUTE
		//System.out.println(objectDescriptor);

		Mat signDescriptor = Principale.bddd.get(index);

		/*Matching*/
		MatOfDMatch matchs = new MatOfDMatch();
		DescriptorMatcher matcher = DescriptorMatcher.create(DescriptorMatcher.BRUTEFORCE_L1);
		matcher.match(objectDescriptor,  signDescriptor, matchs);
		Mat matchedImage =new Mat(panneauref.rows(),panneauref.cols()*2,panneauref.type());
		Features2d.drawMatches(object, objectKeypoints, panneauref, signKeypoints, matchs,matchedImage);
		//afficheImage("matching",matchedImage);
		//System.out.println(matchs.dump());
		int score=0;	//moyenne de match
		double dmin=1250;
		for (int i = 0; i < matchs.height(); i++) {
			double x=matchs.get(i, 0)[3];
			if(x<=dmin ){
				score++;
			}
		}
		
		//System.out.println(score);
		return score;
		

	}
	
	public static Mat PlusieursSeuils(Mat m) {


		Mat threshold_img1 = new Mat();
		Mat threshold_img2 = new Mat();
		Mat threshold_img = new Mat();
		Core.inRange(m, new Scalar(0,100,100), new Scalar(10,255,255), threshold_img1);
		Core.inRange(m, new Scalar(160,100,100), new Scalar(179,255,255), threshold_img2);
		Core.bitwise_or(threshold_img1,threshold_img2,threshold_img);
		Imgproc.GaussianBlur(threshold_img, threshold_img, new Size(9,9), 2,2);
		return threshold_img;
	}

}

