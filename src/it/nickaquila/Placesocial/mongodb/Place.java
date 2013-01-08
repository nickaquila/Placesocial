package it.nickaquila.Placesocial.mongodb;

import java.util.HashSet;
import java.util.Set;

import org.bson.types.ObjectId;

import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Id;

@Entity
public class Place {

	@Id private ObjectId id;	
	private String woeId;
	private String placeId;
	private double latitude;
	private double longitude;
	private String name;
	private Set<String> queries;

	public Place(){
		
	}
	
	public Place(com.aetrion.flickr.places.Place place, String query){
		this.name=place.getName().toLowerCase();
		this.woeId=place.getWoeId();
		this.placeId=place.getPlaceId();
		this.latitude=place.getLatitude();
		this.longitude=place.getLongitude();
		this.queries=new HashSet<String>();
		this.queries.add(query.toLowerCase());
	}
	
	public Place(String name, String woeId, String placeId, double latitude, double longitude){
		this.name=name;
		this.woeId=woeId;
		this.placeId=placeId;
		this.latitude=latitude;
		this.longitude=longitude;
	}
	
	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}
	
	public Set<String> getQueries() {
		return queries;
	}

	public void setQueries(Set<String> queries) {
		this.queries = queries;
	}
	
	public void addQuery(String query){
		this.queries.add(query.toLowerCase());
	}
	
	public String getWoeId() {
		return woeId;
	}
	public void setWoeId(String woeId) {
		this.woeId = woeId;
	}
	public String getPlaceId() {
		return placeId;
	}
	public void setPlaceId(String placeId) {
		this.placeId = placeId;
	}
	public double getLatitude() {
		return latitude;
	}
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	public double getLongitude() {
		return longitude;
	}
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name.toLowerCase();
	}
}
