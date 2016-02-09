package rainbow;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.codec.binary.Hex;

public class TimeTest {

	public static void main(String[] args) {
		long starttime = System.currentTimeMillis();
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("SHA-1");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		String digest;
		long j=0;
	    for (long i=0; i<(1024*1024*8); i++) {
	    	md.reset();
			md.update(BigInteger.valueOf(i).toByteArray());
			digest = new String( Hex.encodeHex( md.digest() ) );
			String d0 = new String(digest.substring(0, 8));
			long ld0 = Long.parseLong(d0, 16);
			j += ld0;
	    }
	    
	    System.out.println(j);
	    long endtime = System.currentTimeMillis();
	    System.out.println("Time taken: " + (endtime-starttime)/1000.00);
	}

}
