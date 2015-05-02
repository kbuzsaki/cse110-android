package edu.ucsd.studentpoll.rest;

import org.json.JSONObject;

/**
 * Extremely simple Exception class to be used alongside Andrest.
 *
 * Not entirely sure where this will go, but the ability to pass a
 * JSONObject means that custom errors can be built easily (e.g.
 * for passing things like status codes and metadata).
 *
 * @author  Isaac Whitfield
 * @version 09/03/2014
 *
 */
public class RESTException extends RuntimeException {

    private static final long serialVersionUID = 4491098305202657442L;

    public RESTException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public RESTException(String message){
        this(message, null);
    }

    public RESTException(Throwable throwable) {
        this(null, throwable);
    }

    public RESTException(JSONObject errorObject){
        super(errorObject.toString());
    }
}