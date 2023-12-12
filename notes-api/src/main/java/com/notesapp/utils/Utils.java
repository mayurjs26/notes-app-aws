package com.notesapp.utils;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;
/**
 * Utils
 */
public class Utils {


	public static APIGatewayProxyResponseEvent getResponse(Object responseMap, int statusCode) {

		APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();
		ObjectMapper objectMapper = new ObjectMapper();
		Map<String, String> headers = new HashMap<>();
		headers.put("Access-Control-Allow-Origin", "*");
		System.out.println(responseMap);
		headers.put("Access-Control-Allow-Credentials", String.valueOf(true));
		try {
			response = response.withStatusCode(statusCode).withBody(objectMapper.writeValueAsString(responseMap));
		} catch (Exception e) {

			Map<String, String> err = new HashMap<>();
			err.put("error", e.getClass().toString());
			err.put("message", e.getMessage());
			try {
				response = response.withStatusCode(500).withBody(objectMapper.writeValueAsString(err));
			} catch (Exception e1) {
				response = response.withStatusCode(500).withBody("error : " + "Json Exception" );
			}

		}

		return response.withHeaders(headers);

	}
}
