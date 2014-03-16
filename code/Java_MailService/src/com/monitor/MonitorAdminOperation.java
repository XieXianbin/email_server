package com.monitor;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.bean.ForeignServiceBean;
import com.bean.LocalServiceBean;
import com.bean.UserBean;
import com.onceClass.OnceClass;
import com.receiveMail.ReceiveMailService;
import com.receiveMail.ReceiveMailThread;
import com.sendMail.SendMailService;
import com.service.main.MailServiceMain;
import com.util.Util;

public class MonitorAdminOperation {

	/**
	 * ����Ա����ز�����
	 * 		1.���÷������˿ں�
	 * 		2.�����ʼ��Ľ���·��
	 *		3.�����ʼ������״̬
	 * 		4.���ø��ʼ�ϵͳ�ĺ�׺
	 */
	private PrintStream ps;
	private BufferedReader br;
	private Socket socket;
	public MonitorAdminOperation(PrintStream ps, BufferedReader br, Socket socket) {
		// TODO Auto-generated constructor stub
		this.ps = ps;
		this.br = br;
		this.socket = socket;
	}
	
	private OnceClass once;
	private LocalServiceBean localServiceInfo;
	//����ɹ�������˵�
	public String adminLoginSuccess(String userName) {
		once = OnceClass.getOnce();
		while(true){
			// TODO Auto-generated method stub
			Util.printlns(ps, "���˵���1.���÷������˿ں�\t2.�����ʼ��Ľ���·��\t3.�����ʼ������״̬\r\n\t4.���ø��ʼ�ϵͳ�ĺ�׺\t5.���������ʼ��ṩ�̵���Ϣ\r\n\t6.�鿴�����û�\t7.�˳�\t������������");
			String subMenu = Util.getInput(br);
			if(subMenu == null){
				return "NullPointerException";	
			}
			if(subMenu.equals("1")){
				while(true){
					//��ȡ���ص�
					
					//���÷������Ķ˿ں�
					Util.printlns(ps, "�Ӳ˵���1.����STMP�˿ں�\t2.����POP3�˿ں�\t���������ϼ�");
					String subsubMenu = Util.getInput(br);
					if(subsubMenu == null){
						return "NullPointerException";
					}
//////////////////////////////////�˿ںű�ռ��δ��������������������������
					localServiceInfo = once.getLocalServiceInfo();
					if(subsubMenu.equals("1")){
						//�����ʼ����Ͷ˿ں�
						
						Util.println(ps, "��ǰ�ʼ����Ͷ˿ںţ�"+localServiceInfo.getStmpPort());
						Util.printlns(ps, "�������µĶ˿ں�:(����)");
						String newSentPort = Util.getInput(br);
						if(newSentPort == null){
							return "NullPointerException";
						}
//						int newPort = Integer.parseInt(newSentPort);
						localServiceInfo.setStmpPort(newSentPort);
						//д���ļ�
						Util.write2LocalServiceFile();
						Util.println(ps, "����STMP�˿ڳɹ���������������Ч��");
					}else if(subsubMenu.equals("2")){
						//�����ʼ����ն˿ں�
						Util.println(ps, "��ǰ�ʼ����ն˿ںţ�"+localServiceInfo.getPop3Port());
						Util.printlns(ps, "�������µĶ˿ں�:(����)");
						String newReceivePort = Util.getInput(br);
						if(newReceivePort == null){
							return "NullPointerException";
						}
//						int newPort = Integer.parseInt(newReceivePort);
						
						localServiceInfo.setPop3Port(newReceivePort);
						//д���ļ�
						Util.write2LocalServiceFile();
						Util.println(ps, "����POP3�ɹ���������Ч��");
					}else{
						break;
					}
				}
			}else if(subMenu.equals("2")){
				//�����û����ʼ�����·��
				localServiceInfo = once.getLocalServiceInfo();
				Util.printlns(ps, "��ǰ�ʼ���Ĭ��·��Ϊ��"+localServiceInfo.getMailPath()+"�������µ�·����");
				String mailPath = Util.getInput(br);
				if(mailPath == null){
					return "NullPointerException";
				}
				//�ж��Ƿ�Ϊ·��
				File mailFile = new File(mailPath);
				if(mailFile.isAbsolute() && !mailFile.isFile()){
					//�ж��Ƿ���ڣ��������ڣ��򴴽���
					if(!mailFile.exists()){
						mailFile.mkdirs();
					}
					//���浽user
					localServiceInfo.setMailPath(mailPath);
					//д���ļ�
					Util.write2LocalServiceFile();
					Util.println(ps, "�����ʼ�·���ɹ���������Ч��");
				}else{
					Util.println(ps, "������Ĳ���һ��·��������ʧ�ܡ�");
				}
				
			}else if(subMenu.equals("3")){
				/**
				 * ���÷�����������״̬:
				 * 		������running
				 * 		ֹͣ��shutdown
				 *	���д���ļ�
				 */
				once = OnceClass.getOnce();
				localServiceInfo = once.getLocalServiceInfo();
				while(true){
					localServiceInfo = once.getLocalServiceInfo();
					Util.println(ps, "��ǰ������������״̬��"+localServiceInfo.getServiceState());
					Util.printlns(ps, "�˵���1.��������/ֹͣ\t2.����������\t3.�����ϼ�\t������������");
					String subMenu3 = Util.getInput(br);
					if(subMenu3 == null){
						return "NullPointerException";
					}
					if(subMenu3.equals("1")){
						//���÷�����������״̬������ǰΪ���У�����Ϊֹͣ��
						once = OnceClass.getOnce();
						Map<String, ServerSocket> serverSocketMap = once.getServerSocketMap();
						if(localServiceInfo.getServiceState().equals("running")){
							localServiceInfo.setServiceState("shutdown");
							Util.println(ps, "5s��POP3������ֹͣ����...");
							try {
								Thread.sleep(5000);
								serverSocketMap.get("receiveMailServerSocket").close();
								serverSocketMap.remove("receiveMailServerSocket");
								Util.println(ps, "POP3��������ֹͣ���С��˿ںţ�"+localServiceInfo.getPop3Port()+"��������ʱ�䣺"+Util.getNowTime());
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							Util.println(ps, "5s��SMTP������ֹͣ����...");
							try {
								Thread.sleep(5000);
								serverSocketMap.get("sendMailServerSocket").close();
								serverSocketMap.remove("sendMailServerSocket");
								Util.println(ps, "SMTP��������ֹͣ���С��˿ںţ�"+localServiceInfo.getStmpPort()+"��������ʱ�䣺"+Util.getNowTime());
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							
							Util.write2LocalServiceFile();
						}else if(localServiceInfo.getServiceState().equals("shutdown")){
							localServiceInfo.setServiceState("running");
							Util.write2LocalServiceFile();
							//����POP3������
							ReceiveMailService rms = new ReceiveMailService();
							Thread rmsTh = new Thread(rms);
							rmsTh.start();
							Util.println(ps, "POP3�����������ɹ����˿ں�Ϊ��"+localServiceInfo.getPop3Port()+"��ʱ��Ϊ��"+Util.getNowTime());
							//����SMTP������
							SendMailService sms = new SendMailService();
							Thread smsTh = new Thread(sms);
							smsTh.start();
							Util.println(ps, "SMTP�����������ɹ����˿ں�Ϊ��"+localServiceInfo.getStmpPort()+"��ʱ��Ϊ��"+Util.getNowTime());
						}else{
							localServiceInfo.setServiceState("running");
							Util.write2LocalServiceFile();
						}
						//Util.println(ps, "���óɹ���������Ч��");
					}else if(subMenu3.equals("2")){
						/**����������
						 * 		1. ���������У�������SMTP��������POP3�����������б�־λ flagΪfalse��
						 * 		2. ������һ���̺߳󣬰���Ϣput����Ӧmap
						 * 		3. ���÷�����������״̬Ϊtrue��
						 * 		4. ����SMTP��������POP3��������
						 * ���η�����
						 * 		1. 
						*/
						//
						SendMailService send = new SendMailService();
						ReceiveMailService rece = new ReceiveMailService();
						
						if(once.getServerSocketMap().size() != 0){
							int oldSMTPPort = once.getServerSocketMap().get("sendMailServerSocket").getLocalPort();
							int oldPOP3Port = once.getServerSocketMap().get("receiveMailServerSocket").getLocalPort();
							int newPOP3Port = Integer.parseInt(once.getLocalServiceInfo().getPop3Port());
							int newSMTPPort = Integer.parseInt(once.getLocalServiceInfo().getStmpPort());
							if(oldSMTPPort == newSMTPPort && oldPOP3Port == newPOP3Port){
								//����������δ���������ʧ��
								Util.println(ps, "����������δ���������ʧ��");
								continue;
							}
						
							//�ֱ�ر�����������
							once = OnceClass.getOnce();
							System.err.println("��ǰϵͳ���߳�����"+Thread.activeCount()+"��");
							Util.println(ps, "ϵͳ������...");
							if(newPOP3Port != oldPOP3Port){
								//�ر��߳�
	//							rece.setPOP3ServiceState();
								once = OnceClass.getOnce();
								Map<String, ServerSocket> serverSocketMap = once.getServerSocketMap();
								try {
//									System.out.println(serverSocketMap.get("receiveMailServerSocket"));
									Util.println(ps, "5s��POP3������������");
									Thread.sleep(5000);
									serverSocketMap.get("receiveMailServerSocket").close();
									serverSocketMap.remove("receiveMailServerSocket");
									Util.println(ps, "POP3�����������ɹ����˿ںţ�"+localServiceInfo.getPop3Port()+"��������ʱ�䣺"+Util.getNowTime());
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (InterruptedException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								
								ReceiveMailService rms = new ReceiveMailService();
								Thread rmsTh = new Thread(rms);
								rmsTh.start();
//								rece.setPOP3ServiceState();
//								rms = new ReceiveMailService();
//								rmsTh = new Thread(rms);
//								rmsTh.start();
							}
							if(newSMTPPort != oldSMTPPort){
								//�ر��߳�
	//							send.setSMTPServiceState();
								once = OnceClass.getOnce();
								Map<String, ServerSocket> serverSocketMap = once.getServerSocketMap();
								try {
//									System.out.println(serverSocketMap.get("sendMailServerSocket"));
									Util.println(ps, "5s��SMTP������������");
									Thread.sleep(5000);
									serverSocketMap.get("sendMailServerSocket").close();
									serverSocketMap.remove("sendMailServerSocket");
									Util.println(ps, "SMTP�����������ɹ����˿ںţ�"+localServiceInfo.getStmpPort()+"��������ʱ�䣺"+Util.getNowTime());
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
									System.err.println("����POP3��������");
								} catch (InterruptedException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								SendMailService sms = new SendMailService();
								Thread smsTh = new Thread(sms);
								smsTh.start();
//								send.setSMTPServiceState();
//								sms = new SendMailService();
//								smsTh = new Thread(sms);
//								smsTh.start();
								
							}
//							Util.saveLog("�����������ϣ�"+Util.getNowTime());
							
		
						}else{
							Util.println(ps, "������δ����������ʧ�ܣ�");
						}
						
					}else if(subMenu3.equals("3")){
						//�����ϼ�
						break;
					}else{
						//�������롢
						continue;
					}
				}
			}else if(subMenu.equals("4")){
				localServiceInfo = once.getLocalServiceInfo();
//				System.out.println("localServiceInfo:"+localServiceInfo);
				//�����ʼ�ϵͳ��������׺
				Util.printlns(ps, "�������ʼ�ϵͳ��������׺Ϊ��"+localServiceInfo.getMailSuffix()+"�������º�׺��(�磺@mail.com)");
				String mailSuffix = Util.getInput(br);
				if(mailSuffix == null){
					return "NullPointerException";	
				}
				mailSuffix = mailSuffix.trim();
				//��ȡ�ʼ�ϵͳ�Ļ�����Ϣ
				localServiceInfo = once.getLocalServiceInfo();
				if(mailSuffix.startsWith("@")){
					//����
					localServiceInfo.setMailSuffix(mailSuffix);
					//����
					Util.write2LocalServiceFile();
					Util.println(ps, "ok");
				}else{
					Util.println(ps, "������󣬷����ϼ���");
				}
			}else if(subMenu.equals("5")){
				while(true){
					once = OnceClass.getOnce();
					Util.printlns(ps, "�˵���1.���\t2.�鿴\t3.ɾ��\t4.�����ϼ�");
					String choice = Util.getInput(br);
					if(choice == null){
						return "NullPointerException";
					}
					if(choice.equals("1")){
						Map<String, ForeignServiceBean> foreignServiceMap = once.getForeignServiceMap();
						//���
						Util.printlns(ps, "��������������ƣ�");
						String serviceName = Util.getInput(br);
						if(serviceName == null){
							return "NullPointerException";
						}
						Util.printlns(ps, "������SMTP_MX:");
						String smtp_mx = Util.getInput(br);
						if(smtp_mx == null){
							return "NullPointerException";
						}
						Util.printlns(ps, "������SMTP_PORT:");
						String smtp_port = Util.getInput(br);
						if(smtp_port == null){
							return "NullPointerException";
						}
						
						ForeignServiceBean fsb = new ForeignServiceBean(serviceName, smtp_mx, smtp_port);
						Util.saveLog("��������ʼ��ṩ�̣�"+serviceName+"\t"+smtp_mx+"\t"+smtp_port+"�ɹ���");
						foreignServiceMap.put(serviceName, fsb);
						Util.write2ForeignServiceFile();
						Util.println(ps, "ok");

					}else if(choice.equals("2")){
						Map<String, ForeignServiceBean> foreignServiceMap = once.getForeignServiceMap();
						if(foreignServiceMap.size()!=0){
							Util.println(ps, "������\tSMTP_MX\t\tSMTP_PORT");
							//�鿴
							Set<String> keySet = foreignServiceMap.keySet();
							Iterator<String> it = keySet.iterator();
							while(it.hasNext()){
								String key = it.next();
								ForeignServiceBean fsb = foreignServiceMap.get(key);
								Util.println(ps, fsb.getServiceName()+"\t"+fsb.getStmpMx()+"\t"+fsb.getStmpPort());
							}
							Util.println(ps, "\nɨ����ɡ�");
						}else{
							Util.println(ps, "�����������������ṩ�̵Ļ�����Ϣ����¼�롣");
						}
						
					}else if(choice.equals("3")){
						//ɾ��
						Util.printlns(ps, "������Ҫɾ���ķ��������ƣ�");
						String serviceName = Util.getInput(br);
						if(serviceName == null){
							return "NullPointerException";
						}
						once = OnceClass.getOnce();
						if(once.getForeignServiceMap().containsKey(serviceName)){
							once.getForeignServiceMap().remove(serviceName);
							Util.write2ForeignServiceFile();
							Util.println(ps, "ok");
						}else{
							Util.println(ps, "������󣬲����ڸ��ʼ��ṩ�̵���Ϣ");
						}
					}else{
						//�����ϼ�
						break;
					}
				}
			}else if(subMenu.equals("6")){
				once= OnceClass.getOnce();
				Map<String, UserBean> userMap = once.getUserMap();
				Set<String> userSet = userMap.keySet();
				Object[] ob = userSet.toArray();
				Util.println(ps, "��ǰϵͳ���û���");
				for(int i=0; i<ob.length; i++){
					Util.println(ps, (String)ob[i]);
				}
				Util.println(ps, "ɨ����ɣ�");
				
			}else if(subMenu.equals("7")){
				//�˳�
				Util.println(ps, "��ϵͳ����3����˳�");
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Util.println(ps, "\n\t�˳��ɹ�����ӭ�´�ʹ�á�");
				return "Exit";
			}else{
				continue;
			}
			//δ֪�����˳���
		}
//		return "NullPointerException";
	}
}
