/**
 * Geospatial services for Smartmad
 * Eliminate the requirement of integrate Mongodb with PHP
 * @author jerry.li
 */
package com.madhouse.ws;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import com.madhouse.ws.pojo.AddPlacesRequest;
import com.madhouse.ws.pojo.AddPlacesResponse;
import com.madhouse.ws.pojo.AddPolygonsRequest;
import com.madhouse.ws.pojo.AddPolygonsResponse;
import com.madhouse.ws.pojo.GeoPolygon;
import com.madhouse.ws.pojo.LBSCampaignResponse;
import com.madhouse.ws.pojo.NearRequest;
import com.madhouse.ws.pojo.NearResponse;
import com.madhouse.ws.pojo.Place;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DB;
import com.mongodb.DBAddress;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.MongoException;
import com.mongodb.MongoOptions;
import com.mongodb.ReadPreference;
import com.mongodb.ServerAddress;

@Path("/geoservice")
@Produces({ "application/json", "application/xml" })
public class GeospatialService {
	public static String campaignTargetCollection = "targeting";
	public static final String collectionName = "places";
	public static final String indexName = "geospatialIdx";
	public static String dbName = "geospatial";
	public static String[] hosts = new String[] { "127.0.0.1:27017" };
	// public static int port = 27017;
	private static String bindHost = "localhost";
	private static int bindPort = 9000;
	private static int mongoConnectionsPerHost = 10;
	private static int mongoThreadsAllowedToBlockForConnectionMultiplier = 5;
	private static int mongoMaxWaitTime = 120000;
	private static int mongoConnectTimeout = 10000;
	private static int mongoSocketTimeout = 0;
	private static boolean mongoAutoConnectRetry = false;
	private Mongo mongo;

	public static Logger logger = null;// Logger.getLogger(GeospatialService.class);

	static {
		String home = System.getProperty(GeospatialConstants.GEO_HOME);
		if (home == null || home.trim().length() == 0) {
			home = System.getProperty("user.dir");
		}
		File homeFile = new File(home);
		home = homeFile.getAbsolutePath();
		String confDir = home + "/etc";
		String confFile = confDir + "/geo.ini";

		File confFileObj = new File(confFile);

		if (!confFileObj.exists()) {
			System.out.println("No config file found, exit!" + confFile);
			System.exit(-1);
		}

		Properties props = new Properties();
		try {
			props.load(new FileInputStream(confFile));
			bindHost = props.getProperty(GeospatialConstants.BIND_HOST);
			bindPort = Integer.parseInt(props
					.getProperty(GeospatialConstants.BIND_PORT));
			String hostString = props
					.getProperty(GeospatialConstants.MONGO_HOSTS);
			StringTokenizer st = new StringTokenizer(hostString,
					GeospatialConstants.COMMA);

			if (st.countTokens() == 0) {
				logger.log(Level.SEVERE, "Mongo hosts configuration not found!");
				System.exit(-1);
			}
			hosts = new String[st.countTokens()];
			int i = 0;
			while (st.hasMoreElements()) {
				hosts[i] = st.nextToken();
				i++;
			}

			campaignTargetCollection = props
					.getProperty(GeospatialConstants.CAMP_TARGET_COLLECTION);

			String mongoConnectionsPerHostStr = props
					.getProperty(GeospatialConstants.MONGO_CONNECTION_PER_HOST);
			mongoConnectionsPerHost = (mongoConnectionsPerHostStr == null || mongoConnectionsPerHostStr
					.trim().length() == 0) ? mongoConnectionsPerHost : Integer
					.parseInt(mongoConnectionsPerHostStr);

			String mongoThreadsAllowedToBlockForConnectionMultiplierStr = props
					.getProperty(GeospatialConstants.MONGO_THREADS_ALLOWED_TO_BLOCK_MUL);
			mongoThreadsAllowedToBlockForConnectionMultiplier = (mongoThreadsAllowedToBlockForConnectionMultiplierStr == null || mongoThreadsAllowedToBlockForConnectionMultiplierStr
					.trim().length() == 0) ? mongoThreadsAllowedToBlockForConnectionMultiplier
					: Integer
							.parseInt(mongoThreadsAllowedToBlockForConnectionMultiplierStr);

			String mongoMaxWaitTimeStr = props
					.getProperty(GeospatialConstants.MONGO_MAX_WAIT_TIME);
			mongoMaxWaitTime = (mongoMaxWaitTimeStr == null || mongoMaxWaitTimeStr
					.trim().length() == 0) ? mongoMaxWaitTime : Integer
					.parseInt(mongoMaxWaitTimeStr);

			String mongoConnectTimeoutStr = props
					.getProperty(GeospatialConstants.MONGO_CONNECTION_TIMEOUT);
			mongoConnectTimeout = (mongoConnectTimeoutStr == null || mongoConnectTimeoutStr
					.trim().length() == 0) ? mongoConnectTimeout : Integer
					.parseInt(mongoConnectTimeoutStr);

			String mongoSocketTimeoutStr = props
					.getProperty(GeospatialConstants.MONGO_SOCKET_TIMEOUT);
			mongoSocketTimeout = (mongoSocketTimeoutStr == null || mongoSocketTimeoutStr
					.trim().length() == 0) ? mongoSocketTimeout : Integer
					.parseInt(mongoSocketTimeoutStr);

			String mongoAutoConnectRetryStr = props
					.getProperty(GeospatialConstants.MONGO_AUOT_CONNECT_RETRY);
			mongoAutoConnectRetry = (mongoAutoConnectRetryStr == null || mongoAutoConnectRetryStr
					.trim().length() == 0) ? mongoAutoConnectRetry : Boolean
					.parseBoolean(mongoAutoConnectRetryStr);

		} catch (IOException ex) {
			logger.log(Level.WARNING, ex.getMessage());
			ex.printStackTrace();
		} catch (NumberFormatException ex) {
			logger.log(Level.WARNING, ex.getMessage());
			ex.printStackTrace();
		}

		// init log configuration
		if (System.getProperty("java.util.logging.config.file") == null) {
			System.setProperty("java.util.logging.config.file", confDir
					+ "/logging.ini");

		}

		logger = Logger.getLogger(GeospatialService.class.getName());

	}

