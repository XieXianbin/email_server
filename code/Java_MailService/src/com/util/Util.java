package com.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Properties;

import com.bean.LocalServiceBean;
import com.bean.UserBean;
import com.onceClass.OnceClass;

public class Util {

	//���߰�
	//��ŷ�����������־���ļ�λ��
	public static final String SERVICELOG = "D://mailBox/service/mailService.log";
	//mail��ŵ�Ĭ��·����
	public static final String DEFAULTMAILPATH = "D://mailBox/userMailPath";
	//�洢�����û�map��·����
	public static final String USERMAPPARH = "D://mailBox/userMapPath/userMap.db";
	//��ű��ط������Ļ�����Ϣ��·��
	public static final String LOCALHOSTINFOPATH = "D://mailBox/service/localServiceInfo.properties";
	//��������������Ļ�����Ϣ��·��
	public static final String FOREIGNSERVICEPATH = "D://mailBox/service/foreignServiceInfo.db";
	//���SMTP��������������־��·��
	public static final String SMTPLOGPATH = "D://mailBox/service/SMTP.log";
	//���POP��������������־��·��
	public static final String POP3LOGPATH = "D://mailBox/service/POP3.log";
	
	//����Ա�ʺź�����
	public static final String ADMIN = "Admin";
	public static final String ADMINPASS = "admin";
	
	//�ļ��Ĵ�С
	private static int size;
	//��ȡ�����ʼ��û��Ŀռ��С
	public static int getFileSize(){
		size = 0;
		File file = new File(OnceClass.getOnce().getLocalServiceInfo().getMailPath());
		if(file.exists())
			digui(file);
		return size;
	}
	public static int getFileSize(File file){
		size = 0;
		if(file.exists() && !file.isFile())
			digui(file);
		return size;
	}
	//�ݹ�
	public static void digui(File file){
		File[] files = file.listFiles();
		for(int i=0; i<files.length; i++){
			if(files[i].isFile()){
				size += files[i].length();
			}else{
				digui(files[i]);
			}
		}
	}
	
