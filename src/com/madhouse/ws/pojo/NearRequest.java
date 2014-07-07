package com.madhouse.ws.pojo;

import javax.xml.bind.annotation.XmlRootElement;  

@XmlRootElement(name = "NearRequest")  
public class NearRequest {
	private Place place;
	private double distance=0;
	private int limit=10;
	
	private String collectionName;
	

	
	public double getDistance(){
		return distance;
	}	

	
	public void setDistance(double distance){
		this.distance = distance;
	}	


	public int getLimit() {
		return limit;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}

	public String getCollectionName() {
		return collectionName;
	}

	public void setCollectionName(String collectionName) {
		this.collectionName = collectionName;
	}

	public Place getPlace() {
		return place;
	}


	public void setPlace(Place place) {
		this.place = place;
	}
	


}
