package com.indian.banks.http;

import java.sql.Connection;
import java.util.Iterator;
import java.util.Map.Entry;

import com.indian.banks.db.DBManager;

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

		vertx.createHttpServer().requestHandler(router).listen(port, hostname);
		System.out.println("HTTP Server started on... " + port + ":" + hostname);

	}

	public void getBranchesInCity(Connection connection, Router router) {

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
						Iterator<Entry<String, String>> itr = queryParams.iterator();

						while (itr.hasNext()) {

							Entry<String, String> entry = itr.next();
							System.out.println("##############" + entry.getKey() + ":" + entry.getValue());
							if (entry.getKey().equalsIgnoreCase("q")) {
								branchName = entry.getValue();
							}
							if (entry.getKey().equalsIgnoreCase("limit")) {
								limit = entry.getValue();
							}
							if (entry.getKey().equalsIgnoreCase("offset")) {
								offset = entry.getValue();
							}
						}
						System.out.println("offset:" + offset + " | limit:" + limit + " | q: " + branchName);
					}
					JsonArray listOfBranchesInCity;

					listOfBranchesInCity = new DBManager().getBranchesInCity(connection, branchName, limit, offset);

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
	
	public void searchPossibleMatches(Connection connection, Router router) {

		router.route(HttpMethod.GET, "/api/branches/autocomplete").handler(new Handler<RoutingContext>() {

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
						Iterator<Entry<String, String>> itr = queryParams.iterator();

						while (itr.hasNext()) {

							Entry<String, String> entry = itr.next();
							System.out.println("##############" + entry.getKey() + ":" + entry.getValue());
							if (entry.getKey().equalsIgnoreCase("q")) {
								branchName = entry.getValue();
							}
							if (entry.getKey().equalsIgnoreCase("limit")) {
								limit = entry.getValue();
							}
							if (entry.getKey().equalsIgnoreCase("offset")) {
								offset = entry.getValue();
							}
						}
						System.out.println("offset:" + offset + " | limit:" + limit + " | q: " + branchName);
					}
					JsonArray listOfBranchesInCity;

					listOfBranchesInCity = new DBManager().searchAllPossibleMatches(connection, branchName, limit, offset);

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
	
}
