package it.nickaquila.Placesocial.exceptions;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

/**
 *  
 * @author Nicolò Aquilini (nickaquila)
 * 
 * An exception class to be thrown in case of uncertainty on the place to be searched
 *
 */
public class PlaceConflictException extends WebApplicationException {
	 
    /**
     * Create a HTTP 409 (Conflict) exception.
     */
    public PlaceConflictException() {
        super(Response.status(Response.Status.CONFLICT).build());
    }
 
    /**
     * Create a HTTP 409 (Conflict) exception.
     * @param message the String that is the entity of the 409 response.
     */
    public PlaceConflictException(String message) {
        super(Response.status(Response.Status.CONFLICT).
                entity(message).type("text/plain").build());
    }
 
}
