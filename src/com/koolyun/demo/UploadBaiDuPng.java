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
 * 爬去百度首页的那一张图片
 * @author 30934
 *
 */
public class UploadBaiDuPng {

	public static void main(String[] args) {
		//定义即将访问的连接
		String url = "http://www.baidu.com";
		//访问连接并获取页面内容
		String result = sendGet(url);
		System.out.println(result);
		//使用正则匹配图片的src内容
		String imgSrc = RegexString(result, "src=//(.+?) width");
		//打印结果
		String isUrl = String.format("http://%s", imgSrc);
		System.out.println("url"+isUrl);
		//路径
		String path = "D:/upload/1.png";
		downloadPicture(isUrl, path);
	}

	/**
	 * 根据url获取网页的源代码
	 * @param url
	 * @return
	 */
	private static String sendGet(String url) {
		//定义一个字符串用来存储网页内容
		String result = "";
		//定义一个缓冲字符输入流
		BufferedReader in = null;
		
		//将string专场url对象
		try {
			URL realUrl = new URL(url);
			//初始化一个链接到哪个url的链接
			URLConnection connection = realUrl.openConnection();
			//开始实际的连接
			connection.connect();
			//初始化BufferedReader输入流来读取URL的相应
			in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			//用来临时存储抓取到的每一行的数据
			String line;
			while((line = in.readLine()) != null) {
				//便利抓取到的每一行并将器存储到result里面
				result += line;
			}
		} catch (Exception e) {
			System.out.println("发送GET请求出现异常！"+e);
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
	 * 获取图片的路径
	 * @param targetStr	根据url拉取的网页源代码
	 * @param patternStr	正则语法
	 * @return
	 */
	static String RegexString(String targetStr, String patternStr) {
		//定义一个样式模板，此中使用正则表达式，括号中是要抓取的内容
		//相当于埋好了陷进匹配的地方就会掉下去
		Pattern pattern = Pattern.compile(patternStr);
		//定义一个matcher用来做匹配
		Matcher matcher = pattern.matcher(targetStr);
		//如果找到了
		if (matcher.find()) {
			//打印结果
			return matcher.group(1);
		} 
		return "Nothing";
	}
	
	/**
	 * 根据url连接下载图片
	 * @param urlList 图片路径
	 * @param path 下载的路径
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
