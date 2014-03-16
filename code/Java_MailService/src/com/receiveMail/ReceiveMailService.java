package com.receiveMail;

import java.io.IOException;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import com.bean.LocalServiceBean;
import com.onceClass.OnceClass;
import com.util.Util;

public class ReceiveMailService implements Runnable {
	/**
	 * �����ʼ��ķ������̣߳�POP3
	 */
	//ĳ���û��Ľ����ʼ��ķ�����
	
	//��ȡ��������������Ϣ
	LocalServiceBean localService = OnceClass.getOnce().getLocalServiceInfo();
	
	//���ܷ������������б�־��
	private boolean receiveServerFlag = true;
	public void setPOP3ServiceState(){
		if(this.receiveServerFlag == false){
			this.receiveServerFlag = true;
		}else{
			this.receiveServerFlag = false;
		}
	}
	@Override
	public void run() {
		//ʵ���������ʼ����������̣߳��˿ںŴ�userBean�л�ȡ
		int port = Integer.parseInt(localService.getPop3Port());
		String ip = "0.0.0.0";
		try {
			ip = InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			ServerSocket receiveMailServerSocket = new ServerSocket(port, 0, InetAddress.getLocalHost());
			Util.saveLog("�ɹ�������POP3�ʼ�������ip��"+ip+"�˿ںţ�"+receiveMailServerSocket.getLocalPort()+"��ʱ��"+Util.getNowTime());
			//��������Ϣ���浽serverSocketMap��
			OnceClass.getOnce().getServerSocketMap().put("receiveMailServerSocket", receiveMailServerSocket);
			while(receiveServerFlag){
				Socket receiveSocket = receiveMailServerSocket.accept();
				ReceiveMailThread receMail = new ReceiveMailThread(receiveSocket);
				Thread receTh = new Thread(receMail);
				receTh.start();
//				System.out.println(receiveSocket);
			}
		} catch (UnknownHostException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
//			e1.printStackTrace();
			Util.saveLog("POP3������ip��"+ip+"�˿ںţ�"+port+"ֹͣ���С�ʱ�䣺"+Util.getNowTime());
		}
	}
}
