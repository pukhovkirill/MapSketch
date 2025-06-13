package com.pukhovkirill.mapsketch.exception;

public class GeoObjectNotFoundException extends RuntimeException{

    public GeoObjectNotFoundException(String message) {
        super(message);
    }

    public GeoObjectNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public GeoObjectNotFoundException(Throwable cause) {
        super(cause);
    }

}
