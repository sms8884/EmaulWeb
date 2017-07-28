package com.jaha.web.emaul.exception;

public class EmaulWebException extends RuntimeException {

	/** SID */
	private static final long serialVersionUID = 5759616166853592425L;
	
	private String message;

	EmaulWebException() {
        super();
    }

    public EmaulWebException(String message) {
        super(message);
        this.message = message;
    }

    public EmaulWebException(String message, Throwable cause) {
        super(message, cause);
    }
    
    @Override
    public String getMessage() {
        return message;
    }

}
