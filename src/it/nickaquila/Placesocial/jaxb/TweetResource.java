package it.nickaquila.Placesocial.jaxb;

import it.nickaquila.Placesocial.exceptions.NoTweetsFoundException;
import it.nickaquila.Placesocial.impl.TwitterCrawlerImpl;
import it.nickaquila.Placesocial.interfaces.TwitterCrawler;
import it.nickaquila.Placesocial.mongodb.Place;
import it.nickaquila.Placesocial.mongodb.PlaceUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/tweets")
public class TweetResource {

	  @GET
	  @Produces({ MediaType.APPLICATION_JSON })
	  public Tweet[] getTweets(@QueryParam("place") String placeQuery, @QueryParam("tags") List<String> tags, @QueryParam("exclude-tags") List<String> excludeTags, @QueryParam("max-results") int maxNumberOfTweets, @QueryParam("recent") boolean recent, @QueryParam("tag-mode") String tagMode) {
		  
		  //retrieve the place object either from the Net or the db (if already present)
		  PlaceUtils placeUtils=new PlaceUtils();
		  Place place=placeUtils.getPlace(placeQuery);
		  
		  TwitterCrawler twitterCrawler=new TwitterCrawlerImpl();
		  ArrayList<Tweet> tweets=new ArrayList<Tweet>();
		  if (maxNumberOfTweets>0){
			  twitterCrawler.setMaxNumberOfTweets(maxNumberOfTweets);
		  }
		  if (tagMode!=null){
			  twitterCrawler.setTagMode(tagMode);
		  }
		  if (recent){
			  long now=System.currentTimeMillis();
			  Date today=new Date(now);
			  Date lastWeek=(Date) today.clone();
			  lastWeek.setDate(today.getDate()-7);
			  if (excludeTags.isEmpty()){
				  tweets=twitterCrawler.getLatestTweetsForPlace(place, new ArrayList<String>(tags), lastWeek);  
			  }else{
				  tweets=twitterCrawler.getLatestTweetsForPlace(place, new ArrayList<String>(tags), new ArrayList<String>(excludeTags), lastWeek);   
			  }
		  }else{
			  if (excludeTags.isEmpty()){
				  tweets=twitterCrawler.getTweetsForPlace(place, new ArrayList<String>(tags));  
			  }else{
				  tweets=twitterCrawler.getTweetsForPlace(place, new ArrayList<String>(tags), new ArrayList<String>(excludeTags));   
			  }
		  }
		  if (tweets.isEmpty()){
			  throw new NoTweetsFoundException("No tweets found!");
		  }
		  if (tweets==null){
			  throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
		  }

		  return tweets.toArray(new Tweet[tweets.size()]);
	  }	 
	
}
