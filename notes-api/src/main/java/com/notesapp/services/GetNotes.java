package com.notesapp.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.notesapp.db.DatabaseClient;
import com.notesapp.utils.Utils;

/**
 */
public class GetNotes {

	private DatabaseClient databaseClient = null;
	public GetNotes() {
		this.databaseClient = new DatabaseClient();
	}

	public APIGatewayProxyResponseEvent getNotes(APIGatewayProxyRequestEvent request, Context context) {

		
		APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();
		LambdaLogger logger = context.getLogger();
				
		try {
			Map<String, String> queryParameters = request.getQueryStringParameters();
			int limit = queryParameters != null && queryParameters.containsKey("limit") ? 
				Integer.parseInt(queryParameters.get("limit")) : 5;
			Map<String, String> headers = request.getHeaders();
			String userId = headers.get("app_user_id");
			System.out.println("User Id : "  + userId);

			long startTimeStamp = queryParameters != null && queryParameters.containsKey("start") ?
						Long.parseLong(queryParameters.get("start")) : 0;
			System.out.println("Start : " + startTimeStamp);
			List<Map<String, Object>> responseItems = databaseClient.getNotes(limit, userId, startTimeStamp);
			Map<String, List<Map<String, Object>>> itemsMap = new HashMap<>();
			itemsMap.put("Items", responseItems);
			response = Utils.getResponse(itemsMap, 200);

		} catch(Exception e) {
			e.printStackTrace();
			Map<String, String> err = new HashMap<>();
			err.put("error", e.getClass().toString());
			err.put("message", e.getMessage());

			response = Utils.getResponse(err, 500);
		}
		return response;
	}
}
