package it.nickaquila.Placesocial.impl;

import it.nickaquila.Placesocial.interfaces.PlaceCrawler;
import it.nickaquila.Placesocial.jaxb.Picture;
import it.nickaquila.Placesocial.mongodb.Place;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.xml.sax.SAXException;

import com.aetrion.flickr.Flickr;
import com.aetrion.flickr.FlickrException;
import com.aetrion.flickr.photos.Extras;
import com.aetrion.flickr.photos.Photo;
import com.aetrion.flickr.photos.PhotoList;
import com.aetrion.flickr.photos.SearchParameters;
import com.aetrion.flickr.places.PlacesInterface;
import com.aetrion.flickr.places.PlacesList;

/**
 * 
 * @author Nicolò Aquilini (nickaquila)
 * 
 * The actual implementation of the FlickrCrawler which exploits the Flickr APIs
 *
 */
public class FlickrCrawlerImpl extends PictureCrawlerImpl implements PlaceCrawler {

	public static final int PICS_LIMIT=500;
	
	private final String API_KEY="2380676873ec7d2f03e963c04817939f";
	private final String SECRET="28c179a9ea2daf51";
	
	private Flickr flickr;
	private int numberOfPicsPerPage;
	private int pages;
	private SearchParameters params;
	private int radius=1;

	
	/**
	 * Basic constructor, it sets the API_KEY
	 */
	public FlickrCrawlerImpl(){
		super();
		this.flickr=new Flickr(API_KEY);
		Flickr.debugStream=false;
		this.numberOfPicsPerPage=this.maxNumberOfPics;
		this.pages=maxNumberOfPics/numberOfPicsPerPage;
		this.params=new SearchParameters();
		//Sort according to the posting date on Flickr
		this.params.setSort(SearchParameters.DATE_POSTED_DESC);
		//return all the picture parameters
		this.params.setExtras(Extras.ALL_EXTRAS);
	}
	
	/**
	 * The getter method to obtain the API_KEY associated to the application 
	 * 
	 * @return the API_KEY associated to the application
	 */
	public String getAPI_KEY(){
		return this.API_KEY;
	}
	
	@Override
	public void setMaxNumberOfPics(int numberOfPics){
		//if invalid value, set it to default
		if (numberOfPics<0){
			numberOfPics=100;
		}
		this.maxNumberOfPics=numberOfPics;
		this.numberOfPicsPerPage=numberOfPics;
	}

	private void setNumberOfPicsPerPage(int numberOfPicsPerPage) {
		this.numberOfPicsPerPage=numberOfPicsPerPage;
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
		
		params.setTags(tagsList);
		params.setTagMode(this.tagMode);
		return this.getPictures(params);
	}
	
	@Override
	public ArrayList<Picture> getLatestPicsForTags(ArrayList<String> tags, Date minUploadDate) {

		//set the upload time restriction
		params.setMinUploadDate(minUploadDate);
		
		return this.getPicsForTags(tags);
	}
	
	@Override
	public ArrayList<Picture> getLatestPicsForTags(ArrayList<String> tags, ArrayList<String> excludeTags, Date minUploadDate) {

		//set the upload time restriction
		params.setMinUploadDate(minUploadDate);	
		return this.getPicsForTags(tags, excludeTags);
	}

	@Override
	public ArrayList<Picture> getPicsForTags(ArrayList<String> tags, ArrayList<String> excludeTags) {
		
		ArrayList<Picture> pics=new ArrayList<Picture>();
		ArrayList<Picture> picsPerTags;
		
		int i=0;
		do{
			i++;
			//since the second loop we look for the remaining pictures in a larger set of pictures
			int newNumberOfPicsPerPage=i*numberOfPicsPerPage;
			this.setNumberOfPicsPerPage((newNumberOfPicsPerPage>PICS_LIMIT)?PICS_LIMIT:newNumberOfPicsPerPage);
			picsPerTags=this.getPicsForTags(tags);
			if (picsPerTags==null){
				return null;
			}
			pics=this.removePicsForTags(picsPerTags, excludeTags);
		}while(pics.size()<maxNumberOfPics && numberOfPicsPerPage<PICS_LIMIT);
		
		//if the number of pics retrieved and filtered by tags exceed the desired number, truncate the list
		if (pics.size()>maxNumberOfPics){
			List<Picture> picsSubList=pics.subList(0, maxNumberOfPics);
			pics=new ArrayList<Picture>(picsSubList);
		}
		
		return pics;
	}
	
