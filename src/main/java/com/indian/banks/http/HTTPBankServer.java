package com.indian.banks.http;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Map.Entry;

import com.indian.banks.db.DBManager;
import com.indian.banks.utility.STATIC;

import io.vertx.core.Handler;
import io.vertx.core.MultiMap;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

public class HTTPBankServer {

	private String hostname = null;
	private Integer port = null;
	final DBManager dbManager = new DBManager();

	public void createHTTPServer(Vertx vertx, JsonObject configJson, Router router) {

		if (configJson.containsKey("http.hostname") && !configJson.getString("http.hostname").isEmpty()
				&& configJson.getString("http.hostname") != null) {
			hostname = configJson.getString("http.hostname");
		} else
			throw new NullPointerException("http.hostname missing in conf file");

		if (configJson.containsKey("http.port") && configJson.getInteger("http.port") != null) {
			port = configJson.getInteger("http.port");
		} else
			throw new NullPointerException("http.port missing in conf file");

		// vertx.createHttpServer().requestHandler(router).listen(port, hostname);
		vertx.createHttpServer().requestHandler(router).listen(Integer.parseInt(System.getenv("PORT")), hostname);
		System.out.println("------------------------------------------------------");
		System.out.println("HTTP Server started on: " + hostname + ":" + System.getenv("PORT"));
		System.out.println("------------------------------------------------------");

	}

