package rainbow;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;
import javax.xml.bind.DatatypeConverter;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;


import org.apache.commons.codec.binary.Hex;

public class RainbowTable {
	
	LinkedHashMap<String, String> table;
	ArrayList<String> hashlist;
	MessageDigest md = null;
	byte bytearray[]  = null;
	byte word[] = null;
	int chainLength;
	int numOfChains;
	String outfile;
	FileOutputStream fos;
	BufferedWriter bw;
	String inputlocation;
	int filenum;
	int rows;
	long currenttime;
	
	
	public RainbowTable(int clength, int fileno, int numOfRows) throws FileNotFoundException {
		filenum = fileno;
		rows = numOfRows;
		outfile = new String("C:\\Users\\user\\Desktop\\rainbow\\tables\\RainbowTable" + filenum + ".txt");
		inputlocation = new String("C:\\Users\\user\\Desktop\\rainbow\\SAMPLE_INPUT.data");
		hashlist = new ArrayList();
		chainLength = clength;
		numOfChains = (16*1024*1024)/chainLength;
		table = new LinkedHashMap();
		try {
			md = MessageDigest.getInstance("SHA-1");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		String digest;
		currenttime = System.currentTimeMillis();
	}
	
	byte[] Hash(byte[] message) {
		md.reset();
		return md.digest(message);
	}
	
	byte[] Reduce(String digest, int i) {
		String reduced = new String(digest.substring(0,2)); //get most significant 8 bits 
		int ired = Integer.parseInt(reduced, 16); 
		ired = (ired + i) % (256);
		String temp = new String(Integer.toHexString(ired) + digest.substring(2,4) + digest.substring(4,6));
		return intToByteArray(Integer.parseInt(temp, 16));
	}
	
	/* adapted a modified version of this function from http://stackoverflow.com/questions/1936857/convert-integer-into-byte-array-java */
	public byte[] intToByteArray(int value) {
	    return new byte[] {
	            (byte)(value >> 16),
	            (byte)(value >> 8),
	            (byte)value}; 
		//return BigInteger.valueOf(value).toByteArray();
	}
	
	/* adapted a modified version of this function from http://www.java2s.com/Code/Java/Data-Type/hexStringToByteArray.htm */
	public static byte[] hexStringToByteArray(String s) {
		
		byte[] b = new byte[s.length() / 2];
	    for (int i = 0; i < b.length; i++) {
	      int index = i * 2;
	      int v = Integer.parseInt(s.substring(index, index + 2), 16);
	      b[i] = (byte) v;
	    }
	    return b;
	}
	
	/* adapted a modified version of this function from http://stackoverflow.com/questions/5399798/byte-array-and-int-conversion-in-java */
	int byteArrayToInt(byte[] b) 
	{
	    int value = 0;
	    for (int i = 0; i < 3; i++) {
	        int shift = (3 - 1 - i) * 8;
	        value += (b[i] & 0x000000FF) << shift;
	    }
	    return value; 
		//return (new BigInteger(b).intValue());
	}
	
	public void buildT() throws IOException {
			
		int message; 
		int size = 0;
		
		outerloop:
		for( message = 1; table.size()<rows;  ) {
			//build chain
			
			String hash;
			bytearray = intToByteArray(message);
			
			for(int i=1; i<=chainLength; i++) {
				
				word = Hash(bytearray);
				hash = DatatypeConverter.printHexBinary(word);  
				bytearray = Reduce(hash, i);
			}
			
			if(table.containsKey(DatatypeConverter.printHexBinary(bytearray))) message++;
			else {
				table.put(DatatypeConverter.printHexBinary(bytearray), Integer.toString(message));	
				message = byteArrayToInt(bytearray); //to ensure we only use words which aren't already in the chain
				//System.out.println("Table size: " + table.size());
			}
		}
		
	}

	public void solve() throws IOException {
		
		buildT();
		
		fos = new FileOutputStream(new File(outfile));
		bw = new BufferedWriter(new OutputStreamWriter(fos));
		
		int i = 0;
		for(String end:table.keySet())
		{
			i++;
			//System.out.println(i + " : " + end + " " + table.get(end));
			bw.write(end + " " + table.get(end)); bw.newLine();
		}
    	bw.close();
	}

}
