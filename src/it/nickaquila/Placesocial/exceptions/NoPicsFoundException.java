package it.nickaquila.Placesocial.exceptions;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import com.sun.jersey.api.Responses;

/**
 *  
 * @author Nicolò Aquilini (nickaquila)
 * 
 * An exception class to be thrown if no pictures are found for a given query (e.g. place name, tags)
 *
 */
public class NoPicsFoundException extends WebApplicationException {
	 
    /**
     * Create a HTTP 404 (Not Found) exception.
     */
    public NoPicsFoundException() {
        super(Responses.notFound().build());
    }
 
    /**
     * Create a HTTP 404 (Not Found) exception.
     * @param message the String that is the entity of the 404 response.
     */
    public NoPicsFoundException(String message) {
        super(Response.status(Responses.NOT_FOUND).
                entity(message).type("text/plain").build());
    }
 
}
