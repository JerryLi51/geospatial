package com.madhouse.test;

import java.net.UnknownHostException;

import java.util.ArrayList;
import java.util.List;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBAddress;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.MongoException;

/**
 * Example code for Geospatial queries in MongoDB
 * As mongo sample of Smartmad
 * @author amresh.singh
 */
public class GeospatialExample {
	public static final String dbName = "geospatial";
	public static final String host = "127.0.0.1";
	public static final int port = 27017;
	public static final String collectionName = "foo";
	public static final String indexName = "geospatialIdx";
	public static final String indexName2d = "geospatialIdx2d";

	Mongo mongo;
	DBCollection collection;

	private Mongo getMongo() {
		try {
			mongo = new Mongo(new DBAddress(host, port, dbName));
		} catch (MongoException e) {
			e.printStackTrace();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		return mongo;
	}

	public static void main(String[] args) {
		new GeospatialExample().runExample();
	}

	private void runExample() {
		collection = getMongo().getDB(dbName).getCollection(collectionName);
		collection.ensureIndex(new BasicDBObject("loc", "2d"), indexName);
		//collection.ensureIndex(new BasicDBObject("loc", "2d"), indexName2d);
		//addPlace(collection, "people", new double[]{31.2340995125, 121.4946413709});

		//addPlaces();
		//findWithinCircle();
		//findWithinBox();
		findWithinPolygon1();
		//findCenterSphere();
		//findNear();
		//findNearSphere();

	}

	private void findWithinCircle() {

		System.out.println("findWithinCircle\n----------------------\n");
		List circle = new ArrayList();
		circle.add(new double[] { 5, 5 }); // Centre of circle
		circle.add(1); // Radius
		BasicDBObject query = new BasicDBObject("loc", new BasicDBObject(
				"$within", new BasicDBObject("$center", circle)));

		printOutputs(query);
	}

	private void findWithinBox() {
		System.out.println("findWithinBox\n----------------------\n");
		List<double[]> box = new ArrayList();
		box.add(new double[] { 4, 4 }); // Starting coordinate
		box.add(new double[] { 6, 6 }); // Ending coordinate
		BasicDBObject query = new BasicDBObject("loc", new BasicDBObject(
				"$within", new BasicDBObject("$box", box)));

		printOutputs(query);

	}

	private void findWithinPolygon() {
		System.out.println("findWithinPolygon\n----------------------\n");
		List<double[]> polygon = new ArrayList();
		polygon.add(new double[] { 3, 3 }); // Starting coordinate
		polygon.add(new double[] { 8, 3 }); // Ending coordinate
		polygon.add(new double[] { 6, 7 }); // Ending coordinate
		BasicDBObject query = new BasicDBObject("loc", new BasicDBObject(
				"$within", new BasicDBObject("$polygon", polygon)));

		printOutputs(query);
	}
	
	private void findWithinPolygon1() {
		System.out.println("findWithinPolygon\n----------------------\n");
		List<double[]> polygon = new ArrayList();
		polygon.add(new double[] { 31.2323196812,121.4984822942 }); // Starting coordinate
		polygon.add(new double[] {31.2312271179, 121.4889111664 }); // Ending coordinate
		polygon.add(new double[] { 31.2362829711,121.4926887228 }); // Ending coordinate
		BasicDBObject query = new BasicDBObject("loc", new BasicDBObject(
				"$within", new BasicDBObject("$polygon", polygon)));

		printOutputs(query);
	}

	private void findNear() {
		System.out.println("findNear\n----------------------\n");
		BasicDBObject filter = new BasicDBObject("$near", new double[] { 4, 4 });
		filter.put("$maxDistance", 2);

		BasicDBObject query = new BasicDBObject("loc", filter);

		printOutputs(query);
	}

	private void findNearSphere() {
		System.out.println("findNearSphere\n----------------------\n");
		BasicDBObject filter = new BasicDBObject("$nearSphere", new double[] {
				5, 5 });
		filter.put("$maxDistance", 0.06);
		// Radius of the earth: 3959.8728

		BasicDBObject query = new BasicDBObject("loc", filter);
		printOutputs(query);
	}

	private void findCenterSphere() {
		System.out.println("findCenterSphere\n----------------------\n");
		List circle = new ArrayList();
		circle.add(new double[] { 5, 5 }); // Centre of circle
		circle.add(0.06); // Radius
		BasicDBObject query = new BasicDBObject("loc", new BasicDBObject(
				"$within", new BasicDBObject("$centerSphere", circle)));

		printOutputs(query);
	}

	public void printOutputs(BasicDBObject query) {
		DBCursor cursor = collection.find(query);
		List<BasicDBList> outputs = new ArrayList<BasicDBList>();
		while (cursor.hasNext()) {
			DBObject result = cursor.next();
			System.out.println(result.get("name") + "--->" + result.get("loc"));
			outputs.add((BasicDBList) result.get("loc"));
		}

		for (int y = 9; y >= 0; y--) {
			String s = "";
			for (int x = 0; x < 10; x++) {
				boolean found = false;
				for (BasicDBList obj : outputs) {
					double xVal = (Double) obj.get(0);
					double yVal = (Double) obj.get(1);
					if (yVal == y && xVal == x) {
						found = true;
					}
				}
				if (found) {
					s = s + " @";
				} else {
					s = s + " +";
				}
			}
			System.out.println(s);
		}
	}

	private void addPlaces() {
		System.out.println("Adding places...");
		for (int i = 0; i < 100; i++) {
			double x = i % 10;
			double y = Math.floor(i / 10);
			addPlace(collection, Places.cities[i], new double[] { x, y });
		}
		System.out.println("All places added");
	}

	private void addPlace(DBCollection collection, String name,
			final double[] location) {
		final BasicDBObject place = new BasicDBObject();
		place.put("name", name);
		place.put("loc", location);
		collection.insert(place);
	}

}
