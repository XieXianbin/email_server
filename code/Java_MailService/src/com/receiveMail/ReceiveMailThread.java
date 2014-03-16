package com.receiveMail;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Map;

import com.base64coder.Base64Coder;
import com.bean.LocalServiceBean;
import com.bean.UserBean;
import com.onceClass.OnceClass;
import com.util.Util;


public class ReceiveMailThread implements Runnable {

	/**
	 * �����ʼ������̣߳�
	 * ��ѯ���Ƿ��и��û���
	 * 		1. user a@57901.com // user 123
	 * 		2. pass 123
	 * 		4. stat
	 * 		3. list
	 */
	private Socket client;
	private PrintStream ps;
	private BufferedReader br;
	public ReceiveMailThread(Socket client){
		this.client = client;
		try {
			ps = new PrintStream(this.client.getOutputStream());
			br = new BufferedReader(new InputStreamReader(this.client.getInputStream()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private OnceClass once;
	private Map<String, UserBean> userMap;
	private LocalServiceBean localServer;
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		startLinkOk();
		//��ѭ�����ж��������ݡ�
		while(true){
			try {
				String mess = br.readLine();
				if(mess == null){
					Util.saveLog(Util.getClientInfo(client)+"��POP�����������жϡ�");
					return ;
				}
				//ת��ΪСд�����޳��ո�
				mess = mess.toLowerCase().trim();
				System.err.println(mess);
				if(mess.equalsIgnoreCase("noop")){
					//�ղ���
					doOk();
				}else if(mess.equalsIgnoreCase("quit")){
					//�˳�
					doOk();
					Util.saveLog(Util.getClientInfo(client)+"�����˳�POP��������");
					//�����˳�
					return ;
				}else if(mess.equalsIgnoreCase("ehlo") || mess.equalsIgnoreCase("helo") || mess.equalsIgnoreCase("ehlo "+Util.getLocalHostName())){
					//
					doEhlo();
				}else if(mess.equalsIgnoreCase("auth login")){
					//�û���¼
					ps.println("334 VXNlcm5hbWU6");
					String userNameBASE64 = br.readLine();
					if(userNameBASE64 == null){
						Util.saveLog(Util.getClientInfo(client)+"��ȡ�û���ʱ��POP�����������жϡ�");
						return;
					}
					System.out.println("userNameBASE64"+userNameBASE64);
					//��¼�ɹ�
					ps.println("334 UGFzc3dvcmQ6");
					String userPassBASE64 = br.readLine().toLowerCase().trim();
					if(userPassBASE64 == null){
						Util.saveLog(Util.getClientInfo(client)+"��ȡ�û�����ʱ��POP�����������жϡ�");
						return ;
					}
					String userName = Base64Coder.deCoder(userNameBASE64);
					String userPass = Base64Coder.deCoder(userPassBASE64);
					once = OnceClass.getOnce();
					userMap = once.getUserMap();
					if(userMap.containsKey(userName) && userMap.get(userName).getUserPass().equals(userPass)){
						//��¼�ɹ�
						ps.println("235 authentication successfully");
						Util.saveLog(userName+Util.getClientInfo(client)+"��¼�ɹ�POP��������");
						//��¼�ɹ������ز���//�����ʼ��Ĳ�����
						if(!receiveMail(userName)){
							Util.saveLog(Util.getClientInfo(client)+"�����쳣�ж��˳�POP��������");
							return ;
						}else{
							Util.saveLog(Util.getClientInfo(client)+"�����˳�POP��������");
							return ;
						}
					}else{
						//��¼ʧ��
						ps.println("535 authentication failed");
					}
				}else if(mess.startsWith("user ")){
					//��ȡ�û���
					String userName = mess.replace("user ", "");
					String mailName = Util.getMailName(userName);
					//�ɹ�
					doOk();
					//��ȡ����
					String passMess = br.readLine();
					if(passMess == null){
						Util.saveLog(Util.getClientInfo(client)+"�ڻ�ȡ����ʱ�����ж�POP��������");
						return ;
					}
					//��ȡ����
					int index = passMess.indexOf(" ");
					String userPass = passMess.substring(index+1);
					
					once = OnceClass.getOnce();
					userMap = once.getUserMap();
					//�û���������ͬʱƥ��
					if(userMap.containsKey(mailName) && userMap.get(mailName).getUserPass().equals(userPass)){
						//��֤�ɹ�
						ps.println("+OK 2 messages");
						Util.saveLog(userName+Util.getClientInfo(client)+"�ɹ���¼POP��������");
						//��֤�ɹ������ز���
						if(!receiveMail(mailName)){
							Util.saveLog(userName+Util.getClientInfo(client)+"��POP�����������쳣�жϡ�");
							return ;
						}else{
							//�����˳�
							Util.saveLog(userName+Util.getClientInfo(client)+"�����˳�POP��������");
							return ;
						}
					}else{
						ps.println("-ERR authorization failed");//����������������롣
					}
				}else{
					ps.println("-ERR command not recognized");
//					System.out.println("wrong Command.");
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
//				e.printStackTrace();
				Util.saveLog(Util.getClientInfo(client)+"��POP�����������жϡ�");
				return ;
			}
		}
	}
	private void doEhlo() {
		// TODO Auto-generated method stub
		ps.println("250-"+Util.getLocalHostIp());
		ps.println("250-PIPELINING");
		ps.println("250-SIZE "+Util.getFileSize());
		ps.println("250-VRFY");
		ps.println("250-ETRN");
		ps.println("250-AUTH LOGIN PLAIN");
		ps.println("250-AUTH=LOGIN PLAIN");
		ps.println("250-ENHANCEDSTATUSCODES");
		ps.println("250-8BITMIME");
		ps.println("250 DSN");
	}
	
	//��ȡ�ʼ�����
	public boolean receiveMail(String userName){
		//��ѭ�����û��жϵ���ɹ�ʱ����ز���
		while(true){
			once = OnceClass.getOnce();
			localServer = once.getLocalServiceInfo();
			//��ȡ�ʼ��Ĵ�ŵ�ַ
			File file = new File(localServer.getMailPath()+File.separator+userName+File.separator+"ReceiveMail");
			//����ļ��в����ڣ��򴴽�
			Util.mkdirs(file);
			File files[] = file.listFiles();
			int count = files.length;
			try {
				//���뷢��ָ��
				String order = br.readLine();
				if(order == null){
					return false;
				}
				System.err.println(order);
				//����order,           δ�������ո�����
				order = order.toLowerCase().trim();
				if(order.equals("noop")){
					doOk();
				}else if(order.equals("quit")){
					//�˳����̷߳���
					doOk();
					return true;
				}else if(order.equals("stat")){
					//��������״̬
					Util.println(ps, "+OK "+files.length+" "+Util.getFileSize(file));
					Util.println(ps, ".");
				}else if(order.equals("list")){
					//���ص�ǰ�����û����ʼ��б���Ϣ���Ե����
					doOk();
					for(int i=0; i<count; i++){
						int size = Util.getFileSize(files[i]);
						Util.println(ps, (i+1)+" "+size);
					}
					ps.println(".");
				}else if(order.contains("top ")){ //top����һ���ʼ��������ظ��ʼ���ͷ
					String[] Int = order.split(" ");
					int num = Integer.parseInt(Int[Int.length-1]);
					if(num > count){
						Util.println(ps, "-ERR - not that many messages only "+count);
						continue;
					}else{
						//����+OK
						doOk();
						//���ص�һ���ʼ����ʼ�ͷ
						BufferedReader br = Util.getBufferedReader(files[num]);
						String subMail = null;
						while((subMail=br.readLine())!=null && !subMail.equals("Subject:")){
							Util.println(ps, subMail);
						}
						Util.println(ps, subMail);
						br.close();
						ps.println(".");
					}
				}else if(order.contains("retr ")){
					doOk();
					int num = Integer.parseInt(order.replace("retr ", ""));
					if(num > count){
						Util.println(ps, "-ERR - not that many messages only "+count);
						continue;
					}else{
						//����+OK
						doOk();
						Util.saveLog(userName+Util.getClientInfo(client)+"��ȡһ���ʼ�");
						//���ص�һ���ʼ����ʼ�ͷ
						BufferedReader br = Util.getBufferedReader(files[num-1]);
						String subMail = null;
						while((subMail=br.readLine())!=null){
							Util.println(ps, subMail);
						}
						br.close();
						ps.println(".");
					}
				}else if(order.contains("del ")){
					int num = Integer.parseInt(order.replace("del ", ""));
					if(num > count){
						Util.println(ps, "-ERR not that many messages only "+count);
						continue;
					}else{
						files[num-1].delete();
					}
					doOk();
				}else if(order.equals("uidl")){
					//�����û��ļ���uidl���ʼ���Ψһ��ʶ��
					doOk();
					//�����ʼ�������
					for(int i=0; i<files.length; i++){
						Util.println(ps, (i+1)+" "+files[i].getName());
//						System.out.println((i+1)+files[i].getName());
					}
					ps.println(".");
				}else{
					//����ָ��
					Util.println(ps, "-ERR - unimplemented");
					continue;
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
//				e.printStackTrace();
				return false;
			}
		}
	}
	//����һ��+ ok
	private void doOk() {
		// TODO Auto-generated method stub
		ps.println("+OK");
	}
	//��ͨ��telnet����ʱ����ʾ��Ϣ
	private void startLinkOk() {
		// TODO Auto-generated method stub
		ps.println("+OK "+OnceClass.getOnce().getLocalServiceInfo().getMailSuffix()+" Server POP3 ready");
	}

}
