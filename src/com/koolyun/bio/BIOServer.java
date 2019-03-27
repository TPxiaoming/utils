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
 * Blocking IO： 同步阻塞的编程方式。 
 * BIO 编程方式通常是在 JDK1.4 版本之前常用的编程方式。
 * 编程实现过程为：
 * 首先在服务 端启动一个 ServerSocket 来监听网络请求，
 * 客户端启动 Socket 发起网络请求，默认情况下 ServerSocket 
 * 会建立一个线程来处理此请求，如果服务端没有线程可用，客户端则会阻塞等 待或遭到拒绝。 
 * 
 * 同步并阻塞，服务器实现模式为一个连接一个线程，
 * 即客户端有连接请求时服务器端就 需要启动一个线程进行处理，
 * 如果这个连接不做任何事情会造成不必要的线程开销，当然可 以通过线程池机制改善。 
 * 
 * BIO 方式适用于连接数目比较小且固定的架构，这种方式对服务器资源要求比较高，
 * 并发局限于应用中，JDK1.4 以前的唯一选择，但程序直观简单易理解。
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