	public ArrayList<Picture> getPicsForPlace(Place place, ArrayList<String> tags) {
		
		ArrayList<Picture> pics=new ArrayList<Picture>();
		
		this.params.setWoeId(place.getWoeId());
		this.params.setRadius(radius);
		pics=this.getPicsForTags(tags);
		
		return pics;
	}
	
	public ArrayList<Picture> getPicsForPlace(Place place, ArrayList<String> tags, ArrayList<String> excludeTags) {
		
		ArrayList<Picture> pics=new ArrayList<Picture>();
		
		this.params.setWoeId(place.getWoeId());
		this.params.setRadius(radius);
		pics=this.getPicsForTags(tags, excludeTags);
		
		return pics;
	}

	@Override
	public ArrayList<Picture> getLatestPicsForPlace(Place place, ArrayList<String> tags, Date minUploadDate) {
		
		ArrayList<Picture> pics=new ArrayList<Picture>();
		
		this.params.setWoeId(place.getWoeId());
		this.params.setRadius(radius);
		pics=this.getLatestPicsForTags(tags, minUploadDate);
		
		return pics;
	}
	
	@Override
	public ArrayList<Picture> getLatestPicsForPlace(Place place, ArrayList<String> tags, ArrayList<String> excludeTags, Date minUploadDate) {
		
		ArrayList<Picture> pics=new ArrayList<Picture>();
			
		this.params.setWoeId(place.getWoeId());
		this.params.setRadius(radius);
		pics=this.getLatestPicsForTags(tags, excludeTags, minUploadDate);
		
		return pics;
	}
	
	@Override
	public List<Place> getPlace(String placeQuery){
		PlacesInterface placesInterface=flickr.getPlacesInterface();
		PlacesList placesList=null;
		try {
			placesList=placesInterface.find(placeQuery);
		} catch (FlickrException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} catch (SAXException e) {
			e.printStackTrace();
			return null;
		}
		
		List<Place> places=new ArrayList<Place>();
		for (int i=0; i<placesList.size(); i++){
			places.add(new Place((com.aetrion.flickr.places.Place)placesList.get(i), placeQuery));
		}
		
		return places;
	}
	
	/**
	 * The method exploits the flicrj API to retrieve the pictures from Flickr according to a set of parameters
	 *  
	 * @param params - the set of parameters
	 * @return the list of pictures references
	 */
	private ArrayList<Picture> getPictures(SearchParameters params){
		ArrayList<Photo> retrievedPics=new ArrayList<Photo>();
		PhotoList picsList;

		try {
			picsList = flickr.getPhotosInterface().search(params, numberOfPicsPerPage, pages);
			for(int i=0;i<picsList.size();i++){
		        Photo pic=(Photo) picsList.get(i);
				retrievedPics.add(pic);
			}
		} catch (IOException e){
			e.printStackTrace();
			return null;
		} catch (SAXException e) {
			e.printStackTrace();
			return null;
		} catch (FlickrException e) {
			e.printStackTrace();
			return null;
		}
		
		return convertIntoPictures(retrievedPics);
	}
	
	protected ArrayList<Picture> convertIntoPictures(ArrayList<Photo> photos) {
		ArrayList<Picture> pics=new ArrayList<Picture>();
		for (Photo photo: photos){
			Picture pic=new Picture(photo);
			pics.add(pic);
		}
		return pics;
	}
	
	public static void main(String[] args){
		FlickrCrawlerImpl flickrCrawler=new FlickrCrawlerImpl();
		PicasaCrawlerImpl picasaCrawler=new PicasaCrawlerImpl();
		ArrayList<String> tagsList=new ArrayList<String>();
		tagsList.add("ocean");
		ArrayList<String> excludeTagsList=new ArrayList<String>();
		excludeTagsList.add("rose");
		long now=System.currentTimeMillis();
		Date today=new Date(now);
		Date yesterday=(Date) today.clone();
		yesterday.setDate(today.getDate()-1);
		flickrCrawler.setTagMode(PictureCrawlerImpl.TAGMODE_ALL);
		flickrCrawler.setMaxNumberOfPics(10);
		List<Place> place=flickrCrawler.getPlace("San Francisco, CA");
		ArrayList<Picture> pics=picasaCrawler.getLatestPicsForPlace(place.get(0), tagsList, yesterday);
		
		for(Object picObject: pics)
		{
			Picture pic=(Picture) picObject;
			System.out.println(pic.getURL());
		}
	}

}
