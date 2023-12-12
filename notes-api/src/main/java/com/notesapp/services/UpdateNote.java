package com.notesapp.services;

import java.util.HashMap;
import java.util.Map;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.notesapp.db.DatabaseClient;
import com.notesapp.utils.Utils;

public class UpdateNote {
	
	private final ObjectMapper objectMapper = new ObjectMapper();
	private DatabaseClient databaseClient;

	public UpdateNote() {
		this.databaseClient = new DatabaseClient();
	}

	public APIGatewayProxyResponseEvent updateNote(APIGatewayProxyRequestEvent request) {

		APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();
		try {
			Map<String, Object> body = objectMapper.readValue(request.getBody(), HashMap.class);
			Map<String, String> headers = request.getHeaders();
			String userId = headers.get("app_user_id");
			String userName = headers.get("app_user_name");
			Map<String, Object> responseMap = databaseClient.updateNote(body, userId, userName); 
			response = Utils.getResponse(responseMap, 200);
		} catch (Exception e) {
			Map<String, String> err = new HashMap<>();
			err.put("error", e.getClass().toString());
			err.put("message", e.getMessage());
			response = Utils.getResponse(err, 500);
		}
		return response; 
	}

}
