package com.indian.banks.http;

import java.util.Iterator;
import java.util.Map.Entry;

import io.vertx.core.Handler;
import io.vertx.core.MultiMap;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

public class GetBranches implements Handler<RoutingContext>{

	@Override
	public void handle(RoutingContext routingContext) {
		// TODO Auto-generated method stub
		MultiMap queryParams = routingContext.queryParams();

		String branchName = queryParams.contains("q") ? queryParams.get("q") : "unknown";

		if (branchName.equalsIgnoreCase("unknown")) {

		} else {
			Iterator<Entry<String, String>> itr = queryParams.iterator();

			while (itr.hasNext()) {
				Entry<String, String> entry = itr.next();
				System.out.println("----" + entry.getKey());
				System.out.println("-----0000" + entry.getValue());
			}
		}

		// Write a json response
		routingContext.json(new JsonObject().put("name", branchName));
		
	}

}
