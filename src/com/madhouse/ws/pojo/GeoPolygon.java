package com.madhouse.ws.pojo;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="GeoPolygon")
public class GeoPolygon {
	private List<Place> places = null;
	
	//描述Within请求中的距离参数，0为在多边形内
	private Double distance = null;
	
	private String oid;
	private String name;
	
	private Place center;
	
	public GeoPolygon(){
		
	}
	
	
	public GeoPolygon(List<Place> places){
		this.places = places;
	}

	@XmlElement(name="place")
	public List<Place> getPlaces() {
		return places;
	}

	public void setPlaces(List<Place> places) {
		this.places = places;
	}

	@XmlElement
	public Double getDistance() {
		return distance;
	}

	public void setDistance(Double distance) {
		this.distance = distance;
	}

	@XmlElement
	public String getOid() {
		return oid;
	}

	public void setOid(String oid) {
		this.oid = oid;
	}

	@XmlElement
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	@XmlElement
	public Place getCenter() {
		return center;
	}


	public void setCenter(Place center) {
		this.center = center;
	}
	
	
	
}
