import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfDMatch;
import org.opencv.core.MatOfInt4;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;
import org.opencv.xfeatures2d.BriefDescriptorExtractor;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Core;
import org.opencv.features2d.DescriptorMatcher;
import org.opencv.features2d.Features2d;
import org.opencv.features2d.ORB;


public class Principale {

	public static void main( String[] args )
	{

		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		Mat m=Imgcodecs.imread("s_p3.jpg.png");
		Vector<Mat> channels = new Vector <Mat>();
		Core.split(m, channels);
		
		/*for (int i=0; i<channels.size();i++) {
			ImShow(Integer.toString(i),channels.get(i));
		}*/
		
		ImShow("Image couleur",m);
		Mat m_coul = NoirBlanc(m);
		ImShow("Image noir et blanc",m_coul);
		
		
		/*Contours_cercles(m);
		Matching(m);
		DetecterCercles(m);
		Mat sroadSign1 =Imgcodecs.imread("ref90.jpg");
		Mat sroadSign2 =Imgcodecs.imread("ref70.jpg");
		Mat sroadSign3 =Imgcodecs.imread("ref110.jpg");
		double test1 = Similitude(m,sroadSign1);
		double test2 = Similitude(m,sroadSign2);
		double test3 = Similitude(m,sroadSign3);
		System.out.println(" le score pour 90 est : \n"+test1);
		System.out.println(" le score pour 70 est : \n"+test2);
		System.out.println(" le score pour 110 est : \n"+test3);*/
		
		//UnSeuil(m);
		
		//PlusieursSeuils(m);
		
		/*JFrame jframe = new JFrame("Detection de panneaux sur un flux vidéo");
		jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JLabel vidpanel = new JLabel();
		jframe.setContentPane(vidpanel);
		jframe.setSize(720, 480);
		jframe.setVisible(true);

		Mat frame = new Mat();
		VideoCapture camera = new VideoCapture("video1.avi");
		Mat PanneauAAnalyser = null;

		while (camera.read(m)) {
		//A completer


		ImageIcon image = new ImageIcon(Mat2bufferedImage(m));
		vidpanel.setIcon(image);
		vidpanel.repaint();*/
	
		//LectureVideo();
		
	}

//lecture vidéo
	public static BufferedImage Mat2bufferedImage(Mat image) {
		MatOfByte bytemat = new MatOfByte();
		Imgcodecs.imencode(".jpg", image, bytemat);
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
	public static void LectureVideo() {
		JFrame jframe = new JFrame("Detection de panneaux sur un flux vidéo");
		jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JLabel vidpanel = new JLabel();
		jframe.setContentPane(vidpanel);
		jframe.setSize(720, 480);
		jframe.setVisible(true);
		Mat frame = new Mat();
		VideoCapture camera = new VideoCapture("video1.avi");
		Mat PanneauAAnalyser = null;
		
		while (camera.read(frame)) {
		
		//A completer
		ImageIcon image = new ImageIcon(Mat2bufferedImage(frame));
		vidpanel.setIcon(image);
		DetecterCercles(frame);
		Matching(frame);
		vidpanel.repaint();
		
		}	
	}
	
public static Mat NoirBlanc(Mat im_coul) {
	//Vector<Mat> channels = new Vector <Mat>();
	//Core.split(im_coul, channels);
	//im_coul=channels.get(0);
	Mat threshold_img = DetecterCercles(im_coul);
	ImShow("Seuillage",threshold_img);
	List <MatOfPoint> contours = DetecterContours(threshold_img);
	
	MatOfPoint2f matOfPoint2f = new MatOfPoint2f();
	float[] radius = new float [1];
	Point center = new Point();
	for(int c=0;c<contours.size();c++) {
		MatOfPoint contour = contours.get(c);
		double contourArea= Imgproc.contourArea(contour);
		matOfPoint2f.fromList(contour.toList());
		Imgproc.minEnclosingCircle(matOfPoint2f, center, radius);
		if((contourArea/(Math.PI*radius[0]*radius[0]))>=0.8) {
			Imgproc.circle(im_coul,center,(int)radius[0],new Scalar(0,255,0),2);
			Rect rect = Imgproc.boundingRect(contour);
			Imgproc.rectangle(im_coul, new Point(rect.x,rect.y), new Point(rect.x+rect.width,rect.y+rect.height),new Scalar (0,255,0),2);
			Mat tmp=im_coul.submat(rect.y,rect.y+rect.height,rect.x,rect.x+rect.width);
			Mat ball= Mat.zeros(tmp.size(), tmp.type());
			tmp.copyTo(ball);
			ImShow("Ball",ball);
			
			// Mise a l'echelle
			Mat sroadSign = Imgcodecs.imread("s_p3.jpg.png");
			Mat sObject = new Mat();
			Imgproc.resize(ball, sObject, sroadSign.size());
			Mat grayObject=new Mat(sObject.rows(),sObject.cols(),sObject.type());
			Imgproc.cvtColor(sObject, grayObject, Imgproc.COLOR_BGRA2GRAY);
			Core.normalize(grayObject, grayObject,0,255,Core.NORM_MINMAX);
			Mat graySign=new Mat(sroadSign.rows(),sroadSign.cols(),sroadSign.type());
			Imgproc.cvtColor(sroadSign, graySign, Imgproc.COLOR_BGRA2GRAY);
			Core.normalize(graySign, graySign,0,255,Core.NORM_MINMAX);
			ImShow("resize",sObject);
		}
	}
		
	UnSeuil(im_coul);
	return im_coul;
}

public static void Matching(Mat m) {
	System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
	//Mat m=Imgcodecs.imread("Billard_Balls.jpg");
	ImShow("panneau",m);
	Mat hsv_image=Mat.zeros(m.size(), m.type());
	Imgproc.cvtColor(m, hsv_image, Imgproc.COLOR_BGR2HSV);
	//ImShow("HSV",hsv_image);
	Mat threshold_img = DetecterCercles(hsv_image);
	DetecterContours(hsv_image);
	ImShow("Seuillage",threshold_img);
	List <MatOfPoint> contours = DetecterContours(threshold_img);
	MatOfPoint2f matOfPoint2f = new MatOfPoint2f();
	float[] radius = new float [1];
	Point center = new Point();
	
	for(int c=0;c<contours.size();c++) {
		MatOfPoint contour = contours.get(c);
		double contourArea= Imgproc.contourArea(contour);
		matOfPoint2f.fromList(contour.toList());
		Imgproc.minEnclosingCircle(matOfPoint2f, center, radius);
		if((contourArea/(Math.PI*radius[0]*radius[0]))>=0.8) {
			Imgproc.circle(m,center,(int)radius[0],new Scalar(0,255,0),2);
			Rect rect = Imgproc.boundingRect(contour);
			Imgproc.rectangle(m, new Point(rect.x,rect.y), new Point(rect.x+rect.width,rect.y+rect.height),new Scalar (0,255,0),2);
			Mat tmp=m.submat(rect.y,rect.y+rect.height,rect.x,rect.x+rect.width);
			Mat ball= Mat.zeros(tmp.size(), tmp.type());
			tmp.copyTo(ball);
			//ImShow("Ball",ball);
			
			//Mise à l'échelle
			double score =0;
			double [] scores = new double[6];
			int indiceM=0;
			Mat sroadSign;
			Mat sroadSign_detecte;
			Mat sroadSign30 =Imgcodecs.imread("ref30.jpg");
			Mat sroadSign50 =Imgcodecs.imread("ref50.jpg");
			Mat sroadSign70 =Imgcodecs.imread("ref70.jpg");
			Mat sroadSign90 =Imgcodecs.imread("ref90.jpg");
			Mat sroadSign110 =Imgcodecs.imread("ref110.jpg");
			Mat sroadSigndouble =Imgcodecs.imread("refdouble.jpg");
			Mat [] matrices_ref = {sroadSign30,sroadSign50,sroadSign70,sroadSign90,sroadSign110,sroadSigndouble};
			
			for (int i=0;  i<6; i++) {
				sroadSign=matrices_ref[i];
				Mat sObject =new Mat();
				Imgproc.resize(ball, sObject, sroadSign.size());
				Mat grayObject=new Mat(sObject.rows(),sObject.cols(),sObject.type());
				Imgproc.cvtColor(sObject, grayObject, Imgproc.COLOR_BGRA2GRAY);
				Core.normalize(grayObject, grayObject,0,255,Core.NORM_MINMAX);
				Mat graySign=new Mat(sroadSign.rows(),sroadSign.cols(),sroadSign.type());
				Imgproc.cvtColor(sroadSign, graySign, Imgproc.COLOR_BGRA2GRAY);
				Core.normalize(graySign, graySign,0,255,Core.NORM_MINMAX);
				
				
				//ImShow("resize",sObject);
				
				//extraction des descripteurs et keypoints
				ORB detector = ORB.create();
				BriefDescriptorExtractor extractor=BriefDescriptorExtractor.create();
				MatOfKeyPoint objectKeypoints=new MatOfKeyPoint();
				detector.detect(grayObject, objectKeypoints);
				MatOfKeyPoint signKeypoints=new MatOfKeyPoint();
				detector.detect(graySign, signKeypoints);
				Mat objectDescriptor = new Mat(ball.rows(),ball.cols(),ball.type());
				extractor.compute(grayObject, objectKeypoints, objectDescriptor);
				Mat signDescriptor = new Mat(sroadSign.rows(),sroadSign.cols(),sroadSign.type());
				extractor.compute(graySign, signKeypoints, signDescriptor);
				
				//Faire le matching
				MatOfDMatch matchs=new MatOfDMatch();
				DescriptorMatcher matcher=DescriptorMatcher.create(DescriptorMatcher.BRUTEFORCE);
				matcher.match(objectDescriptor, signDescriptor, matchs);
				System.out.println(matchs.dump());
				Mat matchedImage =new Mat(sroadSign.rows(),sroadSign.cols()*2,sroadSign.type());
				Features2d.drawMatches(sObject,objectKeypoints,sroadSign,signKeypoints,matchs,matchedImage);
				score=Similitude(sroadSign,sObject);
				scores[i]=score;
				ImShow("matching",matchedImage);
			}
			

			
		}
	}
	
}
	public static void ImShow(String title, Mat img) {
		MatOfByte matOfByte = new MatOfByte();
		Imgcodecs.imencode(".jpg", img, matOfByte);
		byte[] byteArray = matOfByte.toArray();
		BufferedImage bufImage = null;

		try {
			InputStream in = new ByteArrayInputStream(byteArray);
			bufImage = ImageIO.read(in);
			JFrame frame = new JFrame();
			frame.setTitle(title);
			frame.getContentPane().add(new JLabel(new ImageIcon(bufImage)));
			frame.pack();
			frame.setVisible(true);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	

public static Mat DetecterCercles(Mat hsv_image) {
	Mat threshold_img1 = new Mat();
	Mat threshold_img2 = new Mat();
	Mat threshold_img = new Mat();
	Core.inRange(hsv_image, new Scalar(0,100,100), new Scalar(10,255,255), threshold_img1);
	Core.inRange(hsv_image, new Scalar(160,100,100), new Scalar(179,255,255), threshold_img2);
	Core.bitwise_or(threshold_img1,threshold_img2,threshold_img);
	Imgproc.GaussianBlur(threshold_img, threshold_img, new Size(9,9), 2,2);
	return threshold_img;
	
}


public static void Contours_cercles(Mat m) {
	//Mat m;
	ImShow("Cercles",m);
	Mat hsv_image = Mat.zeros(m.size(), m.type());
	Imgproc.cvtColor(m, hsv_image, Imgproc.COLOR_BGR2HSV);
	ImShow("HSV",hsv_image);
	Mat threshold_img = DetecterCercles(hsv_image);
	ImShow("Seuillage",threshold_img);
	int thresh =100;
	Mat canny_output = new Mat();
	List<MatOfPoint> contours= new ArrayList<MatOfPoint>();
	MatOfInt4 hierarchy = new MatOfInt4();
	Imgproc.Canny(threshold_img,canny_output,thresh,thresh*2);
	Imgproc.findContours(canny_output, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
	Mat drawing = Mat.zeros(canny_output.size(),CvType.CV_8UC3);
	Random rand = new Random();
	for (int i=0;i<contours.size();i++) {
		Scalar color = new Scalar ( rand.nextInt(255-0+1),rand.nextInt(255-0+1),rand.nextInt(255-0+1));
		Imgproc.drawContours(drawing,contours,i,color,1,8,hierarchy,0,new Point());
	}
	ImShow("Contours",drawing);
	System.out.println(m);
	
}

public static List<MatOfPoint> DetecterContours(Mat threshold_img) {

	int thresh =100;
	Mat canny_output = new Mat();
	List<MatOfPoint> contours= new ArrayList<MatOfPoint>();
	MatOfInt4 hierarchy = new MatOfInt4();
	Imgproc.Canny(threshold_img,canny_output,thresh,thresh*2);
	Imgproc.findContours(canny_output, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
	Mat drawing = Mat.zeros(canny_output.size(),CvType.CV_8UC3);
	Random rand = new Random();
	for (int i=0;i<contours.size();i++) {
		Scalar color = new Scalar ( rand.nextInt(255-0+1),rand.nextInt(255-0+1),rand.nextInt(255-0+1));
		Imgproc.drawContours(drawing,contours,i,color,1,8,hierarchy,0,new Point());
	}
	return contours;
	
}


public static void UnSeuil(Mat m) {
	System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
	//Mat m=Imgcodecs.imread("s_p1.jpg.png");
	Mat hsv_image=Mat.zeros(m.size(), m.type());
	Imgproc.cvtColor(m, hsv_image, Imgproc.COLOR_BGR2HSV);
	Mat threshold_img = new Mat();
	Core.inRange(hsv_image, new Scalar(0,100,100), new Scalar(100,255,255), threshold_img);
	Imgproc.GaussianBlur(threshold_img, threshold_img, new Size(9,9), 2,2);
	ImShow("Cercles rouge",threshold_img);
}
		

public static void PlusieursSeuils(Mat m) {
	System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
	//Mat m=Imgcodecs.imread("s_p1.jpg.png");
	Mat hsv_image=Mat.zeros(m.size(), m.type());
	Imgproc.cvtColor(m, hsv_image, Imgproc.COLOR_BGR2HSV);
	Mat threshold_img1 = new Mat();
	Mat threshold_img2 = new Mat();
	Mat threshold_img = new Mat();
	Core.inRange(hsv_image, new Scalar(0,100,100), new Scalar(10,255,255), threshold_img1);
	Core.inRange(hsv_image, new Scalar(160,100,100), new Scalar(179,255,255), threshold_img2);
	Core.bitwise_or(threshold_img1,threshold_img2,threshold_img);
	Imgproc.GaussianBlur(threshold_img, threshold_img, new Size(9,9), 2,2);
	ImShow("Cercles rouge",threshold_img);
}

public static double Similitude(Mat object,Mat panneauref) {
	// Conversion du signe de reference en niveaux de gris et normalisation
	Mat graySign = new Mat(panneauref.rows(), panneauref.cols(), panneauref.type());
	Imgproc.cvtColor(panneauref, graySign, Imgproc.COLOR_BGRA2GRAY);
	Core.normalize(graySign, graySign, 0, 255, Core.NORM_MINMAX);
	Mat signeNoirEtBlanc=new Mat();
	
	//Conversion du panneau extrait de l'image en gris et normalisation et redimensionnement � la taille du panneau de r�ference
	Mat grayObject = new Mat(panneauref.rows(), panneauref.cols(), panneauref.type());
	Imgproc.resize(object, object, graySign.size());
	
	/*test match NB*/
	Mat objectNB =NoirBlanc(object);
	//Biblio.afficheImage("Objet rond NB", objectNB);
	
	//afficheImage("Panneau extrait de l'image",object);
	Imgproc.cvtColor(objectNB, grayObject, Imgproc.COLOR_BGRA2GRAY);
	Core.normalize(grayObject, grayObject, 0, 255, Core.NORM_MINMAX);
	Imgproc.resize(grayObject, grayObject, graySign.size());	
	
	
	/*Extraction des caract�eristiques*/
	ORB orbDetector = ORB.create();
	BriefDescriptorExtractor orbExtractor = BriefDescriptorExtractor.create();

	MatOfKeyPoint objectKeypoints = new MatOfKeyPoint();
	orbDetector.detect(grayObject, objectKeypoints);

	MatOfKeyPoint signKeypoints = new MatOfKeyPoint();
	orbDetector.detect(graySign, signKeypoints);

	Mat objectDescriptor = new Mat(graySign.rows(), graySign.cols(), graySign.type());
	orbExtractor.compute(grayObject, objectKeypoints, objectDescriptor);

	Mat signDescriptor = new Mat(panneauref.rows(), panneauref.cols(), panneauref.type());
	orbExtractor.compute(graySign, signKeypoints, signDescriptor);

	/*Matching*/
	MatOfDMatch matchs = new MatOfDMatch();
	DescriptorMatcher matcher = DescriptorMatcher.create(DescriptorMatcher.BRUTEFORCE_L1);
	matcher.match(objectDescriptor,  signDescriptor, matchs);
	//System.out.println(matchs.dump());
	int score=0;	//moyenne de match
	double dmin=1250;
	for (int i = 0; i < matchs.height(); i++) {
		double x=matchs.get(i, 0)[3];
		if(x<=dmin ){
			score++;
		}
	}
	//nb des n meilleur points
	/*int n=100;		
	double moy=0;
	double[] top10 = new double[n];
	for (int i = 0; i < n; i++) {
		top10[i]=matchs.get(i, 0)[3];
	}
	for (int i = n; i < matchs.height(); i++) {
		double x=matchs.get(i, 0)[3];
		if( x < top10[indiceMax(top10)] ){
			top10[indiceMax(top10)]=x;
		}
	}
	for (int i = 0; i < top10.length; i++) {
		moy+=top10[i];
	}
	moy/=n;*/
	Mat matchedImage = new Mat(panneauref.rows(), panneauref.cols()*2, panneauref.type());
	Features2d.drawMatches(object, objectKeypoints, panneauref, signKeypoints, matchs, matchedImage);
	//afficheImage("", matchedImage); // pour afficher les liens entre les references et les images videos
	//System.out.println(score); // score des match dans la console
	return score;
	

}

public static int indiceMax(double[] top ){
	double max=top[0];
	int indice =0;
	for (int i = 0; i < top.length; i++) {
		if ( top[i]>max){
			indice=i;
			max=top[i];
		}
	}
	return indice;
}



}


