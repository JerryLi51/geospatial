package com.madhouse.ws;

public class GeospatialConstants {
	public static final String POINT = "point";
	public static final String POLY = "polygon";
	
	public static final String CAMPAIGN_ID = "campaignId";
	
	public static final int EARTH_RADIUS = 6371;
	public static final int FLAT_UNITS = 111;
	
	public static final String GEO_HOME="GEO_HOME";
	
	public static final String MONGO_HOSTS = "mongo_hosts";
	public static final String MONGO_PORT = "mongo_port";
	public static final String MONGO_DB = "mongo_db";
	public static final String BIND_HOST = "bind_host";
	public static final String BIND_PORT = "bind_port";
	public static final String CAMP_TARGET_COLLECTION="campaign_target_collection";
	
	public static final String COMMA = ",";
	public static final String COLLIN = ":";
	
	//MongoDB options
	public static final String MONGO_CONNECTION_PER_HOST = "mongo_connectionsPerHost";
    public static final String MONGO_THREADS_ALLOWED_TO_BLOCK_MUL =  "mongo_threadsAllowedToBlockForConnectionMultiplier";
	public static final String MONGO_MAX_WAIT_TIME = "mongo_maxWaitTime";
	public static final String MONGO_CONNECTION_TIMEOUT = "mongo_connectTimeout=10000";
	public static final String MONGO_SOCKET_TIMEOUT = "mongo_socketTimeout";
	public static final String MONGO_AUOT_CONNECT_RETRY = "mongo_autoConnectRetry";
	

}
