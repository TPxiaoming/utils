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
 * RSA签名验签类
 * @author 30934
 *
 */
public class RSASignAndCheck {

	public static void main(String[] args) {
		String content = "要进行签名的内容";
		//生成私钥并存放在指定位置
		RSAEnAndDncrypt.genKeyPair("D://upload");
		try {
			//从私钥文件中得到私钥字符串
			String privateKeyStr = RSAEnAndDncrypt.loadKeyByFile("D://upload", "private");
			//根据私钥字符串获得私钥对象
			PrivateKey privateKey = (PrivateKey) RSAEnAndDncrypt.loadKeyByStr(privateKeyStr, "private");
			String charset = "UTF-8";	//编码格式
			String signType = "RSA2";	//签名类型
			String rsaSign = rsaSign(content, privateKey, charset, signType);	//签名值
			System.out.println("签名："+rsaSign);
			
			//从公钥文件中得到公钥字符串
			String publicKeyStr = RSAEnAndDncrypt.loadKeyByFile("D://upload", "public");
			//根据公钥字符串获得公钥对象
			PublicKey publicKey = (PublicKey) RSAEnAndDncrypt.loadKeyByStr(publicKeyStr, "public");
			boolean result = rsaCheckContent(content, rsaSign , publicKey, charset, signType);
			System.out.println("验签结果：" + result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * RSA验签
	 * @param content	待签名的数据，即签名前的数据
	 * @param sign	签名值
	 * @param publicKey	公钥
	 * @param charset	编码格式
	 * @param signType	签名算法
	 * @return
	 * @throws Exception
	 */
	public static boolean rsaCheckContent(String content, String sign, PublicKey publicKey, String charset, String signType) throws Exception {
		String signAlgo = "SHA1WithRSA";
		if ("RSA2".equals(signType)) {
			signAlgo = "SHA256WithRSA";
		}
		//返回实现指定签名算法的Signature对象
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
	 * rsa签名
	 * @param content	要签名的内容
	 * @param privateKey	私钥
	 * @param charset	编码格式
	 * @param signType	签名算法
	 * @return
	 * @throws Exception
	 */
	public static String rsaSign(String content, PrivateKey privateKey, String charset, String signType) throws Exception {
		String signAlgo = "SHA1WithRSA";
		if ("RSA2".equals(signType)) {
			signAlgo = "SHA256WithRSA";
		}
		
		try {
			//返回实现指定签名算法的Signature对象
			Signature signature = Signature.getInstance(signAlgo);
			//初始化此对象进行签名。 如果再次使用不同的参数调用此方法，则会消除此调用的影响。 
			signature.initSign(privateKey);
			//使用指定的字节数组更新要签名或验证的数据。
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
