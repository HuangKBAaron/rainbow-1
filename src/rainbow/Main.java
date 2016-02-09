package rainbow;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

public class Main {

	public static void main(String[] args) throws NoSuchAlgorithmException, IOException {
		
		int chainLength = 196; //based on experimentation to calibrate C, S and t
		int numOfFiles = 1; //keep it as 1
		int extra = 0; //can increase this for accuracy
		RainbowTable rainbowtable = null;
		
		for(int i=1; i<=numOfFiles; i++) {
			rainbowtable = new RainbowTable(chainLength, i, extra + (1024*1024*16)/chainLength); //rows
			rainbowtable.solve();
		}
		
		Invert invert = new Invert(chainLength, numOfFiles);
		invert.solve();
		
	}

}
