package br.com.e8.soupsi.ejb.util;

import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.codec.binary.Hex;

public class HashUtil {
	
	public static String hashToMD5(String text) throws NoSuchAlgorithmException {
		MessageDigest messageDigest;
		messageDigest = MessageDigest.getInstance("MD5");
		messageDigest.reset();
		messageDigest.update(text.getBytes(Charset.forName("UTF8")));
		byte[] resultByte = messageDigest.digest();
		String result = new String(Hex.encodeHex(resultByte));
		return result;
	}
	
	public static String hashToMD5(byte[] bytes) throws NoSuchAlgorithmException {
		MessageDigest messageDigest;
		messageDigest = MessageDigest.getInstance("MD5");
		messageDigest.reset();
		messageDigest.update(bytes);
		byte[] resultByte = messageDigest.digest();
		String result = new String(Hex.encodeHex(resultByte));
		return result;
	}

}
