package com.notesapp.services;

import java.util.HashMap;
import java.util.Map;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.PrimaryKey;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.notesapp.db.DatabaseClient;
import com.notesapp.utils.Utils;

public class DeleteNote {

	private DatabaseClient databaseClient;	
	public DeleteNote() {
		this.databaseClient = new DatabaseClient();
	}
	public APIGatewayProxyResponseEvent deleteNote(APIGatewayProxyRequestEvent request) {

		APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();
		try {
			long timestamp = Long.parseLong(request.getPathParameters().get("timestamp"));
			Map<String, String> headers = request.getHeaders();
			String userId = headers.get("app_user_id");
			boolean success = databaseClient.deleteNote(userId, timestamp);
			response = Utils.getResponse(success, 200);
		
		} catch (Exception e) {
			e.printStackTrace();
			Map<String, String> err = new HashMap<>();
			err.put("error", e.getClass().toString());
			err.put("message", e.getMessage());
			response = Utils.getResponse(err, 500);

		}
		return response; 
	}

}
