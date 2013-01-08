package it.nickaquila.Placesocial.exceptions;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import com.sun.jersey.api.Responses;

/**
 *  
 * @author Nicol� Aquilini (nickaquila)
 * 
 * An exception class to be thrown in case of the place name cannot be resolved
 *
 */
public class NoPlaceFoundException extends WebApplicationException {
	 
    /**
     * Create a HTTP 404 (Not Found) exception.
     */
    public NoPlaceFoundException() {
        super(Responses.notFound().build());
    }
 
    /**
     * Create a HTTP 404 (Not Found) exception.
     * @param message the String that is the entity of the 404 response.
     */
    public NoPlaceFoundException(String message) {
        super(Response.status(Responses.NOT_FOUND).
                entity(message).type("text/plain").build());
    }
 
}
