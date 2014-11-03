package com.ibm;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import twitter4j.*;

import javax.servlet.AsyncContext;

public class TwitterAsyncService implements Runnable{

	private AsyncContext ac;
	private WatsonTranslate wt = new WatsonTranslate();
	
	public TwitterAsyncService(AsyncContext context) {
		this.ac = context;
	}

	
	@Override
	  public void run() {
	    PrintWriter writer = null;
	    String searchTerm = ac.getRequest().getParameter("search");
	    String translation = ac.getRequest().getParameter("translate");
	    
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
        			// Call Watson machine translation
        			if(!translation.equals("")) {
        				json.put("translation", wt.translate(tweet.getText(), translation));
        			}
        			writer.write("data: " + json.toString() + "\n\n");
        			writer.flush();
        		}
        		catch(JSONException e) {
        			System.out.println("BAD JSON");
        		}
        		//System.out.println("@" + tweet.getUser().getScreenName() + " - " + tweet.getText());
        	}
        	writer.write("event: finished\n");
        	writer.close();
        	ac.complete();
        }
        catch(TwitterException e) {
        	System.out.println(e);
        }
	}
}
