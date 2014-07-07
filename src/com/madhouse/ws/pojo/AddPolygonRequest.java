package com.madhouse.ws.pojo;

import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement(name = "AddPolygonRequest") 

public class AddPolygonRequest {
	private String collectionName;
	private String polyName;
	private GeoPolygon[] polygons;
	
	public AddPolygonRequest()
	{
		
	}
	
	public AddPolygonRequest(String collectionName, String name, GeoPolygon[] polygons)
	{
		this.collectionName = collectionName;
		this.polyName = name;
		this.polygons = polygons;
	}
	public String getCollectionName() {
		return collectionName;
	}
	public void setCollectionName(String collectionName) {
		this.collectionName = collectionName;
	}
	public String getPolyName() {
		return polyName;
	}
	public void setPolyName(String polyName) {
		this.polyName = polyName;
	}
	public GeoPolygon[] getPolygons() {
		return polygons;
	}
	public void setPolygon(GeoPolygon[] polygons) {
		this.polygons = polygons;
	}

}
