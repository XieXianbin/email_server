package com.bean;

import com.util.Util;

public class LocalServiceBean {

	//���ط������Ļ���������Ϣ
	private String mailSuffix; //�������ṩ�ķ����ʼ���׺ 
	private String stmpPort; //���ط�������stmp�˿�
	private String pop3Port; //���ط�������pop3�˿�
	private String mailPath; //Ĭ�ϵ��ʼ�·��
	private String ServiceState; //�û����������״̬��running:��ʾ��������  shutdown:��ʾ����رա�
	public String getMailSuffix() {
		return mailSuffix;
	}
	public void setMailSuffix(String mailSuffix) {
		this.mailSuffix = mailSuffix;
	}
	public String getStmpPort() {
		return stmpPort;
	}
	public void setStmpPort(String stmpPort) {
		this.stmpPort = stmpPort;
	}
	public String getPop3Port() {
		return pop3Port;
	}
	public void setPop3Port(String pop3Port) {
		this.pop3Port = pop3Port;
	}
	public String getMailPath() {
		return mailPath;
	}
	public void setMailPath(String mailPath) {
		this.mailPath = mailPath;
	}
	public String getServiceState() {
		return ServiceState;
	}
	public void setServiceState(String serviceState) {
		ServiceState = serviceState;
	}
}
