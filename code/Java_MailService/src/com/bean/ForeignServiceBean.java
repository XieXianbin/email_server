package com.bean;

import java.io.Serializable;

public class ForeignServiceBean implements Serializable {

	/**
	 * ����洢���������ʼ��������Ļ�����Ϣ//���հ�������ӵ�foreignServiceBean��
	 * 		�ʼ��ṩ������/�磺qq.com/163.com/sina.com
	 * 		��������mx��¼
	 *		 �ʼ��ṩ�̵�SMTP�������Ķ˿ں�
	 */
	private String serviceName;
	private String stmpMx;
	private String stmpPort;
	//������
	public ForeignServiceBean(String name, String smx, String sport){
		this.serviceName = name;
		this.stmpMx = smx;
		this.stmpPort = sport;
	}
	public String getServiceName() {
		return serviceName;
	}
	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}
	public String getStmpMx() {
		return stmpMx;
	}
	public void setStmpMx(String stmpMx) {
		this.stmpMx = stmpMx;
	}
	public String getStmpPort() {
		return stmpPort;
	}
	public void setStmpPort(String stmpPort) {
		this.stmpPort = stmpPort;
	}
}
