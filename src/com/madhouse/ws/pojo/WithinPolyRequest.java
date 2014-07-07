package com.madhouse.ws.pojo;

import javax.xml.bind.annotation.XmlRootElement;  

@XmlRootElement(name = "WithinPolyRequest")  
public class WithinPolyRequest {
	private Place place;
	private String collectionName;
	private double distance = 0;//������Ȧ���ɹ��﹦�ܣ����Ϊ0����Ϊ����Ȧ��
	
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
