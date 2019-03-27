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

			System.out.println("---------------私钥签名过程------------------");
			String content = "这是用于签名的原始数据";
			System.out.println("签名原始数据：" + content);
			String rsaSign = RSASignAndCheck.rsaSign(content, privateKey, "UTF-8", "RSA");
			System.out.println("签名后数据:" + rsaSign);
			System.out.println();
			
			System.out.println("---------------公钥校验签名------------------");
			boolean rsaCheckContent = RSASignAndCheck.rsaCheckContent(content, rsaSign, publicKey, "UTF-8", "RSA");
			System.out.println("验签结果：" + rsaCheckContent);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 私钥加密公钥解密
	 * 1.发送方使用私钥进行加密，得到密文数组
	 * 2.对密文数组进行Base64加密得到密文字符串
	 * 3.接收方对密文字符串进行Base64机密，得到密文数组
	 * 4.使用公钥对密文数组进行解密，得到明文
	 */
	private static void priEnPubDe() {
		String filePath ="D://upload";
		RSAEnAndDncrypt.genKeyPair(filePath);
		System.out.println("--------------私钥加密公钥解密过程-------------------");
		String content="私钥加密和公钥解密的内容";
		System.out.println("原文：" + content);
		//私钥解密过程
		String privateKeyStr;
		try {
			//私钥加密过程
			privateKeyStr = RSAEnAndDncrypt.loadKeyByFile(filePath, "private");
			RSAPrivateKey privateKey = (RSAPrivateKey) RSAEnAndDncrypt.loadKeyByStr(privateKeyStr, "private");
			byte[] miwen = RSAEnAndDncrypt.crypt(privateKey, "enecrypt", content.getBytes());
			String miwenStr = new BASE64Encoder().encode(miwen);
			System.out.println("加密：" + miwenStr);

			//公钥解密
			String publicKeyStr = RSAEnAndDncrypt.loadKeyByFile(filePath, "public");
			RSAPublicKey publicKey = (RSAPublicKey) RSAEnAndDncrypt.loadKeyByStr(publicKeyStr, "public");
			byte[] mingwen = RSAEnAndDncrypt.crypt(publicKey, "decrypt", new BASE64Decoder().decodeBuffer(miwenStr));
			String mingwenStr = new String(mingwen);
			System.out.println("解密：" + mingwenStr);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 公钥加密私钥解密
	 * 1.发送方使用公钥进行加密，得到密文数组
	 * 2.对密文数组进行Base64加密得到密文字符串
	 * 3.接收方对密文字符串进行Base64机密，得到密文数组
	 * 4.使用私钥对密文数组进行解密，得到明文
	 */
	private static void pubEnPriDe() {
		String filePath ="D://upload";
		RSAEnAndDncrypt.genKeyPair(filePath);
		System.out.println("--------------公钥加密私钥解密过程-------------------");
		String content="公钥加密和私钥解密的内容";
		try {
			//公钥加密
			String publicKeyStr = RSAEnAndDncrypt.loadKeyByFile(filePath, "public");
			RSAPublicKey publicKey = (RSAPublicKey) RSAEnAndDncrypt.loadKeyByStr(publicKeyStr, "public");
			byte[] cipherData = RSAEnAndDncrypt.crypt(publicKey, "enecrypt", content.getBytes());
			String cipher = new BASE64Encoder().encode(cipherData);
			
			//私钥解密过程
			String privateKeyStr = RSAEnAndDncrypt.loadKeyByFile(filePath, "private");
			RSAPrivateKey privateKey = (RSAPrivateKey) RSAEnAndDncrypt.loadKeyByStr(privateKeyStr, "private");
			byte[] res = RSAEnAndDncrypt.crypt(privateKey, "decrypt", new BASE64Decoder().decodeBuffer(cipher));
			String restr = new String(res);
			System.out.println("原文：" + content);
			System.out.println("加密：" + cipher);
			System.out.println("解密：" + restr);
			
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
