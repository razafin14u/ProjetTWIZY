import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;

public class Exercice2
{
	public static void main( String[] args )
	{

		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		Mat m=Imgcodecs.imread("bgr.png");
		Vector<Mat> channels = new Vector <Mat>();
		Core.split(m, channels);
		
		for (int i=0; i<channels.size();i++) {
			ImShow(Integer.toString(i),channels.get(i));
		}
	}

	public static void exo1(){
		System.out.println(Core.NATIVE_LIBRARY_NAME);
		System.loadLibrary( Core.NATIVE_LIBRARY_NAME );
		Mat mat = Mat.eye( 3, 3, CvType.CV_8UC3 );
		System.out.println( "mat = " + mat.dump() );

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
}