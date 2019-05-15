package cn.itcast_01;
/**
 *	 聊天室客户端
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
	//封装了TCP协议,使用它就可以基于TCP协议进行网络通讯
	private Socket socket;

	
	/**
	  * 构造方法，用于初始化客户端
	 */
	public Client() throws Exception{
		socket=new Socket("localhost", 12891);
		System.out.println("已与服务端建立联系");
	}
	
	/**
	 * 	启动客户端的方法
	 */
	public void start() {
		/***
		 * 	将字符串发送到服务端
		 */
		PrintWriter pw=null;
		try {
			Scanner sc=new Scanner(System.in);
			
			/**
			 * 	先要求用户输入一个昵称
			 */
			String name=null;
			while(true) {
				System.out.println("请输入一个用户名：");
				name=sc.nextLine();
				if(name.length()>0) {
					break;
				}else {
					System.out.println("输入有误");
				}
			}
			System.out.println("Welcome"+name);
			
			OutputStream os=socket.getOutputStream();//字节流（低级）
			OutputStreamWriter osw=new OutputStreamWriter(os, "utf-8");//转换流（高级）
			pw=new PrintWriter(osw,true);//打印流（高级）
			
			/**
			 * 先将昵称发给服务器
			 */
			pw.println(name);
			
			
			/***
			 * 	启动读取服务端发送过来的消息的进程
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
			System.out.println("客户端启动失败");
		}
	}
	
	/***
	 * 	该线程用来读取服务端发送过来的消息，
	 * 	并输出到控制台显示
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
