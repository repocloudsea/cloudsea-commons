package com.cloudsea.services.db;

import java.net.UnknownHostException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Primary;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
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
public class MutiTenantMongoConfiguration {

	@Bean
	public HandlerInterceptor getRequestInterceptorForDatabase() {
		return new HandlerInterceptor() {

			@Override
			public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
					throws Exception {

				return StringUtils.isBlank(request.getHeader("org_id")) ? true
						: addToRequestContextAndReturnTrue(request.getHeader("org_id"));
			}

			private boolean addToRequestContextAndReturnTrue(String orgId) {
				RequestContextHolder.getRequestAttributes()
						.setAttribute("org_id", orgId, RequestAttributes.SCOPE_REQUEST);
				return true;
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

		Builder mongoOptionBuilder = MongoClientOptions.builder()//
				.connectionsPerHost(5)//
				.connectTimeout(2000);//

		MongoClientURI mongoClientURI = new MongoClientURI("mongodb://localhost:27017/demo", mongoOptionBuilder);

		return new MultiTenantMongoDbFactory(mongoClientURI);
	}

	@Bean
	@Primary
	public MongoTemplate mongoTemplate() throws UnknownHostException {
		return new MongoTemplate(mongoDbFactory());
	}

}
