package com.indian.banks.utility;

public final class STATIC {

	public final static class HTTP {

		public final static class RESPONSE {
			public final static String EXIT_CODE = "exit_code";
			public final static String STATUS = "status";

			public final static class HEADERS {
				public final static String ACCESS_CONTROL_ALLOW_HEADERS = "Access-Control-Allow-Headers";
				public final static String ACCESS_CONTROL_ALLOW_METHODS = "Access-Control-Allow-Methods";
				public final static String ACCESS_CONTROL_ALLOW_ORIGIN = "Access-Control-Allow-Origin";

			}
		}

		public final static class API {
			public final static String SEARCH = "/api/branches/autocomplete";
			public final static String GET_BRANCHES = "/api/branches";
		}

	}

}
