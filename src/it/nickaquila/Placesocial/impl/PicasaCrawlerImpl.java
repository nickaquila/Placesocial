package it.nickaquila.Placesocial.impl;

import it.nickaquila.Placesocial.interfaces.PictureCrawler;
import it.nickaquila.Placesocial.jaxb.Picture;
import it.nickaquila.Placesocial.mongodb.Place;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import com.google.gdata.client.Query;
import com.google.gdata.client.photos.PicasawebService;
import com.google.gdata.data.DateTime;
import com.google.gdata.data.photos.AlbumFeed;
import com.google.gdata.data.photos.PhotoEntry;
import com.google.gdata.util.ServiceException;

/**
 * 
 * @author Nicolò Aquilini (nickaquila)
 * 
 * The actual implementation of the PicasaCrawler which exploits the Picasa APIs
 *
 */
public class PicasaCrawlerImpl extends PictureCrawlerImpl{
	
	public static final int PICS_LIMIT=1000;
	
	private URL baseSearchUrl;
	private PicasawebService picasa;
	private Query query;
	private int radius=1;
	
	/**
	 * Basic constructor, it sets the API_KEY
	 */
	public PicasaCrawlerImpl(){
		super();
		this.picasa=new PicasawebService("PicsPerTags");
		try {
			this.baseSearchUrl=new URL("https://picasaweb.google.com/data/feed/api/all");
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		//sets the query object
		query=new Query(baseSearchUrl);
		//we are looking for picture!
		query.setStringCustomParameter("kind", "photo");
		//search through just public pictures
		query.setStringCustomParameter("access","public");
	}
	
	@Override
	public ArrayList<Picture> getPicsForTags(ArrayList<String> tags) {
		
		String[] tagsList;
		if (tags.size()>tagsLimit){
			List<String> tagsSubList=tags.subList(0,tagsLimit);
			tagsList=tagsSubList.toArray(new String[tagsSubList.size()]);
		}else{
			tagsList=tags.toArray(new String[tags.size()]);
		}
		
		String separator;
		if (this.tagMode.equals(PictureCrawlerImpl.TAGMODE_ALL)){
			separator=",";
		}else{
			separator="|";
		}
		
		if (tags.size()>0){
			String tagsQuery="";
			for (int i=0;i<tagsList.length-1;i++){
				tagsQuery+=tagsList[i]+separator;	
			}
			tagsQuery+=tagsList[tagsList.length-1];
			query.setStringCustomParameter("tag", tagsQuery);
		}
		
		return this.getPictures(query);
	}
	
	@Override
	public ArrayList<Picture> getLatestPicsForTags(ArrayList<String> tags, Date minUploadDate) {

		DateTime dateTime=new DateTime(minUploadDate);
		//set the upload time restriction
		query.setPublishedMin(dateTime);
		
		return this.getPicsForTags(tags);
	}
	
	@Override
	public ArrayList<Picture> getLatestPicsForTags(ArrayList<String> tags, ArrayList<String> excludeTags, Date minUploadDate) {

		DateTime dateTime=new DateTime(minUploadDate);
		//set the upload time restriction
		query.setPublishedMin(dateTime);
		
		return this.getPicsForTags(tags, excludeTags);
	}

	@Override
	public ArrayList<Picture> getPicsForTags(ArrayList<String> tags, ArrayList<String> excludeTags) {
		
		ArrayList<Picture> pics=new ArrayList<Picture>();
		ArrayList<Picture> picsForTags;
		
		int i=0;
		do{
			i++;
			//since the second loop we look for the remaining pictures in a larger set of pictures
			int newNumberOfPics=i*maxNumberOfPics;
			this.setMaxNumberOfPics((newNumberOfPics>PICS_LIMIT)?PICS_LIMIT:newNumberOfPics);
			picsForTags=this.getPicsForTags(tags);
			if (picsForTags==null){
				return null;
			}
			pics=this.removePicsForTags(picsForTags, excludeTags);
		}while(pics.size()<maxNumberOfPics && maxNumberOfPics<PICS_LIMIT);
		
		//if the number of pics retrieved and filtered by tags exceed the desired number, truncate the list
		if (pics.size()>maxNumberOfPics){
			List<Picture> picsSubList=pics.subList(0, maxNumberOfPics);
			pics=new ArrayList<Picture>(picsSubList);
		}
		
		return pics;
	}
	
	@Override
	public ArrayList<Picture> getPicsForPlace(Place place,
			ArrayList<String> tags) {
		
		double[] bbox=this.getBbox(place.getLatitude(), place.getLongitude(), radius);
		query.setStringCustomParameter("bbox", bbox[0]+","+bbox[1]+","+bbox[2]+","+bbox[3]);
		
		return this.getPicsForTags(tags);
	}

	@Override
	public ArrayList<Picture> getPicsForPlace(Place place,
			ArrayList<String> tags, ArrayList<String> excludeTags) {
		double[] bbox=this.getBbox(place.getLatitude(), place.getLongitude(), radius);
		query.setStringCustomParameter("bbox", bbox[0]+","+bbox[1]+","+bbox[2]+","+bbox[3]);
		
		return this.getPicsForTags(tags, excludeTags);
	}
	

	@Override
	public ArrayList<Picture> getLatestPicsForPlace(Place place,
			ArrayList<String> tags, Date minUploadDate) {
		double[] bbox=this.getBbox(place.getLatitude(), place.getLongitude(), radius);
		query.setStringCustomParameter("bbox", bbox[0]+","+bbox[1]+","+bbox[2]+","+bbox[3]);
		
		return this.getLatestPicsForTags(tags, minUploadDate);
	}

	@Override
	public ArrayList<Picture> getLatestPicsForPlace(Place place,
			ArrayList<String> tags, ArrayList<String> excludeTags,
			Date minUploadDate) {
		double[] bbox=this.getBbox(place.getLatitude(), place.getLongitude(), radius);
		query.setStringCustomParameter("bbox", bbox[0]+","+bbox[1]+","+bbox[2]+","+bbox[3]);
		
		return this.getLatestPicsForTags(tags, excludeTags, minUploadDate);
	}

	protected ArrayList<Picture> convertIntoPictures(ArrayList<PhotoEntry> photos) {
		ArrayList<Picture> pics=new ArrayList<Picture>();
		for (PhotoEntry photo: photos){
			Picture pic=new Picture(photo);
			pics.add(pic);
		}
		return pics;
	}

	/**
	 * The method exploits the Picasa web Album API to retrieve the pictures from Picasa according to a set of parameters
	 *  
	 * @param params - the set of parameters
	 * @return the list of pictures references
	 */
	private ArrayList<Picture> getPictures(Query query){
		ArrayList<PhotoEntry> retrievedPics=new ArrayList<PhotoEntry>();
		AlbumFeed searchResultsFeed;
		query.setMaxResults(maxNumberOfPics);
		try {
			searchResultsFeed = picasa.query(query, AlbumFeed.class);
			for (PhotoEntry photo : searchResultsFeed.getPhotoEntries()) {
			    retrievedPics.add(photo);
			}
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} catch (ServiceException e) {
			e.printStackTrace();
			return null;
		}
		
		return convertIntoPictures(retrievedPics);
	}
	
	private double[] getBbox(double latitude, double longitude, int distance){
	
		 double radius = 3963.1; // of earth in miles
		        
		 // bearings     
		 double due_north = 0;
		 double due_south = 180;
		 double due_east = 90;
		 double due_west = 270;
		        
		 // convert latitude and longitude into radians 
		 double lat_r = Math.toRadians(latitude);
		 double lon_r = Math.toRadians(longitude);
		                        
		 // find the northmost, southmost, eastmost and westmost corners $distance_in_miles away
		 // original formula from
		 // http://www.movable-type.co.uk/scripts/latlong.html
		        
		 double northmost  = Math.asin(Math.sin(lat_r) * Math.cos(distance/radius) + Math.cos(lat_r) * Math.sin(distance/radius) * Math.cos(due_north));
		 double southmost  = Math.asin(Math.sin(lat_r) * Math.cos(distance/radius) + Math.cos(lat_r) * Math.sin(distance/radius) * Math.cos(due_south));
		                
		 double eastmost = lon_r + Math.atan2(Math.sin(due_east)*Math.sin(distance/radius)*Math.cos(lat_r),Math.cos(distance/radius)-Math.sin(lat_r)*Math.sin(lat_r));
		 double westmost = lon_r + Math.atan2(Math.sin(due_west)*Math.sin(distance/radius)*Math.cos(lat_r),Math.cos(distance/radius)-Math.sin(lat_r)*Math.sin(lat_r));
		                                        
		 northmost = Math.toDegrees(northmost);
		 southmost = Math.toDegrees(southmost);
		 eastmost = Math.toDegrees(eastmost);
		 westmost = Math.toDegrees(westmost);
		 
		 double[] bbox={westmost, southmost, eastmost, northmost};
		 
		 return bbox;

	}
	
	public static void main(String[] args){
		PictureCrawler picasaCrawler=new PicasaCrawlerImpl();
		ArrayList<String> tagsList=new ArrayList<String>();
		tagsList.add("sun");
		tagsList.add("sea");
		ArrayList<String> excludeTagsList=new ArrayList<String>();
		excludeTagsList.add("light");
		long now=System.currentTimeMillis();
		Date today=new Date(now);
		Date yesterday=(Date) today.clone();
		yesterday.setDate(today.getDate()-1);
		picasaCrawler.setTagMode(PicasaCrawlerImpl.TAGMODE_ALL);
		picasaCrawler.setMaxNumberOfPics(10);
		ArrayList<Picture> pics=picasaCrawler.getLatestPicsForTags(tagsList, excludeTagsList, yesterday);
		
		for(Object picObject: pics)
		{
			Picture pic=(Picture) picObject;
			System.out.println(pic.getURL());
		}
	}

}
