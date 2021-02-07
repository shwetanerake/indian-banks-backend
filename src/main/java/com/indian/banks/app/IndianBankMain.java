package com.indian.banks.app;

import java.sql.SQLException;

import io.vertx.core.Vertx;

public class IndianBankMain {
	
	public static void main(String[] args) throws SQLException {
		System.out.println("In main....");
		Vertx vertx = Vertx.vertx();
		vertx.deployVerticle("com.indian.banks.app.IndianBanksMainVerticle");
		
		
	}
}
/**extends AbstractVerticle {

	@Override
	public void start() throws Exception {
		// TODO Auto-generated method stub
		super.start();

		Router router = Router.router(vertx);

		//ConfigRetriever retriever = ConfigRetriever.create(vertx);

		System.out.println("ddddddddd" + config().getString("host"));
		
		router.route(HttpMethod.GET, "/api/branches").handler(context -> {

			String address = context.request().connection().remoteAddress().toString();

			MultiMap queryParams = context.queryParams();

			String name = queryParams.contains("branch-name") ? queryParams.get("branch-name") : "unknown";

			if (name.equalsIgnoreCase("unknown")) {

			} else {
				Iterator<Entry<String, String>> itr = queryParams.iterator();

				while (itr.hasNext()) {
					Entry<String, String> entry = itr.next();
					System.out.println("----" + entry.getKey());
					System.out.println("-----0000" + entry.getValue());
				}
			}

			// Write a json response
			context.json(new JsonObject().put("name", name).put("address", address).put("message",
					"Hello " + name + " connected from " + address));
		});

		// Create the HTTP server
		vertx.createHttpServer()
		// Handle every request using the router
		.requestHandler(router)
		// Start listening
		.listen(8082)
		// Print the port
		.onSuccess(server -> System.out.println("HTTP server started on port " + server.actualPort()));
	}
}**/
