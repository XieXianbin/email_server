package com.bean;

import java.io.Serializable;

import com.util.Util;

public class UserBean implements Serializable {

	//�û�bean��
	private String userName; //�˻�
	private String userPass;  //����
	private String userQuestion; //�ܱ�����
	private String userAnswer; //�ܱ���
	private String userRegedtiDate; //ע��ʱ��

	//������
	public UserBean(String userName, String userPass, String  userQuertion, String userAnswer, String userRegedtiDate){
		this.userAnswer = userAnswer;
		this.userName = userName;
		this.userPass = userPass;
		this.userQuestion = userQuertion;
		this.userRegedtiDate = userRegedtiDate;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getUserPass() {
		return userPass;
	}
	public void setUserPass(String userPass) {
		this.userPass = userPass;
	}
	public String getUserQuestion() {
		return userQuestion;
	}
	public void setUserQuestion(String userQuestion) {
		this.userQuestion = userQuestion;
	}
	public String getUserAnswer() {
		return userAnswer;
	}
	public void setUserAnswer(String userAnswer) {
		this.userAnswer = userAnswer;
	}
	public String getUserRegedtiDate() {
		return userRegedtiDate;
	}
	public void setUserRegedtiDate(String userRegedtiDate) {
		this.userRegedtiDate = userRegedtiDate;
	}
}
