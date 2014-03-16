package com.service.main;


import java.io.IOException;

import com.bean.LocalServiceBean;
import com.initializa.Initilize;
import com.monitor.MonitorThread;
import com.onceClass.OnceClass;
import com.receiveMail.ReceiveMailService;
import com.receiveMail.ReceiveMailThread;
import com.sendMail.SendMailService;
import com.sendMail.SendMailThread;
import com.util.Util;


public class MailServiceMain {
	/**
	 * 	�ʼ������������������������������̡�
	 *  �ʼ��������̰߳�����
	 *  	1.�����ʼ���������POP3
	 *  	2.�����ʼ���������SMTP
	 * 	������������Ϣ��
	 * 		1.�����ʼ��������˿�
	 * 		2.�����ʼ��������˿�
	 */
	//����飬��ʼ����Ϣ
	static {
		Initilize init = new Initilize();
		init.initUser();
	}
	//��ʶ����������������״̬
	public static boolean stmpFlag = false;
	public static boolean pop3Flag = false;
	public static void main(String[] args) {
		boolean flag = false; //��ʶ������������״̬

		//��ȡ�̵߳�����״̬
		LocalServiceBean localService = OnceClass.getOnce().getLocalServiceInfo();
		if(localService.getServiceState().equals("running")){
			//��ʼ���߳�����״̬
			flag = true;
		}else if(localService.getServiceState().equals("shutdown")){
			flag = false;
		}
		//�ֱ�ʵ����receiveMmailThread��sendMailThread����ʵ�����̶߳�������mail��������
		if(flag){
			//���������ʼ�����������
			ReceiveMailService rms = new ReceiveMailService();
			Thread rmsTh = new Thread(rms);
			rmsTh.start();
			//���������ʼ�����������
			SendMailService sms = new SendMailService();
			Thread smsTh = new Thread(sms);
			smsTh.start();
		}else{
			Util.saveLog("���߳������ɹ�����SMTP��������POP�����������رա������Ա��¼������ʱ��:"+Util.getNowTime());
		}
		//������ؽ��̣�ʱ�̼���û�����Ϣ����������������Ϣ��
		MonitorThread moni = new MonitorThread();
		Thread moniTh = new Thread(moni);
		moniTh.start();
		
		System.out.println("�ʼ������������������ɹ���ʱ��:"+Util.getNowTime());
		//��ѭ��ɨ�����
		OnceClass once = OnceClass.getOnce();
//		while(true){
//			//sleep();���������ѹ��
//			if(once.getServerSocketMap().size() != 0){
//				if(stmpFlag){
//					try {
//						once.getServerSocketMap().get("sendMailServerSocket").close();
//						//���������ʼ�����������
//						SendMailService sms = new SendMailService();
//						Thread smsTh = new Thread(sms);
//						smsTh.start();
//						
//						stmpFlag = false;
//					} catch (IOException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//				}
//				if(pop3Flag){
//					try {
//						once.getServerSocketMap().get("receiveMailServerSocket").close();
//						//���������ʼ�����������
//						ReceiveMailService rms = new ReceiveMailService();
//						Thread rmsTh = new Thread(rms);
//						rmsTh.start();
//						
//						pop3Flag = false;
//					} catch (IOException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//				}
//			}
//		}
	}
}
