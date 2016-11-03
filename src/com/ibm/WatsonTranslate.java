package com.ibm;

import java.io.IOException;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.ibm.json.java.JSONArray;
import com.ibm.json.java.JSONObject;


import com.ibm.watson.developer_cloud.language_translator.v2.LanguageTranslator;
import com.ibm.watson.developer_cloud.language_translator.v2.model.Language;
import com.ibm.watson.developer_cloud.language_translator.v2.model.Translation;
import com.ibm.watson.developer_cloud.language_translator.v2.model.TranslationResult;
import com.ibm.watson.developer_cloud.natural_language_classifier.v1.NaturalLanguageClassifier;
import com.ibm.watson.developer_cloud.natural_language_classifier.v1.model.Classification;

public class WatsonTranslate {
	private static Logger logger = Logger.getLogger(WatsonTranslate.class.getName());

	private static String translationService = "language_translator";
	private static String languageIDService = "natural_language_classifier";

	// If running locally add the information from VCAP_SERVICES
	private static String baseURLTranslation = "put url here";
	private static String usernameTranslation = "put username here";
	private static String passwordTranslation = "put password here";

	private static String baseURLIDLanguage = "put url here";
	private static String usernameIDLanguage = "put username here";
	private static String passwordIDLanguage = "put password here";

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

				logger.info("Looking for: "+ languageIDService);

				if (sysEnv.containsKey(languageIDService)) {
					JSONArray services = (JSONArray)sysEnv.get(languageIDService);
					JSONObject service = (JSONObject)services.get(0);
					JSONObject credentials = (JSONObject)service.get("credentials");
					baseURLIDLanguage = (String)credentials.get("url");
					usernameIDLanguage = (String)credentials.get("username");
					passwordIDLanguage = (String)credentials.get("password");
					logger.info("baseURL  = "+baseURLIDLanguage);
					logger.info("username   = "+usernameIDLanguage);
					logger.info("password = "+passwordIDLanguage);
				} else {
					logger.warning(languageIDService + " is not available in VCAP_SERVICES, "
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
			Language sourceLang = Language.ENGLISH;
			Language targetLang = Language.ENGLISH;

			LanguageTranslator service = new LanguageTranslator();
			service.setUsernameAndPassword(usernameTranslation, passwordTranslation);

			if(sid.equals("mt-arar-enus")) {
				sourceLang = Language.ARABIC;
				targetLang = Language.ENGLISH;
			}
			else if(sid.equals("mt-ptbr-enus")) {
				sourceLang = Language.PORTUGUESE;
				targetLang = Language.ENGLISH;
			}
			else if(sid.equals("mt-enus-ptbr")) {
				sourceLang = Language.ENGLISH;
				targetLang = Language.PORTUGUESE;
			}
			else if(sid.equals("mt-enus-frfr")) {
				sourceLang = Language.ENGLISH;
				targetLang = Language.FRENCH;
			}
			else if(sid.equals("mt-enus-eses")) {
				sourceLang = Language.ENGLISH;
				targetLang = Language.SPANISH;
			}
			else if(sid.equals("mt-frfr-enus")) {
				sourceLang = Language.FRENCH;
				targetLang = Language.ENGLISH;
			}
			else if(sid.equals("mt-eses-enus")) {
				sourceLang = Language.SPANISH;
				targetLang = Language.ENGLISH;
			}

			TranslationResult translationResult = service.translate(text, sourceLang, targetLang).execute();

			Iterator<Translation> itr = translationResult.getTranslations().iterator();
			while(itr.hasNext()) {
				tweetTranslation = itr.next().getTranslation();
			}

			return tweetTranslation;
		}

		public String identify(String text) {
				String language = "";

				NaturalLanguageClassifier service = new NaturalLanguageClassifier();
				service.setUsernameAndPassword(usernameIDLanguage, passwordIDLanguage);

				Classification classification = service.classify("<classifier-id>", text).execute();

				language = classification.getId();

				return language;
			}

}