	// DBCollection collection;

	public static void main(String[] args) {

		// Service instance

		GeospatialService geoService = new GeospatialService();
		geoService.initGeospatialService();

		JAXRSServerFactoryBean restServer = new JAXRSServerFactoryBean();
		
		//change to Jackson Json provider
		JacksonJsonProvider provider = new JacksonJsonProvider();
		ObjectMapper mapper = new ObjectMapper();
		mapper.enable(SerializationFeature.WRAP_ROOT_VALUE);
		provider.setMapper(mapper);
		
		restServer.setProvider(provider);

		restServer.setResourceClasses(NearRequest.class);
		List<Object> services = new ArrayList<Object>();
		services.add(geoService);

		restServer.setServiceBeans(services);
		String address = "http://" + bindHost + ":" + bindPort + "/";

		restServer.setAddress(address);

		restServer.create();

		while (true) {
			try {

				logger.log(Level.FINEST,
						"System running at: " + System.currentTimeMillis());
				Thread.sleep(600000);

			} catch (Exception ex) {
				ex.printStackTrace();
				logger.log(Level.SEVERE, ex.getMessage());
				System.out.println("System exists! " + ex.getMessage());
			}

		}

	}

	public void initGeospatialService() {
		try {
			// logger.log(Level.INFO, "Connecting to MongoDB at " + host +":" +
			// port + ":" + dbName);
			List<ServerAddress> hostList = new ArrayList<ServerAddress>();
			for (String host : hosts) {
				StringTokenizer st = new StringTokenizer(host,
						GeospatialConstants.COLLIN);
				if (st.countTokens() == 1) {
					hostList.add(new DBAddress(st.nextToken(), 27017, dbName));
				} else if (st.countTokens() == 2) {
					String hostName = st.nextToken();
					int port = Integer.parseInt(st.nextToken());
					hostList.add(new DBAddress(hostName, port, dbName));
				}
			}

			// Mongo options
			MongoOptions option = new MongoOptions();
			option.setAutoConnectRetry(mongoAutoConnectRetry);
			option.setConnectionsPerHost(mongoConnectionsPerHost);
			option.setConnectTimeout(mongoConnectTimeout);
			option.setMaxWaitTime(mongoMaxWaitTime);
			option.setSocketTimeout(mongoSocketTimeout);
			option.setReadPreference(ReadPreference.nearest());
			option.setThreadsAllowedToBlockForConnectionMultiplier(mongoThreadsAllowedToBlockForConnectionMultiplier);

			mongo = new Mongo(hostList, option);

		} catch (MongoException e) {
			logger.log(Level.SEVERE, e.getMessage());
			e.printStackTrace();
		} catch (UnknownHostException e) {
			logger.log(Level.SEVERE, e.getMessage());
			e.printStackTrace();
		}

	}

