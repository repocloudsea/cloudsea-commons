package com.cloudsea.services.db;

import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;

import com.mongodb.MongoClient;

/**
 * A factory class to create Morphia Datastore
 * This class must be extended by every dao class
 * 
 * @author shahbaz03
 *
 */
public class MorphiaDatastore {
	
	private Datastore ds;

	public Datastore getDs(MongoClient client, String db) {
		final Morphia morphia = new Morphia();
		// tell Morphia where to find your classes
    	// can be called multiple times with different packages or classes
    	morphia.mapPackage("com.cloudsea.services");

    	// create the Datastore connecting to the default port on the local host
    	ds = morphia.createDatastore(client, db);
    	ds.ensureIndexes();
    	return ds;
	}

}
