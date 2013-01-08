package it.nickaquila.Placesocial.jaxb;

import it.nickaquila.Placesocial.exceptions.NoPicsFoundException;
import it.nickaquila.Placesocial.impl.FlickrCrawlerImpl;
import it.nickaquila.Placesocial.interfaces.PictureCrawler;
import it.nickaquila.Placesocial.mongodb.Place;
import it.nickaquila.Placesocial.mongodb.PlaceUtils;
import java.util.ArrayList;
import java.util.Date;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/pics")
@Produces({ MediaType.APPLICATION_JSON })
public class PictureResource{

	  @GET
	  @Path("/flickr")
	  public Picture[] getFlickrPics(@QueryParam("place") String placeQuery, @QueryParam("tags") String tags, @QueryParam("exclude-tags") String excludeTags, @QueryParam("max-results") int maxNumberOfPics, @QueryParam("recent") boolean recent, @QueryParam("tag-mode") String tagMode) {
		  FlickrCrawlerImpl flickrCrawler=new FlickrCrawlerImpl();
		  ArrayList<Picture> pics=new ArrayList<Picture>();
		  
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
		  
		  if (maxNumberOfPics>0){
			  flickrCrawler.setMaxNumberOfPics(maxNumberOfPics);
		  }
		  if (tagMode!=null){
			  flickrCrawler.setTagMode(tagMode);
		  }
		  if (recent){
			  long now=System.currentTimeMillis();
			  Date today=new Date(now);
			  Date lastWeek=(Date) today.clone();
			  lastWeek.setDate(today.getDate()-7);
			  if (excludeTagsList.isEmpty()){
				  pics=flickrCrawler.getLatestPicsForPlace(place, tagsList, lastWeek);  
			  }else{
				  pics=flickrCrawler.getLatestPicsForPlace(place, tagsList, excludeTagsList, lastWeek);   
			  }
		  }else{
			  if (excludeTagsList.isEmpty()){
				  pics=flickrCrawler.getPicsForPlace(place, tagsList);  
			  }else{
				  pics=flickrCrawler.getPicsForPlace(place, tagsList, excludeTagsList);   
			  }
		  }
		  if (pics.isEmpty()){
			  throw new NoPicsFoundException("No pictures found!");
		  }
		  if (pics==null){
			  throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
		  }

		  return pics.toArray(new Picture[pics.size()]);
	  }	 
	  

	  
	  @GET
	  @Path("/picasa")
	  @Produces({ MediaType.APPLICATION_JSON })
	  public Picture[] getPicasaPics(@QueryParam("place") String placeQuery, @QueryParam("tags") String tags, @QueryParam("exclude-tags") String excludeTags, @QueryParam("max-results") int maxNumberOfPics, @QueryParam("recent") boolean recent, @QueryParam("tag-mode") String tagMode) {
		 	  
		  PictureCrawler flickrCrawler=new FlickrCrawlerImpl();
		  ArrayList<Picture> pics=new ArrayList<Picture>();
		  
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
		  
		  if (maxNumberOfPics>0){
			  flickrCrawler.setMaxNumberOfPics(maxNumberOfPics);
		  }
		  if (tagMode!=null){
			  flickrCrawler.setTagMode(tagMode);
		  }
		  if (recent){
			  long now=System.currentTimeMillis();
			  Date today=new Date(now);
			  Date lastWeek=(Date) today.clone();
			  lastWeek.setDate(today.getDate()-7);
			  if (excludeTagsList.isEmpty()){
				  pics=flickrCrawler.getLatestPicsForPlace(place, tagsList, lastWeek);  
			  }else{
				  pics=flickrCrawler.getLatestPicsForPlace(place, tagsList, excludeTagsList, lastWeek);   
			  }
		  }else{
			  if (excludeTagsList.isEmpty()){
				  pics=flickrCrawler.getPicsForPlace(place, tagsList);  
			  }else{
				  pics=flickrCrawler.getPicsForPlace(place, tagsList, excludeTagsList);   
			  }
		  }
		  if (pics.isEmpty()){
			  throw new NoPicsFoundException("No pictures found!");
		  }
		  if (pics==null){
			  throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
		  }

		  return pics.toArray(new Picture[pics.size()]);
	  }	 
}
