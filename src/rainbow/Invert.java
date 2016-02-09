package rainbow;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;
import javax.xml.bind.DatatypeConverter;
import java.util.Scanner;
import java.util.Set;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;

public class Invert {
	
	LinkedHashMap<String, String> tables[];
	ArrayList<Long> messagelist;
	ArrayList<String> hashlist;
	MessageDigest md = null;
	int chainLength;
	int numOfChains;
	String tablelocations[];
	String inputlocation;
	String outputlocation;
	int rows;
	int numberOfFiles;
	
	public Invert(int clength, int numOfFiles) {
		chainLength = clength;
		numberOfFiles = numOfFiles;
		numOfChains = (16*1024*1024)/chainLength;
		tablelocations = new String[numOfFiles+1];
		tables = new LinkedHashMap[numOfFiles+1];
		for(int i=1; i<=numOfFiles; i++) { 
			tablelocations[i] = new String("C:\\Users\\user\\Desktop\\rainbow\\tables\\RainbowTable" + i + ".txt");
			tables[i] = new LinkedHashMap();
		}
		inputlocation = new String("C:\\Users\\user\\Desktop\\rainbow\\SAMPLE_INPUT.data");
		outputlocation = new String("C:\\Users\\user\\Desktop\\rainbow\\SAMPLE_OUTPUT.data");
		messagelist = new ArrayList();
		hashlist = new ArrayList();
		try {
			md = MessageDigest.getInstance("SHA-1");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		String digest;
		rows = 1000;
	}
	
	public void readT() throws IOException {
		
		BufferedReader br; int size;
		br = new BufferedReader(new FileReader(inputlocation));
		size = 0;
	    for(String line; (line = br.readLine()) != null; ) {
	    	if(line.trim().equals("")) continue;
	    	Scanner sc = new Scanner(line);
	    	String hash = new String(sc.next()+sc.next()+sc.next()+sc.next()+sc.next()); 
	    	size++;
	    	hashlist.add(hash);
	    	//System.out.println(hash);
	    } //System.out.println("Size: " + size);
	    
	    br = new BufferedReader(new FileReader(outputlocation));
		size = 0;
		br.readLine(); br.readLine(); br.readLine();
	    for(String line; (line = br.readLine()) != null; ) {
	    	if(line.trim().equals("")) continue; if(line.contains("The total number of words found is")) continue;
	    	Scanner sc = new Scanner(line);
	    	String message = new String(sc.next()); 
	    	size++;
	    	messagelist.add(Long.parseLong(message, 16));
	    	//System.out.println(message);
	    } //System.out.println("Size: " + size);
	    
	    for(int i=1; i<=numberOfFiles; i++) {
	    	
	    	br = new BufferedReader(new FileReader(tablelocations[i]));
			size = 0; 
		    for(String line; (line = br.readLine()) != null; ) {
		    	if(line.trim().equals("")) continue; 
		    	Scanner sc = new Scanner(line);
		    	String endword = sc.next();
		    	String startword = new String(sc.next());
		    	size++;
		    	tables[i].put(endword, startword); 
		    	//System.out.println(endword + " " + startword);
		    } //System.out.println("Size" + i + ": " + tables[i].size() + " " + size);
		    br.close();
	    }
	    
	}
	
	public void solve() throws IOException {
		
		System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out)));
		
		readT();
		
		long starttime = System.currentTimeMillis();
		
		int totalfound = 0;
		for( int i=0; i<1000; i++) {
			
			String soughthash = hashlist.get(i);
			Boolean found = false;
			
			for( int t=1; t<=numberOfFiles; t++) {
				
				found = false;
				String soughtword = new String(DatatypeConverter.printHexBinary(Reduce(soughthash, chainLength)));
				
				if(tables[t].containsKey(soughtword)) {
					int password = recover(soughthash, soughtword, chainLength, t, chainLength);
					if(password!=0) { //do a sanity check
						if(DatatypeConverter.printHexBinary(Hash(intToByteArray(password))).toUpperCase().equals(soughthash)) {
							System.out.println(Integer.toHexString(password).toUpperCase()/* + " " + soughthash*/);
							totalfound++;
							found = true;
						}
					}
				}
				if(found) break;
				
				for( int j=chainLength; j>=0; j-- ) { 
					
					byte[] hash = null;
					byte[] reduced = Reduce(soughthash, j); 
					
					for(int k=j+1; k<chainLength+1; k++) {
						hash = Hash(reduced);
						reduced = Reduce(DatatypeConverter.printHexBinary(hash), k);
					}
					
					if(tables[t].containsKey(DatatypeConverter.printHexBinary(reduced))) {
						int password = recover(soughthash, DatatypeConverter.printHexBinary(reduced), chainLength, t, j);
						if(password!=0) { //do a sanity check
							if(DatatypeConverter.printHexBinary(Hash(intToByteArray(password))).toUpperCase().equals(soughthash)) {
								System.out.println(Integer.toHexString(password).toUpperCase()/* + " " + soughthash*/);
								totalfound++;
								found = true;
								break;
							}
						}
					}
					
					
				}
				if(found) break;
			}
			if(!found) System.out.println(0);
		}
		
		System.out.println("The total number of words found is: " + totalfound);
		long endtime = System.currentTimeMillis() - starttime;
		System.out.println("Time taken = " + endtime/1000.0);
		
	}
			
	private int recover(String soughthash, String endword, int chainLength, int t, int j) {
		
		int start = Integer.parseInt(tables[t].get(endword));
		byte reduced[] = intToByteArray(start);
				
		if(DatatypeConverter.printHexBinary(md.digest(reduced)).equals(soughthash)) return start;
		
		for(int i=1; i<j; i++) {
			
			byte[] hash = Hash(reduced);
			reduced = Reduce(DatatypeConverter.printHexBinary(hash), i);
		}
		
		String finalhash = DatatypeConverter.printHexBinary(Hash(reduced));

		if(finalhash.equals(soughthash)) return Integer.parseInt(DatatypeConverter.printHexBinary(reduced), 16);
				
		return 0; //not found
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
	
}


