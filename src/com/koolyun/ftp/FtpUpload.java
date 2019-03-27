package com.koolyun.ftp;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.SocketException;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
/**
 * ftp上传文件或者文件夹
 * 需要导入commons-net架包,并且创建一个ftp服务器
 * @author xiaoming
 *
 */
public class FtpUpload {

	private FTPClient ftp;
	/**
	 * 测试链接
	 * @param workingPath	上传到ftp服务器的哪个路径下
	 * @param hostname	地址
	 * @param port	端口号
	 * @param uname	用户名
	 * @param password	密码
	 * @return
	 * @throws SocketException
	 * @throws IOException
	 */
	private boolean connect(String workingPath, String hostname, int port, String uname, String password)  {
		ftp = new FTPClient();
		try {
			ftp.connect(hostname, port);
			ftp.login(uname, password);
			ftp.setFileType(FTPClient.BINARY_FILE_TYPE);
			int reply = ftp.getReplyCode();
			if(!FTPReply.isPositiveCompletion(reply)) {
				ftp.disconnect();
				return false;
			}
			ftp.changeWorkingDirectory(workingPath);
		} catch (SocketException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return true;
	}
	
	/**
	 * 上传文件或文件夹
	 * @param file	文件或文件夹
	 * @throws IOException
	 */
	private void upload(File file) throws IOException {
		//如果是文件夹
		if(file.isDirectory()) {
			//创建一个同样名称的文件夹
			ftp.makeDirectory(file.getName());
			//进入该文件夹
			ftp.changeWorkingDirectory(file.getName());
			//获取文件夹中所有的文件
			String[] files = file.list();
			for (int i = 0; i < files.length; i++) {
				File file1 = new File(file.getPath() + "\\" + files[i]);
				//判断是否是文件夹
				if (file1.isDirectory()) {
					//是文件夹，递归调用下载
					upload(file1);
					ftp.changeToParentDirectory();
				}else {
					//不是的话，保存文件
					File file2 = new File(file.getPath() + "\\" + files[i]);
					FileInputStream input = new FileInputStream(file2);
					ftp.storeFile(file2.getName(), input);
					input.close();
				}
			}
		}else {
			File file2 = new File(file.getPath());
			FileInputStream input = new FileInputStream(file2);
			ftp.storeFile(file2.getName(), input);
			input.close();
		}
	}
	
	public static void main(String[] args) throws SocketException, IOException {
		FtpUpload ftp = new FtpUpload();
		//浏览器访问地址为：ftp://192.168.88.132/
		boolean connect = ftp.connect("/home/", "192.168.88.132", 21, "test", "test");
		System.out.println(connect);
		File file= new File("E:\\BaiduNetdiskDownload");
		ftp.upload(file);
	}
}
