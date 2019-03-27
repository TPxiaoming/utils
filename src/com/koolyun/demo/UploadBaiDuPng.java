package com.koolyun.demo;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/**
 * ��ȥ�ٶ���ҳ����һ��ͼƬ
 * @author 30934
 *
 */
public class UploadBaiDuPng {

	public static void main(String[] args) {
		//���弴�����ʵ�����
		String url = "http://www.baidu.com";
		//�������Ӳ���ȡҳ������
		String result = sendGet(url);
		System.out.println(result);
		//ʹ������ƥ��ͼƬ��src����
		String imgSrc = RegexString(result, "src=//(.+?) width");
		//��ӡ���
		String isUrl = String.format("http://%s", imgSrc);
		System.out.println("url"+isUrl);
		//·��
		String path = "D:/upload/1.png";
		downloadPicture(isUrl, path);
	}

	/**
	 * ����url��ȡ��ҳ��Դ����
	 * @param url
	 * @return
	 */
	private static String sendGet(String url) {
		//����һ���ַ��������洢��ҳ����
		String result = "";
		//����һ�������ַ�������
		BufferedReader in = null;
		
		//��stringר��url����
		try {
			URL realUrl = new URL(url);
			//��ʼ��һ�����ӵ��ĸ�url������
			URLConnection connection = realUrl.openConnection();
			//��ʼʵ�ʵ�����
			connection.connect();
			//��ʼ��BufferedReader����������ȡURL����Ӧ
			in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			//������ʱ�洢ץȡ����ÿһ�е�����
			String line;
			while((line = in.readLine()) != null) {
				//����ץȡ����ÿһ�в������洢��result����
				result += line;
			}
		} catch (Exception e) {
			System.out.println("����GET��������쳣��"+e);
			e.printStackTrace();
		}finally {
			try {
				if(in != null) {
					in.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return result;
	}
	
	/**
	 * ��ȡͼƬ��·��
	 * @param targetStr	����url��ȡ����ҳԴ����
	 * @param patternStr	�����﷨
	 * @return
	 */
	static String RegexString(String targetStr, String patternStr) {
		//����һ����ʽģ�壬����ʹ��������ʽ����������Ҫץȡ������
		//�൱��������ݽ�ƥ��ĵط��ͻ����ȥ
		Pattern pattern = Pattern.compile(patternStr);
		//����һ��matcher������ƥ��
		Matcher matcher = pattern.matcher(targetStr);
		//����ҵ���
		if (matcher.find()) {
			//��ӡ���
			return matcher.group(1);
		} 
		return "Nothing";
	}
	
	/**
	 * ����url��������ͼƬ
	 * @param urlList ͼƬ·��
	 * @param path ���ص�·��
	 */
	@SuppressWarnings("resource")
	static void downloadPicture(String urlList, String path) {
		URL url = null;
		try {
			url = new URL(urlList);
			DataInputStream dataInputStream = new DataInputStream(url.openStream());
			FileOutputStream fileOutputStream = new FileOutputStream(new File(path));
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			byte[] buffer = new byte[999999999];
			int length;
			while((length = dataInputStream.read(buffer)) > 0) {
				outputStream.write(buffer, 0, length);
			}
			fileOutputStream.write(outputStream.toByteArray());
			dataInputStream.close();
			fileOutputStream.close();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
