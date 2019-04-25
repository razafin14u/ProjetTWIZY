import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

public class Exercice1 {
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println(Core.NATIVE_LIBRARY_NAME);
		System.loadLibrary( Core.NATIVE_LIBRARY_NAME );
		Mat mat = Mat.eye( 3, 3, CvType.CV_8UC3 );
		System.out.println( "mat = " + mat.dump() );

		Mat m=Imgcodecs.imread("opencv.png");
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

	
}
