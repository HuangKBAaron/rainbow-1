package rainbow;
/* Ignore this file - it just has code I used for testing small functions */
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;

public class Test {

	public static void main(String[] args) throws NoSuchAlgorithmException {
		
		
		MessageDigest md = null;
		
		md = MessageDigest.getInstance("SHA-1");
		md.reset();
		//System.out.println(DigestUtils.shaHex("153707832").toUpperCase());
		
		md.reset();
		byte[] b = intToByteArray(1234);
		//byte[] b = ByteBuffer.allocate(3).putInt(1234).array();
		//byte[] b = BigInteger.valueOf(1234).toByteArray();
		md.update(b);
		String digest = new String( Hex.encodeHex( md.digest() ) );
		System.out.println(digest.toUpperCase());
		
		
		md.reset();
		byte[] result = md.digest("1234".getBytes());
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < result.length; i++) {
            sb.append(Integer.toString((result[i] & 0xff) + 0x100, 16).substring(1));
        }
		
		
		
		
		long x = 45;
		String y = Long.toHexString(x).toUpperCase();
		//System.out.println(y);
		
		//String d = "EE16C39B";
		//long i = Long.parseLong(d, 16); i = i%256;
		//System.out.println(String.valueOf(i));
		
		//String d0 = new String(digest.substring(0, 8));
		//String d1 = new String(digest.substring(8, 16));
		//String d2 = new String(digest.substring(16, 24));
		//String d3 = new String(digest.substring(24, 32));
		//String d4 = new String(digest.substring(32, 40));
		
		//System.out.println(sb.toString().toUpperCase());
		
		//System.out.println(d0);
		//System.out.println(d1);
		//System.out.println(d2);
		//System.out.println(d3);
		//System.out.println(d4);
		
		/*long i;
		long range = 1024*1024*8;
		
		for(i=0; i < range; i++) {
			md.reset();
			
			md.update(BigInteger.valueOf(i).toByteArray());
			String digest = new String( Hex.encodeHex( md.digest() ) );

			if(digest.equals("E63B2BAFAAB041E3F46165AE557ACB0234192D2A".toLowerCase())) {
				System.out.println("FOUND with i:" + i);
				break;
			}
			//if(i%100000==0) System.out.println("Gone past i="+i);
		}
		System.out.println("Completed with i: " + range);*/


	}

	public static byte[] intToByteArray(int value) {
	    byte b1 = (byte) (value >> 16);
	    byte b2 = (byte) (value >> 8);
	    byte b3 = (byte) value;
		return new byte[] { b1, b2, b3
	            /*(byte)(value >>> 16),
	            (byte)(value >>> 8),
	            (byte)value */};
	}
	
}

/*String reduced = new String(digest.substring(0,6)); //get most significant 24 bits = 3 bytes 
int ired = Integer.parseInt(reduced, 16); 
ired = (ired + i) % (16777216);
return intToByteArray(ired);*/
