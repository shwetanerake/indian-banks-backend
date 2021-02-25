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

	/**
	 * 
	 * @param configJson
	 * @return
	 * @throws Exception
	 */
	public Connection getConnection(JsonObject configJson) throws Exception {
		String jdbcURL = null;
		Connection connection = null;

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
		System.out.println("establishing jdbc connection..");
		jdbcURL = "jdbc:" + dbType + "://" + dbHostname + ":" + dbPort + "/" + dbName;
		System.out.println("JDBC Connection URL: " + jdbcURL);
		System.out.println("------------------------------------------------------");

		connection = DriverManager.getConnection(jdbcURL, dbUsername, dbPassword);
		System.out.println("database connection established..");
		return connection;

	}

	/**
	 * 
	 * @param connection
	 * @param cityName
	 * @param limit
	 * @param offset
	 * @return
	 * @throws SQLException
	 * @throws Exception
	 */
	public JsonArray listBranchesByCityName(Connection connection, String cityName, String limit, String offset)
			throws SQLException, Exception {
		Statement statement;
		JsonObject resultSetJson = null;
		JsonArray resultSetArray = new JsonArray();

		statement = connection.createStatement();
		String query = "select COUNT(*) OVER (), * from bank_branches where city='" + cityName
				+ "' order by ifsc offset " + offset + " limit " + limit;
		System.out.println("findBranchesInCity | executing query: " + query);
		ResultSet resultSet = statement.executeQuery(query);
		ResultSetMetaData metaData = resultSet.getMetaData();

		while (resultSet.next()) {
			resultSetJson = new JsonObject();
			for (int i = 1; i <= metaData.getColumnCount(); i++) {
				/*
				 * System.out.println("findBranchesInCity | column name: " +
				 * metaData.getColumnName(i) + " | column data: " + resultSet.getString(i));
				 */
				resultSetJson.put(metaData.getColumnName(i), resultSet.getString(i));
			}
			resultSetArray.add(resultSetJson);

		}
		// System.out.println("findBranchesInCity | db result: " +
		// resultSetArray.encodePrettily());
		return resultSetArray;

	}

	/**
	 * 
	 * @param connection
	 * @param cityName
	 * @param searchString
	 * @param limit
	 * @param offset
	 * @return
	 * @throws SQLException
	 * @throws Exception
	 */
	public JsonArray searchAllRowsAndColumns(Connection connection, String cityName, String searchString, String limit,
			String offset) throws SQLException, Exception {
		Statement statement;
		JsonObject resultSetJson = null;
		JsonArray resultSetArray = new JsonArray();

		statement = connection.createStatement();

		if (searchString.isEmpty() || searchString == null) {
			resultSetArray = listBranchesByCityName(connection, cityName, limit, offset);
		} else {
			String likePsqlString = "'%" + searchString + "%'";
			String query = "SELECT COUNT(*) OVER (), * FROM bank_branches ib WHERE ( ib.ifsc ilike " + likePsqlString
					+ " or ib.branch ilike " + likePsqlString + " or ib.address ilike " + likePsqlString
					+ " or ib.district ilike " + likePsqlString + " or ib.city ilike " + likePsqlString
					+ " or ib.state ilike " + likePsqlString + " or ib.bank_name ilike " + likePsqlString
					+ " ) AND ib.city='" + cityName + "' order by ifsc limit " + limit + " offset " + offset;
			System.out.println("search all rows and columns | Executing query: " + query);
			ResultSet resultSet = statement.executeQuery(query);
			ResultSetMetaData metaData = resultSet.getMetaData();

			while (resultSet.next()) {
				resultSetJson = new JsonObject();
				for (int i = 1; i <= metaData.getColumnCount(); i++) {
					/*
					 * System.out.println("search all rows and columns | column name: " +
					 * metaData.getColumnName(i) + " | column data: " + resultSet.getString(i));
					 */
					resultSetJson.put(metaData.getColumnName(i), resultSet.getString(i));
				}
				resultSetArray.add(resultSetJson);
			}
		}
		// System.out.println("search all rows and columns | db result: " +
		// resultSetArray.encodePrettily());
		return resultSetArray;

	}

	/**
	 * 
	 * @param connection
	 * @param ifsc
	 * @return
	 * @throws SQLException
	 * @throws Exception
	 */
	public JsonObject findBranchByIfsc(Connection connection, String ifsc) throws SQLException, Exception {
		Statement statement;
		JsonObject resultSetJson = new JsonObject();
		
		statement = connection.createStatement();
		String query = "select * from bank_branches where ifsc='" + ifsc + "'";
		System.out.println("findBranchByIfsc | executing query: " + query);
		ResultSet resultSet = statement.executeQuery(query);
		ResultSetMetaData metaData = resultSet.getMetaData();

		while (resultSet.next()) {
			//resultSetJson = new JsonObject();
			for (int i = 1; i <= metaData.getColumnCount(); i++) {
				/*
				 * System.out.println("findBranchesInCity | column name: " +
				 * metaData.getColumnName(i) + " | column data: " + resultSet.getString(i));
				 */
				resultSetJson.put(metaData.getColumnName(i), resultSet.getString(i));
			}
		}
		System.out.println("findBranchByIfsc | db result: " + resultSetJson.encodePrettily());
		return resultSetJson;

	}

}
