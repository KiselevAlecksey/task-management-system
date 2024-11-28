package ru.tms.exception;

public class InvalidJWTClaimsException extends RuntimeException {
    public InvalidJWTClaimsException(String message) {
        super(message);
    }
}
