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
		
		
		final DBManager dbManager = new DBManager();
		
		final JsonObject configJson = config();
		
		Router router = Router.router(vertx);
		
		HTTPBankServer httpBankServer = new HTTPBankServer();

		httpBankServer.createHTTPServer(vertx, configJson, router);

		System.out.println("going to establish jdbc connection..");
		connection = dbManager.establishDBConnection(configJson);

		httpBankServer.getBranchesInCity(connection, router);
		httpBankServer.searchPossibleMatches(connection, router);
		
	}

	/**
	 * 
	 */
	@Override
	public void stop() throws Exception {
		// TODO Auto-generated method stub
		super.stop();

		System.out.println("stopping vertx..");
		vertx.close();
	}

}
