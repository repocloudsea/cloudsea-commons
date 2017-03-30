package com.cloudsea.services;

/**
 * The context holder will hold the
 * company name throughout the request
 * life. At any point of the request
 * the company name can be fetched
 * by the ThreadLocal. It can be used for 
 * connecting to datastore, logging etc.
 * 
 * @author shahbaz03
 *
 */
public class CompanyContextHolder {
	private static final ThreadLocal<String> context = new ThreadLocal<String>();

	public static String getCompany(){
		return context.get();
	}
	
	public static void setCompany(String company){
		context.set(company);
	}
	
	public static void removeCompany(){
		context.remove();
	}
	
}
