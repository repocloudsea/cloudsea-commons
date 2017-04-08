package com.cloudsea.services.db;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;

/**
 * A configuration class to create MongoClient
 * 
 * Move the Mongo URI to read from properties file.
 * 
 * @author shahbaz03
 *
 */

@Configuration
public class MongoConfiguration {

	@Bean
	public MongoClient getMongoClient() {
		MongoClientOptions mongoOptions = new MongoClientOptions.Builder().connectionsPerHost(8)
				.build();
		MongoClient client = new MongoClient("localhost:27017", mongoOptions);
		return client;
	}

}
