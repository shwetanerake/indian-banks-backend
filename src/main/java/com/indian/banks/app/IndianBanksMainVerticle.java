package com.indian.banks.app;

import java.sql.Connection;
import com.indian.banks.db.DBManager;
import com.indian.banks.http.HTTPBankServer;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;

public class IndianBanksMainVerticle extends AbstractVerticle {

	Connection connection = null;

	/**
	 * 
	 */
	@Override
	public void start() throws Exception {
		// TODO Auto-generated method stub
		super.start();

		final JsonObject configJson = config();
		System.out.println("Configuration json: " + configJson.encodePrettily());

		final Router router = Router.router(vertx);

		final HTTPBankServer httpBankServer = new HTTPBankServer();

		//http server creation on port and host as per given configuration
		httpBankServer.createHTTPServer(vertx, configJson, router);

		final DBManager dbManager = new DBManager();
		//db connection as per given configuration
		connection = dbManager.getConnection(configJson);

		httpBankServer.getBranchesByCityName(connection, router);
		
		httpBankServer.searchPossibleMatches(connection, router);
		
		

	}

	/**
	 * 
	 */
	@Override
	public void stop() throws Exception {
		// TODO Auto-generated method stub
		super.stop();
		if(connection!=null)
		{
			System.out.println("closing db connection...");
			connection.close();
		}
		System.out.println("stopping vertx..");
		vertx.close();
	}

}
