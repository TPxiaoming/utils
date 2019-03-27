package com.koolyun.nio;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Scanner;

import com.sun.corba.se.pept.transport.Acceptor;

public class NIOServer implements Runnable{
	
	//��·��������ѡ����������ע��ͨ���ġ�
	private Selector selector;
	//�������������档�ֱ����ڶ���д����ʼ���ռ��С��λΪ�ֽڡ�
	private ByteBuffer readBuffer = ByteBuffer.allocate(1024);
	private ByteBuffer writeBuffer = ByteBuffer.allocate(1024);

	public static void main(String[] args) throws Exception {
		new Thread(new NIOServer(9999)).start();;
	}
	
	public NIOServer(int port) throws Exception {
		init(port);
	}

	private void init(int port) throws Exception {
		System.out.println("server starting at port " + port + "...");
		//������·������
		this.selector = Selector.open();
		//��������ͨ��
		ServerSocketChannel socketChannel = ServerSocketChannel.open();
		//��������������ݲ���Ϊtrue��Ϊ����ģʽ
		socketChannel.configureBlocking(false);
		//�󶨶˿�
		socketChannel.bind(new InetSocketAddress(port));
		//ע�ᣬ����ǵ�ǰ����ͨ��״̬
		/*
		 * register(Selector, int)
		 * int - ״̬����
		 *  OP_ACCEPT �� ���ӳɹ��ı��λ��
		 *  OP_READ �� ���Զ�ȡ���ݵı��
		 *  OP_WRITE �� ����д�����ݵı��
		 *  OP_CONNECT �� ���ӽ�����ı��
		 */
		socketChannel.register(this.selector, SelectionKey.OP_ACCEPT);
		
	}

	@Override
	public void run() {
		while(true) {
			try {
				//����������������һ��ͨ����ѡ�У��˷�������
				//ͨ���Ƿ�ѡ����ע�ᵽ��·�������е�ͨ����Ǿ�����
				this.selector.select();
				//������ѡ�е�ͨ����Ǽ��ϣ������б������ͨ���ı�ǡ��൱����ͨ����ID
				Iterator<SelectionKey> keys = this.selector.selectedKeys().iterator();
				while(keys.hasNext()) {
					SelectionKey key = keys.next();
					//������Ҫ�����ͨ���Ӽ�����ɾ�����´�ѭ�������µ�ͨ���б��ٴ�ִ�б�Ҫ��ҵ���߼�
					keys.remove();
					//ͨ���Ƿ���Ч
					if(key.isValid()) {
						//����״̬
						try {
							if(key.isAcceptable()) {
								accept(key);
							}
						} catch (Exception e) {
							//�Ͽ����ӡ� �����쳣
							key.channel();
						}
						//�ɶ�״̬
						try {
							if(key.isReadable()) {
								read(key);
							}
						} catch (Exception e) {
							//�Ͽ����ӡ� �����쳣
							key.channel();
						}
						//��д״̬
						try {
							if(key.isWritable()) {
								write(key);
							}
						} catch (Exception e) {
							//�Ͽ����ӡ� �����쳣
							key.channel();
						}
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void write(SelectionKey key) {
		//��ն�����
		this.writeBuffer.clear();
		//��ȡͨ��
		SocketChannel channel = (SocketChannel) key.channel();
		Scanner reader = new Scanner(System.in);
		
		try {
			System.out.println("put message for send to client > ");
			String line = reader.nextLine();
			//������̨������ַ���д��Buffer�С�д���������һ���ֽ����顣
			writeBuffer.put(line.getBytes("UTF-8"));
			writeBuffer.flip();
			channel.write(writeBuffer);
			
			//ע��ͨ�������Ϊ������
			channel.register(this.selector, SelectionKey.OP_READ);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void read(SelectionKey key) {
		try {
			//��ն�����
			this.readBuffer.clear();
			//��ȡͨ��
			SocketChannel channel = (SocketChannel) key.channel();
			//��ͨ���е����ݶ�ȡ�������С� ͨ���е����ݣ����ǿͻ��˷��͸�����˵����ݡ�
			int readLength = channel.read(readBuffer);
			//���ͻ����Ƿ�д�����ݡ�
			if(readLength == -1) {
				//�ر�ͨ��
				key.channel().close();
				//�ر�����
				key.cancel();
				return;
			}
			/*
			 * flip�� NIO����ӵĲ�������Buffer�Ŀ��ơ�
			 * Buffer����һ���αꡣ�α���Ϣ�ڲ����󲻻���㣬���ֱ�ӷ���Buffer�Ļ��������в�һ�µĿ��ܡ�
			 * flip�������α�ķ�����NIO����У�flip�����ǳ��÷�����
			 */
			this.readBuffer.flip();
			//�ֽ����飬����������ݵġ� Buffer.remaining() -> �ǻ�ȡBuffer����Ч���ݳ��ȵķ���
			byte[] datas = new byte[readBuffer.remaining()];
			//�ǽ�Buffer�е���Ч���ݱ��浽�ֽ�������
			readBuffer.get(datas);
			System.out.println("from" + channel.getRemoteAddress() + " client:" + new String(datas, "UTF-8"));
			
			//ע��ͨ�������Ϊд����
			channel.register(this.selector, SelectionKey.OP_WRITE);
		} catch (IOException e) {
			e.printStackTrace();
			try {
				key.channel().close();
				key.cancel();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}

	private void accept(SelectionKey key) {
		try {
			//��ͨ��Ϊinit������ע�ᵽSelector�ϵ�ServerSocketChannel
			ServerSocketChannel socketChannel = (ServerSocketChannel) key.channel();
			//�������������ͻ��˷�������󷵻ء���ͨ���Ϳͻ���һһ��Ӧ��
			SocketChannel channel = socketChannel.accept();
			channel.configureBlocking(false);
			//���ö�Ӧ�ͻ��˵�ͨ�����״̬����ͨ��Ϊ��ȡ����ʹ�õ�
			channel.register(this.selector, SelectionKey.OP_READ);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
