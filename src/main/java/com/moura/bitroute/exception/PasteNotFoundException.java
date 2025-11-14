package com.moura.bitroute.exception;

public class PasteNotFoundException extends RuntimeException {
    
    public PasteNotFoundException(String shortlink) {
        super("Paste not found with shortlink: " + shortlink);
    }
}