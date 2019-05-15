package cn.itcast_01;
/**
 *	 �����ҿͻ���
 * @author shixuewei
 *
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client {
	//��װ��TCPЭ��,ʹ�����Ϳ��Ի���TCPЭ���������ͨѶ
	private Socket socket;

	
	/**
	  * ���췽�������ڳ�ʼ���ͻ���
	 */
	public Client() throws Exception{
		socket=new Socket("localhost", 12891);
		System.out.println("�������˽�����ϵ");
	}
	
	/**
	 * 	�����ͻ��˵ķ���
	 */
	public void start() {
		/***
		 * 	���ַ������͵������
		 */
		PrintWriter pw=null;
		try {
			Scanner sc=new Scanner(System.in);
			
			/**
			 * 	��Ҫ���û�����һ���ǳ�
			 */
			String name=null;
			while(true) {
				System.out.println("������һ���û�����");
				name=sc.nextLine();
				if(name.length()>0) {
					break;
				}else {
					System.out.println("��������");
				}
			}
			System.out.println("Welcome"+name);
			
			OutputStream os=socket.getOutputStream();//�ֽ������ͼ���
			OutputStreamWriter osw=new OutputStreamWriter(os, "utf-8");//ת�������߼���
			pw=new PrintWriter(osw,true);//��ӡ�����߼���
			
			/**
			 * �Ƚ��ǳƷ���������
			 */
			pw.println(name);
			
			
			/***
			 * 	������ȡ����˷��͹�������Ϣ�Ľ���
			 */
			ServerHalder sh=new ServerHalder();
			Thread t=new Thread(sh);
			t.start();
			
			while(true) {
				pw.println(sc.nextLine());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
			pw.close();
		}
		
	}
	
	public static void main(String[] args) {
		try {
			Client client=new Client();
			client.start();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("�ͻ�������ʧ��");
		}
	}
	
	/***
	 * 	���߳�������ȡ����˷��͹�������Ϣ��
	 * 	�����������̨��ʾ
	 */
	
	class ServerHalder implements Runnable{
		
		private Socket socket;
		@Override
		public void run() {
			try {
				InputStream is=socket.getInputStream();
				InputStreamReader isr=new InputStreamReader(is,"utf-8");
				BufferedReader br=new BufferedReader(isr);
				String str=null;
				while((str=br.readLine())!=null) {
					System.out.println(str);
				}
			} catch (Exception e) {
				
			}
			
		}
		
	}
}
