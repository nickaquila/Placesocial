package it.nickaquila.Placesocial.jaxb;

import it.nickaquila.Placesocial.impl.FlickrCrawlerImpl;
import it.nickaquila.Placesocial.impl.PicasaCrawlerImpl;
import it.nickaquila.Placesocial.impl.PictureCrawlerImpl;
import it.nickaquila.Placesocial.impl.TwitterCrawlerImpl;
import it.nickaquila.Placesocial.mongodb.Place;
import it.nickaquila.Placesocial.mongodb.PlaceUtils;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

@Path("/place")
public class PlaceResource{
	
	  @GET
	  @Produces({ MediaType.APPLICATION_JSON })
	  public PlaceResults getResults(@QueryParam("place") String placeQuery, @QueryParam("tags") String tags, @QueryParam("excludeTags") String excludeTags, @QueryParam("tagMode") String tagMode){
		  
		  ArrayList<String> tagsList=new ArrayList<String>();
		  ArrayList<String> excludeTagsList=new ArrayList<String>();
		  
		  //check place
		  if (placeQuery==null){
			  return null;
		  }
		  
		  //retrieve the place object either from the Net or the db (if already present)
		  PlaceUtils placeUtils=new PlaceUtils();
		  Place place=placeUtils.getPlace(placeQuery);
		  
		  //get tags
		  if (tags!=null){
			  for (String tag: tags.split(",")){
				  tagsList.add(tag);
			  }
		  }else{
			  return null;
		  }
		  
		//get excludeTags (if there)
		  if (excludeTags!=null){
			  for (String tag: excludeTags.split(",")){
				  excludeTagsList.add(tag);
			  }
		  }
		  
		  //search for social activities since a week before now
		  long now=System.currentTimeMillis();
		  Date today=new Date(now);
		  Date lastWeek=(Date) today.clone();
		  lastWeek.setDate(today.getDate()-7);
		  
		  FlickrCrawlerImpl flickrCrawler=new FlickrCrawlerImpl();
		  PicasaCrawlerImpl picasaCrawler=new PicasaCrawlerImpl();
		  TwitterCrawlerImpl twitterCrawler=new TwitterCrawlerImpl();
		  
		  if (tagMode==null){
			  //set to default
			  flickrCrawler.setTagMode(PictureCrawlerImpl.TAGMODE_ALL);
			  picasaCrawler.setTagMode(PictureCrawlerImpl.TAGMODE_ALL);
			  twitterCrawler.setTagMode(TwitterCrawlerImpl.TAGMODE_ALL);
		  }else{
			  if (tagMode.toLowerCase().equals(PictureCrawlerImpl.TAGMODE_ANY)){
				  flickrCrawler.setTagMode(PictureCrawlerImpl.TAGMODE_ANY);
				  picasaCrawler.setTagMode(PictureCrawlerImpl.TAGMODE_ANY);
				  twitterCrawler.setTagMode(TwitterCrawlerImpl.TAGMODE_ANY);
			  }else{
				  flickrCrawler.setTagMode(PictureCrawlerImpl.TAGMODE_ALL);
				  picasaCrawler.setTagMode(PictureCrawlerImpl.TAGMODE_ALL);
				  twitterCrawler.setTagMode(TwitterCrawlerImpl.TAGMODE_ALL);
			  }
		  }
		  
		  ArrayList<Picture> flickrPics=null;
		  ArrayList<Picture> picasaPics=null;
		  List<Tweet> tweets=null;
		  
		  //set results limits to the maximum allowed
		  flickrCrawler.setMaxNumberOfPics(FlickrCrawlerImpl.PICS_LIMIT);
		  picasaCrawler.setMaxNumberOfPics(PicasaCrawlerImpl.PICS_LIMIT);
		  twitterCrawler.setMaxNumberOfTweets(TwitterCrawlerImpl.TWEETS_LIMIT);
		  
		  //get latest pictures from Flickr
		  //get latest pictures from Picasa
		  //get latest tweets
		  if (excludeTagsList.isEmpty()){
			  flickrPics=flickrCrawler.getLatestPicsForPlace(place, tagsList, lastWeek);
			  picasaPics=picasaCrawler.getLatestPicsForPlace(place, tagsList, lastWeek);
			  tweets=twitterCrawler.getLatestTweetsForPlace(place, tagsList, lastWeek);
		  }else{
			  flickrPics=flickrCrawler.getLatestPicsForPlace(place, tagsList, excludeTagsList, lastWeek);
			  picasaPics=picasaCrawler.getLatestPicsForPlace(place, tagsList, excludeTagsList, lastWeek);
			  tweets=twitterCrawler.getLatestTweetsForPlace(place, tagsList, excludeTagsList, lastWeek);
		  }
		  
		  PlaceResults results=new PlaceResults(flickrPics.toArray(new Picture[flickrPics.size()]), picasaPics.toArray(new Picture[picasaPics.size()]), tweets.toArray(new Tweet[tweets.size()]));
		  
		  return results;
	  }	
	  
}
