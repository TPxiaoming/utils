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
 * RSA ���ɹ�Կ˽Կ
 * @author 30934
 *
 */
public class CreateSecrteKey {
	
	//������Կ�㷨ΪRSA
	public static final String KEY_ALGORITHM = "RSA";
	
	private static final String PUBLIC_KEY = "RSAPublicKey";	//��Կ
	private static final String PRIVATE_KEY = "RSAPrivateKey";	//˽Կ
	
	/**
	 * ��ȡ��Կ
	 * @param keyMap
	 * @return
	 */
	public static String getPublicKey(Map<String, Object> keyMap) {
		//���map�еĹ�Կ����	תΪkey����
		Key key = (Key) keyMap.get(PUBLIC_KEY);
		//���뷵���ַ���
		return encryptBASE64(key.getEncoded());
	}
	
	/**
	 * ��ȡ˽Կ
	 * @param keyMap
	 * @return
	 */
	public static String getPrivateKey(Map<String, Object> keyMap) {
		//���map�еĹ�Կ����	תΪkey����
		Key key = (Key) keyMap.get(PRIVATE_KEY);
		//���뷵���ַ���
		return encryptBASE64(key.getEncoded());
	}
	
	/**
	 * ���б��룬�����ַ���
	 * @param encoded
	 * @return
	 */
	private static String encryptBASE64(byte[] key) {
		BASE64Encoder encoder = new BASE64Encoder();
		return encoder.encodeBuffer(key);
	}
	
	/**
	 * ���룬����byte����
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
	 * ����Կ˽Կ����map����
	 * @return
	 * @throws Exception
	 */
	public static Map<String, Object> initKey() throws Exception{
		//��ö���KeyPairGenerator ����RSA 1024���ֽ�
		KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(KEY_ALGORITHM);
		keyPairGenerator.initialize(1024);
		//ͨ������KeyPairGenerator��ȡ����KeyPair
		KeyPair keyPair = keyPairGenerator.generateKeyPair();
		
		//ͨ������keyPair��ȡRSA��˽Կ����RSAPublicKey	RSAPrivateKey
		@SuppressWarnings("unused")
		RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
		@SuppressWarnings("unused")
		RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
		
		//����Կ˽Կ�������map��
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
