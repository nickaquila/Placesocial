package it.nickaquila.Placesocial.impl;

import it.nickaquila.Placesocial.interfaces.TwitterCrawler;
import it.nickaquila.Placesocial.jaxb.Tweet;
import it.nickaquila.Placesocial.mongodb.Place;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import twitter4j.GeoLocation;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

/**
 * 
 * @author Nicolò Aquilini (nickaquila)
 * 
 * The actual implementation of the TwitterCrawler which exploits the twitter4j APIs
 *
 */
public class TwitterCrawlerImpl implements TwitterCrawler {

	public static final String TAGMODE_ALL="all";
	public static final String TAGMODE_ANY="any";
	public static final int TWEETS_LIMIT=100;
	public static final int TWEETS_DEFAULT=100;
	
	private final String CONSUMER_KEY = "C6fRxXZepgEGBdxRj5VYqA";
	private final String CONSUMER_SECRET = "LE1qYj63slFf4QInUN6TfRFUpdwLgjhZTcyyyBiqU";
	private final String ACCESS_TOKEN = "119803957-sDeB6BSuz5w7GvIWuOWzIJqkxbRoh920E4tRFCoB";
	private final String TOKEN_SECRET = "H6kRd4KV4mgj8D9u96dfk2RxxRd0CbZwNfHZyYAXQU";

	private Twitter twitter;
	private Query query;
	private int numberOfTweets;
	private String tagMode;
	private int range;

	public TwitterCrawlerImpl() {
		ConfigurationBuilder cb = new ConfigurationBuilder();
		cb.setDebugEnabled(true);
		cb.setOAuthConsumerKey(CONSUMER_KEY);
		cb.setOAuthConsumerSecret(CONSUMER_SECRET);
		cb.setOAuthAccessToken(ACCESS_TOKEN);
		cb.setOAuthAccessTokenSecret(TOKEN_SECRET);
		TwitterFactory tf = new TwitterFactory(cb.build());
		twitter = tf.getInstance();
		this.numberOfTweets=TWEETS_LIMIT;
		query = new Query();
		range=10;
		
	}

	@Override
	public void setMaxNumberOfTweets(int numberOfTweets){
		//if invalid value, set it to default
		if (numberOfTweets<0){
			numberOfTweets=TWEETS_DEFAULT;
		}
		this.numberOfTweets=numberOfTweets;
		query.setCount(numberOfTweets);
	}
	
	@Override
	public void setTagMode(String tagMode) {
		if (tagMode.equals(TAGMODE_ALL) || tagMode.equals(TAGMODE_ANY)){
			this.tagMode=tagMode;
		}else{
			this.tagMode=TAGMODE_ANY;
		}
	}
	
	@Override
	public ArrayList<Tweet> getTweetsForTags(ArrayList<String> tags) {
		if (!tags.isEmpty()){
			String hashtagString = this.getQueryString(tags);
			query.setQuery(hashtagString);
		}
		query.setCount(numberOfTweets);
		QueryResult results = null;
		try {
			results = twitter.search(query);
		} catch (TwitterException e) {
			e.printStackTrace();
		}
		List<Status> status = results.getTweets();
		ArrayList<Tweet> tweets = new ArrayList<Tweet>();
		for (Status stat : status) {
			tweets.add(new Tweet(stat));
		}

		return tweets;
	}

	@Override
	public ArrayList<Tweet> getTweetsForTags(ArrayList<String> tags,
			ArrayList<String> excludeTags) {
		ArrayList<Tweet> tweets = new ArrayList<Tweet>();
		tweets = this.getTweetsForTags(tags);
		return this.removeTweetsForTags(tweets, excludeTags);
	}

	@Override
	public ArrayList<Tweet> getLatestTweetsForTags(ArrayList<String> tags,
			ArrayList<String> excludeTags, Date sinceDate) {
		ArrayList<Tweet> tweets = new ArrayList<Tweet>();
		tweets = this.getLatestTweetsForTags(tags, sinceDate);
		return this.removeTweetsForTags(tweets, excludeTags);
	}

