package com.moura.bitroute.exception;

public class ShortlinkGenerationException extends RuntimeException {
    
    public ShortlinkGenerationException(String message) {
        super(message);
    }
    
    public ShortlinkGenerationException(String message, Throwable cause) {
        super(message, cause);
    }
}