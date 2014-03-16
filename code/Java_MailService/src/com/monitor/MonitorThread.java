package com.monitor;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import com.util.Util;

public class MonitorThread implements Runnable {

	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			ServerSocket server = new ServerSocket(8888, 0, InetAddress.getLocalHost());
			Util.saveLog("�ɹ����������÷�����ip��"+InetAddress.getLocalHost().getHostAddress()+"�˿ںţ�"+server.getLocalPort()+"��ʱ��"+Util.getNowTime());
			//ʱ�̼����ö˿ڣ������û�����ʱ�������û����ò˵���
			while(true){
				Socket clientSocket = server.accept();
				//���ͻ��ν���ʱ������һ���̣߳�ʵ�����÷�������Ϣ��
				MonitorSubThread msth = new MonitorSubThread(clientSocket);
				Thread clinetThread = new Thread(msth);
				clinetThread.start();
			}
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
