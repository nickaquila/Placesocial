package it.nickaquila.Placesocial.jaxb;

import javax.xml.bind.annotation.XmlRootElement;

import org.codehaus.jackson.annotate.JsonProperty;

@XmlRootElement
public class PlaceResults {
	
	private int numberOfSocialActivities;
	@JsonProperty("details") private PlaceResultsDetails details;
	
	public PlaceResults(Picture[] flickrPics, Picture[] picasaPics, Tweet[] tweets){
		this.details=new PlaceResultsDetails(flickrPics, picasaPics, tweets);
		this.numberOfSocialActivities=details.getNumberOfSocialActivities();
	}

	public int getNumberOfSocialActivities() {
		return numberOfSocialActivities;
	}
	
	public PlaceResultsDetails getDetails() {
		return details;
	}

	public void setDetails(PlaceResultsDetails details) {
		this.details = details;
	}
	
}
