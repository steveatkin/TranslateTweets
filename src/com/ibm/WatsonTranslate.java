package com.ibm;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.ibm.json.java.JSONArray;
import com.ibm.json.java.JSONObject;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.NameValuePair;
import org.apache.http.client.fluent.Executor;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.ContentType;
import org.apache.http.message.BasicNameValuePair;

public class WatsonTranslate {
	private static Logger logger = Logger.getLogger(WatsonTranslate.class.getName());

	private static String serviceName = "machine_translation";
	
	// If running locally complete the variables below with the information in VCAP_SERVICES
	private static String baseURL = "https://gateway.watsonplatform.net/laser/service/api/v1/smt/9c7376c0-24c7-4cf6-9194-baad1c2d2061";
	private static String username = "6e9ade17-a5c9-4ecc-98cb-18e51656feda";
	private static String password = "1bUlwNuOdUBB";
	
	static {
		processVCAP_Services();
	}
	
	private static void processVCAP_Services() {
    	logger.info("Processing VCAP_SERVICES");
        JSONObject sysEnv = getVcapServices();
        if (sysEnv == null) return;
        logger.info("Looking for: "+ serviceName );
        
        if (sysEnv.containsKey(serviceName)) {
			JSONArray services = (JSONArray)sysEnv.get(serviceName);
			JSONObject service = (JSONObject)services.get(0);
			JSONObject credentials = (JSONObject)service.get("credentials");
			baseURL = (String)credentials.get("url");
			username = (String)credentials.get("username");
			password = (String)credentials.get("password");
			logger.info("baseURL  = "+baseURL);
			logger.info("username   = "+username);
			logger.info("password = "+password);
    	} else {
        	logger.warning(serviceName + " is not available in VCAP_SERVICES, "
        			+ "please bind the service to your application");
        }
    }
	
	private static JSONObject getVcapServices() {
        String envServices = System.getenv("VCAP_SERVICES");
        if (envServices == null) return null;
        JSONObject sysEnv = null;
        try {
        	 sysEnv = JSONObject.parse(envServices);
        } catch (IOException e) {
        	// Do nothing, fall through to defaults
        	logger.log(Level.SEVERE, "Error parsing VCAP_SERVICES: "+e.getMessage(), e);
        }
        return sysEnv;
    }
	
	public String translate(String text, String sid) {
		String response = "";
		List<NameValuePair> qparams = new ArrayList<NameValuePair>();
		qparams.add(new BasicNameValuePair("txt",text ));
		qparams.add(new BasicNameValuePair("sid",sid ));
		qparams.add(new BasicNameValuePair("rt","text" ));
		
		try {
			Executor executor = Executor.newInstance();
    		URI serviceURI = new URI(baseURL).normalize();
    	    String auth = username + ":" + password;
    	    byte[] responseB = executor.execute(Request.Post(serviceURI)
			    .addHeader("Authorization", "Basic "+ Base64.encodeBase64String(auth.getBytes()))
			    .bodyString(URLEncodedUtils.format(qparams, "utf-8"), 
			    		ContentType.APPLICATION_FORM_URLENCODED)
			    ).returnContent().asBytes();

    	    response = new String (responseB,"UTF-8");
		}
		catch(Exception e) {
			logger.log(Level.SEVERE, "got error: "+e.getMessage(), e);
		}
		
		return response;
	}
}
