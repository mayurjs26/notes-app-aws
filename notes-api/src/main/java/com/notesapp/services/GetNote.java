package com.notesapp.services;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.QueryOutcome;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;

public class GetNote {

	public APIGatewayProxyResponseEvent getNote(APIGatewayProxyRequestEvent request) {
		
		APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();
		AmazonDynamoDB client = null;
		DynamoDB dynamoDB = null; 
		try {
			client = AmazonDynamoDBClientBuilder.standard().build();
			dynamoDB = new DynamoDB(client);
			String tableName = "notes-dev";
			String noteId = request.getQueryStringParameters().get("noteId");
			QuerySpec querySpec = new QuerySpec().withKeyConditionExpression("noteId = :noteId").
				withValueMap(new ValueMap().withString(":noteId", noteId));
			ItemCollection<QueryOutcome> items = dynamoDB.getTable(tableName).getIndex("noteIdIndex").query(querySpec);
			Iterator<Item> iterator = items.iterator();
			if (iterator.hasNext()) {
				// Item found, process it
				Item item = iterator.next();
				System.out.println("Item found: " + item.asMap());
				response = response.withStatusCode(200).withBody(item.toJSON()); 
			} else {
				 // Item not found
				 System.out.println("Item not found");
				 response = response.withStatusCode(404);
			}
						
		} catch (Exception e) {
			e.printStackTrace();
			Map<String, String> err = new HashMap<>();
			err.put("error", e.getClass().toString());
			err.put("message", e.getMessage());
			response = response.withStatusCode(500).withBody("error : " + e.getClass().toString());
		} finally {
			dynamoDB.shutdown();
		}

		return response;


	}
}