	@POST
	@Path("/near")
	@Consumes({ "application/json", "application/xml" })
	// Use Mongo find to get all locations near a place
	// first version test service, function not in use
	@Deprecated
	public Response findNear(NearRequest request) {
		double x = request.getPlace().getLongitude();
		double y = request.getPlace().getLatitude();
		double distance = request.getDistance();
		int numLimit = request.getLimit();
		String collectionName = request.getCollectionName();

		DB db = mongo.getDB(dbName);
		db.requestStart();
		NearResponse response = new NearResponse();

		try {
			db.requestEnsureConnection();
			DBCollection collection = db.getCollection(collectionName);

			double distAngle = distance / GeospatialConstants.FLAT_UNITS;// GeospatialService.EARTH_RADIUS;

			BasicDBObject filter = new BasicDBObject("$near", new double[] { x,
					y });
			filter.put("$maxDistance", distAngle);

			BasicDBObject query = new BasicDBObject("loc", filter);

			DBCursor cursor = collection.find(query).limit(numLimit);

			List<Place> places = new ArrayList<Place>();

			while (cursor.hasNext()) {
				DBObject result = cursor.next();

				BasicDBList location = (BasicDBList) result.get("loc");

				Place p = new Place((Double) location.get(0),
						(Double) location.get(1));
				p.setName(result.get("name").toString());

				places.add(p);
			}

			response.setPlaces(places);

		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			db.requestDone();
		}

		return Response.ok(response).build();

	}

