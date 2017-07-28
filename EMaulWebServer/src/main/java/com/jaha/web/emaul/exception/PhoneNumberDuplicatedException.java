package com.jaha.web.emaul.exception;

public class PhoneNumberDuplicatedException extends RuntimeException {

    /** SID */
    private static final long serialVersionUID = -3267069539523604276L;

    private String phoneNumber;
    private String message;

    public PhoneNumberDuplicatedException(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        this.message = String.format("이미 등록된 핸드폰번호 입니다. [%s]", phoneNumber);
    }

    public PhoneNumberDuplicatedException(String phoneNumber, String message) {
        this.phoneNumber = phoneNumber;
        this.message = message;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    @Override
    public String getMessage() {
        return message;
    }

}
