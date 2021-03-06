package com.cloudsea.commons.config;

import java.net.UnknownHostException;

import org.apache.commons.lang3.StringUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import com.mongodb.DB;
import com.mongodb.MongoClientURI;

public class CloudSeaMongoDbFactory extends SimpleMongoDbFactory {

	private String database;

	public CloudSeaMongoDbFactory(MongoClientURI uri) throws UnknownHostException {
		super(uri);
		database = uri.getDatabase();
	}

	@Override
	public DB getDb() throws DataAccessException {
		Object orgId = RequestContextHolder.getRequestAttributes()
				.getAttribute("org_id", RequestAttributes.SCOPE_REQUEST);

		if (orgId instanceof String && StringUtils.isNotBlank(orgId.toString()))
			return getDb(orgId.toString());

		return super.getDb(database);
	}

}
