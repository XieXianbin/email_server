package com.monitor;

import java.io.BufferedReader;
import java.io.PrintStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import com.bean.UserBean;
import com.onceClass.OnceClass;
import com.util.Util;

public class MonitorSubThread implements Runnable {

	//�����û��Ŀͻ����׽��֡���ȡ��Ӧ�������������
	private Socket clientSocket;
	private PrintStream ps;
	private BufferedReader br;
	//����ý��̵����б�ʶ��
	private boolean flag = true;
	//�����û����������
	private MonitorUserOperation mo;
	private MonitorAdminOperation ao;
	//���������û�����ͻ����׽��֣�����ȡ��Ӧ������/������
	public MonitorSubThread(Socket clientSocket){
		this.clientSocket = clientSocket;
		ps = Util.getPrintStream(clientSocket);
		br = Util.getBufferedReader(clientSocket);
	}
	private OnceClass once ;
	@Override
	public void run() {
		// TODO Auto-generated method stub
		//��ȡ�ͻ����׽�����Ϣ��
		String clientInformation = Util.getClientInfo(clientSocket);
		Util.saveLog(clientInformation+"���ӷ������ɹ�����ǰ���̵Ļ�߳�����"+Thread.activeCount());
		//ʵ������¼�ɹ����û������ķ�������
		mo = new MonitorUserOperation(ps, br, clientSocket);
		ao = new MonitorAdminOperation(ps, br, clientSocket);
		once = OnceClass.getOnce();
		//��ȡ������
		while(flag){
			String mailSuffix = once.getLocalServiceInfo().getMailSuffix();
			Util.println(ps, "**********************"+once.getLocalServiceInfo().getMailSuffix().replace("@", "")+"�ʼ�ϵͳ***************************");
			Util.printlns(ps, "��ѡ���û���ݣ�1.�����û�\t2.����������Ա\t3.�˳�\t������������");
			String choiceMenu = Util.getInput(br);
			if(choiceMenu == null){
				Util.saveLog(clientInformation+"������������쳣�жϡ�");
				return ;
			}
			if(choiceMenu.equals("1")){
				while(flag){
					//�����û�
					Util.printlns(ps, "�˵���1.��¼\t2.ע��\t3.�һ�����\t4.�����ϼ�\t������������");
					String menu = Util.getInput(br);
					//��menuΪnull�����û��˳��������ý��̡�
					if(menu == null){
						Util.saveLog(clientInformation+"�������¼����������������쳣�жϡ�");
						return ;
					}
					if(menu.equals("1")){
						//��¼
						Util.printlns(ps, "�������û���:");
						String userName = Util.getInput(br);
						if(userName == null){
							Util.saveLog(clientInformation+"�����û���ʱ������������쳣�жϡ�");
							return ;
						}
						userName = userName.toLowerCase();
						//��ȡuserMap
						Map<String, UserBean> userMap;
						once = OnceClass.getOnce();
						userMap= once.getUserMap();
						if(userMap.containsKey(userName) || userMap.containsKey(userName+mailSuffix)){
							if(!userName.contains(mailSuffix))
								userName += mailSuffix;
							Util.printlns(ps, "����������:");
							String userPass = Util.getInput(br);
							if(userPass == null){
								Util.saveLog(clientInformation+"�����û�����ʱ������������쳣�жϡ�");
								return ;
							}
							//��ȡonLineMap
							Map<String, Socket> onLineMap = once.getOnLineMap();
							//ƥ������
							if(userMap.get(userName).getUserPass().equals(userPass)){
								if(!onLineMap.containsKey(userName)){
									//��¼�ɹ�
									Util.saveLog(userName+"��"+clientInformation+"����������ɹ���");
									Util.println(ps, userName+"��"+Util.getNowTime()+"����ɹ���");
									//���û�������״̬��ӵ�onLineMap����
									onLineMap.put(userName, clientSocket);
									//�������ɹ�������˵�
									String runState = mo.loginSuccess(userName);
									
									if(runState.equals("NullPointerException"))
									{
										//ɾ���ѵǳ��û�
										Util.deleLogOutUser(userName);
										Util.saveLog(userName+"��"+clientInformation+"����ʧ��������������쳣�жϡ�");
										return ;
									}else if(runState.equals("Exit")){
										//ɾ���ѵǳ��û�
										Util.deleLogOutUser(userName);
										Util.saveLog(userName+"��"+clientInformation+"�ɹ��ǳ���");
										continue;
									}
								}else{
									//��ʾ�쳣��¼��Ϣ
									Socket loginedSocket = onLineMap.get(userName);
									//�����
									PrintStream loginedPS = Util.getPrintStream(loginedSocket);
									String message = userName+"��"+Util.getClientInfo(clientSocket)+"�쳣��¼ʧ�ܡ�";
									Util.printlns(loginedPS, "��Ҫ��ʾ��"+message);
									String message2 = Util.getClientInfo(loginedSocket);
									//���½����ʾ
									Util.printlns(ps, userName+"��"+message2+"��¼");
									//������־
									Util.saveLog(message+"��ǰ��¼Ϊ��"+message2);
								}
							}else{
								Util.println(ps, "����������󣬷��ز˵���");
								continue;
							}
						}else{
							Util.println(ps, "�û��������ڣ�����ע�ᡣ");
							continue;
						}
					}else if(menu.equals("2")){
						//ע��
						boolean fl = mo.regeditNewUser();
						if(!fl){
							Util.saveLog(clientInformation+"ע�����û�ʱ������������쳣�жϣ�ע��ʧ�ܡ�");
							return ;
						}
					}else if(menu.equals("3")){
						//�һ�����
						boolean fl = mo.findUserPass();
						if(!fl){
							Util.saveLog(clientInformation+"�û��һ�����ʱ������������쳣�жϡ�");
							return ;
						}
						
					}else if(menu.equals("4")){
						//�����ϼ�
						break;
					}else{
						//��������
						continue;
					}
				}
				
			}else if(choiceMenu.equals("2")){
				//����������Ա
				while(flag){
					Util.printlns(ps, "�˵���1.��¼\t2.�����ϼ�\t������������");
					String menu = Util.getInput(br);
					//��menuΪnull�����û��˳��������ý��̡�
					if(menu == null){
						Util.saveLog(clientInformation+"�ڹ���Ա��¼����������������쳣�жϡ�");
						return ;
					}
					if(menu.equals("1")){
						//��¼
						Util.printlns(ps, "���������Ա�ʺ�:");
						String adminName = Util.getInput(br);
						if(adminName == null){
							Util.saveLog(clientInformation+"�������Ա�ʺ�ʱ������������쳣�жϡ�");
							return ;
						}
						if(adminName.equals(Util.ADMIN)){
							Util.printlns(ps, "���������Ա����:");
							String adminPass = Util.getInput(br);
							if(adminPass == null){
								Util.saveLog(clientInformation+"�������Ա����ʱ������������쳣�жϡ�");
								return ;
							}
							if(adminPass.equals(Util.ADMINPASS)){
								//��¼�ɹ�
								Util.saveLog(adminName+"��"+clientInformation+"����������ɹ���");
								Util.println(ps, adminName+"��"+Util.getNowTime()+"����ɹ���");
								String runState = ao.adminLoginSuccess(adminName);
								if(runState.equals("NullPointerException"))
								{
									Util.saveLog(adminName+"��"+clientInformation+"����ʧ��������������쳣�жϡ�");
									return ;
								}else if(runState.equals("Exit")){
									Util.saveLog(adminName+"��"+clientInformation+"�ɹ��ǳ���");
									continue;
								}else{
									Util.println(ps, "����������󣬷��ز˵���");
									continue;
								}
							}else{
								Util.println(ps, "�ù���Ա�˻������ڣ����������롣");
								continue;
							}
						}
					}else if(menu.equals("2")){
						//�����ϼ�
						break;
					}else{
						//��������
						continue;
					}
				}
			}else if(choiceMenu.equals("3")){
				//�˳�����
				Util.saveLog(clientInformation+"���������˳���");
				Util.println(ps, "\n\n\t���ڷ������ɹ��Ͽ����ӡ���رոô��ڡ�ллʹ�á�");
				return;
			}else{
				//��������
				continue;
			}
		}
	}
}
