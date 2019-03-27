package com.koolyun.rsa;

import java.io.IOException;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.HashMap;
import java.util.Map;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;


/**
 * RSA 生成公钥私钥
 * @author 30934
 *
 */
public class CreateSecrteKey {
	
	//定义密钥算法为RSA
	public static final String KEY_ALGORITHM = "RSA";
	
	private static final String PUBLIC_KEY = "RSAPublicKey";	//公钥
	private static final String PRIVATE_KEY = "RSAPrivateKey";	//私钥
	
	/**
	 * 获取公钥
	 * @param keyMap
	 * @return
	 */
	public static String getPublicKey(Map<String, Object> keyMap) {
		//获得map中的公钥对象	转为key对象
		Key key = (Key) keyMap.get(PUBLIC_KEY);
		//编码返回字符串
		return encryptBASE64(key.getEncoded());
	}
	
	/**
	 * 获取私钥
	 * @param keyMap
	 * @return
	 */
	public static String getPrivateKey(Map<String, Object> keyMap) {
		//获得map中的公钥对象	转为key对象
		Key key = (Key) keyMap.get(PRIVATE_KEY);
		//编码返回字符串
		return encryptBASE64(key.getEncoded());
	}
	
	/**
	 * 进行编码，返回字符串
	 * @param encoded
	 * @return
	 */
	private static String encryptBASE64(byte[] key) {
		BASE64Encoder encoder = new BASE64Encoder();
		return encoder.encodeBuffer(key);
	}
	
	/**
	 * 解码，返回byte数组
	 * @param key
	 * @return
	 * @throws IOException 
	 */
	@SuppressWarnings("unused")
	private static byte[] decryptBASE64(String key) throws IOException {
		BASE64Decoder decoder = new BASE64Decoder();
		return decoder.decodeBuffer(key);
	}
	
	/**
	 * 将公钥私钥存入map对象
	 * @return
	 * @throws Exception
	 */
	public static Map<String, Object> initKey() throws Exception{
		//获得对象KeyPairGenerator 参数RSA 1024个字节
		KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(KEY_ALGORITHM);
		keyPairGenerator.initialize(1024);
		//通过对象KeyPairGenerator获取对象KeyPair
		KeyPair keyPair = keyPairGenerator.generateKeyPair();
		
		//通过对象keyPair获取RSA公私钥对象RSAPublicKey	RSAPrivateKey
		@SuppressWarnings("unused")
		RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
		@SuppressWarnings("unused")
		RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
		
		//将公钥私钥对象存入map中
		@SuppressWarnings("unused")
		Map<String, Object> keyMap = new HashMap<String, Object>(2);
		keyMap.put(PUBLIC_KEY, publicKey);
		keyMap.put(PRIVATE_KEY, privateKey);
		return keyMap;
	}

	public static void main(String[] args) {
		Map<String, Object> keyMap;
		try {
			keyMap = initKey();
			for (Map.Entry<String, Object> entry : keyMap.entrySet()) {
				System.out.println("key = " + entry.getKey() +
						" and value = " + entry.getValue());
			}
			String publicKey = getPublicKey(keyMap);
			String privateKey = getPrivateKey(keyMap);
			System.out.println();
			System.out.println(publicKey);
			System.out.println();
			System.out.println(privateKey);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	

}
