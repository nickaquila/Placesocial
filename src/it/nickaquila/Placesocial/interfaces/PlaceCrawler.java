package it.nickaquila.Placesocial.interfaces;

import java.util.List;
import it.nickaquila.Placesocial.mongodb.Place;

/**
 * 
 * @author Nicol� Aquilini (nickaquila)
 *
 * A simple interface for resolving a place given its name
 */
public interface PlaceCrawler {

	public List<Place> getPlace(String placeName);
}