	@Override
	public ArrayList<Tweet> getLatestTweetsForPlace(Place place,
			ArrayList<String> tags, ArrayList<String> excludeTags,
			Date sinceDate) {
		ArrayList<Tweet> tweets = new ArrayList<Tweet>();
		tweets = this.getLatestTweetsForPlace(place, tags, sinceDate);
		return this.removeTweetsForTags(tweets, excludeTags);
	}

	@Override
	public ArrayList<Tweet> getLatestTweetsForTags(ArrayList<String> tags,
			Date sinceDate) {
		String queryString = this.getQueryString(tags);
		query.setQuery(queryString);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		query.since(sdf.format(sinceDate));

		ArrayList<Tweet> tweets = this.getTweetsForTags(tags);
		ArrayList<Tweet> tweetsSinceDate = new ArrayList<Tweet>();

		for (Tweet tweet : tweets) {
			// exclude tweets before the sinceDate (filter per hour)
			if (!tweet.getPostingDate().before(sinceDate)) {
				tweetsSinceDate.add(tweet);
			}
		}

		return tweetsSinceDate;
	}

	@Override
	public ArrayList<Tweet> getTweetsForPlace(Place place, ArrayList<String> tags) {
		String queryString = this.getQueryString(tags);
		query.setQuery(queryString);
		GeoLocation location = new GeoLocation(place.getLatitude(), place
				.getLongitude());
		query.geoCode(location, range, Query.KILOMETERS);

		return this.getTweetsForTags(tags);
	}

	@Override
	public ArrayList<Tweet> getTweetsForPlace(Place place,
			ArrayList<String> tags, ArrayList<String> excludeTags) {
		ArrayList<Tweet> tweets = new ArrayList<Tweet>();
		tweets = this.getTweetsForPlace(place, tags);
		return this.removeTweetsForTags(tweets, excludeTags);
	}

	@Override
	public ArrayList<Tweet> getLatestTweetsForPlace(Place place,
			ArrayList<String> tags, Date sinceDate) {
		String queryString = this.getQueryString(tags);
		query.setQuery(queryString);
		GeoLocation location = new GeoLocation(place.getLatitude(), place
				.getLongitude());
		query.geoCode(location, range, Query.KILOMETERS);

		return this.getLatestTweetsForTags(tags, sinceDate);
	}

	/**
	 * Set up the query string according to the tags and the tag mode
	 * 
	 * @param tags
	 * @return the query string
	 */
	private String getQueryString(ArrayList<String> tags) {
		String queryString = "";

		Iterator<String> it=tags.iterator();
		while(it.hasNext()) {
			String tag=it.next();
			tag = tag.replaceAll(" ", "");
			queryString += tag;
			if (it.hasNext()){
				if (tagMode.equals(TAGMODE_ALL)){
					queryString += " ";
				}else{
					queryString += " OR ";
				}
			}
		}

		return queryString;
	}

	/**
	 * Given a list of tweets, the method removes all the tweets which are
	 * tagged with the exclusion tags, according to the exclusion mode
	 * 
	 * @param tweets
	 *            - the list of tweets
	 * @param excludeTags
	 *            - the list of exclusion tags
	 * @return the list of pictures pruned according to the exclusion tags
	 */
	private ArrayList<Tweet> removeTweetsForTags(List<Tweet> tweets,
			ArrayList<String> excludeTags) {

		ArrayList<Tweet> tws = new ArrayList<Tweet>();

		for (Tweet tweet : tweets) {
			String[] hashtags = tweet.getHashtags();
			boolean toDiscard = false;
			for (String excludeTag : excludeTags) {
				for (String tag : hashtags) {
					if (tag.equals(excludeTag)) {
						toDiscard = true;
						break;
					}
				}
			}
			if (!toDiscard) {
				tws.add(tweet);
			}
		}

		return tws;
	}

	public static void main(String[] args) {
		TwitterCrawlerImpl twitterCrawler = new TwitterCrawlerImpl();
		// twitterCrawler.getTweets("#sanfrancisco#job");
	}

}
