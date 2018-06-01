package com.gome.util;

import java.io.IOException;
import java.util.Properties;

public class Config {
	static Properties properties;
	static{
		properties = new Properties();
		try {
			properties.load(Config.class.getClassLoader().getResourceAsStream("config.properties"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static String clustername = properties.getProperty("es.cluster.name");
	public static String esip = properties.getProperty("es.cluster.ip");
	public static int esport = Integer.parseInt(properties.getProperty("es.cluster.port"));
	public static String esdatabase = properties.getProperty("es.database");
	public static String estable = properties.getProperty("es.table");


}
