package com.ibm;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import twitter4j.*;

import javax.servlet.AsyncContext;

public class TwitterAsyncService implements Runnable{

	private AsyncContext ac;
	private WatsonTranslate wt = new WatsonTranslate();
	private static Logger logger = Logger.getLogger(TwitterAsyncService.class.getName());

	public TwitterAsyncService(AsyncContext context) {
		this.ac = context;
	}


	@Override
	  public void run() {
	    PrintWriter writer = null;
	    String searchTerm = ac.getRequest().getParameter("search");
	    String targetLang = ac.getRequest().getParameter("translate");
		Locale requestLocale = ac.getRequest().getLocale();

	    try {
	    	writer = ac.getResponse().getWriter();
	    }
	    catch (IOException e) {
	    	System.out.println(e);
	    }

	    Query query = new Query(searchTerm);
	    query.setResultType(Query.POPULAR);

        Twitter twitter = TwitterFactory.getSingleton();
        try {
        	// Just get the first page of results to avoid exceeding the Twitter rate limit
        	QueryResult result = twitter.search(query);

        	List<Status> tweets = result.getTweets();
        	for (Status tweet : tweets) {
        		JSONObject json = new JSONObject();
        		try {
        			json.put("screenName", tweet.getUser().getScreenName());
        			json.put("message", tweet.getText());
        			// Call Watson machine translation if there is a request to translate
        			if(!targetLang.equals("")) {
        				json.put("translation", wt.translate(tweet.getText(), targetLang));
        			}
					// Call Watson language identification service and map the BCP-47 tag
					// to a locale
					Locale tweetLocale = Locale.forLanguageTag(wt.identify(tweet.getText()));
					json.put("language", tweetLocale.getDisplayLanguage(requestLocale));

        			writer.write("data: " + json.toString() + "\n\n");
        			writer.flush();
        		}
        		catch(JSONException e) {
							logger.log(Level.SEVERE, "JSON Error: "+e.getMessage(), e);
        		}
        	}
        	writer.write("event: finished\n");
					writer.write("data: \n\n");
					writer.flush();
        	writer.close();
        	ac.complete();
        }
        catch(TwitterException e) {
        	logger.log(Level.SEVERE, "Twitter Error: "+e.getMessage(), e);
        }
	}
}
