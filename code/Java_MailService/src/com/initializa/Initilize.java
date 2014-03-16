package com.initializa;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import com.bean.ForeignServiceBean;
import com.bean.LocalServiceBean;
import com.bean.UserBean;
import com.onceClass.OnceClass;
import com.util.Util;

public class Initilize {

	/**
	 * ��ʼ���ʼ���������
	 * 			��ʼ��userMap
	 * 			��ʼ�����ʼ�ϵͳ��STMP��POP3�˿ںš��ʼ���Ĭ�ϴ��·�����ʼ��ĺ�׺�����ʼ�ϵͳʹ�õĺ�׺���ƣ�
	 * 			��ʼ�������ʼ���������stmp��pop3��Ϣ
	 */
	OnceClass once;
	public void initUser(){
		//��ʼ���ʼ�������������־�ļ�
		File serviceLogFile = new File(Util.SERVICELOG);
		if(!serviceLogFile.getParentFile().exists()){
			serviceLogFile.getParentFile().mkdirs();
		}
		//��ʼ��userMap
		initUserMap();
		//��ʼ�����ʼ�ϵͳ��STMP��POP3�˿ںš��ʼ���Ĭ�ϴ��·�����ʼ��ĺ�׺ localServer
		initLocalHost();
		//��ʼ�������ʼ���������stmp��pop3��Ϣ
		initForeignService();
	}
	//��ʼ�����ʼ�ϵͳ��STMP��POP3�˿ںš��ʼ���Ĭ�ϴ��·�����ʼ��ĺ�׺
	private void initLocalHost() {
		// TODO Auto-generated method stub
		once = OnceClass.getOnce();
		File file = new File(Util.LOCALHOSTINFOPATH);
		if(!file.getParentFile().exists()){
			file.getParentFile().mkdirs();
		}
		if(file.exists() && file.isFile() && file.length()!=0){
			//�����ڱ����ʼ��������Ļ���������Ϣ�������
			BufferedReader br = Util.getBufferedReader(file);
			Properties ps = new Properties();
			try {
				ps.load(br);
				//��ȡ�������е�local...����
				LocalServiceBean localService = once.getLocalServiceInfo();
				localService.setMailPath(ps.getProperty("mailPath"));
				localService.setMailSuffix(ps.getProperty("mailSuffix"));
				localService.setPop3Port(ps.getProperty("pop3Port"));
				localService.setServiceState(ps.getProperty("serviceState"));
				localService.setStmpPort(ps.getProperty("stmpPort"));
				br.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else{
			//�������ڱ��ط������Ļ���������Ϣʱ����ʼ��ΪĬ��ֵ
			once = OnceClass.getOnce();
			LocalServiceBean localService = once.getLocalServiceInfo();
			localService.setMailPath(Util.DEFAULTMAILPATH);
			localService.setMailSuffix("@domain.com");
			localService.setPop3Port("110");
			localService.setServiceState("shutdown");
			localService.setStmpPort("25");
			Util.write2LocalServiceFile();
		}
	}
	//��ʼ��userMap
	public void initUserMap(){
		//��ʼ��userMap
		once = OnceClass.getOnce();
		File file = new File(Util.USERMAPPARH);
		if(!file.getParentFile().exists()){
			file.getParentFile().mkdirs();
		}
		//ʵ�������userMap���ļ�����
		if(file.exists() && file.length()!=0){
			try {
				ObjectInputStream objIn = new ObjectInputStream(new FileInputStream(file));
				Map<String, UserBean> userMap = (Map<String, UserBean>) objIn.readObject();
				once.setUserMap(userMap);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	//��ʼ�������ʼ���������stmp��pop3��Ϣ
	public void initForeignService(){
		File file = new File(Util.FOREIGNSERVICEPATH);
		if(!file.getParentFile().exists()){
			file.getParentFile().exists();
		}
		if(file.exists() && file.isFile() && file.length()!=0){
			once = OnceClass.getOnce();
			try {
				InputStream in = new FileInputStream(file);
				ObjectInputStream objIn = new ObjectInputStream(in);
				Map<String, ForeignServiceBean> foreignServiceMap;
				foreignServiceMap = (Map<String, ForeignServiceBean>) objIn.readObject();
				once.setForeignServiceMap(foreignServiceMap);
				objIn.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}
}
