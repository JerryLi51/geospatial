/**
 * Geospatial services Test Client for Smartmad
 * 
 * @author jerry.li
 */
package com.madhouse.ws;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.apache.cxf.jaxrs.client.WebClient;

import com.madhouse.ws.pojo.AddPlacesRequest;
import com.madhouse.ws.pojo.AddPlacesResponse;
import com.madhouse.ws.pojo.AddPolygonsRequest;
import com.madhouse.ws.pojo.AddPolygonsResponse;
import com.madhouse.ws.pojo.GeoPolygon;
import com.madhouse.ws.pojo.LBSCampaignResponse;
import com.madhouse.ws.pojo.NearRequest;
import com.madhouse.ws.pojo.NearResponse;
import com.madhouse.ws.pojo.Place;
import com.madhouse.ws.pojo.WithinPolyRequest;
import com.madhouse.ws.pojo.WithinPolyResponse;



public class GeospatialClient {

	// Put some static value
	private static final String GEO_URL = "http://172.16.27.69:9000/";
	
	private static final String TYPE_XML = "application/xml";
	private static final String TYPE_JSON = "application/json";

	public static void main(String[] args) {

		// System.out.println("Format is " + args[0]);

		//testFindNear(TYPE_XML);
		//System.out.println("*******************************");
		
		//testFindNearWithDistance(TYPE_XML);
		//testFindNear(TYPE_JSON);
		//testAddPoly(TYPE_XML);
		//testWithinPoly(TYPE_XML);
		testAddCampaignPoly(TYPE_JSON);
		testAddCampaignPlaces(TYPE_JSON);
		//testLBSGeoNear(TYPE_JSON);


	}

	private static void testFindNear(final String format) {

		System.out.println("testFindNear called with format " + format);
		WebClient client = WebClient.create(GEO_URL);
		client.path("/geoservice/near").accept(format).type(format);
		NearRequest req = new NearRequest();
		req.setPlace(new Place(1,1));

		req.setCollectionName("places");
		req.setLimit(20);
		req.setDistance(150);

		NearResponse catResponse = client.post(req, NearResponse.class);
				
		List<Place> places = catResponse.getPlaces();
		
		for(Place p : places){
			System.out.println(p.getName() + " - "+ p.getLongitude() + "," + p.getLatitude());
		}
	}
	
	private static void testFindNearWithDistance(final String format) {

		System.out.println("testFindNear called with format " + format);
		WebClient client = WebClient.create(GEO_URL);
		client.path("/geoservice/nearwithdistance").accept(format).type(format);
		NearRequest req = new NearRequest();
		req.setPlace(new Place(121.4946413709, 31.2340995125));
		//req.setPlace(new Place(1,1));

		req.setCollectionName("polyTest");
		//req.setCollectionName("polyTest");
		req.setLimit(5);
		req.setDistance(150);

		NearResponse catResponse = client.post(req, NearResponse.class);
		
		List<Place> places = catResponse.getPlaces();
		
		for(Place p : places){
			System.out.println(p.getName() + " - "+ p.getLongitude() + "," + p.getLatitude() + "=" + p.getDistance());
		}
	}
	
	private static void testWithinPoly(final String format) {

		System.out.println("testWithinPoly called with format " + format);
		WebClient client = WebClient.create(GEO_URL);
		client.path("/geoservice/withinpoly").accept(format).type(format);
		
		WithinPolyRequest request =new WithinPolyRequest();
		
		request.setCollectionName("polyTest");
		
		request.setDistance(0.5);//KM
		request.setPlace(new Place(121.4946413709, 31.2340995125));
		
		WithinPolyResponse response = client.post(request, WithinPolyResponse.class);
		
		List<GeoPolygon> polys = response.getGeoPolys();
		
		for(GeoPolygon poly : polys){
			System.out.println(poly.getOid() + "-" + poly.getName() + "-" + poly.getDistance());
		}

		
	}
	
