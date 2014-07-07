package com.madhouse.ws.pojo;

import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="AddPlacesRequest")
public class AddPlacesRequest{
	private List<Place> places;
	
	private String campaignId="0";
	
	private double maxDistance =0;

	@XmlElement
	public List<Place> getPlaces() {
		return places;
	}

	public void setPlaces(List<Place> places) {
		this.places = places;
	}
	
	@XmlElement
	public String getCampaignId(){
		return campaignId;
	}
	
	public void setCampaignId(String campaignId){
		this.campaignId = campaignId;
	}
	
	@XmlElement
	public double getMaxDistance(){
		return maxDistance;
	}
	
	public void setMaxDistance(double maxDistance){
		this.maxDistance = maxDistance;
	}
	
	public boolean validateRequest(){
		boolean valid = true;
		if(campaignId == null || campaignId.trim().length() == 0 ||campaignId.trim().equals("0")){
			valid = false;
			
		}
		
		if(places == null || places.size() == 0){
			valid = false;
		}
		
		return valid;
	}

}
