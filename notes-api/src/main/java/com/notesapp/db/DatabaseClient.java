package com.notesapp.db;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.PrimaryKey;
import com.amazonaws.services.dynamodbv2.document.QueryOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.PutItemSpec;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
/**
 * DatabaseClient
 */
public class DatabaseClient {

	private static final String NOTES_TABLE_NAME = "notes-dev";
	private AmazonDynamoDB client = null;
	private DynamoDB dynamoDB = null;

	public DatabaseClient() {
		this.client = AmazonDynamoDBClientBuilder.standard().build();
		this.dynamoDB = new DynamoDB(client);
	}

	public List<Map<String, Object>> getNotes (Integer limit, String userId, Long startTimeStamp) {
		
		List<Map<String, Object>> responseItems = new ArrayList<>();

		Table table = dynamoDB.getTable(NOTES_TABLE_NAME);

		QuerySpec querySpec = new QuerySpec()
			.withKeyConditionExpression("user_id = :uid")
			.withValueMap(new HashMap<String, Object>() {{
					put(":uid", userId);
					}})
			.withScanIndexForward(false)
			.withMaxResultSize(limit);


		if (startTimeStamp > 0) 
			 querySpec.withExclusiveStartKey(new PrimaryKey("user_id", userId, "timestamp", startTimeStamp));

		 ItemCollection<QueryOutcome> items = table.query(querySpec);

		 items.forEach(item -> {
			responseItems.add(item.asMap());
			System.out.println(item.toString());
		 });

		return responseItems;
	}

	public Map<String, Object> createNote (Map<String, Object> body, String userId, String userName)  {

		Map<String, Object> itemAttributes = new HashMap<>();
		itemAttributes.put("user_id", userId);
		itemAttributes.put("user_name", userName);
		itemAttributes.put("note_id", itemAttributes.get("user_id") + ":" + UUID.randomUUID().toString());
		itemAttributes.put("timestamp", System.currentTimeMillis() / 1000L);
		itemAttributes.put("expires", System.currentTimeMillis() / 1000L + (90 * 24 * 60 * 60)); // 90 days in seconds
		Table notesTable = dynamoDB.getTable(NOTES_TABLE_NAME);
		Item item = new Item();
		itemAttributes.forEach(item::with);
		notesTable.putItem(item);
		System.out.println("Item : " + item.toString());

		return itemAttributes;
	}

	public Map<String, Object> updateNote(Map<String, Object> body, String userId, String userName) {
		
		Map<String, Object> itemAttributes = new HashMap<>();
		itemAttributes.put("user_id", userId);
		itemAttributes.put("user_name", userName);
		itemAttributes.put("expires", System.currentTimeMillis() / 1000L + (90 * 24 * 60 * 60)); // 90 days in seconds
		Table table =dynamoDB.getTable(NOTES_TABLE_NAME);
		Item item = new Item();
		itemAttributes.forEach(item::with);
		PutItemSpec putItemSpec = new PutItemSpec()
			.withConditionExpression("#t = :t")
			.withNameMap(new HashMap<String, String>() {{
				put("#t", "timestamp");
			}})
			.withValueMap(new ValueMap().with(":t", item.get("timestamp")))
			.withItem(item);

		table.putItem(putItemSpec);
		return itemAttributes;
	}

	public boolean deleteNote(String userId, long timeStamp) {

		Table table = dynamoDB.getTable(NOTES_TABLE_NAME);
		table.deleteItem(new PrimaryKey("user_id", userId, "timestamp", timeStamp));
		return true;
			



	}
}


	
