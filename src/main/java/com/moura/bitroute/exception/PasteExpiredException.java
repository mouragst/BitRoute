package com.moura.bitroute.exception;

public class PasteExpiredException extends RuntimeException {
    
    public PasteExpiredException(String shortlink) {
        super("Paste expired with shortlink: " + shortlink);
    }
}