package com.sendMail;

import java.io.IOException;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import com.bean.LocalServiceBean;
import com.onceClass.OnceClass;
import com.util.Util;

public class SendMailService implements Runnable {

	/**
	 * �����ʼ��ķ������̣߳��÷������Ķ˿ںŴ��Ѷ����map�л�ȡ
	 */
	
	//SMTP������������״̬
	private boolean flag = true;
	public void setSMTPServiceState(){
		if(this.flag == false){
			this.flag = true;
			System.out.println("���ú��flag:"+this.flag);
		}else{
			this.flag = false;
			System.out.println("���ú��flag:"+this.flag);
		}
		System.out.println("setSMTPServiceState");
	}
	@Override
	public void run() {
		//ʵ���������ʼ��ķ��������󣬶���˿ںţ�25
		LocalServiceBean localService = OnceClass.getOnce().getLocalServiceInfo();
		int port = Integer.parseInt(localService.getStmpPort());
		String ip = "0.0.0.0";
		try {
			ip = InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			ServerSocket sendMailServerSocket = new ServerSocket(port, 0, InetAddress.getLocalHost());
			Util.saveLog("�ɹ�������ESMTP�ʼ�������ip��"+ip+"�˿ںţ�"+port+"��ʱ��"+Util.getNowTime());
			//��������Ϣ���浽serverSocketMap��
			OnceClass.getOnce().getServerSocketMap().put("sendMailServerSocket", sendMailServerSocket);
			while(flag){
				//���������ɹ���Ϣ
				Socket client = sendMailServerSocket.accept();
				//����һ�������ʼ����̣߳���
				SendMailThread smTh = new SendMailThread(client);
				Thread sentTh = new Thread(smTh);
				sentTh.start();
			}
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			Util.saveLog("SMTP������ip��"+ip+"�˿ںţ�"+port+"ֹͣ���С�ʱ�䣺"+Util.getNowTime());
		}
	}
}
