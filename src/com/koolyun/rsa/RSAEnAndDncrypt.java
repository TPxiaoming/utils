package com.koolyun.rsa;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import javax.crypto.Cipher;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

/**
 * RSA加密解密类
 * 
 * @author xiaoming
 *
 */
public class RSAEnAndDncrypt {
	private static final String PRI = "private";
	private static final String PUB = "public";
	// 字节数据转字符串专用集合
	private static final char[] HEX_CHAR = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e',
			'f' };

	/**
	 * 随机生成公钥私钥密钥对 并保存到文件中
	 * 
	 * @param filePath
	 *            文件路径
	 */
	@SuppressWarnings("resource")
	public static void genKeyPair(String filePath) {
		// KeyPairGenerator类用于生成公钥和私钥对，基于RSA算法生成对象
		KeyPairGenerator keyPairGenerator = null;
		try {
			keyPairGenerator = KeyPairGenerator.getInstance("RSA");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}

		// 初始化密钥对生成器，密钥大小为96-1024位
		keyPairGenerator.initialize(1024, new SecureRandom());
		// 生成一个密钥对，保存在KeyPair中
		KeyPair keyPair = keyPairGenerator.generateKeyPair();
		// 得到公钥
		RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
		// 得到私钥
		RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
		// 编码得到公钥字符串
		String publicKeyString = new BASE64Encoder().encode(publicKey.getEncoded());
		// 编码得到私钥字符串
		String privateKeyString = new BASE64Encoder().encode(privateKey.getEncoded());
		try {
			// 将密钥对写入文件
			FileWriter pubfw = new FileWriter(filePath + "/publicKey.keystore");
			FileWriter prifw = new FileWriter(filePath + "/privateKey.keystore");
			BufferedWriter pubBw = new BufferedWriter(pubfw);
			BufferedWriter priBw = new BufferedWriter(prifw);
			pubBw.write(publicKeyString);
			priBw.write(privateKeyString);
			pubBw.flush();
			pubBw.close();
			pubfw.close();
			priBw.flush();
			priBw.close();
			prifw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 从对应路径文件根据公私钥类型生成对应的公钥字符串或私钥字符串
	 * 
	 * @param path
	 *            路径
	 * @param type
	 *            类型，public:公钥，private:私钥
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("resource")
	public static String loadKeyByFile(String path, String type) throws Exception {
		try {
			BufferedReader bufferedReader = null;
			if (PUB.equals(type)) {
				bufferedReader = new BufferedReader(new FileReader(path + "/publicKey.keystore"));
			} else if (PRI.equals(type)) {
				bufferedReader = new BufferedReader(new FileReader(path + "/privateKey.keystore"));
			} else {
				return "输入的类型不对";
			}

			String readLine = null;
			StringBuilder stringBuilder = new StringBuilder();
			while ((readLine = bufferedReader.readLine()) != null) {
				stringBuilder.append(readLine);
			}
			bufferedReader.close();
			return stringBuilder.toString();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			throw new Exception("私钥数据读取错误");
		} catch (IOException e) {
			e.printStackTrace();
			throw new Exception("私钥输入流为空！");
		}
	}

	/**
	 * 根据公钥或私钥字符串生成对应的密钥对象
	 * 
	 * @param keyStr
	 *            密钥字符串
	 * @param type
	 *            密钥类型
	 * @return
	 * @throws Exception
	 */
	public static Object loadKeyByStr(String keyStr, String type) throws Exception {
		try {
			// 解码
			byte[] buffer = new BASE64Decoder().decodeBuffer(keyStr);
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			if (PUB.equals(type)) {
				X509EncodedKeySpec keySpec = new X509EncodedKeySpec(buffer);
				return (RSAPublicKey) keyFactory.generatePublic(keySpec);
			} else {
				PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(buffer);
				return (RSAPrivateKey) keyFactory.generatePrivate(keySpec);
			}
		} catch (IOException e) {
			e.printStackTrace();
			throw new Exception("无法解码");
		}
	}

	/**
	 * 加密，解密过程
	 * @param key	密钥对象，公钥：RSAPublicKey，私钥：RSAPrivateKey
	 * @param type	加密 解密
	 * @param data	明文数据
	 * @return
	 * @throws Exception
	 */
	public static byte[] crypt(Object key, String type, byte[] data) throws Exception {
		if (key == null) {
			throw new Exception("密钥为空，请设置");
		}
		// Cipher类为加密和解密提供密码功能
		Cipher cipher = null;
		// Cipher类是一个引擎类，它需要通过getInstance()工厂方法来实例化对
		// 使用默认RSA
		cipher = Cipher.getInstance("RSA");
		if ("decrypt".equals(type)) {
			//用密钥初始化此 Cipher对象
			cipher.init(Cipher.DECRYPT_MODE, (Key) key);
		} else if("enecrypt".equals(type)) {
			//用密钥初始化此 Cipher对象
			cipher.init(Cipher.ENCRYPT_MODE, (Key) key);
		}
		//按单部分操作加密或解密数据，或者结束一个多部分操作
		byte[] output = cipher.doFinal(data);
		return output;
	}
	
	/**
	 * 字节数据转十六进制字符串
	 * @param data	输入的字节数据
	 * @return	十六进制内容
	 */
	public static String byteArrayToString(byte[] data) {
		StringBuilder stringBuilder = new StringBuilder();
		for (int i = 0; i < data.length; i++) {
			//取出字节的高四位，作为索引得到相应的十六进制标识符，注意无符号右移
			stringBuilder.append(HEX_CHAR[(data[i] & 0xf0) >>> 4]);
			if(i < data.length - 1) {
				stringBuilder.append(' ');
			}
		}
		return stringBuilder.toString();
	}
	
	public static void main(String[] args) {
		genKeyPair("D://upload");
		try {
			System.out.println("通过文件加载密钥，返回密钥字符串");
			System.out.println("公钥：" + loadKeyByFile("D://upload", "public"));
			System.out.println("私钥：" + loadKeyByFile("D://upload", "private"));
			RSAPrivateKey privateKey = (RSAPrivateKey) loadKeyByStr(loadKeyByFile("D://upload", "private"), "private");
			RSAPublicKey publicKey  = (RSAPublicKey) loadKeyByStr(loadKeyByFile("D://upload", "public"), "public");
			System.out.println("根据密钥字符串得到密钥对象");
			System.out.println(privateKey);
			System.out.println(publicKey);
			System.out.println("根据密钥对象加密");
			byte[] data = {'x','i','a','o'};
			byte[] crypt = crypt(publicKey, "enecrypt", data);
			String miwen = new BASE64Encoder().encode(crypt);
			System.out.println(miwen);
			byte[] crypt2 = crypt(privateKey, "decrypt", crypt);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
