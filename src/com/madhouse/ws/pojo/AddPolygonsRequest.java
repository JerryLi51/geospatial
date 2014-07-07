package com.madhouse.ws.pojo;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="AddPolysRequest")
public class AddPolygonsRequest{
	
    
	private List<GeoPolygon> polygons = new ArrayList<GeoPolygon>();
	
	private String campaignId;
	
	private Double maxDistance;
	
	public AddPolygonsRequest()
	{
		
	}
	
	public AddPolygonsRequest(String campaignId, List<GeoPolygon> polygons)
	{
		this.campaignId = campaignId;
		this.polygons = polygons;
	}

    @XmlElement
	public List<GeoPolygon> getPolygons() {
		return polygons;
	}
	public void setPolygon(List<GeoPolygon> polygons) {
		this.polygons = polygons;
	}

	
	public boolean validateRequest() {		
		if(campaignId == null || campaignId.trim().length() == 0 ||campaignId.trim().equals("0")){
			return false;
		}
		
		if(polygons == null || polygons.isEmpty()){
			return false;
		}		

		return true;
	}

	@XmlElement
	public String getCampaignId() {
		return campaignId;
	}

	public void setCampaignId(String campaignId) {
		this.campaignId = campaignId;
	}

	public Double getMaxDistance() {
		return maxDistance;
	}

	public void setMaxDistance(Double maxDistance) {
		this.maxDistance = maxDistance;
	}

}