	@POST
	@Path("/nearwithdistance")
	@Consumes({ "application/json", "application/xml" })
	@Deprecated
	// use mongodb geoNear to find locations near some place, this can return
	// distance in kilo, first version test function, not in use
	public Response findNearWithDistance(NearRequest request) {
		double x = request.getPlace().getLongitude();
		double y = request.getPlace().getLatitude();
		double distance = request.getDistance();
		// int numLimit = request.getLimit();
		String collectionName = request.getCollectionName();

		DB db = mongo.getDB(dbName);
		db.requestStart();
		NearResponse response = new NearResponse();

		try {
			db.requestEnsureConnection();
			// DBCollection collection =
			// mongo.getDB(dbName).getCollection(collectionName);
			BasicDBObject geoNearCommand = new BasicDBObject();
			geoNearCommand.append("geoNear", collectionName);
			double[] loc = { x, y };

			geoNearCommand.append("near", loc);
			geoNearCommand.append("spherical", true);
			geoNearCommand.append("distanceMultiplier",
					GeospatialConstants.EARTH_RADIUS);

			// 设置了distanceMultiplier,maxDistance还是要除
			geoNearCommand.append("maxDistance", distance
					/ GeospatialConstants.EARTH_RADIUS);

			BasicDBObject geoNearResult = db.command(geoNearCommand);

			BasicDBList result = (BasicDBList) geoNearResult.get("results");

			Iterator<Object> iter = result.iterator();

			List<Place> places = new ArrayList<Place>();

			while (iter.hasNext()) {
				BasicDBObject obj = (BasicDBObject) iter.next();

				double disVal = ((Double) obj.get("dis")).doubleValue();

				BasicDBObject pobj = (BasicDBObject) obj.get("obj");

				String name = (String) pobj.get("name");
				BasicDBList xy = (BasicDBList) pobj.get("loc");

				double xval = (Double) xy.get(0);
				double yval = (Double) xy.get(1);

				Place p = new Place(xval, yval);
				p.setName(name);

				p.setDistance(disVal);
				places.add(p);

			}

			response.setPlaces(places);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return Response.ok(response).build();
	}

	@POST
	@Path("/addplaces")
	@Consumes({ "application/json", "application/xml" })
	// add places, use for add point type campaign targeting
	public Response addPlaces(AddPlacesRequest request) {

		AddPlacesResponse response = new AddPlacesResponse();
		response.setErrCode(GeospatialError.OK);

		if (!request.validateRequest()) {
			response.setErrCode(GeospatialError.PARAM_ERROR);
			response.setErrString("Request parameter incorrect.");
			return Response.ok(response).build();
		}

		String campaignId = request.getCampaignId();

		DB db = mongo.getDB(dbName);
		db.requestStart();

		try {
			db.requestEnsureConnection();

			this.buildCampaignTarget(db, request);

			// 如果活动数据集已经存在，则删除
			if (this.isCampaingCollectionExists(db, campaignId)) {
				db.getCollection(campaignId).drop();
			}

			DBCollection collection = db.getCollection(request.getCampaignId());
			collection.ensureIndex(new BasicDBObject("loc", "2d"));

			List<Place> places = request.getPlaces();

			for (Place p : places) {
				double lon = p.getLongitude();
				double lat = p.getLatitude();

				String name = p.getName();

				double[] pos = { lon, lat };

				BasicDBObject pObj = new BasicDBObject();
				pObj.put("name", name);
				pObj.put("loc", pos);

				collection.insert(pObj);
				logger.log(Level.INFO, "Place " + pObj.toString() + " saved.");
			}

		} catch (Exception ex) {
			response.setErrCode(GeospatialError.ERROR_INTERNAL);
			response.setErrString(ex.getMessage());
			logger.log(Level.SEVERE, ex.getMessage());
			ex.printStackTrace();
		} finally {
			db.requestDone();
		}

		return Response.ok(response).build();

	}

	@POST
	@Path("/addpolygons")
	@Consumes({ "application/json", "application/xml" })
	public Response addPolygons(AddPolygonsRequest request) {
		DB db = mongo.getDB(dbName);
		db.requestStart();

		String campaignId = request.getCampaignId();

		AddPolygonsResponse response = new AddPolygonsResponse();
		response.setErrCode(GeospatialError.OK);

		if (!request.validateRequest()) {
			response.setErrCode(GeospatialError.PARAM_ERROR);
			response.setErrString("Parameter Error.");
			return Response.ok(response).build();
		}

		for (GeoPolygon poly : request.getPolygons()) {
			if (poly.getPlaces().size() < 3) {
				response.setErrCode(GeospatialError.PARAM_ERROR);
				response.setErrString("At least 3 points to define polygon.");
				return Response.ok(response).build();
			}

			for (Place p : poly.getPlaces()) {
				if (!p.validate()) {
					response.setErrCode(GeospatialError.PARAM_ERROR);
					response.setErrString("Coordinate Error, use longtitude first (-180 ~ 180)");
					Response.ok(response).build();
				}
			}
		}

		try {
			db.requestEnsureConnection();

			this.buildCampaignTarget(db, request);

			if (this.isCampaingCollectionExists(db, campaignId)) {
				db.getCollection(campaignId).drop();
			}

			DBCollection collection = db.getCollection(request.getCampaignId());
			collection.ensureIndex(new BasicDBObject("loc", "2dsphere"));
			for (GeoPolygon poly : request.getPolygons()) {

				DBObject polyObject = new BasicDBObject();

				Map<String, Object> map = new HashMap<String, Object>();
				map.put("type", "Polygon");

				int arraySize = poly.getPlaces().size();
				if (!poly
						.getPlaces()
						.get(0)
						.equals(poly.getPlaces().get(
								poly.getPlaces().size() - 1))) {
					arraySize++;
				}

				double[][][] points = new double[1][arraySize][];

				int i = 0;
				for (Place po : poly.getPlaces()) {
					double lon = po.getLongitude();
					double lat = po.getLatitude();

					points[0][i] = new double[] { lon, lat };
					i++;
				}
				if (response.getErrCode() != GeospatialError.OK) {
					break;
				}

				// Mongdb requires the first point of polygon are the same with
				// the first
				if (points[0][0][0] != points[0][arraySize - 2][0]
						|| points[0][0][1] != points[0][arraySize - 2][1]) {
					points[0][points[0].length - 1] = points[0][0];
				}
				map.put("coordinates", points);

				Map<String, Object> dataMap = new HashMap<String, Object>();
				dataMap.put("loc", map);
				dataMap.put("name", poly.getName());

				Place center = poly.getCenter();
				if (center != null) {
					dataMap.put("center", new double[] { center.getLongitude(),
							center.getLatitude() });
				}

				polyObject = BasicDBObjectBuilder.start(dataMap).get();

				collection.insert(polyObject);
				logger.log(Level.INFO, "Poly object " + polyObject.toString()
						+ "saved.");
			}

		} catch (Exception ex) {
			response.setErrCode(GeospatialError.ERROR_INTERNAL);
			response.setErrString(ex.getMessage());
			logger.log(Level.SEVERE, ex.getMessage());
			ex.printStackTrace();

		} finally {
			db.requestDone();
		}

		return Response.ok(response).build();
	}

	@GET
	@Path("/lbscampaign")
	@Produces({ "application/json" })
	// 引擎调用，判断活动条件
	public Response nearGeoLocation(
			@QueryParam("campaignid") String campaignid,
			@QueryParam("longitude") double longitude,
			@QueryParam("latitude") double latitude) {

		logger.log(Level.INFO, "LBS campaign request: " + campaignid + ":"
				+ longitude + ":" + latitude);

		LBSCampaignResponse response = new LBSCampaignResponse();
		response.setErrCode(GeospatialError.OK);

		Place place = new Place(longitude, latitude);

		if (!place.validate()) {
			response.setErrCode(GeospatialError.PARAM_ERROR);
			response.setErrString("Invalid geospatial place.");
			return Response.ok(response).build();

		}

		DB db = mongo.getDB(dbName);
		db.requestStart();

		db.requestEnsureConnection();

		DBCollection targetCollection = db
				.getCollection(campaignTargetCollection);

		BasicDBObject filter = new BasicDBObject("campaignId", campaignid);
		DBCursor cursor = targetCollection.find(filter).limit(1);

		String targetType;
		double maxDistance;
		if (cursor.hasNext()) {
			BasicDBObject targetObj = (BasicDBObject) cursor.next();
			targetType = targetObj.getString("targetType");
			maxDistance = targetObj.getDouble("maxDistance");
			if (maxDistance == 0) {
				maxDistance = 0.001;
			}
			response.setType(targetType);
		} else {
			response.setErrCode(GeospatialError.CAMPAIGN_NOT_EXIST);
			response.setErrString("The campaign target not exists.");
			return Response.ok(response).build();
		}

		if (targetType.equalsIgnoreCase(GeospatialConstants.POLY)) {

			try {

				BasicDBObject geoNearCommand = new BasicDBObject();
				geoNearCommand.append("geoNear", campaignid);
				double[] loc = { longitude, latitude };

				geoNearCommand.append("near", loc);
				geoNearCommand.append("spherical", true);
				geoNearCommand.append("distanceMultiplier",
						GeospatialConstants.EARTH_RADIUS);

				// 设置了distanceMultiplier,maxDistance还是要除
				geoNearCommand.append("maxDistance", maxDistance
						/ GeospatialConstants.EARTH_RADIUS);
				logger.log(Level.INFO,
						"Execute db command: " + geoNearCommand.toString());

				BasicDBObject geoNearResult = db.command(geoNearCommand);

				if (geoNearResult == null || geoNearResult.isEmpty()
						|| geoNearResult.get("results") == null) {
					response.setErrCode(GeospatialError.ERROR_NOT_FOUND);
					response.setErrString("Can't found polygons to full fill the condition.");
					return Response.ok(response).build();

				}

				BasicDBList result = (BasicDBList) geoNearResult.get("results");
				if (result.isEmpty()) {
					response.setErrCode(GeospatialError.ERROR_NOT_FOUND);
					response.setErrString("Can't found polygons to full fill the condition.");
					return Response.ok(response).build();
				}

				Iterator<Object> iter = result.iterator();

				List<GeoPolygon> polygons = new ArrayList<GeoPolygon>();

				while (iter.hasNext()) {
					BasicDBObject obj = (BasicDBObject) iter.next();

					double disVal = ((Double) obj.get("dis")).doubleValue();
					BasicDBObject objVal = (BasicDBObject) obj.get("obj");
					String oid = objVal.getObjectId("_id").toString();
					String name = (String) objVal.get("name");

					BasicDBList center = (BasicDBList) objVal.get("center");
					double centerLon = (Double) center.get(0);
					double centerLat = (Double) center.get(1);
					Place centerPlace = new Place(centerLon, centerLat);

					List<Place> places = new ArrayList<Place>();
					BasicDBObject locObj = (BasicDBObject) objVal.get("loc");
					BasicDBList coordinatesObj = (BasicDBList) locObj
							.get("coordinates");

					for (int i = 0; i < coordinatesObj.size(); i++) {
						BasicDBList coordinatesObjL1 = (BasicDBList) coordinatesObj
								.get(i);
						for (int j = 0; j < coordinatesObjL1.size(); j++) {
							BasicDBList realObj = (BasicDBList) coordinatesObjL1
									.get(j);
							Double lon = (Double) realObj.get(0);
							Double lat = (Double) realObj.get(1);
							places.add(new Place(lon, lat));
						}

					}

					GeoPolygon poly = new GeoPolygon();
					poly.setCenter(centerPlace);
					poly.setDistance(disVal);
					poly.setOid(oid);
					poly.setName(name);

					poly.setPlaces(places);

					polygons.add(poly);
				}

				response.setPolygons(polygons);
			} catch (Exception ex) {
				response.setErrCode(GeospatialError.ERROR_INTERNAL);
				response.setErrString(ex.getMessage());
				ex.printStackTrace();
			} finally {
				db.requestDone();
			}

			return Response.ok(response).build();
		} else if (targetType.trim()
				.equalsIgnoreCase(GeospatialConstants.POINT)) {
			try {
				BasicDBObject geoNearCommand = new BasicDBObject();
				geoNearCommand.append("geoNear", campaignid);
				double[] loc = { longitude, latitude };

				geoNearCommand.append("near", loc);
				geoNearCommand.append("spherical", true);
				geoNearCommand.append("distanceMultiplier",
						GeospatialConstants.EARTH_RADIUS);

				// 设置了distanceMultiplier,maxDistance还是要除
				geoNearCommand.append("maxDistance", maxDistance
						/ GeospatialConstants.EARTH_RADIUS);

				logger.log(Level.INFO,
						"Execute db command: " + geoNearCommand.toString());

				BasicDBObject geoNearResult = db.command(geoNearCommand);

				BasicDBList result = (BasicDBList) geoNearResult.get("results");

				if (result == null || result.isEmpty()) {
					response.setErrCode(GeospatialError.ERROR_NOT_FOUND);
					response.setErrString("No records found.");
					return Response.ok(response).build();
				}

				Iterator<Object> iter = result.iterator();

				List<Place> places = new ArrayList<Place>();

				while (iter.hasNext()) {
					BasicDBObject obj = (BasicDBObject) iter.next();

					double disVal = ((Double) obj.get("dis")).doubleValue();

					BasicDBObject pobj = (BasicDBObject) obj.get("obj");

					String name = (String) pobj.get("name");
					BasicDBList xy = (BasicDBList) pobj.get("loc");

					double xval = (Double) xy.get(0);
					double yval = (Double) xy.get(1);

					Place p = new Place(xval, yval);
					p.setName(name);

					p.setDistance(disVal);
					places.add(p);

				}

				response.setPlaces(places);
			} catch (Exception ex) {
				response.setErrCode(GeospatialError.ERROR_INTERNAL);
				response.setErrString(ex.getMessage());
				logger.log(Level.SEVERE, ex.getMessage());
				ex.printStackTrace();
			}

		}
		return Response.ok(response).build();

	}

	@GET
	@Path("/samplereq")
	@Produces({ "application/json" })
	public Response sampleRequest(@QueryParam("type") String type) {
		Object requestObj = null;
		if (type.equalsIgnoreCase("addPoly")) {
			Place p = new Place(121.4946413709, 31.2340995125);

			List<Place> places = new ArrayList<Place>();
			places.add(new Place(121.4984822942, 31.2323196812));
			places.add(new Place(121.4889111664, 31.2312271179));
			places.add(new Place(121.4926887228, 31.2362829711));
			// places.add(new Place(121.4984822942, 31.2323196812));

			GeoPolygon poly = new GeoPolygon(places);
			poly.setName("Test");

			List<GeoPolygon> polys = new ArrayList<GeoPolygon>();

			poly.setCenter(p);

			polys.add(poly);
			polys.add(poly);

			AddPolygonsRequest request = new AddPolygonsRequest("900001", polys);
			request.setMaxDistance(new Double(0.5));
			requestObj = request;
		} else if (type.equalsIgnoreCase("addPoint")) {
			Place p = new Place(121.4946413709, 31.2340995125);

			List<Place> places = new ArrayList<Place>();
			places.add(new Place(121.4984822942, 31.2323196812));
			places.add(new Place(121.4889111664, 31.2312271179));
			places.add(new Place(121.4926887228, 31.2362829711));
			// places.add(new Place(121.4984822942, 31.2323196812));

			for (int i = 0; i < places.size(); i++) {
				places.get(i).setName("Place" + i);
			}

			AddPlacesRequest request = new AddPlacesRequest();
			request.setCampaignId("900002");
			request.setMaxDistance(0.5);
			request.setPlaces(places);

			requestObj = request;
		} else if (type.equalsIgnoreCase("addPointResponse")) {
			AddPlacesResponse addPlacesResponse = new AddPlacesResponse();
			addPlacesResponse.setErrCode(GeospatialError.PARAM_ERROR);
			addPlacesResponse.setErrString("parameter incorrect.");
			requestObj = addPlacesResponse;

		} else if (type.equalsIgnoreCase("addPolyResponse")) {
			AddPolygonsResponse addPolyResponse = new AddPolygonsResponse();
			addPolyResponse.setErrCode(GeospatialError.CAMPAIGN_NOT_EXIST);
			addPolyResponse.setErrString("campaign does not exist.");
			requestObj = addPolyResponse;

		}

		if (requestObj != null) {
			return Response.ok(requestObj).build();
		}

		return Response.serverError().build();

	}

	private boolean isCampaingCollectionExists(DB db, String campaignId) {
		Set<String> collectionNames = db.getCollectionNames();
		if (collectionNames != null) {
			return collectionNames.contains(campaignId);
		}
		return false;
	}

	// 创建活动条件部分，campaignid和距离要求存入mongodb
	private void buildCampaignTarget(DB db, Object request) {
		DBCollection targetCollection = db
				.getCollection(campaignTargetCollection);
		targetCollection.ensureIndex("campaignId");

		String campaignId = "0";// request.getCampaignId();

		double distance = 0;
		String type = null;
		if (request instanceof AddPlacesRequest) {
			campaignId = ((AddPlacesRequest) request).getCampaignId();
			distance = ((AddPlacesRequest) request).getMaxDistance();
		} else if (request instanceof AddPolygonsRequest) {
			AddPolygonsRequest polyRequest = (AddPolygonsRequest) request;
			campaignId = polyRequest.getCampaignId();
			if (polyRequest.getMaxDistance() != null) {
				distance = polyRequest.getMaxDistance().doubleValue();
			}
			type = GeospatialConstants.POLY;
		} else {
			logger.log(Level.WARNING, "Invalid campaign type:"
					+ request.getClass().getName());
			return;
		}

		// 如果条件已经存在，则删除条件, 插入新条件

		BasicDBObject existObj = new BasicDBObject("campaignId", campaignId);
		targetCollection.remove(existObj);

		BasicDBObject newOne = new BasicDBObject("campaignId", campaignId);
		newOne.put("targetType", type);
		newOne.put("maxDistance", distance);

		targetCollection.insert(newOne);
		logger.log(Level.INFO, "Campaign target data " + newOne.toString()
				+ "saved.");
	}

}
