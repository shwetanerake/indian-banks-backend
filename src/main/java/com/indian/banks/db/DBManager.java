package com.indian.banks.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public class DBManager {

	private String dbType = null;
	private String dbHostname = null;
	private String dbUsername = null;
	private Integer dbPort = null;
	private String dbPassword = null;
	private String dbName = null;

	public Connection establishDBConnection(JsonObject configJson) throws Exception {
		String jdbcURL = null;
		System.out.println("establishing jdbc connection..");
		Connection connection = null;
		try {
			if (configJson.containsKey("db.type") && !configJson.getString("db.type").isEmpty()
					&& configJson.getString("db.type") != null) {
				dbType = configJson.getString("db.type");
			} else {
				throw new NullPointerException("db.type missing in conf file");
			}

			if (configJson.containsKey("db.hostname") && !configJson.getString("db.hostname").isEmpty()
					&& configJson.getString("db.hostname") != null) {
				dbHostname = configJson.getString("db.hostname");
			} else {
				throw new NullPointerException("db.hostname missing in conf file");
			}

			if (configJson.containsKey("db.username") && !configJson.getString("db.username").isEmpty()
					&& configJson.getString("db.username") != null) {
				dbUsername = configJson.getString("db.username");
			} else {
				throw new NullPointerException("db.username missing in conf file");
			}

			if (configJson.containsKey("db.password") && !configJson.getString("db.password").isEmpty()
					&& configJson.getString("db.password") != null) {
				dbPassword = configJson.getString("db.password");
			} else {
				throw new NullPointerException("db.password missing in conf file");
			}

			if (configJson.containsKey("db.name") && !configJson.getString("db.name").isEmpty()
					&& configJson.getString("db.name") != null) {
				dbName = configJson.getString("db.name");
			} else {
				throw new NullPointerException("db.name missing in conf file");
			}

			if (configJson.containsKey("db.port") && configJson.getInteger("db.port") != null) {
				dbPort = configJson.getInteger("db.port");
			} else {
				throw new NullPointerException("db.port missing in conf file");
			}

			System.out.println("------------------------------------------------------");
			jdbcURL = "jdbc:" + dbType + "://" + dbHostname + ":" + dbPort + "/" + dbName;
			System.out.println("JDBC Connection URL: " + jdbcURL);

			System.out.println("-------------------------------------rrrr-----------------");
			connection = DriverManager.getConnection(jdbcURL, dbUsername, dbPassword);
			System.out.println("Database connection established..");

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		} finally {

			/*
			 * if (connection != null) { connection.close(); }
			 */
		}
		return connection;

	}

	public JsonArray getBranchesInCity(Connection connection, String cityName, String limit, String offset)
			throws SQLException, Exception {
		Statement statement;
		JsonObject selectRowData = null;
		JsonArray selectAllRowData = new JsonArray();

		statement = connection.createStatement();
		String query = "select COUNT(*) OVER (), * from branches where city='" + cityName + "' order by ifsc offset " + offset + " limit "
				+ limit;
		System.out.println("----------------------" + query);
		ResultSet resultSet = statement.executeQuery(query);
		ResultSetMetaData metaData = resultSet.getMetaData();

		while (resultSet.next()) {
			selectRowData = new JsonObject();
			for (int i = 1; i <= metaData.getColumnCount(); i++) {
				selectRowData.put(metaData.getColumnName(i), resultSet.getString(i));
			}
			selectAllRowData.add(selectRowData);
			
		}

		return selectAllRowData;

	}
	
	public JsonArray searchAllPossibleMatches(Connection connection, String cityName, String limit, String offset)
			throws SQLException, Exception {
		Statement statement;
		JsonObject selectRowData = null;
		JsonArray selectAllRowData = new JsonArray();

		statement = connection.createStatement();
		String likePsqlString = "'%" + cityName + "%'";
		String query = "SELECT COUNT(*) OVER (), * FROM branches ib WHERE ib.ifsc like " + likePsqlString + 
						" or ib.branch like " + likePsqlString + 
						" or ib.address like " + likePsqlString +
						" or ib.district like " + likePsqlString + 
						" or ib.city like " + likePsqlString + 
						" or ib.state like " + likePsqlString +
						" order by ifsc limit " + limit +
						" offset " + offset;
		
		System.out.println("----------------------" + query);
		ResultSet resultSet = statement.executeQuery(query);
		ResultSetMetaData metaData = resultSet.getMetaData();

		while (resultSet.next()) {
			selectRowData = new JsonObject();
			for (int i = 1; i <= metaData.getColumnCount(); i++) {
				System.out.println("uuuuu" + metaData.getColumnName(i) + " : " +  resultSet.getString(i));
				selectRowData.put(metaData.getColumnName(i), resultSet.getString(i));
			}
			selectAllRowData.add(selectRowData);
		}

		return selectAllRowData;

	}
}
