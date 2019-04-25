import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
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
import org.opencv.xfeatures2d.BriefDescriptorExtractor;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Core;
import org.opencv.features2d.FastFeatureDetector;
import org.opencv.features2d.ORB;
import org.opencv.features2d.BOWImgDescriptorExtractor;
import org.opencv.features2d.DescriptorMatcher;
import org.opencv.features2d.Feature2D;
import org.opencv.features2d.Features2d;

@SuppressWarnings("unused")
public class TravailOussama{
	public static void main( String[] args )
	{

		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		Mat m=Imgcodecs.imread("bgr.png");
		Vector<Mat> channels = new Vector <Mat>();
		Core.split(m, channels);
		
		// mode gris
		/*for (int i=0; i<channels.size();i++) {
			ImShow(Integer.toString(i),channels.get(i));
		}
		*/
		//mode couleurs
		Mat dst= Mat.zeros(m.size(), m.type());
		Vector<Mat> chans = new Vector<Mat>();
		Mat empty = Mat.zeros(m.size(),CvType.CV_8UC1);
		for (int i=0; i<channels.size();i++) {
			ImShow(Integer.toString(i),channels.get(i));
			chans.removeAllElements();
			for(int j=0;j<channels.size();j++) {
				if(j!=i) {
					chans.add(empty);
				}else {chans.add(channels.get(i));}
			}
			Core.merge(chans, dst);
			ImShow(Integer.toString(i),dst);
		}
		// BGR TO HSV 
		BGR_HSV();
		
		// Seuillage 
			// Un seul Seuil
		UnSeuil();
		
			// Plusieurs Seuillage 
		PlusieursSeuils();
		
		// Contours
		Contours_cercles();
		//Reconnaissances cercles rouges
		Reconnaissance_cercles_rouges();
		
		// Extraitre_Balles_rouges
		Extraitre_Balles_rouges();
		
	}

	public static void exo1(){
		System.out.println(Core.NATIVE_LIBRARY_NAME);
		System.loadLibrary( Core.NATIVE_LIBRARY_NAME );
		/*Mat mat = Mat.eye( 3, 3, CvType.CV_8UC3 );
		System.out.println( "mat = " + mat.dump() );
*/
		Mat m=Imgcodecs.imread("opencv.png");
		//System.out.println(m.dump());
		for(int i=0;i<m.height();i++) {
			for(int j=0;j<m.width();j++) {
				double[] BGR=m.get(i, j);
				if (BGR[0]==255 && BGR[1]==255 && BGR[2]==255)
					System.out.print(".");
				else
					System.out.print("+");
			}
			System.out.println();
		}
	}

	public static void ImShow(String title, Mat img) {
		MatOfByte matOfByte = new MatOfByte();
		Imgcodecs.imencode(".png", img, matOfByte);
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

	public static void BGR_HSV() {
		Mat m=Imgcodecs.imread("hsv.png");
		Mat output = Mat.zeros(m.size(), m.type());
		Imgproc.cvtColor(m, output, Imgproc.COLOR_BGR2HSV);
		ImShow("HSV",output);
		Vector<Mat> channels=new Vector<Mat>();
		Core.split(output, channels);
		double hsv_values [][]= {{1,255,255},{19,1,255},{179,0,1}};
		for(int i=0;i<3;i++) {
			ImShow(Integer.toString(i)+"-HSV",channels.get(i));
			Mat chans[]=new Mat[3];
			for(int j=0;j<3;j++) {
				Mat empty = Mat.ones(m.size(), CvType.CV_8UC1);
				Mat comp = Mat.ones(m.size(),CvType.CV_8UC1);
				Scalar v=new Scalar(hsv_values[i][j]) ;
				Core.multiply(empty, v, comp);
				chans[j]=comp;
			}
			chans[i]=channels.get(i);
			Mat dst =Mat.zeros(output.size(),output.type());
			Mat res =Mat.zeros(dst.size(),dst.type());
			Core.merge(Arrays.asList(chans), dst);
			Imgproc.cvtColor(dst, res, Imgproc.COLOR_HSV2BGR);
			ImShow(Integer.toString(i),res);
		}
	}

	public static void UnSeuil() {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		Mat m=Imgcodecs.imread("circles.jpg");
		Mat hsv_image=Mat.zeros(m.size(), m.type());
		Imgproc.cvtColor(m, hsv_image, Imgproc.COLOR_BGR2HSV);
		Mat threshold_img = new Mat();
		Core.inRange(hsv_image, new Scalar(0,100,100), new Scalar(10,255,255), threshold_img);
		Imgproc.GaussianBlur(threshold_img, threshold_img, new Size(9,9), 2,2);
		ImShow("Cercles rouge",threshold_img);
	}

	public static void PlusieursSeuils() {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		Mat m=Imgcodecs.imread("circles.jpg");
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
	
	public static void Contours_cercles() {
		Mat m=Imgcodecs.imread("circles.jpg");
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
	
	public static void Reconnaissance_cercles_rouges() {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		Mat m=Imgcodecs.imread("circles_rectangles.jpg");
		ImShow("Cercles",m);
		Mat hsv_image=Mat.zeros(m.size(), m.type());
		Imgproc.cvtColor(m, hsv_image, Imgproc.COLOR_BGR2HSV);
		ImShow("HSV",hsv_image);
		Mat threshold_img = DetecterCercles(hsv_image);
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
			}
		}
		ImShow("Detection des cercles rouges",m);
	}

	public static void Extraitre_Balles_rouges() {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		Mat m=Imgcodecs.imread("Billard_Balls.jpg");
		ImShow("billards balls",m);
		Mat hsv_image=Mat.zeros(m.size(), m.type());
		Imgproc.cvtColor(m, hsv_image, Imgproc.COLOR_BGR2HSV);
		ImShow("HSV",hsv_image);
		Mat threshold_img = DetecterCercles(hsv_image);
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
				ImShow("Ball",ball);
				
				// Mise a l'echelle
				Mat sroadSign = Imgcodecs.imread("Ball_three.png");
				Mat sObject = new Mat();
				Imgproc.resize(ball, sObject, sroadSign.size());
				Mat grayObject=new Mat(sObject.rows(),sObject.cols(),sObject.type());
				Imgproc.cvtColor(sObject, grayObject, Imgproc.COLOR_BGRA2GRAY);
				Core.normalize(grayObject, grayObject,0,255,Core.NORM_MINMAX);
				Mat graySign=new Mat(sroadSign.rows(),sroadSign.cols(),sroadSign.type());
				Imgproc.cvtColor(sroadSign, graySign, Imgproc.COLOR_BGRA2GRAY);
				Core.normalize(graySign, graySign,0,255,Core.NORM_MINMAX);
				ImShow("resize",sObject);
				
				//Extraction des descripteurs et keypoints
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
				ImShow("matching",matchedImage);
			}
		}
		
		
	}
	
}
