package ru.tms.exception;

public class UnsupportedRoleException extends IllegalArgumentException {
    public UnsupportedRoleException(String message) {
        super(message);
    }
}
