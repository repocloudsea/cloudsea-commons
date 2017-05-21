package com.cloudsea.commons.config;

import java.net.UnknownHostException;
import java.util.Arrays;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Primary;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.MappedInterceptor;

import com.mongodb.MongoClientOptions;
import com.mongodb.MongoClientOptions.Builder;
import com.mongodb.MongoClientURI;

/**
 * A configuration class to create MongoClient
 * 
 * 
 * @author shahbaz03
 *
 */

@Configuration
@Lazy
public class CloudSeaMongoConfiguration {

	@Value("${MONGO_HOST:127.0.0.1}")
	String mongoHost;

	@Value("${MONGO_PORT:27017}")
	String mongoPort;

	@Value("${MONGO_DATABASE_NAME:demo}")
	String mongoDatabase;

	@Bean
	public RestTemplate getRestTemplate() {
		return new RestTemplate();
	}

	@Bean
	public HandlerInterceptor getRequestInterceptorForDatabase() {
		return new HandlerInterceptor() {

			@Override
			public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
					throws Exception {

				if (StringUtils.isBlank(request.getHeader("org_id")))
					throw new Exception("No organization id forund in header");

				if (!isOrgarnizationExists((request.getHeader("org_id"))))
					throw new Exception("No organization exists");

				return addToRequestContextAndReturnTrue(request.getHeader("org_id"));

			}

			@Override
			public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
					ModelAndView modelAndView) throws Exception {
			}

			@Override
			public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,
					Exception ex) throws Exception {
			}
		};
	}

	@Bean
	public MappedInterceptor myMappedInterceptor() {
		return new MappedInterceptor(new String[] { "/cloudsea/**" }, getRequestInterceptorForDatabase());
	}

	@Bean
	@Primary
	public MongoDbFactory mongoDbFactory() throws UnknownHostException {

		Builder mongoOptionBuilder = MongoClientOptions.builder()
				.connectionsPerHost(5)
				.connectTimeout(2000);

		MongoClientURI mongoClientURI = new MongoClientURI(
				"mongodb://" + mongoHost + ":" + mongoPort + "/" + mongoDatabase, mongoOptionBuilder);

		return new CloudSeaMongoDbFactory(mongoClientURI);
	}

	@Bean
	@Primary
	public MongoTemplate mongoTemplate() throws UnknownHostException {
		return new MongoTemplate(mongoDbFactory());
	}

	private boolean addToRequestContextAndReturnTrue(String orgId) {
		RequestContextHolder.getRequestAttributes()
				.setAttribute("org_id", orgId, RequestAttributes.SCOPE_REQUEST);
		return true;
	}

	private boolean isOrgarnizationExists(String orgId) {

		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		HttpEntity<String> entity = new HttpEntity<String>("parameters", headers);
		ResponseEntity<String> result = getRestTemplate().exchange("http://localhost:8082/findById/" + orgId,
				HttpMethod.GET, entity, String.class);
		return result.getStatusCode() != HttpStatus.NO_CONTENT;
	}

}
