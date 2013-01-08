package it.nickaquila.Placesocial.interfaces;

import it.nickaquila.Placesocial.jaxb.Tweet;
import it.nickaquila.Placesocial.mongodb.Place;
import java.util.ArrayList;
import java.util.Date;

/**
 * 
 * @author Nicolò Aquilini (nickaquila)
 * 
 * An interface which provides the means to interact with the twitter4j API
 *
 */
public interface TwitterCrawler {
	
	public ArrayList<Tweet> getTweetsForTags(ArrayList<String> tags);
	
	public ArrayList<Tweet> getTweetsForTags(ArrayList<String> tags, ArrayList<String> excludeTags);
	
	public ArrayList<Tweet> getLatestTweetsForTags(ArrayList<String> tags, Date sinceDate);
	
	public ArrayList<Tweet> getLatestTweetsForTags(ArrayList<String> tags, ArrayList<String> excludeTags, Date sinceDate);
	
	public ArrayList<Tweet> getTweetsForPlace(Place place, ArrayList<String> tags);
	
	public ArrayList<Tweet> getTweetsForPlace(Place place, ArrayList<String> tags, ArrayList<String> excludeTags);
	
	public ArrayList<Tweet> getLatestTweetsForPlace(Place place, ArrayList<String> tags, Date sinceDate);
	
	public ArrayList<Tweet> getLatestTweetsForPlace(Place place, ArrayList<String> tags, ArrayList<String> excludeTags, Date sinceDate);

	void setMaxNumberOfTweets(int maxNumberOfTweets);

	void setTagMode(String tagMode);
}
