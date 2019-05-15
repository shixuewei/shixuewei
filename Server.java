package cn.itcast_01;
/**
 * 	��������
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
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {
	/**
	 * 	�����ڷ������˵�ServerSocket��Ҫ����
	 * 1.��ϵͳ�������˿ڣ��ͻ���ͨ������˿���֮����
	 * 2.��������ķ���˿ڣ���һ���ͻ���ͨ���ö˿ڳ�������ʱ��
	 * ServerSocket���ڷ���˴���һ��Socket��֮����
	 */
	private ServerSocket serversocket;
	/**
	 * 	�������пͻ������������
	 */
	private List<PrintWriter> allOut;
	/**
	 * 	����˹��췽��
	 */
	public Server() throws Exception{
		/**
		 * 	��ʼ����ͬʱ�������˿�
		 */
		serversocket=new ServerSocket(12891);
		allOut=new ArrayList<PrintWriter>();
	}
	/**
	 * ����������������빲����
	 * @param out
	 */
	private synchronized void addOut(PrintWriter out) {
		allOut.add(out);
	}
	/**
	 * ��������������ӹ�������ɾ��
	 * @param out
	 */
	private synchronized void removeOut(PrintWriter out) {
		allOut.remove(out);
	}
	/**
	 * 	����������Ϣ���͵����пͻ���
	 * @param message
	 */
	private synchronized void sendMessage(String message) {
		for(PrintWriter out:allOut) {
			out.println(message);
		}
	}
	
	/**
	 * 	����˿�������
	 */
	public void start() {
		try {
			/**
			 * ����һ�����������������Ǽ�������˿ڣ�֪��һ���ͻ������ӣ�������һ��Socket��
			 * ʹ�ø�socket���������˽��н���
			 * */
			while(true) {
				System.out.println("�ȴ��ͻ������ӡ�����");
				Socket socket=serversocket.accept();
				System.out.println("һ���ͻ����Ѿ����ӣ�");
				/**
				 * ����һ���̣߳�����ɿͻ��˵Ľ���
				 */
				ClientHalder ch=new ClientHalder(socket);
				Thread t=new Thread(ch);
				t.start();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		try {
			Server server=new Server();
			server.start();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("���������ʧ��");
		}
	}
	
	/***
	 * ���̸߳�����һ���ͻ��˵Ľ���
	 * @author shixuewei
	 *
	 */
	
	class ClientHalder implements Runnable{
		/**
		 * ���̴߳���Ŀͻ��˵�Socket
		 */
		private Socket socket;
		private String host;
		private int port;
		//�����ǳ�
		private String name;
		
		public ClientHalder(Socket socket) {
			this.socket = socket;
			InetAddress address=socket.getInetAddress();
			host=address.getHostAddress();
			port=socket.getPort();
		}

		@Override
		public void run() {
			BufferedReader br=null;
			PrintWriter pw=null;
			try {
				/***
				 * 	���ܿͻ��˷��͵�����
				 */
				InputStream is=socket.getInputStream();
				InputStreamReader isr=new InputStreamReader(is,"utf-8");
				br=new BufferedReader(isr);
				//���ȶ�ȡһ���ַ��������ǳ�
				name=br.readLine();
				sendMessage(name+"-"+host+"-"+port+":  "+"������");
				
				/**
				 * 	ͨ��Socket��������������ڽ���Ϣ���͸��ͻ���
				 * 
				 */
				OutputStream ops=socket.getOutputStream();
				OutputStreamWriter osw=new OutputStreamWriter(ops, "utf-8");
				pw=new PrintWriter(osw,true);
				
				/**
				 * 	���ü��ϵ���������뵽��������
				 */
				addOut(pw);
				
				String str=null;
				while((str=br.readLine())!=null) {
					//System.out.println(host+"---"+port+"say:"+str);
					//pw.write(host+"-"+"port"+":  "+str);
					/**
					 * 	�㲥��Ϣ
					 */
					sendMessage(name+"-"+host+"-"+"port"+":  "+str);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}finally {
				/***
				 * ����ǰ�ͻ��˶Ͽ�����߼�
				 */
				
				/***
				 * 	���ÿͻ��˵�������ӹ�������ɾ��
				 */
				removeOut(pw);
				
				//System.out.println(host+"-"+port+":  "+"������");
				sendMessage(name+"-"+host+"-"+port+":  "+"������");
				try {
					socket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
		}
		
	}
}
