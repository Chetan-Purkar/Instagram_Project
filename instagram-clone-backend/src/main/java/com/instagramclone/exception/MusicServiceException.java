package com.instagramclone.exception;
@SuppressWarnings("serial")
public class MusicServiceException extends RuntimeException {
    public MusicServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}