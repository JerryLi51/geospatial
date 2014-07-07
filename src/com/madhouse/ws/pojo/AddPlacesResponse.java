package com.madhouse.ws.pojo;

import javax.xml.bind.annotation.XmlRootElement;


@SuppressWarnings("unused")
@XmlRootElement(name = "AddPlacesResponse") 
public class AddPlacesResponse {
	
	private int errCode;
	private String errString;
	
	
	public int getErrCode() {
		return errCode;
	}
	public void setErrCode(int errCode) {
		this.errCode = errCode;
	}
	public String getErrString() {
		return errString;
	}
	public void setErrString(String errString) {
		this.errString = errString;
	}

}
