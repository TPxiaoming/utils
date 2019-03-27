package com.koolyun.bio;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Blocking IO�� ͬ�������ı�̷�ʽ�� 
 * BIO ��̷�ʽͨ������ JDK1.4 �汾֮ǰ���õı�̷�ʽ��
 * ���ʵ�ֹ���Ϊ��
 * �����ڷ��� ������һ�� ServerSocket ��������������
 * �ͻ������� Socket ������������Ĭ������� ServerSocket 
 * �Ὠ��һ���߳��������������������û���߳̿��ã��ͻ������������ �����⵽�ܾ��� 
 * 
 * ͬ����������������ʵ��ģʽΪһ������һ���̣߳�
 * ���ͻ�������������ʱ�������˾� ��Ҫ����һ���߳̽��д���
 * ���������Ӳ����κ��������ɲ���Ҫ���߳̿�������Ȼ�� ��ͨ���̳߳ػ��Ƹ��ơ� 
 * 
 * BIO ��ʽ������������Ŀ�Ƚ�С�ҹ̶��ļܹ������ַ�ʽ�Է�������ԴҪ��Ƚϸߣ�
 * ����������Ӧ���У�JDK1.4 ��ǰ��Ψһѡ�񣬵�����ֱ�ۼ�����⡣
 * 
 * @author xiaoming
 *
 */
public class BIOServer {

	public static void main(String[] args) {
		int port = getPort(args);
		ServerSocket server = null;
		ExecutorService service = Executors.newFixedThreadPool(500);
		
		try {
			server = new ServerSocket(port);
			System.out.println("service start!");
			while(true) {
				Socket socket = server.accept();
				service.execute(new Handler(socket));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
			if(server!=null) {
				try {
					server.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			server = null;
		}
	}

	private static int getPort(String[] args) {
		if(args.length > 0) {
			try {
				return Integer.parseInt(args[0]);
			} catch (NumberFormatException e) {
				return 9999;
			}
		}else {
			return 9999;
		}
	}
	
	static class Handler implements Runnable{

		Socket socket = null;
		public Handler(Socket socket) {
			this.socket = socket;
		}
		
		@Override
		public void run() {
			BufferedReader reader = null;
			PrintWriter writer = null;

			try {
				reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
				writer = new PrintWriter(socket.getOutputStream(), true);
				String message = null;
				while(true) {
					System.out.println("server reading ... ");
					if((message = reader.readLine()) == null) {
						break;
					}
					System.out.println(message);
					writer.println("server recive:"+message);
					writer.flush();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}finally {
				if(socket!=null) {
					try {
						socket.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				socket = null;
				if(reader != null) {
					try {
						reader.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				reader = null;
				if(writer!=null) {
					writer.close();
				}
				writer = null;
			}
		}
		
	}
}
