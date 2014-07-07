package com.madhouse.ws.pojo;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement(name = "WithinPolyResponse") 
public class WithinPolyResponse{
	private List<GeoPolygon> geoPolys;

	public List<GeoPolygon> getGeoPolys() {
		return geoPolys;
	}

	public void setGeoPolys(List<GeoPolygon> geoPolys) {
		this.geoPolys = geoPolys;
	}



}