	//������"<"��">"���ַ���
	public static String[] dealString(String str){
		String[] s = str.split(":");
		//����s[0]
		s[0] = s[0].trim();
		int first = s[0].indexOf(" ");
		int last = s[0].lastIndexOf(" ");
		//����ո���ֵ�λ�ò�����ͬ�������޳�����
		if(first<last){
			String[] subStr = s[0].split(" ");
			StringBuffer sb = new StringBuffer();
			sb.append(subStr[0]);
			for(int i=1; i<subStr.length; i++){
				if(!subStr[i-1].equals("") && subStr[i].equals("")){
					sb.append(" ");
				}else if(subStr[i-1].equals("") && !subStr[i].equals("")){
					sb.append(subStr[i]);
				}else{
					continue;
				}
			}
			s[0] = sb.toString();
		}
		//����s[1]
		s[1] = s[1].trim();
		String mailName;
		String size;
		if(!s[1].contains("=")){
			s[1] = s[1].replace("<", "").replace(">", "").trim();
			return s;
		}else{
			int left = s[1].indexOf("<");
			int right = s[1].indexOf(">");
			mailName = s[1].substring(left+1, right).trim();
			int lastCh = str.indexOf("=");
			size = str.substring(lastCh+1).trim();
		}
		String[] ss = {s[0], mailName, size};
		return ss;
	}
	//��ȡ���صļ��������
	public static String getLocalHostName(){
		String hostName = null;
		try {
			hostName = InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return hostName;
	}
	//��ȡ���صļ����ip
	public static String getLocalHostIp(){
		String ip = null;
		try {
			ip = InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ip;
	}
	//��ȡ�ʼ��˻�����
	public static String getMailName(String mail){
		String mailName = null;
		mail = mail.trim();
		String suffix = OnceClass.getOnce().getLocalServiceInfo().getMailSuffix();
		if(!mail.contains(suffix) ){
			mailName = mail+suffix;
		}else{
			mailName = mail;
		}
		return mailName;
	}
	//��ȡ��ǰϵͳʱ��
	public static String getNowTime(){
		Date date = new Date();
		DateFormat df = DateFormat.getDateTimeInstance();
		return df.format(date);
	}
	//��ȡ�ͻ�������������
	public static PrintStream getPrintStream(Socket clientSocket){
		PrintStream ps = null;
		try {
			ps = new PrintStream(clientSocket.getOutputStream());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ps;
	}
	//��ȡ�ͻ��������
	public static BufferedReader getBufferedReader(Socket clientSocket){
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return br;
	}
	//��ȡ�ļ������������׷�ӷ�ʽ
	public static BufferedWriter getBufferedWriter(File file){
		BufferedWriter bw = null;
		if(!file.exists()){
			try {
				file.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		try {
			FileOutputStream out = new FileOutputStream(file, true);
			OutputStreamWriter osw = new OutputStreamWriter(out);
			bw = new BufferedWriter(osw);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return bw;
	}
	//��ȡ�ļ�����������Ը��Ƿ�ʽ
	public static BufferedWriter getBufferedWriterFg(File file){
		BufferedWriter bw = null;
		if(!file.exists()){
			try {
				file.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		try {
			FileOutputStream out = new FileOutputStream(file);
			OutputStreamWriter osw = new OutputStreamWriter(out);
			bw = new BufferedWriter(osw);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return bw;
	}
	//��ȡ�ļ���������
	public static BufferedReader getBufferedReader(File file){
		BufferedReader br = null;
		try {
			FileInputStream in = new FileInputStream(file);
			if(!file.exists()){
				file.createNewFile();
			}
			InputStreamReader isr = new InputStreamReader(in);
			br = new BufferedReader(isr);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return br;
	}
	//�������뷽��
	public static String getInput(BufferedReader br){
		String str = null;
		try {
			str =  br.readLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
		}
		return str;
	}
	//�����������
	public static void println(PrintStream ps, String mess){
		ps.println(mess);
		ps.flush();
	}
	public static void print(PrintStream ps, String mess){
		ps.print(mess);
	}
	public static void printlns(PrintStream ps, String mess){
		ps.println(mess);
		ps.print(">>");
	}
	//����ϵͳ����ϵͳ��־�ķ���
	public static void saveLog(String mess){
		//��ӡ������̨
		System.out.println(mess);
		//���浽�ļ�//////׷�ӷ�ʽ
		File serviceLogFile = new File(Util.SERVICELOG);
		try {
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(serviceLogFile, true)));
			bw.write(mess+"\r\n");
			bw.flush();
			bw.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	//����ϵͳ����ϵͳ��־�ķ���
	public static void saveSMTPLog(String mess){
		//��ӡ������̨
		System.out.println(mess);
		//���浽�ļ�//////׷�ӷ�ʽ
		File serviceLogFile = new File(Util.SMTPLOGPATH);
		try {
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(serviceLogFile, true)));
			bw.write(mess+"\r\n");
			bw.flush();
			bw.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	//����ϵͳ����ϵͳ��־�ķ���
	public static void savePop3Log(String mess){
		//��ӡ������̨
		System.out.println(mess);
		//���浽�ļ�//////׷�ӷ�ʽ
		File serviceLogFile = new File(Util.POP3LOGPATH);
		try {
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(serviceLogFile, true)));
			bw.write(mess+"\r\n");
			bw.flush();
			bw.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	//�����ȡ�ͻ����׽��ֵķ���
	public static String getClientInfo(Socket clientSocket){
		return Util.getNowTime()+"����ip��"+clientSocket.getInetAddress().getHostAddress()+"�˿ڣ�"+clientSocket.getPort();
	}
	//�ѵ�����д��usepMap��Ӧ��file��
	public static void write2UserMapFile() {
		File userMapFile = new File(Util.USERMAPPARH);
		Map<String, UserBean> userMap = OnceClass.getOnce().getUserMap();
		try {
			ObjectOutputStream objOut = new ObjectOutputStream(new FileOutputStream(userMapFile));
			//д�룬�ر���
			objOut.writeObject(userMap);
			objOut.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	//��foreignServiceMap�е���Ϣ���浽�ļ���
	public static void write2ForeignServiceFile(){
		File file = new File(Util.FOREIGNSERVICEPATH);
		if(!file.getParentFile().exists()){
			file.getParentFile().exists();
		}
		try {
			OutputStream out = new FileOutputStream(file);
			ObjectOutputStream objOut = new ObjectOutputStream(out);
			//��ȡforeignServiceMap
			objOut.writeObject(OnceClass.getOnce().getForeignServiceMap());
			objOut.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	
	//�ѵ�����д��foreignServiceMap��Ӧ��file��
	public static void write2LocalServiceFile(){
		File LocalHostFile = new File(Util.LOCALHOSTINFOPATH);
		if(!LocalHostFile.exists()){
			try {
				LocalHostFile.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		Properties ps = new Properties();
		LocalServiceBean lostService = OnceClass.getOnce().getLocalServiceInfo();
		BufferedReader br = Util.getBufferedReader(LocalHostFile);
		BufferedWriter bw = Util.getBufferedWriterFg(LocalHostFile);
		try {
			ps.load(br);
			ps.setProperty("mailPath", lostService.getMailPath());
			ps.setProperty("mailSuffix", lostService.getMailSuffix());
			ps.setProperty("serviceState", lostService.getServiceState());
			ps.setProperty("pop3Port", lostService.getPop3Port());
			ps.setProperty("stmpPort", lostService.getStmpPort());
			ps.store(bw, Util.getNowTime());
			br.close();
			bw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	//��onLineMapɾ����ǰ�ѵ����û�
	public static void deleLogOutUser(String userName) {
		// TODO Auto-generated method stub
		OnceClass.getOnce().getOnLineMap().remove(userName);
	}
	//�����ļ���
	public static void mkdirs(File sendFile) {
		// TODO Auto-generated method stub
		if(!sendFile.exists()){
			sendFile.mkdirs();
		}
	}
	//��ȡ�ļ����ֵķ���   20130101121212311.13
	public static String getMailName() {
		// TODO Auto-generated method stub
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS"+Math.random());
		Date date = new Date();
		return sdf.format(date);
	}
	//�����ļ�
	public static void creatNewFile(File sendFile) {
		// TODO Auto-generated method stub
		if(!sendFile.exists()){
			try {
				sendFile.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
