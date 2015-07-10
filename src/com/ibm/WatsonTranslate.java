package com.ibm;

import java.io.IOException;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.ibm.json.java.JSONArray;
import com.ibm.json.java.JSONObject;


import com.ibm.watson.developer_cloud.language_translation.v2.LanguageTranslation;
import com.ibm.watson.developer_cloud.language_translation.v2.model.Translation;
import com.ibm.watson.developer_cloud.language_translation.v2.model.TranslationResult;
import com.ibm.watson.developer_cloud.language_identification.v1.LanguageIdentification;
import com.ibm.watson.developer_cloud.language_identification.v1.model.IdentifiedLanguage;

public class WatsonTranslate {
	private static Logger logger = Logger.getLogger(WatsonTranslate.class.getName());

	private static String translationService = "language_translation";
	private static String languageService = "language_translation";

	// If running locally add the information from VCAP_SERVICES
	private static String baseURLTranslation = "put url here";
	private static String usernameTranslation = "put username here";
	private static String passwordTranslation = "put password here";

	private static String baseURLLanguage = "put url here";
	private static String usernameLanguage = "put username here";
	private static String passwordLanguage = "put password here";

	static {
		processVCAP_Services();
	}

	private static void processVCAP_Services() {
    	logger.info("Processing VCAP_SERVICES");

				JSONObject sysEnv = getVcapServices();
        if (sysEnv == null) {
					return;
				}

        if (sysEnv.containsKey(translationService)) {
					JSONArray services = (JSONArray)sysEnv.get(translationService);
					JSONObject service = (JSONObject)services.get(0);
					JSONObject credentials = (JSONObject)service.get("credentials");
					baseURLTranslation = (String)credentials.get("url");
					usernameTranslation = (String)credentials.get("username");
					passwordTranslation = (String)credentials.get("password");
					logger.info("baseURL  = "+baseURLTranslation);
					logger.info("username   = "+usernameTranslation);
					logger.info("password = "+passwordTranslation);
    		}

				logger.info("Looking for: "+ languageService);

				if (sysEnv.containsKey(languageService)) {
					JSONArray services = (JSONArray)sysEnv.get(languageService);
					JSONObject service = (JSONObject)services.get(0);
					JSONObject credentials = (JSONObject)service.get("credentials");
					baseURLLanguage = (String)credentials.get("url");
					usernameLanguage = (String)credentials.get("username");
					passwordLanguage = (String)credentials.get("password");
					logger.info("baseURL  = "+baseURLLanguage);
					logger.info("username   = "+usernameLanguage);
					logger.info("password = "+passwordLanguage);
				} else {
					logger.warning(languageService + " is not available in VCAP_SERVICES, "
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
			String tweetTranslation = "";
			String sourceLang = "en";
			String targetLang = "en";

			LanguageTranslation service = new LanguageTranslation();
			service.setUsernameAndPassword(usernameLanguage, passwordLanguage);

			if(sid.equals("mt-arar-enus")) {
				sourceLang = "ar";
				targetLang = "en";
			}
			else if(sid.equals("mt-ptbr-enus")) {
				sourceLang = "pt";
				targetLang = "en";
			}
			else if(sid.equals("mt-enus-ptbr")) {
				sourceLang = "en";
				targetLang = "pt";
			}
			else if(sid.equals("mt-enus-frfr")) {
				sourceLang = "en";
				targetLang = "fr";
			}
			else if(sid.equals("mt-enus-eses")) {
				sourceLang = "en";
				targetLang = "es";
			}
			else if(sid.equals("mt-frfr-enus")) {
				sourceLang = "fr";
				targetLang = "en";
			}
			else if(sid.equals("mt-eses-enus")) {
				sourceLang = "es";
				targetLang = "en";
			}

			TranslationResult translationResult = service.translate(text, sourceLang, targetLang);

			Iterator<Translation> itr = translationResult.getTranslations().iterator();
			while(itr.hasNext()) {
				tweetTranslation = itr.next().getTranslation();
			}

			return tweetTranslation;
		}

		public String identify(String text) {
				String language = "";

				LanguageIdentification service = new LanguageIdentification();
				service.setUsernameAndPassword(usernameLanguage, passwordLanguage);

				IdentifiedLanguage lang = service.identify(text);
				language = lang.getId();

				return language;
			}

}
