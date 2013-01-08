package it.nickaquila.Placesocial.mongodb;

import it.nickaquila.Placesocial.exceptions.NoPlaceFoundException;
import it.nickaquila.Placesocial.exceptions.PlaceConflictException;
import it.nickaquila.Placesocial.impl.FlickrCrawlerImpl;
import it.nickaquila.Placesocial.interfaces.PlaceCrawler;
import java.net.UnknownHostException;
import java.util.List;
import com.google.code.morphia.Datastore;
import com.google.code.morphia.Morphia;
import com.mongodb.Mongo;

/**
 * 
 * @author Nicolò Aquilini (nickaquila)
 * 
 * Tha class provide the means to interact with the MongoDB instance containing the metadata of the places
 *
 */
public class PlaceUtils {
	
	private static Mongo mongo;
	private Morphia morphia;
	
	public Place getPlace(String placeQuery){
		try {
			  mongo = new Mongo("127.0.0.1", 27017);
			  morphia = new Morphia();
			  
		  } catch (UnknownHostException e) {
			  e.printStackTrace();
		  }
		  morphia.map(Place.class);
		  Datastore ds = morphia.createDatastore(mongo, "placesocial");
		  PlaceDAO placeDAO=new PlaceDAO(morphia, mongo);
		  List<Place> placesByName=placeDAO.getPlaceByName(placeQuery);
		  List<Place> placesList=null;
		  Place place=null;
		  if (placesByName.size()==0){
			  PlaceCrawler placeCrawler=new FlickrCrawlerImpl();
			  placesList=placeCrawler.getPlace(placeQuery);
			  if (placesList.size()==0){
				  throw new NoPlaceFoundException("Place not found");
			  }
			  if (placesList.size()>1){
				  throw new PlaceConflictException("Ambigous request, try with a more specific place name (e.g. San Francisco, CA, USA)");
			  }
			  if (placesList.size()==1){
				  place=placesList.get(0);
			  }
			  //check if the same place is already present (different query)
			  List<Place> placesById=placeDAO.getPlaceById(place.getPlaceId());
			  if (placesById.size()>0){
				  //the place is present
				  Place place2=placesById.get(0);
				  //add the new query that generate the same place
				  place2.addQuery(placeQuery);
				  //update the place
				  placeDAO.save(place2);
				  place=place2;
			  }else{
				  //the place is not present, save it
				  placeDAO.save(place);
			  }
		  }else{
			  place=placesByName.get(0);
		  }
		  
		  return place;
	}
	
}
