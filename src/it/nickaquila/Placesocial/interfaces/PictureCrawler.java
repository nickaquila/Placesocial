package it.nickaquila.Placesocial.interfaces;

import it.nickaquila.Placesocial.jaxb.Picture;
import it.nickaquila.Placesocial.mongodb.Place;

import java.util.ArrayList;
import java.util.Date;

/**
 * 
 * @author Nicolò Aquilini (nickaquila)
 * 
 * Interface for retrieving images attributes (URL, tags, etc...) from a generic picture social platform
 *
 */
public interface PictureCrawler {
	
	/**
	 * The method to obtain the pictures from a Web Service since a given date, tagged with a given sets of tags, according to the tag mode
	 * 
	 * @param tags - the set of tags
	 * @param minUploadDate - the given date
	 * @return a list containing the pictures
	 */
	public ArrayList<Picture> getPicsForTags(ArrayList<String> tags);
	
	/**
	 * The method to obtain the pictures from a Web Service, tagged with the tags in a given set, according to the tag mode,
	 * and that do not contain the tags in the exclude set, according to the exclusion mode
	 * 
	 * @param tags - the set of tags
	 * @param excludeTags - the exclude set of tags
	 * @return a list containing the pictures
	 */
	public ArrayList<Picture> getPicsForTags(ArrayList<String> tags, ArrayList<String> excludeTags);
	
	/**
	 * The method to obtain the pictures from a Web Service since a given date, tagged with a given set of tags, according to the tag mode
	 * 
	 * @param tags - the set of tags
	 * @param minUploadDate - the given date
	 * @return a list containing the pictures
	 */
	public ArrayList<Picture> getLatestPicsForTags(ArrayList<String> tags, Date minUploadDate);
	

	/**
	 * The method to obtain the pictures from a Web Service since a given date, 
	 * tagged with a given set of tag, according to the tag mode, and that do not contain the tags in the exclude set,
	 * according to the exclusion mode 
	 * 
	 * @param tags - the set of tags
	 * @param excludeTags - the exclude set of tags
	 * @param minUploadDate - the given date
	 * @return a list containing the pictures
	 */
	public ArrayList<Picture> getLatestPicsForTags(ArrayList<String> tags, ArrayList<String> excludeTags, Date minUploadDate);
		
	/**
	 * The method to obtain the pictures, from a Web Service, geotagged with a given place ("San Francisco, CA") 
	 * 
	 * @param the place's name
	 * @param tags - the set of tags
	 * @return a list containing the pictures
	 */
	public ArrayList<Picture> getPicsForPlace(Place place, ArrayList<String> tags);
	
	/**
	 * The method to obtain the pictures, from a Web Service, geotagged with a given place ("San Francisco, CA") 
	 * 
	 * @param the place
	 * @param tags - the set of tags
	 * @param excludeTags - the exclude set of tags
	 * @return a list containing the pictures
	 */
	public ArrayList<Picture> getPicsForPlace(Place place, ArrayList<String> tags, ArrayList<String> excludeTags);
	
	/**
	 * The method to obtain the pictures, from a Web Service, geotagged with a given place ("San Francisco, CA") 
	 * 
	 * @param the place
	 * @param tags - the set of tags
	 * @param minUploadDate - the given date
	 * @return a list containing the pictures
	 */
	public ArrayList<Picture> getLatestPicsForPlace(Place place, ArrayList<String> tags, Date minUploadDate);
	
	/**
	 * The method to obtain the pictures, from a Web Service, geotagged with a given place ("San Francisco, CA") 
	 * 
	 * @param the place
	 * @param tags - the set of tags
	 * @param excludeTags - the exclude set of tags
	 * @param minUploadDate - the given date
	 * @return a list containing the pictures
	 */
	public ArrayList<Picture> getLatestPicsForPlace(Place place, ArrayList<String> tags, ArrayList<String> excludeTags, Date minUploadDate);
	
	
	/**
	 * Sets the tag mode (default ANY)
	 * 
	 * @param tagMode
	 */
	public void setTagMode(String tagMode);
	
	/**
	 * Return the tag mode
	 * 
	 * @return tagMode
	 */
	public String getTagMode();
	
	/**
	 * Sets the maximum number of pictures to be retrieved from a Web Service
	 * 
	 * @param maxNumberOfPics
	 */
	public void setMaxNumberOfPics(int maxNumberOfPics);
	
	/**
	 * Return the maximum number of pictures to be retrieved from a Web Service
	 * 
	 * @return maxNumberOfPics
	 */
	public int getMaxNumberOfPics();

}
