package it.nickaquila.Placesocial.jaxb;

import org.codehaus.jackson.annotate.JsonIgnore;
import com.sun.xml.internal.txw2.annotation.XmlElement;

@XmlElement
public class PlaceResultsDetails {
	
	@JsonIgnore private Picture[] flickrPics;
	@JsonIgnore private Picture[] picasaPics;
	@JsonIgnore private Tweet[] tweets;
	private int numberOfFlickrPics;
	private int numberOfPicasaPics;
	private int numberOfTweets;
	
	public PlaceResultsDetails(Picture[] flickrPics, Picture[] picasaPics, Tweet[] tweets){
		this.flickrPics=flickrPics;
		this.picasaPics=picasaPics;
		this.tweets=tweets;
		this.numberOfFlickrPics=flickrPics.length;
		this.numberOfPicasaPics = picasaPics.length;
		this.numberOfTweets=tweets.length;
	}
	
	public Picture[] getFlickrPics() {
		return flickrPics;
	}
	
	public void setFlickrPics(Picture[] pics) {
		this.flickrPics = pics;
		this.numberOfFlickrPics=pics.length;
	}
	
	public Picture[] getPicasaPics() {
		return picasaPics;
	}

	public void setPicasaPics(Picture[] picasaPics) {
		this.picasaPics = picasaPics;
		this.numberOfPicasaPics = picasaPics.length;
	}
	
	public Tweet[] getTweets() {
		return tweets;
	}
	
	public void setTweets(Tweet[] tweets) {
		this.tweets = tweets;
		this.numberOfTweets=tweets.length;
	}
	
	public int getNumberOfFlickrPics() {
		return numberOfFlickrPics;
	}
	
	public int getNumberOfPicasaPics() {
		return numberOfPicasaPics;
	}
	
	public int getNumberOfTweets() {
		return numberOfTweets;
	}
	
	@JsonIgnore
	public int getNumberOfSocialActivities() {
		return numberOfFlickrPics+numberOfPicasaPics+numberOfTweets;
	}
}
