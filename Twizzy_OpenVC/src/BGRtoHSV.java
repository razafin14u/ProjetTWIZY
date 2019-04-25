import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

public class BGRtoHSV {

	public static void main(String[] args) {
		System.out.println(Core.NATIVE_LIBRARY_NAME);
		System.loadLibrary( Core.NATIVE_LIBRARY_NAME );
		// TODO Auto-generated method stub
		Mat m = Imgcodecs.imread("hsv.png");
		Mat output=Mat.zeros(m.size(), m.type());
		Imgproc.cvtColor(m,output,Imgproc.COLOR_BGR2HSV);
		ImShow("HSV",output);
		Vector<Mat> channels = new Vector<Mat>();
		Core.split(output, channels);
		double hsv_values[][] = {{1,255,255},{179,1,255},{179,0,1}};
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

}
