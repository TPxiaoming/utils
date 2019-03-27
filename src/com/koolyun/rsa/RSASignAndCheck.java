package com.koolyun.rsa;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.interfaces.RSAPrivateKey;
import java.util.Base64;

import com.sun.xml.internal.ws.util.StringUtils;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

/**
 * RSAǩ����ǩ��
 * @author 30934
 *
 */
public class RSASignAndCheck {

	public static void main(String[] args) {
		String content = "Ҫ����ǩ��������";
		//����˽Կ�������ָ��λ��
		RSAEnAndDncrypt.genKeyPair("D://upload");
		try {
			//��˽Կ�ļ��еõ�˽Կ�ַ���
			String privateKeyStr = RSAEnAndDncrypt.loadKeyByFile("D://upload", "private");
			//����˽Կ�ַ������˽Կ����
			PrivateKey privateKey = (PrivateKey) RSAEnAndDncrypt.loadKeyByStr(privateKeyStr, "private");
			String charset = "UTF-8";	//�����ʽ
			String signType = "RSA2";	//ǩ������
			String rsaSign = rsaSign(content, privateKey, charset, signType);	//ǩ��ֵ
			System.out.println("ǩ����"+rsaSign);
			
			//�ӹ�Կ�ļ��еõ���Կ�ַ���
			String publicKeyStr = RSAEnAndDncrypt.loadKeyByFile("D://upload", "public");
			//���ݹ�Կ�ַ�����ù�Կ����
			PublicKey publicKey = (PublicKey) RSAEnAndDncrypt.loadKeyByStr(publicKeyStr, "public");
			boolean result = rsaCheckContent(content, rsaSign , publicKey, charset, signType);
			System.out.println("��ǩ�����" + result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * RSA��ǩ
	 * @param content	��ǩ�������ݣ���ǩ��ǰ������
	 * @param sign	ǩ��ֵ
	 * @param publicKey	��Կ
	 * @param charset	�����ʽ
	 * @param signType	ǩ���㷨
	 * @return
	 * @throws Exception
	 */
	public static boolean rsaCheckContent(String content, String sign, PublicKey publicKey, String charset, String signType) throws Exception {
		String signAlgo = "SHA1WithRSA";
		if ("RSA2".equals(signType)) {
			signAlgo = "SHA256WithRSA";
		}
		//����ʵ��ָ��ǩ���㷨��Signature����
		Signature signature;
		try {
			signature = Signature.getInstance(signAlgo);
			signature.initVerify(publicKey);
			if (charset != null && charset != "") {
				signature.update(content.getBytes(charset));
			}else {
				signature.update(content.getBytes());
			}
			byte[] buffer = new BASE64Decoder().decodeBuffer(sign);
			return signature.verify(buffer);
		} catch (Exception e) {
			throw new Exception("RSAcontent = " + content + ",sign=" + sign + ",charset = " + charset, e);
		}	
	}

	/**
	 * rsaǩ��
	 * @param content	Ҫǩ��������
	 * @param privateKey	˽Կ
	 * @param charset	�����ʽ
	 * @param signType	ǩ���㷨
	 * @return
	 * @throws Exception
	 */
	public static String rsaSign(String content, PrivateKey privateKey, String charset, String signType) throws Exception {
		String signAlgo = "SHA1WithRSA";
		if ("RSA2".equals(signType)) {
			signAlgo = "SHA256WithRSA";
		}
		
		try {
			//����ʵ��ָ��ǩ���㷨��Signature����
			Signature signature = Signature.getInstance(signAlgo);
			//��ʼ���˶������ǩ���� ����ٴ�ʹ�ò�ͬ�Ĳ������ô˷�������������˵��õ�Ӱ�졣 
			signature.initSign(privateKey);
			//ʹ��ָ�����ֽ��������Ҫǩ������֤�����ݡ�
			if (charset != null && charset != "") {
				signature.update(content.getBytes(charset));
			}else {
				signature.update(content.getBytes());
			}
			byte[] sign = signature.sign();
			String result = new BASE64Encoder().encode(sign);
			return result;
		} catch (Exception e) {
			throw new Exception("sign exception", e);
		}
	}
}
