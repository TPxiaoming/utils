package com.koolyun.rsa;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

public class MainTest {

	public static void main(String[] args) {
		String filePath ="D://upload";
		RSAEnAndDncrypt.genKeyPair(filePath);
		String privateKeyStr;
		try {
			privateKeyStr = RSAEnAndDncrypt.loadKeyByFile(filePath, "private");
			RSAPrivateKey privateKey = (RSAPrivateKey) RSAEnAndDncrypt.loadKeyByStr(privateKeyStr, "private");
			String publicKeyStr = RSAEnAndDncrypt.loadKeyByFile(filePath, "public");
			RSAPublicKey publicKey = (RSAPublicKey) RSAEnAndDncrypt.loadKeyByStr(publicKeyStr, "public");

			System.out.println("---------------˽Կǩ������------------------");
			String content = "��������ǩ����ԭʼ����";
			System.out.println("ǩ��ԭʼ���ݣ�" + content);
			String rsaSign = RSASignAndCheck.rsaSign(content, privateKey, "UTF-8", "RSA");
			System.out.println("ǩ��������:" + rsaSign);
			System.out.println();
			
			System.out.println("---------------��ԿУ��ǩ��------------------");
			boolean rsaCheckContent = RSASignAndCheck.rsaCheckContent(content, rsaSign, publicKey, "UTF-8", "RSA");
			System.out.println("��ǩ�����" + rsaCheckContent);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * ˽Կ���ܹ�Կ����
	 * 1.���ͷ�ʹ��˽Կ���м��ܣ��õ���������
	 * 2.�������������Base64���ܵõ������ַ���
	 * 3.���շ��������ַ�������Base64���ܣ��õ���������
	 * 4.ʹ�ù�Կ������������н��ܣ��õ�����
	 */
	private static void priEnPubDe() {
		String filePath ="D://upload";
		RSAEnAndDncrypt.genKeyPair(filePath);
		System.out.println("--------------˽Կ���ܹ�Կ���ܹ���-------------------");
		String content="˽Կ���ܺ͹�Կ���ܵ�����";
		System.out.println("ԭ�ģ�" + content);
		//˽Կ���ܹ���
		String privateKeyStr;
		try {
			//˽Կ���ܹ���
			privateKeyStr = RSAEnAndDncrypt.loadKeyByFile(filePath, "private");
			RSAPrivateKey privateKey = (RSAPrivateKey) RSAEnAndDncrypt.loadKeyByStr(privateKeyStr, "private");
			byte[] miwen = RSAEnAndDncrypt.crypt(privateKey, "enecrypt", content.getBytes());
			String miwenStr = new BASE64Encoder().encode(miwen);
			System.out.println("���ܣ�" + miwenStr);

			//��Կ����
			String publicKeyStr = RSAEnAndDncrypt.loadKeyByFile(filePath, "public");
			RSAPublicKey publicKey = (RSAPublicKey) RSAEnAndDncrypt.loadKeyByStr(publicKeyStr, "public");
			byte[] mingwen = RSAEnAndDncrypt.crypt(publicKey, "decrypt", new BASE64Decoder().decodeBuffer(miwenStr));
			String mingwenStr = new String(mingwen);
			System.out.println("���ܣ�" + mingwenStr);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * ��Կ����˽Կ����
	 * 1.���ͷ�ʹ�ù�Կ���м��ܣ��õ���������
	 * 2.�������������Base64���ܵõ������ַ���
	 * 3.���շ��������ַ�������Base64���ܣ��õ���������
	 * 4.ʹ��˽Կ������������н��ܣ��õ�����
	 */
	private static void pubEnPriDe() {
		String filePath ="D://upload";
		RSAEnAndDncrypt.genKeyPair(filePath);
		System.out.println("--------------��Կ����˽Կ���ܹ���-------------------");
		String content="��Կ���ܺ�˽Կ���ܵ�����";
		try {
			//��Կ����
			String publicKeyStr = RSAEnAndDncrypt.loadKeyByFile(filePath, "public");
			RSAPublicKey publicKey = (RSAPublicKey) RSAEnAndDncrypt.loadKeyByStr(publicKeyStr, "public");
			byte[] cipherData = RSAEnAndDncrypt.crypt(publicKey, "enecrypt", content.getBytes());
			String cipher = new BASE64Encoder().encode(cipherData);
			
			//˽Կ���ܹ���
			String privateKeyStr = RSAEnAndDncrypt.loadKeyByFile(filePath, "private");
			RSAPrivateKey privateKey = (RSAPrivateKey) RSAEnAndDncrypt.loadKeyByStr(privateKeyStr, "private");
			byte[] res = RSAEnAndDncrypt.crypt(privateKey, "decrypt", new BASE64Decoder().decodeBuffer(cipher));
			String restr = new String(res);
			System.out.println("ԭ�ģ�" + content);
			System.out.println("���ܣ�" + cipher);
			System.out.println("���ܣ�" + restr);
			
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
