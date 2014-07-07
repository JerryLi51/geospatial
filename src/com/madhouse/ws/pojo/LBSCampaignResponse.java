package com.madhouse.ws.pojo;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.madhouse.ws.GeospatialConstants;


@XmlRootElement(name = "LBSCampaignResponse")
public class LBSCampaignResponse {
	private String type = GeospatialConstants.POINT;
	
	
	private List<Place> places = new ArrayList<Place>();
	
	
	private List<GeoPolygon> polygons = new ArrayList<GeoPolygon>();
	
	private int errCode;
	private String errString;
	
	
	@XmlElement
	public int getErrCode() {
		return errCode;
	}
	
	
	public void setErrCode(int errCode) {
		this.errCode = errCode;
	}
	
	@XmlElement
	public String getErrString() {
		return errString;
	}
	public void setErrString(String errString) {
		this.errString = errString;
	}
	
	@XmlElement
	public String getType(){
		return type;
	}
	
	public void setType(String type){
		this.type = type;
	}
	
	@XmlElement(name="polygon")
	public List<GeoPolygon> getPolygons(){
		return polygons;
	}
	
	public void setPolygons(List<GeoPolygon> polygons){
		this.polygons = polygons;
	}
	
	@XmlElement(name="place")
	public List<Place> getPlaces(){
		return this.places;
	}
	
	public void setPlaces(List<Place> places){
		this.places = places;
	}

}
