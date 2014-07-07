package com.madhouse.ws.pojo;

import javax.xml.bind.annotation.XmlRootElement;  

@XmlRootElement(name = "WithinPolyRequest")  
public class WithinPolyRequest {
	private Place place;
	private String collectionName;
	private double distance = 0;//距离商圈若干公里功能，如果为0，则为在商圈内
	
	public WithinPolyRequest(){
		
	}
	
	public WithinPolyRequest(String collectionName, Place place){
		this.setPlace(place);
		this.setCollectionName(collectionName);
	}

	public Place getPlace() {
		return place;
	}

	public void setPlace(Place place) {
		this.place = place;
	}

	public String getCollectionName() {
		return collectionName;
	}

	public void setCollectionName(String collectionName) {
		this.collectionName = collectionName;
	}

	public double getDistance() {
		return distance;
	}

	public void setDistance(double distance) {
		this.distance = distance;
	}


}
