package com.koolyun.ftp;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.SocketException;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
/**
 * ftp�ϴ��ļ������ļ���
 * ��Ҫ����commons-net�ܰ�,���Ҵ���һ��ftp������
 * @author xiaoming
 *
 */
public class FtpUpload {

	private FTPClient ftp;
	/**
	 * ��������
	 * @param workingPath	�ϴ���ftp���������ĸ�·����
	 * @param hostname	��ַ
	 * @param port	�˿ں�
	 * @param uname	�û���
	 * @param password	����
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
	 * �ϴ��ļ����ļ���
	 * @param file	�ļ����ļ���
	 * @throws IOException
	 */
	private void upload(File file) throws IOException {
		//������ļ���
		if(file.isDirectory()) {
			//����һ��ͬ�����Ƶ��ļ���
			ftp.makeDirectory(file.getName());
			//������ļ���
			ftp.changeWorkingDirectory(file.getName());
			//��ȡ�ļ��������е��ļ�
			String[] files = file.list();
			for (int i = 0; i < files.length; i++) {
				File file1 = new File(file.getPath() + "\\" + files[i]);
				//�ж��Ƿ����ļ���
				if (file1.isDirectory()) {
					//���ļ��У��ݹ��������
					upload(file1);
					ftp.changeToParentDirectory();
				}else {
					//���ǵĻ��������ļ�
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
		//��������ʵ�ַΪ��ftp://192.168.88.132/
		boolean connect = ftp.connect("/home/", "192.168.88.132", 21, "test", "test");
		System.out.println(connect);
		File file= new File("E:\\BaiduNetdiskDownload");
		ftp.upload(file);
	}
}
