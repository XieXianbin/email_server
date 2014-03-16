package com.sendMail;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Map;

import sun.misc.BASE64Decoder;

import com.base64coder.Base64Coder;
import com.bean.ForeignServiceBean;
import com.bean.LocalServiceBean;
import com.bean.UserBean;
import com.onceClass.OnceClass;
import com.util.Util;


public class SendMailThread implements Runnable {

	/**
	 * �����ʼ������߳��̣߳�ʹ��SMTPЭ��
	 */
	private Socket client;
	private PrintStream ps;
	private BufferedReader br;
	
	public SendMailThread(Socket client){
		this.client = client;
		try {
			ps = new PrintStream(client.getOutputStream());
			br = new BufferedReader(new InputStreamReader(client.getInputStream()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	private OnceClass once;
	private Map<String, UserBean> userMap;
	private LocalServiceBean localService;
	private Map<String, ForeignServiceBean> foreignMap;
	@Override
	public void run() {
		/**
		 * ͨ��telnet����ʱ���ṩ����ص��ʴ���Ϣ��
		 */
		startLinkOk();
		int time = 0; 
		//ʹ����ѭ������ʾ��ʾ�Ự
		while(true){
			/////////////////////��foxmail�Ͽ�����ʱ�ر�����SocketException:Connection reset
			String mess;
			try {
				mess = br.readLine();
			} catch (IOException e) {
				// TODO Auto-generated catch block
//				e.printStackTrace();
				Util.saveLog(Util.getClientInfo(client)+"��SMTP�����������쳣�жϡ�");
				return ;
			}
			//�������Ϊnullʱ�����������������쳣Ϊ����ʾ
			if(mess == null){
				//��ϵͳ����δ֪�ж�///////////��д����־�ļ����������ý���
				Util.saveLog(Util.getClientInfo(client)+"��SMTP�����������쳣�жϡ�");
				return ;
			}
			//��ȡһ���ַ���������ת��ΪСд��ʽ�����޳���ͷ�ͽ�β�Ŀմ�
			mess = mess.toLowerCase().trim();
				System.err.println(mess);
			if(mess.equalsIgnoreCase("noop")){
				//�ղ�������������ok��Ӧ
				doOk();
			}else if(mess.startsWith("ehlo") || mess.startsWith("helo")){//��ehlo��helo��ͷ
				//������ESMTPЭ��
				if(mess.equals("ehlo")){
					Util.println(ps, "501 #5.0.0 EHLO requires domain address");
				}else{
					//��ȡ��������Ϣ
					String connectServer = mess.split(" ")[1];
					once = OnceClass.getOnce();
					localService = once.getLocalServiceInfo();
					//��ȡuserMap
					userMap = once.getUserMap();
					//��ͨ�ͻ������������ͨ�š�
					if(!connectServer.contains(".")){
						Util.println(ps, "250-"+Util.getLocalHostName());
						Util.println(ps, "250-mail");
						Util.println(ps, "250-8BITMIME");
						Util.println(ps, "250-SIZE "+Util.getFileSize());//�ļ��еĴ�С
						Util.println(ps, "250-AUTH PLAIN LOGIN");
						Util.println(ps, "250 AUTH=PLAIN LOGIN");
									
						//��ȡ��ָ��
						String subMess;
						try {
							subMess = br.readLine();
						} catch (IOException e) {
							// TODO Auto-generated catch block
//							e.printStackTrace();
							Util.saveLog(Util.getClientInfo(client)+"��SMTP�����������쳣�жϡ�");
							return ;
						}
						if(subMess == null){
							//��ϵͳ����δ֪�ж�///////////��д����־�ļ����������ý���
							Util.saveLog(Util.getClientInfo(client)+"��SMTP�����������쳣�жϡ�");
							return ;
						}
						subMess = subMess.toLowerCase().trim();
    							System.err.println(subMess);
						if(subMess.equals("auth login")){
							//�����û���
							Util.println(ps, "334 VXNlcm5hbWU6");
							String userNameBase64 = Util.getInput(br);
							if(userNameBase64 == null){
								//��ϵͳ����δ֪�ж�///////////��д����־�ļ����������ý���
								Util.saveLog(Util.getClientInfo(client)+"��SMTP�����������쳣�жϡ�");
								return ;
							}
							//���ܺ���û���
							String userName = Base64Coder.deCoder(userNameBase64);
//								System.err.println(userName);
							//��������
							Util.println(ps, "334 UGFzc3dvcmQ6");
							String userPassBase64 = Util.getInput(br);
							if(userPassBase64 == null){
								//��ϵͳ����δ֪�ж�///////////��д����־�ļ����������ý���
								Util.saveLog(Util.getClientInfo(client)+"��SMTP�����������쳣�жϡ�");
								return ;
							}
							//���ܺ���û���
							String userPass = Base64Coder.deCoder(userPassBase64);
//								System.err.println(userPass);
							//�ж��û����������Ƿ�ƥ��
							if(userMap.containsKey(userName) && userMap.get(userName).getUserPass().equals(userPass)){
								//��¼�ɹ�
								doLoginOK();
								Util.saveLog(userName+Util.getClientInfo(client)+"��¼SMTP�������ɹ���");
								doOk();
								//��¼�ɹ�����ͨ�û������ʼ�
								sendLocalMail(userName);
							}else{//�û�������ƥ��ʱ����������
								//�û������������
								Util.println(ps, "535 #5.7.0 Authentication failed");
							}
						//auth loginͬ��ָ��
						}else if(subMess.equals("noop")){
							//�ղ���
							doOk();
						}else if(subMess.equals("quit")){
							//��ֹ�ʼ��Ự���������ý��̡�
							doQuit();
							Util.saveLog(Util.getClientInfo(client)+"��SMTP���������������˳���");
							return ;
						}else{
							//�����ָ�����
							doWrongCommand();
						}
					}else{
						//������������ͻ��˵�ͨ�š�
						receForeignMail(connectServer);
					}
				}//��ehlo��ͷ��
			}else if(mess.equals("quit")){
				//��ֹ�ʼ��Ự���������ý��̡�
				doQuit();
				Util.saveLog(Util.getClientInfo(client)+"��SMTP���������������˳���");
				return ;
			}else{
				doWrongCommand();
			}
		}
	}
	//�����ʼ������ṩ����ϵͳ�����ʼ�
	private void sendLocalMail(String userName) {
		//���淢����
		String sentMailer = "";
		String foreSendMailer = "";
		//�����ռ���
		String receMailer = "";
		String foreReceMailer = "";
		// TODO Auto-generated method stub
		while(true){
			//��ָ��
			String message;
			try {
				message = br.readLine();
			if(message == null){
				//��ϵͳ����δ֪�ж�///////////��д����־�ļ����������ý���
				Util.saveLog(Util.getClientInfo(client)+"��SMTP�����������쳣�жϡ� ");
				return ;
			}
			message = message.toLowerCase().trim();
			//��ʼmail from
			if(message.equalsIgnoreCase("auth login")){
				//���ٴ�����auth loginʱ����ʾ�ѵ�½
				Util.println(ps, "503 #5.5.0 Already authenticated");
			}else if (message.contains("<") && message.contains(">") && message.contains(":")){
				//��ȡ������ϵ��ַ�����s[0]���洢�������s[1]�洢��������
				String s[] = Util.dealString(message);
				//�ж��ʼ��Ƿ���һ���ʼ���ַ
				String mailName = s[1];
//										if(mailName.endsWith(localService.getMailSuffix()) && userMap.containsKey(mailName)){
				//����վ�ڵ��ʼ�
				if(s[0].equalsIgnoreCase("mail from")){
					//������
					sentMailer = mailName;
					foreSendMailer = message;
					doOk();
				}else if(s[0].equalsIgnoreCase("rcpt to")){
					foreReceMailer = message;
					//�ռ���
					if(receMailer.equals(""))
						receMailer = mailName;
					else
						receMailer += "/"+mailName;
					doOk();
				}else{
					//������վ�ʼ����ڱ�վ���������󣬷��͸���Ŀ�������
					doWrongCommand();
				}
			}else if(message.equalsIgnoreCase("data")){
				//��Ӧdata�ظ�
				responseData();
				/////////////////////////////��ʼ�����ʼ�������������
				if(receMailer.endsWith(localService.getMailSuffix())){
					//��ϵͳ�ڵ��ʼ�����
					String sendState = sendLocalMail(sentMailer, receMailer);
					if(sendState.equals("NullPointerException")){
						Util.saveLog(userName+Util.getClientInfo(client)+"��SMTP�����������쳣�жϡ�");
						return ;
					}else if(sendState.equals("quit")){
						Util.saveLog(sentMailer+"��"+Util.getNowTime()+"��"+receMailer+"������һ���ʼ��ɹ���");
						return ;
					}else if(sendState.equals("ok")){
						Util.saveLog(sentMailer+"��"+Util.getNowTime()+"��"+receMailer+"������һ���ʼ��ɹ���");
//											return ;
					}
				}else{
					//�����ʼ������ṩ�̵��ʼ�
					String sendState = sendForeignMail(foreSendMailer, foreReceMailer);
					if(sendState.equals("NullPointerException")){
						//��ָ���쳣
						Util.saveLog(userName+Util.getClientInfo(client)+"��SMTP�����������쳣�жϡ�");
						return ;
					}else if(sendState.equals("quit")){
						Util.saveLog(sentMailer+"��"+Util.getNowTime()+"��"+receMailer+"������һ���ʼ��ɹ���");
						return ;
					}else{
						Util.saveLog(sentMailer+"��"+Util.getNowTime()+"��"+receMailer+"������һ���ʼ��ɹ���");
					}
				}
			}else if(message.equals("noop")){
				doOk();
			}else if(message.equals("quit")){
//				doQuit();
				Util.println(ps, "221 Bye");
				Util.saveLog(userName+Util.getClientInfo(client)+"��SMTP���������������˳���");
				return ;
			}else{
				//����ָ��
				doWrongCommand();
//										Util.println(ps, "�û������������");
			}
			} catch (IOException e) {
				// TODO Auto-generated catch block
//				e.printStackTrace();
				Util.saveLog(userName+Util.getClientInfo(client)+"��SMTP���������������˳���");
				return ;
			}
		}//while(true)
	}
	//�����ʼ������ṩ����ϵͳ�����ʼ�
	public boolean receForeignMail(String conncetServer){
		//�������������ӳɹ�
		Util.println(ps, "250-"+Util.getLocalHostName());
		Util.println(ps, "250-mail");
//		Util.println(ps, "250-SIZE "+Util.getFileSize());//�ļ��еĴ�С
		Util.println(ps, "250-8BITMIME");
		System.out.println("250-8BITMIME");
		
		String sentMailer = "";
		String receMailer = "";
		String foreSendMailer = "";
		String foreReceMailer = "";
		int time = 0;
		while(true){
			String mess;
			try {
				mess = br.readLine();
			} catch (IOException e) {
				// TODO Auto-generated catch block
//				e.printStackTrace();
				return false;
			}
//			mess = mess.toLowerCase().trim();
				System.err.println("receForeignMail:"+mess);
			if (mess.contains("<") && mess.contains(">") && mess.contains(":")){
				if(time++==0){
					Util.saveLog(conncetServer+Util.getNowTime()+"��������������ӳɹ���");
				}
				//��ȡ������ϵ��ַ�����s[0]���洢�������s[1]�洢��������
				String s[] = Util.dealString(mess);
				//�ж��ʼ��Ƿ���һ���ʼ���ַ
				String mailName = s[1];
//				if(mailName.endsWith(localService.getMailSuffix()) && userMap.containsKey(mailName)){
				//����վ�ڵ��ʼ�
				if(s[0].equalsIgnoreCase("mail from")){
					//������
					sentMailer = mailName;
					foreSendMailer = mess;
					doOk();
				}else if(s[0].equalsIgnoreCase("rcpt to")){
					foreReceMailer = mess;
					//�ռ���
					if(receMailer.equals(""))
						receMailer = mailName;
					else
						receMailer += "/"+mailName;
					doOk();
				}else{
					//������վ�ʼ����ڱ�վ���������󣬷��͸���Ŀ�������
					doWrongCommand();
				}
			}else if(mess.equalsIgnoreCase("data")){
				//��Ӧdata�ظ�
				responseData();
				/////////////////////////////��ʼ�����ʼ�������������
				if(receMailer.endsWith(localService.getMailSuffix())){
					//��ϵͳ�ڵ��ʼ�����
					String sendState = receForeignMailContext(receMailer);
					if(sendState.equals("NullPointerException")){
						Util.saveLog(sentMailer+Util.getClientInfo(client)+"��SMTP�����������쳣�жϡ�");
						return false;
					}else if(sendState.equals("quit")){
						Util.saveLog(sentMailer+"��"+Util.getNowTime()+"��"+receMailer+"������һ���ʼ��ɹ���");
						return true;
					}else if(sendState.equals("ok")){
						Util.saveLog(sentMailer+"��"+Util.getNowTime()+"��"+receMailer+"������һ���ʼ��ɹ���");
//										return ;
					}
				}else if(mess.equalsIgnoreCase("quit")){
					//��ֹ�ʼ��Ự���������ý��̡�
					doQuit();
					Util.saveLog(Util.getClientInfo(client)+"��SMTP���������������˳���");
					return true;
				}else{
					doWrongCommand();
				}
			}
		}//while(true)
	}
	//�����������ʼ�
	private String receForeignMailContext(String receMailer) {
		//�����ļ�
		File file = new File(Util.DEFAULTMAILPATH+File.separator+receMailer+File.separator+"ReceiveMail");
		Util.mkdirs(file);
		file = new File(file, File.separator+Util.getMailName());//�ļ�·��
		BufferedWriter bw = Util.getBufferedWriter(file);//�����
		String subMail = null;
		try {
			while(!(subMail=Util.getInput(br)).equals(".")){
					bw.write(subMail);
					bw.newLine();
					bw.flush();
			}
			bw.write(".");
			bw.close();
			//�ظ��ʼ����ճɹ�
			Util.println(ps, "250 Mail OK ");
			return "ok";
			
		} catch (IOException e) {
//			e.printStackTrace();
			return "NullPointerException";
		}
//		return "ok";
	}


	//���������ʼ�����     ����
	public String sendForeignMail(String foreSendMailer, String foreReceMailer){
		//��ȡ�ռ��˵��ʼ���������Ϣ
//		String[] recips = recipient.split("/");
		//��ȡrecipient���ʼ��ṩ�����ͣ���socket
		int at = foreReceMailer.indexOf("@");
		int l = foreReceMailer.indexOf(">");
		String mailSupplier = foreReceMailer.substring((at+1), l);
		System.err.println("mailSupplier:"+mailSupplier);
		
		foreignMap = OnceClass.getOnce().getForeignServiceMap();
		ForeignServiceBean foreignService = foreignMap.get(mailSupplier);
		//��������MX��¼
		String smtpMx = foreignService.getStmpMx();
		System.err.println(smtpMx);
		
		int smtpPort = Integer.parseInt(foreignService.getStmpPort());
		try {
			//�����ʼ��ṩ�̵�SMTP������
			Socket clientSocket = new Socket(smtpMx, smtpPort);
			//��ȡ���������
			BufferedReader foreignBR = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			PrintStream foreignPS = new PrintStream(clientSocket.getOutputStream());
			String returnMess = foreignBR.readLine();//��ȡ����������Ϣ
				System.out.println("�ʼ��������ķ�����Ϣ��linkOK:"+returnMess);
			if(!returnMess.contains("220")){//���������220����ʾ������δ������������Ϣ��
				
			}
			foreignPS.println("ehlo smtp.57901.com");
//				System.out.println("ehlo smtp.57901.com");
			int count;
			if(smtpMx.contains("qq"))
				count=3;
			else if(smtpMx.contains("sina"))
				count = 2;
			else if(smtpMx.contains("163"))
				count = 6;
			else 
				count = 3;
			for(int i=0; i<count; i++){//163��ȡ6�з�������//qq��ȡ3������//���˷���2��
				returnMess = foreignBR.readLine();//250 ok
//					System.out.println(returnMess+"   ehlo>�ظ�");
			}
			foreignPS.println(foreSendMailer);//mail from:<xiexianbin@163.com>
			returnMess = foreignBR.readLine();//250 Mail OK
//				System.err.println("�ʼ��������ķ�����Ϣ��"+returnMess);
			foreignPS.println(foreReceMailer);//rcpt to:<xianbinxie@163.com>
			returnMess = foreignBR.readLine();//250 Mail OK
//				System.err.println("�ʼ��������ķ�����Ϣ��"+returnMess);
			
			//�������
			String order = "data";
			//����data
			foreignPS.println(order);//data
			returnMess = foreignBR.readLine();//354 End data with <CR><LF>.<CR><LF>
//				System.err.println("�ʼ��������ķ�����Ϣ��"+returnMess);
			String subForMess = null;
			while((subForMess=br.readLine())!=null && !subForMess.equals(".") && !subForMess.equals("quit") & !subForMess.equals("QUIT")){
				foreignPS.println(subForMess);
//				System.out.println(subForMess);
				foreignPS.flush();
			}
			foreignPS.println(".");
			foreignPS.flush();
			returnMess = foreignBR.readLine();
//				System.err.println("�ʼ��������ķ�����Ϣ��"+returnMess);
			if(returnMess.contains("250")){
				ps.println("250 Mail OK");
				//���ʼ����������ӶϿ�
				foreignPS.println("quit");
				return "ok";
			}
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
			return "NullPointerException";
			
		}
		return "ok";
	}
	
	
	
	
	//�����ʼ������ʼ�����    
	public String sendLocalMail(String sender, String recipient){
		//��ʼ�������ݣ��ж��Ƿ���.����
		String subMail = null;
		//��ȡonce
		once = OnceClass.getOnce();
		//��ȡ�ļ���Ĭ�ϴ��·��
		localService = once.getLocalServiceInfo();
		String mailPath = localService.getMailPath();

		//���浽�����˵ķ����ļ�����
		File sendFile = new File(mailPath+File.separator+sender+File.separator+"SendMail");//���浽�����˵� �ѷ����ʼ��б� ��
		Util.mkdirs(sendFile);
		sendFile = new File(sendFile, File.separator+Util.getMailName()+".eml");
		Util.creatNewFile(sendFile);
		BufferedWriter sendBW = Util.getBufferedWriter(sendFile);
		String[] recips = recipient.split("/");
		//��ȡȺ�������û���bw
		BufferedWriter[] reciBW = new BufferedWriter[recips.length];
		File[] reFile = new File[recips.length];
		for(int i=0; i<recips.length; i++){
			File reciFile = new File(mailPath+File.separator+recips[i]+File.separator+"ReceiveMail");
			Util.mkdirs(reciFile);
			reciFile = new File(reciFile, File.separator+Util.getMailName()+sender+".eml");
			reFile[i] = reciFile;
			Util.creatNewFile(reciFile);
			reciBW[i] = Util.getBufferedWriter(reciFile);
		}
		//�Ѷ�ȡ�����ʼ�����д��ֱ�д�뵽**.eml�ļ���
		//quit������
		try {
			while(((subMail=br.readLine())!=null) && !subMail.equals(".") && !subMail.equals("QUIT") && !subMail.equals("quit")){
				//д�뷢���˵��ļ���
				sendBW.write(subMail);
				sendBW.newLine();
				sendBW.flush();
				//���浽�ռ��˵��ռ��ļ�����
				for(int i=0; i<recips.length; i++){
					reciBW[i].write(subMail);
					reciBW[i].newLine();
					reciBW[i].flush();
				}
			}
			for(int i=0; i<recips.length; i++){
				reciBW[i].close();
			}
			sendBW.close();
			if(subMail == null){
				//�����쳣�رգ�����
//				Util.saveLog(Util.getClientInfo(client)+"��SMTP�����������쳣�жϡ�");
				return "NullPointerException";
			}
			if(subMail.equals("QUIT") || subMail.equals("quit")){
				doQuit();
//				Util.saveLog(Util.getClientInfo(client)+"����������������˳���ʱ�䣺"+Util.getNowTime());
				return "quit";
			}
			//�ظ��ʼ����ͳɹ�
			overData();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
//			Util.saveLog(Util.getClientInfo(client)+"��SMTP�����������쳣�жϡ�");
			for(int i=0; i<reFile.length; i++){
				try {
					reciBW[i].close();
					reFile[i].delete();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			return "NullPointerException";
		}
		//��������������true��
		return "ok";
	}
	
	//��¼�ɹ�
	private void doLoginOK() {
		// TODO Auto-generated method stub
		Util.print(ps, "235 #2.0.0 OK Authenticated");
	}
	//��Ӧ�����command
	public void doWrongCommand(){
		Util.println(ps, "500 #5.5.1 command not recognized");
	}
	//��Ӧdata����
	public void responseData(){
		Util.println(ps, "354 End data with .");
	}
	//data����ִ�гɹ�ʱ����Ӧ
	public void overData(){
		Util.println(ps, "250 OK: mail sent success");
	}
	//�رշ����� //say bye
	private void doQuit() {
		// TODO Auto-generated method stub
		Util.println(ps, "221 Bye");
		Util.println(ps, "Connection closed by foreign host.");
	}
	//��������Ӧok
	private void doOk() {
		// TODO Auto-generated method stub
		Util.println(ps, "250 OK");
	}
	//����telnetʱ��ʾ��Ϣ
	public void startLinkOk(){
		Util.println(ps, "Trying "+Util.getLocalHostIp()+"...");
		Util.println(ps, "Connected to "+Util.getLocalHostIp()+".");
		Util.println(ps, "220 "+Util.getLocalHostIp()+" ESMTP Postfix - by "+OnceClass.getOnce().getLocalServiceInfo().getMailSuffix());
	}
}
