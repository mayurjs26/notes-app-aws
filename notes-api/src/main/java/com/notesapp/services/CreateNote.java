package com.notesapp.services;

import java.util.HashMap;
import java.util.Map;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.notesapp.db.DatabaseClient;
import com.notesapp.utils.Utils;
/**
 * CreateOrder
 */
public class CreateNote {
	
	private final ObjectMapper objectMapper;
	private DatabaseClient databaseClient;

	public CreateNote() {
		this.databaseClient = new DatabaseClient();
		this.objectMapper = new ObjectMapper();
	}

	public APIGatewayProxyResponseEvent createNote(APIGatewayProxyRequestEvent request) {
	
		APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();
		try {
			Map<String, Object> body = objectMapper.readValue(request.getBody(), HashMap.class);
			Map<String, String> headers = request.getHeaders();
			String userId = headers.get("app_user_id");
			String userName = headers.get("app_user_name");
			Map<String, Object> responseMap = databaseClient.createNote(body, userId, userName); 
			response = Utils.getResponse(responseMap, 200);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			Map<String, Object> err = new HashMap<>();
			err.put("error", e.getClass().toString());
			err.put("message", e.getMessage());
			response = Utils.getResponse(err, 500);
		}
		return response;
	}
	
}
