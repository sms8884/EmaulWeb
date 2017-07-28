package com.jaha.web.emaul.exception;

public class JsonApiException extends RuntimeException {

	/** SID */
	private static final long serialVersionUID = 6291975246784919565L;
	
	private String message;

    JsonApiException() {
        super();
    }

    public JsonApiException(String message) {
        super(message);
        this.message = message;
    }

    public JsonApiException(String message, Throwable cause) {
        super(message, cause);
    }
    
    @Override
    public String getMessage() {
        return message;
    }

}
