package com.care.utils;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import javax.crypto.Cipher;

public class RSAUtil {
	private static final int MAX_ENCRYPT_BLOCK = 117;
	private static final int MAX_DECRYPT_BLOCK = 256;

	public static HashMap<String, Object> getKeys(Integer lentg)
			throws NoSuchAlgorithmException {
		HashMap<String, Object> map = new HashMap();
		KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
		keyPairGen.initialize(lentg.intValue());
		KeyPair keyPair = keyPairGen.generateKeyPair();
		RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
		RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
		map.put("public", publicKey);
		map.put("private", privateKey);
		return map;
	}

	public static RSAPublicKey getPublicKey(String modulus, String exponent) {
		try {
			BigInteger b1 = new BigInteger(modulus);
			BigInteger b2 = new BigInteger(exponent);
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			RSAPublicKeySpec keySpec = new RSAPublicKeySpec(b1, b2);
			return (RSAPublicKey) keyFactory.generatePublic(keySpec);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static RSAPrivateKey getPrivateKey(String modulus, String exponent) {
		try {
			BigInteger b1 = new BigInteger(modulus);
			BigInteger b2 = new BigInteger(exponent);
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			RSAPrivateKeySpec keySpec = new RSAPrivateKeySpec(b1, b2);
			return (RSAPrivateKey) keyFactory.generatePrivate(keySpec);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String encryptByPublicKey(String data, RSAPublicKey publicKey)
			throws Exception {
		Cipher cipher = Cipher.getInstance("RSA");
		cipher.init(1, publicKey);

		int key_len = publicKey.getModulus().bitLength() / 8;

		String[] datas = splitString(data, key_len - 11);
		String mi = "";
		for (String s : datas) {
			mi = mi + bcd2Str(cipher.doFinal(s.getBytes()));
		}
		return mi;
	}

	public static String decryptByPrivateKey(String data,
			RSAPrivateKey privateKey) throws Exception {
		Cipher cipher = Cipher.getInstance("RSA");
		cipher.init(2, privateKey);

		int key_len = privateKey.getModulus().bitLength() / 8;
		byte[] bytes = data.getBytes();
		byte[] bcd = ASCII_To_BCD(bytes, bytes.length);
		System.err.println(bcd.length);

		String ming = "";
		byte[][] arrays = splitArray(bcd, key_len);
		for (byte[] arr : arrays) {
			ming = ming + new String(cipher.doFinal(arr));
		}
		return ming;
	}

	public static byte[] encryptData(byte[] data, PublicKey publicKey) {
		try {
			Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
			cipher.init(1, publicKey);

			int inputLen = data.length;
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			int offSet = 0;

			int i = 0;
			while (inputLen - offSet > 0) {
				byte[] cache;
				if (inputLen - offSet > 117) {
					cache = cipher.doFinal(data, offSet, 117);
				} else {
					cache = cipher.doFinal(data, offSet, inputLen - offSet);
				}
				out.write(cache, 0, cache.length);
				i++;
				offSet = i * 117;
			}
			byte[] encryptedData = out.toByteArray();
			out.close();
			return encryptedData;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static byte[] decryptData(byte[] encryptedData, PrivateKey privateKey) {
		try {
			Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
			cipher.init(2, privateKey);

			int inputLen = encryptedData.length;
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			int offSet = 0;

			int i = 0;
			while (inputLen - offSet > 0) {
				byte[] cache;
				if (inputLen - offSet > 256) {
					cache = cipher.doFinal(encryptedData, offSet, 256);
				} else {
					cache = cipher.doFinal(encryptedData, offSet, inputLen
							- offSet);
				}
				out.write(cache, 0, cache.length);
				i++;
				offSet = i * 256;
			}
			byte[] decryptedData = out.toByteArray();
			out.close();
			return decryptedData;
		} catch (Exception e) {
		}
		return null;
	}

	public static byte[] ASCII_To_BCD(byte[] ascii, int asc_len) {
		byte[] bcd = new byte[asc_len / 2];
		int j = 0;
		for (int i = 0; i < (asc_len + 1) / 2; i++) {
			bcd[i] = asc_to_bcd(ascii[(j++)]);
			bcd[i] = ((byte) ((j >= asc_len ? 0 : asc_to_bcd(ascii[(j++)])) + (bcd[i] << 4)));
		}
		return bcd;
	}

	public static byte asc_to_bcd(byte asc) {
		byte bcd;
		if ((asc >= 48) && (asc <= 57)) {
			bcd = (byte) (asc - 48);
		} else {
			if ((asc >= 65) && (asc <= 70)) {
				bcd = (byte) (asc - 65 + 10);
			} else {
				if ((asc >= 97) && (asc <= 102)) {
					bcd = (byte) (asc - 97 + 10);
				} else {
					bcd = (byte) (asc - 48);
				}
			}
		}
		return bcd;
	}

	public static String bcd2Str(byte[] bytes) {
		char[] temp = new char[bytes.length * 2];
		for (int i = 0; i < bytes.length; i++) {
			char val = (char) ((bytes[i] & 0xF0) >> 4 & 0xF);
			temp[(i * 2)] = ((char) (val > '\t' ? val + 'A' - 10 : val + '0'));

			val = (char) (bytes[i] & 0xF);
			temp[(i * 2 + 1)] = ((char) (val > '\t' ? val + 'A' - 10
					: val + '0'));
		}
		return new String(temp);
	}

	public static String[] splitString(String string, int len) {
		int x = string.length() / len;
		int y = string.length() % len;
		int z = 0;
		if (y != 0) {
			z = 1;
		}
		String[] strings = new String[x + z];
		String str = "";
		for (int i = 0; i < x + z; i++) {
			if ((i == x + z - 1) && (y != 0)) {
				str = string.substring(i * len, i * len + y);
			} else {
				str = string.substring(i * len, i * len + len);
			}
			strings[i] = str;
		}
		return strings;
	}

	public static byte[][] splitArray(byte[] data, int len) {
		int x = data.length / len;
		int y = data.length % len;
		int z = 0;
		if (y != 0) {
			z = 1;
		}
		byte[][] arrays = new byte[x + z][];
		for (int i = 0; i < x + z; i++) {
			byte[] arr = new byte[len];
			if ((i == x + z - 1) && (y != 0)) {
				System.arraycopy(data, i * len, arr, 0, y);
			} else {
				System.arraycopy(data, i * len, arr, 0, len);
			}
			arrays[i] = arr;
		}
		return arrays;
	}

	public static PublicKey loadPublicKey(String publicKeyStr) throws Exception {
		try {
			byte[] buffer = Base64Utils.decode(publicKeyStr);
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			X509EncodedKeySpec keySpec = new X509EncodedKeySpec(buffer);
			return (RSAPublicKey) keyFactory.generatePublic(keySpec);
		} catch (NoSuchAlgorithmException e) {
			throw new Exception("无此算法");
		} catch (InvalidKeySpecException e) {
			throw new Exception("公钥非法");
		} catch (NullPointerException e) {
			throw new Exception("公钥数据为空");
		}
	}

	public static PrivateKey loadPrivateKey(String privateKeyStr)
			throws Exception {
		try {
			byte[] buffer = Base64Utils.decode(privateKeyStr);

			PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(buffer);
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			return (RSAPrivateKey) keyFactory.generatePrivate(keySpec);
		} catch (NoSuchAlgorithmException e) {
			throw new Exception("无此算法");
		} catch (InvalidKeySpecException e) {
			throw new Exception("私钥非法");
		} catch (NullPointerException e) {
			throw new Exception("私钥数据为空");
		}
	}

	public static byte[] privatesign(String src, RSAPrivateKey rsaPrivateKey)
			throws Exception {
		try {
			PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(
					rsaPrivateKey.getEncoded());
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			PrivateKey privateKey = keyFactory
					.generatePrivate(pkcs8EncodedKeySpec);
			Signature signature = Signature.getInstance("MD5withRSA");

			signature.initSign(privateKey);

			signature.update(src.getBytes());

			return signature.sign();
		} catch (Exception e) {
		}
		return null;
	}

	public static boolean publicsign(String src, byte[] sign,
			RSAPublicKey rsaPublicKey) throws Exception {
		try {
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			Signature signature = Signature.getInstance("MD5withRSA");

			X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(
					rsaPublicKey.getEncoded());
			keyFactory = KeyFactory.getInstance("RSA");
			PublicKey publicKey = keyFactory.generatePublic(x509EncodedKeySpec);
			signature = Signature.getInstance("MD5withRSA");

			signature.initVerify(publicKey);

			signature.update(src.getBytes());

			return signature.verify(sign);
		} catch (Exception e) {
		}
		return false;
	}
}