	private static void testAddPoly(final String format) {

		System.out.println("testAddPoly called with format " + format);
		WebClient client = WebClient.create(GEO_URL);
		client.path("/geoservice/addpolygon").accept(format).type(format);
		
		Place p = new Place(121.4946413709, 31.2340995125);
		
		List<Place> places = new ArrayList<Place>();
		places.add(new Place(121.4984822942, 31.2323196812));
		places.add(new Place(121.4889111664, 31.2312271179));
		places.add(new Place(121.4926887228, 31.2362829711));
		//places.add(new Place(121.4984822942, 31.2323196812));
		
				
		List<GeoPolygon> polys = new ArrayList<GeoPolygon>();
		
		GeoPolygon polygon = new GeoPolygon(places);
		polygon.setCenter(p);
		
		polys.add(polygon);

		
		AddPolygonsRequest request = new AddPolygonsRequest("polyTest", polys);
		
		JAXBContext jaxbContext;
		try {
			jaxbContext = JAXBContext.newInstance(AddPolygonsRequest.class);
			 Marshaller marshaller = jaxbContext.createMarshaller();  
			 marshaller.setProperty("eclipselink.media-type", "application/json");
		     marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		     marshaller.marshal(request, System.out);
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
       
				
		client.post(request);		
	}
	
	private static void testAddCampaignPoly(final String format) {

		System.out.println("testAddPoly called with format " + format);
		WebClient client = WebClient.create(GEO_URL);
		client.path("/geoservice/addpolygons").accept(format).type(format);
		
		Place p = new Place(121.4946413709, 31.2340995125);
		
		List<Place> places = new ArrayList<Place>();
		places.add(new Place(121.4984822942, 31.2323196812));
		places.add(new Place(121.4889111664, 31.2312271179));
		places.add(new Place(121.4926887228, 31.2362829711));
		//places.add(new Place(121.4984822942, 31.2323196812));
		
		GeoPolygon poly = new GeoPolygon(places);
		poly.setName("Test");
		
		List<GeoPolygon> polys = new ArrayList<GeoPolygon>();
		
		poly.setCenter(p);

		polys.add(poly);
		polys.add(poly);
		
		AddPolygonsRequest request = new AddPolygonsRequest("900001", polys);
				
		AddPolygonsResponse response = client.post(request, AddPolygonsResponse.class);
		
	
		
		System.out.println(response.getErrCode());
	}
	
	private static void testAddCampaignPlaces(final String format) {

		System.out.println("testAddPlaces called with format " + format);
		WebClient client = WebClient.create(GEO_URL);
		client.path("/geoservice/addplaces").accept(format).type(format);
		
		Place p = new Place(121.4946413709, 31.2340995125);
		
		List<Place> places = new ArrayList<Place>();
		places.add(new Place(121.4984822942, 31.2323196812));
		places.add(new Place(121.4889111664, 31.2312271179));
		places.add(new Place(121.4926887228, 31.2362829711));
		//places.add(new Place(121.4984822942, 31.2323196812));
		
		for(int i =0; i < places.size(); i++){
			places.get(i).setName("Place" + i);
		}
		
		AddPlacesRequest request = new AddPlacesRequest();
		request.setCampaignId("900002");
		request.setMaxDistance(1000);
		request.setPlaces(places);
			
		AddPlacesResponse response = client.post(request, AddPlacesResponse.class);	
		
		System.out.println(response.getErrCode());
	}
	
	private static void testLBSGeoNear(final String format) {

		System.out.println("testLBSGeoNear called with format " + format);
		WebClient client = WebClient.create(GEO_URL);
		client.path("/geoservice/neargeolocation/").path("900001").path(121.2345).path(31.9876).accept(format).type(format);
				

		LBSCampaignResponse response = client.get(LBSCampaignResponse.class);	
		
		System.out.println(response.getErrCode());
	}





}