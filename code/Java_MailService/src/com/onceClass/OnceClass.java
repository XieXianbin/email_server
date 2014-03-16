package com.onceClass;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import com.bean.ForeignServiceBean;
import com.bean.LocalServiceBean;
import com.bean.UserBean;

public class OnceClass {
	
	//����û���ϢMap<v, k> v = userName(�ʼ���ַ) k = UserBean
	private Map<String, UserBean> userMap = new HashMap<String, UserBean>();
	//�����û�����
	private Map<String, Socket> onLineMap = new HashMap<String, Socket>();
	//���汾�������Ļ�����Ϣ
	private LocalServiceBean localServiceInfo = new LocalServiceBean();
	//����������������ķ�����������Ϣ
	private Map<String, ForeignServiceBean> foreignServiceMap = new HashMap<String, ForeignServiceBean>();
	//���������ķ�������Ϣ
	private Map<String, ServerSocket> serverSocketMap = new HashMap<String, ServerSocket>();

	//˽�л�������
	private OnceClass(){}
	//ʵ������ǰ���һ������
	private static OnceClass once ;
	//��ȡ�������һ������
	public static OnceClass getOnce() {
		if(once == null){
			once = new OnceClass();
		}
		return once;
	}
	public Map<String, Socket> getOnLineMap() {
		return onLineMap;
	}
	public Map<String, UserBean> getUserMap() {
		return userMap;
	}
	public void setUserMap(Map<String, UserBean> userMap) {
		this.userMap = userMap;
	}
	public LocalServiceBean getLocalServiceInfo() {
		return localServiceInfo;
	}
	public void setLocalServiceInfo(LocalServiceBean localServiceInfo) {
		this.localServiceInfo = localServiceInfo;
	}
	public Map<String, ForeignServiceBean> getForeignServiceMap() {
		return foreignServiceMap;
	}
	public void setForeignServiceMap(
			Map<String, ForeignServiceBean> foreignServiceMap) {
		this.foreignServiceMap = foreignServiceMap;
	}
	public Map<String, ServerSocket> getServerSocketMap() {
		return serverSocketMap;
	}
}
