package com.koolyun.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Scanner;
/**
 * NIO��� Unblocking IO��New IO����ͬ���������ı�̷�ʽ
 * NIO�����ǻ����¼�����˼������ɵģ�ֻҪ��������BIO�Ĵ󲢷����⣬NIO����Reactor��
 * ��socket�����ɶ����д��socketʱ������ϵͳ����Ӧ֪ͨ���ó�����д���Ӧ���ٽ�����
 * ȡ����������д�����ϵͳ��Ҳ����˵�����ʱ���Ѿ�����һ�����Ӿ�Ҫ��Ӧһ�������߳��ˣ�����
 * ��Ч�����󣬶�Ӧһ���̣߳�������û������ʱ����û�й����߳�������ġ�
 * 
 * NIO������Ҫ�ĵط��ǵ�һ�����Ӵ����󣬲���Ҫ��Ӧһ���̣߳�������ӻᱻע�ᵽ��·���������棬
 * �������е�����ֻ��Ҫһ���߳̾Ϳ��Ը㶨��������߳��еĶ�·������������ѯ��ʱ�򣬷���������
 * ������Ļ����ſ���һ���߳̽��д���Ҳ����һ������һ���߳�ģʽ��
 * 
 * ��NIO�Ĵ���ʽ�У���һ���������Ļ��������߳̽��д������ܻ�ȴ����Ӧ�õ���Դ��JDBC���ӵȣ���
 * ��ʵ����߳̾ͱ������ˣ������������Ļ������ǻ���BIOһ�������⡣
 * 
 * ͬ����������������ʵ��ģʽΪһ������һ��ͨ�������ͻ��˷��͵��������󶼻�ע�ᵽ��·�������ϣ�
 * ��·��������ѯ��������I/O����ʱ������һ���߳̽��д���
 * 
 * NIO��ʽ������������Ŀ�������ӱȽ϶̣���������ļܹ����������������������������
 * Ӧ���У���̸��ӣ�JDK1.4��ʼ֧�֡�
 * 
 * @author xiaoming
 *
 */
public class NIOClient {

	@SuppressWarnings("resource")
	public static void main(String[] args) {
		//Զ�̵�ַ����
		InetSocketAddress remote = new InetSocketAddress("localhost", 9999);
		SocketChannel channel = null;
		
		//���建��
		ByteBuffer buffer = ByteBuffer.allocate(1024);
		
		try {
			//����ͨ��
			channel = SocketChannel.open();
			//����Զ�̷�����
			channel.connect(remote);
			Scanner reader = new Scanner(System.in);
			while(true) {
				System.out.println("put message for send to server >");
				String line = reader.nextLine();
				if(line.equals("exit")) {
					break;
				}
				//������̨���������д�뵽����
				buffer.put(line.getBytes("UTF-8"));
				//�����α�
				buffer.flip();
				//�����ݷ��͸�������
				channel.write(buffer);
				//��ջ������ݡ�
				buffer.clear();
				
				//��ȡ���������ص�����
				int readLength = channel.read(buffer);
				if(readLength == -1) {
					break;
				}
				//�����α�
				buffer.flip();
				byte[] datas = new byte[buffer.remaining()];
				//��ȡ���ݵ��ֽ����顣
				buffer.get(datas);
				System.out.println("from server : " + new String(datas, "UTF-8"));
				//��ջ���
				buffer.clear();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
			if(null != channel) {
				try {
					channel.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