	/**
	 * 
	 * @param connection
	 * @param router
	 */
	public void getBranchesByCityName(Connection connection, Router router) {

		JsonObject responseJson = new JsonObject();
		responseJson.put(STATIC.HTTP.RESPONSE.EXIT_CODE, 0);
		responseJson.put(STATIC.HTTP.RESPONSE.STATUS, "success");

		router.route(HttpMethod.GET, "/api/branches").handler(new Handler<RoutingContext>() {

			@Override
			public void handle(RoutingContext routingContext) {
				// TODO Auto-generated method stub
				try {
					MultiMap queryParams = routingContext.queryParams();

					String offset = queryParams.contains("offset") ? queryParams.get("q") : "0";
					String limit = queryParams.contains("limit") ? queryParams.get("q") : "ALL";
					String branchName = queryParams.contains("q") ? queryParams.get("q") : "unknown";

					if (branchName.equalsIgnoreCase("unknown")) {

					} else {
						Iterator<Entry<String, String>> queryParamsIterator = queryParams.iterator();

						while (queryParamsIterator.hasNext()) {

							Entry<String, String> queryParamEntry = queryParamsIterator.next();
							System.out.println("/api/branches | http query params | key: " + queryParamEntry.getKey()
									+ " | value: " + queryParamEntry.getValue());
							if (queryParamEntry.getKey().equalsIgnoreCase("q")) {
								branchName = queryParamEntry.getValue();
							}
							if (queryParamEntry.getKey().equalsIgnoreCase("limit")) {
								limit = queryParamEntry.getValue();
							}
							if (queryParamEntry.getKey().equalsIgnoreCase("offset")) {
								offset = queryParamEntry.getValue();
							}
						}
						System.out.println(
								"/api/branches | offset:" + offset + " | limit:" + limit + " | q: " + branchName);
					}
					JsonArray listOfBranchesInCity = new DBManager().findBranchesInCity(connection, branchName, limit,
							offset);

					// Write a json response
					JsonObject responseJson = new JsonObject();
					responseJson.put("branches", listOfBranchesInCity);
					responseJson.put("count", 1250);

					routingContext.response().putHeader("Access-Control-Allow-Headers", "Content-Type");
					routingContext.response().putHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
					routingContext.response().putHeader("Access-Control-Allow-Origin", "*");
					routingContext.response().send(responseJson.toString());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		});
	}

	/**
	 * 
	 * @param connection
	 * @param router
	 */
	public void searchPossibleMatches(Connection connection, Router router) {

		JsonObject responseJson = new JsonObject();
		responseJson.put(STATIC.HTTP.RESPONSE.EXIT_CODE, 0);
		responseJson.put(STATIC.HTTP.RESPONSE.STATUS, "success");

		router.route(HttpMethod.GET, STATIC.HTTP.API.SEARCH).handler(new Handler<RoutingContext>() {

			@Override
			public void handle(RoutingContext routingContext) {
				// TODO Auto-generated method stub
				try {
					MultiMap queryParams = routingContext.queryParams();

					String offset = queryParams.contains("offset") ? queryParams.get("q") : "0";
					String limit = queryParams.contains("limit") ? queryParams.get("q") : "ALL";
					String searchString = queryParams.contains("q") ? queryParams.get("q") : "";
					String cityName = queryParams.contains("city-name") ? queryParams.get("city-name") : "unknown";

					if (cityName.equalsIgnoreCase("unknown")) {
						routingContext.response().putHeader(STATIC.HTTP.RESPONSE.HEADERS.ACCESS_CONTROL_ALLOW_HEADERS,
								"Content-Type");
						routingContext.response().putHeader(STATIC.HTTP.RESPONSE.HEADERS.ACCESS_CONTROL_ALLOW_METHODS,
								"GET, POST, OPTIONS");
						routingContext.response().putHeader(STATIC.HTTP.RESPONSE.HEADERS.ACCESS_CONTROL_ALLOW_ORIGIN,
								"*");
						responseJson.put(STATIC.HTTP.RESPONSE.EXIT_CODE, -2);
						responseJson.put(STATIC.HTTP.RESPONSE.STATUS, "unknown city");
						responseJson.put("result", new JsonArray());
						routingContext.response().send(responseJson.toString());
					} else {

						Iterator<Entry<String, String>> queryParamIterator = queryParams.iterator();

						while (queryParamIterator.hasNext()) {

							Entry<String, String> queryParamEntry = queryParamIterator.next();
							System.out.println(STATIC.HTTP.API.SEARCH + "| http query params | key: "
									+ queryParamEntry.getKey() + " | value: " + queryParamEntry.getValue());

							if (queryParamEntry.getKey().equalsIgnoreCase("q")) {
								searchString = queryParamEntry.getValue();
							}
							if (queryParamEntry.getKey().equalsIgnoreCase("limit")) {
								limit = queryParamEntry.getValue();
							}
							if (queryParamEntry.getKey().equalsIgnoreCase("offset")) {
								offset = queryParamEntry.getValue();
							}
							if (queryParamEntry.getKey().equalsIgnoreCase("city-name")) {
								cityName = queryParamEntry.getValue();
							}
						}
						System.out.println(STATIC.HTTP.API.SEARCH + " | city-name:" + cityName + " | offset:" + offset
								+ " | limit:" + limit + " | q: " + searchString);

						JsonArray dbResultArray = dbManager.searchAllRowsAndColumns(connection, cityName, searchString,
								limit, offset);

						responseJson.put("result", dbResultArray);
					}

				} catch (SQLException sqlException) {
					// TODO: handle exception
					sqlException.printStackTrace();
					responseJson.put(STATIC.HTTP.RESPONSE.EXIT_CODE, -1);
					responseJson.put(STATIC.HTTP.RESPONSE.STATUS, sqlException.getLocalizedMessage());
				} catch (Exception exception) {
					// TODO Auto-generated catch block
					exception.printStackTrace();
					responseJson.put(STATIC.HTTP.RESPONSE.EXIT_CODE, -1);
					responseJson.put(STATIC.HTTP.RESPONSE.STATUS, exception.getLocalizedMessage());
				}
				routingContext.response().putHeader(STATIC.HTTP.RESPONSE.HEADERS.ACCESS_CONTROL_ALLOW_HEADERS,
						"Content-Type");
				routingContext.response().putHeader(STATIC.HTTP.RESPONSE.HEADERS.ACCESS_CONTROL_ALLOW_METHODS,
						"GET, POST, OPTIONS");
				routingContext.response().putHeader(STATIC.HTTP.RESPONSE.HEADERS.ACCESS_CONTROL_ALLOW_ORIGIN, "*");
				routingContext.response().send(responseJson.toString());
			}

		});

	}

}
