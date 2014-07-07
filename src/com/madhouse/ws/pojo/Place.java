package com.madhouse.ws.pojo;

import javax.xml.bind.annotation.XmlElement;


public class Place {
	private double longitude;
	private double latitude;
	private String name;
	
	//只在nearWithDistance的回应中使用，标识结果距离传入位置的距离
	private Double distance = null;
	
	public Place(){
		
	}
	
	public Place(double longitude, double latitude)
	{
		this.longitude = longitude;
		this.latitude = latitude;
	}
	
	

	@XmlElement
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	@XmlElement
	public Double getDistance() {
		return distance;
	}
	public void setDistance(Double distance) {
		this.distance = distance;
	}
	
	@XmlElement
	public double getLongitude() {
		return longitude;
	}
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	
	@XmlElement
	public double getLatitude() {
		return latitude;
	}
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	
	public boolean validate(){
		if(longitude > 180 || longitude < -180){
			return false;
		}
		
		if(latitude > 90 || latitude < -90){
			return false;
		}
		return true;
	}
	
	public boolean equals(Place place){
		return this.longitude == place.getLongitude() && this.latitude == place.getLatitude();
	}
	
	

}
