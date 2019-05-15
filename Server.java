package cn.itcast_01;
/**
 * 	服务器端
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
	 * 	运行在服务器端的ServerSocket主要负责：
	 * 1.向系统申请服务端口，客户端通过这个端口与之相连
	 * 2.监听申请的服务端口，当一个客户端通过该端口尝试连接时，
	 * ServerSocket会在服务端创建一个Socket与之相连
	 */
	private ServerSocket serversocket;
	/**
	 * 	保存所有客户端输出流集合
	 */
	private List<PrintWriter> allOut;
	/**
	 * 	服务端构造方法
	 */
	public Server() throws Exception{
		/**
		 * 	初始化的同时申请服务端口
		 */
		serversocket=new ServerSocket(12891);
		allOut=new ArrayList<PrintWriter>();
	}
	/**
	 * 将给定的输出流存入共享集合
	 * @param out
	 */
	private synchronized void addOut(PrintWriter out) {
		allOut.add(out);
	}
	/**
	 * 将给定的输出流从共享集合中删除
	 * @param out
	 */
	private synchronized void removeOut(PrintWriter out) {
		allOut.remove(out);
	}
	/**
	 * 	将给定的信息发送到所有客户端
	 * @param message
	 */
	private synchronized void sendMessage(String message) {
		for(PrintWriter out:allOut) {
			out.println(message);
		}
	}
	
	/**
	 * 	服务端开启方法
	 */
	public void start() {
		try {
			/**
			 * 这是一个阻塞方法，作用是监听服务端口，知道一个客户端连接，并创建一个Socket，
			 * 使用该socket即可与服务端进行交互
			 * */
			while(true) {
				System.out.println("等待客户端连接。。。");
				Socket socket=serversocket.accept();
				System.out.println("一个客户端已经连接！");
				/**
				 * 启动一个线程，来完成客户端的交互
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
			System.out.println("服务端启动失败");
		}
	}
	
	/***
	 * 该线程负责处理一个客户端的交互
	 * @author shixuewei
	 *
	 */
	
	class ClientHalder implements Runnable{
		/**
		 * 该线程处理的客户端的Socket
		 */
		private Socket socket;
		private String host;
		private int port;
		//聊天昵称
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
				 * 	接受客户端发送的数据
				 */
				InputStream is=socket.getInputStream();
				InputStreamReader isr=new InputStreamReader(is,"utf-8");
				br=new BufferedReader(isr);
				//首先读取一行字符串当作昵称
				name=br.readLine();
				sendMessage(name+"-"+host+"-"+port+":  "+"上线了");
				
				/**
				 * 	通过Socket创建输出流，用于将消息发送给客户端
				 * 
				 */
				OutputStream ops=socket.getOutputStream();
				OutputStreamWriter osw=new OutputStreamWriter(ops, "utf-8");
				pw=new PrintWriter(osw,true);
				
				/**
				 * 	将该集合的输出流存入到共享集合中
				 */
				addOut(pw);
				
				String str=null;
				while((str=br.readLine())!=null) {
					//System.out.println(host+"---"+port+"say:"+str);
					//pw.write(host+"-"+"port"+":  "+str);
					/**
					 * 	广播消息
					 */
					sendMessage(name+"-"+host+"-"+"port"+":  "+str);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}finally {
				/***
				 * 处理当前客户端断开后的逻辑
				 */
				
				/***
				 * 	将该客户端的输出流从共享集合中删除
				 */
				removeOut(pw);
				
				//System.out.println(host+"-"+port+":  "+"下线了");
				sendMessage(name+"-"+host+"-"+port+":  "+"下线了");
				try {
					socket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
		}
		
	}
}
