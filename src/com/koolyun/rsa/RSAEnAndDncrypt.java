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
 * RSA���ܽ�����
 * 
 * @author xiaoming
 *
 */
public class RSAEnAndDncrypt {
	private static final String PRI = "private";
	private static final String PUB = "public";
	// �ֽ�����ת�ַ���ר�ü���
	private static final char[] HEX_CHAR = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e',
			'f' };

	/**
	 * ������ɹ�Կ˽Կ��Կ�� �����浽�ļ���
	 * 
	 * @param filePath
	 *            �ļ�·��
	 */
	@SuppressWarnings("resource")
	public static void genKeyPair(String filePath) {
		// KeyPairGenerator���������ɹ�Կ��˽Կ�ԣ�����RSA�㷨���ɶ���
		KeyPairGenerator keyPairGenerator = null;
		try {
			keyPairGenerator = KeyPairGenerator.getInstance("RSA");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}

		// ��ʼ����Կ������������Կ��СΪ96-1024λ
		keyPairGenerator.initialize(1024, new SecureRandom());
		// ����һ����Կ�ԣ�������KeyPair��
		KeyPair keyPair = keyPairGenerator.generateKeyPair();
		// �õ���Կ
		RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
		// �õ�˽Կ
		RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
		// ����õ���Կ�ַ���
		String publicKeyString = new BASE64Encoder().encode(publicKey.getEncoded());
		// ����õ�˽Կ�ַ���
		String privateKeyString = new BASE64Encoder().encode(privateKey.getEncoded());
		try {
			// ����Կ��д���ļ�
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
	 * �Ӷ�Ӧ·���ļ����ݹ�˽Կ�������ɶ�Ӧ�Ĺ�Կ�ַ�����˽Կ�ַ���
	 * 
	 * @param path
	 *            ·��
	 * @param type
	 *            ���ͣ�public:��Կ��private:˽Կ
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
				return "��������Ͳ���";
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
			throw new Exception("˽Կ���ݶ�ȡ����");
		} catch (IOException e) {
			e.printStackTrace();
			throw new Exception("˽Կ������Ϊ�գ�");
		}
	}

	/**
	 * ���ݹ�Կ��˽Կ�ַ������ɶ�Ӧ����Կ����
	 * 
	 * @param keyStr
	 *            ��Կ�ַ���
	 * @param type
	 *            ��Կ����
	 * @return
	 * @throws Exception
	 */
	public static Object loadKeyByStr(String keyStr, String type) throws Exception {
		try {
			// ����
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
			throw new Exception("�޷�����");
		}
	}

	/**
	 * ���ܣ����ܹ���
	 * @param key	��Կ���󣬹�Կ��RSAPublicKey��˽Կ��RSAPrivateKey
	 * @param type	���� ����
	 * @param data	��������
	 * @return
	 * @throws Exception
	 */
	public static byte[] crypt(Object key, String type, byte[] data) throws Exception {
		if (key == null) {
			throw new Exception("��ԿΪ�գ�������");
		}
		// Cipher��Ϊ���ܺͽ����ṩ���빦��
		Cipher cipher = null;
		// Cipher����һ�������࣬����Ҫͨ��getInstance()����������ʵ������
		// ʹ��Ĭ��RSA
		cipher = Cipher.getInstance("RSA");
		if ("decrypt".equals(type)) {
			//����Կ��ʼ���� Cipher����
			cipher.init(Cipher.DECRYPT_MODE, (Key) key);
		} else if("enecrypt".equals(type)) {
			//����Կ��ʼ���� Cipher����
			cipher.init(Cipher.ENCRYPT_MODE, (Key) key);
		}
		//�������ֲ������ܻ�������ݣ����߽���һ���ಿ�ֲ���
		byte[] output = cipher.doFinal(data);
		return output;
	}
	
	/**
	 * �ֽ�����תʮ�������ַ���
	 * @param data	������ֽ�����
	 * @return	ʮ����������
	 */
	public static String byteArrayToString(byte[] data) {
		StringBuilder stringBuilder = new StringBuilder();
		for (int i = 0; i < data.length; i++) {
			//ȡ���ֽڵĸ���λ����Ϊ�����õ���Ӧ��ʮ�����Ʊ�ʶ����ע���޷�������
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
			System.out.println("ͨ���ļ�������Կ��������Կ�ַ���");
			System.out.println("��Կ��" + loadKeyByFile("D://upload", "public"));
			System.out.println("˽Կ��" + loadKeyByFile("D://upload", "private"));
			RSAPrivateKey privateKey = (RSAPrivateKey) loadKeyByStr(loadKeyByFile("D://upload", "private"), "private");
			RSAPublicKey publicKey  = (RSAPublicKey) loadKeyByStr(loadKeyByFile("D://upload", "public"), "public");
			System.out.println("������Կ�ַ����õ���Կ����");
			System.out.println(privateKey);
			System.out.println(publicKey);
			System.out.println("������Կ�������");
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
