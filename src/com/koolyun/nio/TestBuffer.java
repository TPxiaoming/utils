package com.koolyun.nio;

import java.nio.Buffer;
import java.nio.ByteBuffer;
/**
 * buffer��Ӧ�ù̶��߼�
 * д����˳��
 * 1.clear()
 * 2.put() -> д����
 * 3.flip() -> �����α�
 * 4.SocketChannel.write(buffer); -> ���������ݷ��͵��������һ��
 * 5.chear()
 * 
 * ������˳��
 * 1.clear()
 * 2.SocketChannel.read(buffer); -> �������ж�ȡ����
 * 3.buffer.flip() -> �����α�
 * 4.buffer.get() -> ��ȡ����
 * 5.buffer.clear()
 * 
 * @author xiaoming
 *
 */
public class TestBuffer {
	public static void main(String[] args) {
		ByteBuffer buffer = ByteBuffer.allocate(8);
		
		byte[] temp = new byte[] {3,2,1};
		
		//д������֮ǰ��java.nio.HeapByteBuffer[pos=0 lim=8 cap=8]
		//pos - �α�λ�ã� lim - ���������� cap - �������
		System.out.println("д������֮ǰ��" + buffer);
		
		//д���ֽ����鵽����
		buffer.put(temp);
		
		//д������֮��java.nio.HeapByteBuffer[pos=3 lim=8 cap=8]
		//�α�Ϊ3�� ����Ϊ8������Ϊ8
		System.out.println("д������֮��" + buffer);
		
		//�����α�
		buffer.flip();
		
		//�����α�֮��java.nio.HeapByteBuffer[pos=0 lim=3 cap=8]
		//�α�Ϊ0�� ����Ϊ3������Ϊ8
		System.out.println("�����α�֮��" + buffer);
		
		// ���Buffer�� pos = 0; lim = cap;
		// buffer.clear();
		
		// get() -> ��ȡ��ǰ�α�ָ���λ�õ����ݡ�
		// System.out.println(buffer.get());
		
		for (int i = 0; i < buffer.remaining(); i++) {
			int data = buffer.get(i);
			System.out.println(i + "-" + data);
		}
		
	}

}
