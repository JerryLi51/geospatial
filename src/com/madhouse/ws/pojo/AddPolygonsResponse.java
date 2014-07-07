package com.madhouse.ws.pojo;

import javax.xml.bind.annotation.XmlRootElement;  

@XmlRootElement(name = "AddPolygonsResponse") 
public class AddPolygonsResponse {
	
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