package com.lifeix.dovebox.client;

import com.l99.exception.service.L99IllegalOperateException;
import com.lifeix.common.utils.CipherUtil;

public class AuthorizationData {

	/**
	 * 用户名
	 */
	private String username;
	/**
	 * 密码
	 */
	private String password;
	/**
	 * 版本号
	 */
	private String version;
	/**
	 * ip地址
	 */
	private String ipAddress;

	private String authorization;

	public AuthorizationData(String ipAddress, String authorization) throws L99IllegalOperateException {
		this.ipAddress = ipAddress;
		this.authorization = authorization;
		if (authorization==null) {
			throw new L99IllegalOperateException("authorization is null");
		}
		CipherUtil cipherUtil = CipherUtil.getInstance();
		String data = cipherUtil.base64Decode(authorization.split(" ")[1]);
		String[] keys = data.split(":");
		String username = cipherUtil.aesDecode(keys[0]);
		String code = cipherUtil.aesDecode(keys[1]);
		
		if(code == null){
			throw new L99IllegalOperateException(" authorization is error ");
		}
		String[] codes = code.split(":");
		if(codes.length < 2){
			throw new L99IllegalOperateException("  authorization code length <2");
		}
		this.username = username;
		this.password = codes[0];
		this.version =codes[1];
	}

	public AuthorizationData(String username,String password,String version) throws L99IllegalOperateException{
		if (username==null||username.equals("")||password==null||password.equals("")||version==null) {
			throw new L99IllegalOperateException(" username password version param is error ");
		}
		this.username = username;
		this.password = password;
		this.version =version;
	}
	
	
	
	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	public String getVersion() {
		return version;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public String getAuthorization() {
		return authorization;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	@Override
	public String toString() {
		return "AuthorizationData [username=" + username + ", password="
				+ password + ", version=" + version + ", ipAddress="
				+ ipAddress + ", authorization=" + authorization + "]";
	}
	
}
