package com.monitor;

import java.io.BufferedReader;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Map;

import com.bean.UserBean;
import com.onceClass.OnceClass;
import com.util.Util;

public class MonitorUserOperation {

	private PrintStream ps;
	private BufferedReader br;
	private Socket socket;
	private String mailSuffix;
	public MonitorUserOperation(PrintStream ps, BufferedReader br, Socket socket) {
		// TODO Auto-generated constructor stub
		this.ps = ps;
		this.br = br;
		this.socket = socket;
	}
	
	private OnceClass once;
	//����ɹ�������˵�
	public String loginSuccess(String userName) {
		once = OnceClass.getOnce();
		while(true){
			// TODO Auto-generated method stub
			Util.printlns(ps, "���˵���1.ע������\t2.�޸�����\t3.�˳�\t������������");
			String subMenu = Util.getInput(br);
			if(subMenu == null){
				return "NullPointerException";	
			}
			if(subMenu.equals("1")){
				UserBean user = once.getUserMap().get(userName);
				Util.printlns(ps, userName+"ע����"+user.getUserRegedtiDate()+"�Ƿ�ע��(y/n)");
				String choice = Util.getInput(br);
				if(choice == null){
					return "NullPointerException";	
				}
				if(choice.equals("y")){
					once.getUserMap().remove(userName);
					Util.write2UserMapFile();
					Util.saveLog("����"+userName+"ע���ɹ���ʱ�䣺"+Util.getNowTime());
					Util.println(ps, "����"+userName+"ע���ɹ���ϵͳ����3����˳���");
					try {
						Thread.sleep(3000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					return "Exit";
				}else if(choice.equals("n")){
					Util.println(ps, "�����ϼ�");
				}else{
					continue;
				}
		
			}else if(subMenu.equals("2")){
				//�޸�����
				Util.printlns(ps, "�����뵱ǰ���룺");
				String pass = Util.getInput(br);
				if(pass == null){
					return "NullPointerException";	
				}
				//��ȡ����ƥ��
				UserBean user = once.getUserMap().get(userName);
				if(user.getUserPass().equals(pass)){
					Util.printlns(ps, "�����������룺");
					String newPass = Util.getInput(br);
					if(newPass == null){
						return "NullPointerException";	
					}
					user.setUserPass(newPass);
					//д���ļ�
					Util.write2UserMapFile();
					Util.println(ps, "���óɹ���");
				}else{
					Util.println(ps, "����������󡣷����ϼ���");
				}
			}else if(subMenu.equals("3")){
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
	//ע�����û�
	public boolean regeditNewUser() {
		// TODO Auto-generated method stub
		Map<String, UserBean> userMap;
		mailSuffix = OnceClass.getOnce().getLocalServiceInfo().getMailSuffix();
		once = OnceClass.getOnce();
		Util.printlns(ps, "�������û���(������"+mailSuffix+")���֣�");
		String userName = Util.getInput(br);
		if(userName == null){
			return false;
		}
		userName = userName.toLowerCase();
		userMap = once.getUserMap();
		if(!userMap.containsKey(userName)){
			Util.printlns(ps, "�������û�����:");
			String userPass = Util.getInput(br);
			if(userPass == null){
				return false;
			}
			Util.printlns(ps, "�������ܱ�����:");
			String userQuestion = Util.getInput(br);
			if(userQuestion == null){
				return false;
			}
			Util.printlns(ps, "�������ܱ���:");
			String userAnswer = Util.getInput(br);
			if(userAnswer == null){
				return false;
			}
			UserBean user = new UserBean((userName+mailSuffix), userPass, userQuestion, userAnswer, Util.getNowTime());
			userMap.put((userName+mailSuffix), user);
			Util.println(ps, userName+"ע��ɹ������¼��");
			//д��map��
			Util.write2UserMapFile();
			Util.saveLog((userName+mailSuffix)+"��"+Util.getClientInfo(socket)+"ע��ɹ���");
		}else{
			Util.println(ps, "�û����Ѵ��ڣ����¼��");
		}
		
		return true;
	}
	//�һ�����
	public boolean findUserPass() {
		// TODO Auto-generated method stub
		Util.printlns(ps, "�����������û���:");
		String userName = Util.getInput(br);
		if(userName == null){
			return false;
		}
		userName = userName.toLowerCase();
		//��ȡuserMap
		Map<String, UserBean> userMap;
		once = OnceClass.getOnce();
		userMap= once.getUserMap();
		if(userMap.containsKey(userName)){
			//�û����ڣ��һ�
			UserBean user = userMap.get(userName);
			Util.printlns(ps, "�����ܱ������ǣ�"+user.getUserAnswer()+" �������ܱ��𰸣�");
			String question = Util.getInput(br);
			if(question == null){
				return false;
			}
			if(question.equals(user.getUserQuestion())){
				Util.printlns(ps, "�����������룺");
				String newPass = Util.getInput(br);
				if(newPass == null){
					return false;
				}
				user.setUserPass(newPass);
				Util.println(ps, userName+"�޸�����ɹ������¼��");
				Util.saveLog(userName+"��"+Util.getNowTime()+"�޸�����ɹ���");
				return true;
			}else{
				Util.println(ps, "����������󣬷����ϼ���");
				return true;
			}
		}else{
			//�û�������
			Util.println(ps, "��������û��������ڣ������ϼ���");
			return true;
		}
	}
}
