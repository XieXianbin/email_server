package com.base64coder;

import java.io.IOException;

import sun.misc.BASE64Encoder;
import sun.misc.BASE64Decoder;

public class Base64Coder {

	//����
	public static String enCoder(String str){
		BASE64Encoder encoder = new BASE64Encoder();
		return encoder.encode(str.getBytes());
	}
	//����
	public static String deCoder(String str){
		BASE64Decoder decoder = new BASE64Decoder();
		String okcoder = "";	//��okcoder��ʼ��Ϊ""���ұ������⡣
		try {
			okcoder = new String(decoder.decodeBuffer(str));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return okcoder;
	}
}
