package it.nickaquila.Placesocial.mongodb;

import java.util.List;

import com.google.code.morphia.Morphia;
import com.google.code.morphia.dao.BasicDAO;
import com.mongodb.Mongo;

public class PlaceDAO extends BasicDAO<Place, String> {
	
	public PlaceDAO(Morphia morphia, Mongo mongo) {
		super(mongo, morphia, "placesocial");
	}
	
	public boolean isPlacePresent(String placeId){
		return !getPlaceById(placeId).isEmpty();
	}
	
	public List<Place> getPlaceById(String placeId){
		List<Place> results=ds.find(Place.class).field("placeId").contains(placeId).asList();
		return results;
	}

	public List<Place> getPlaceByName(String placeQuery){
		List<Place> results=ds.find(Place.class).field("queries").contains(placeQuery.toLowerCase()).asList();
		return results;
	}
	
	public void setPlace(Place place){
		ds.save(place);
	}
}