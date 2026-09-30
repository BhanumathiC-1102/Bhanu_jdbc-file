package com.hospital.util;

/** Unchecked wrapper so servlets do not need try/catch around every JDBC call. */
public class DataAccessException extends RuntimeException {
    public DataAccessException(String message, Throwable cause) {
        super(message, cause);
    }
}
