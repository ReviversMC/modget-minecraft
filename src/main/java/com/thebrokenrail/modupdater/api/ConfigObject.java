package com.thebrokenrail.modupdater.api;

public interface ConfigObject {
    String getString(String str) throws MissingValueException;

    int getInt(String str) throws MissingValueException;

    boolean getBoolean(String str) throws MissingValueException;

    class MissingValueException extends Exception {
        private static final String MISSING_MSG = "Missing Configuration Property: %s";
        private static final String INVALID_MSG = "Invalid Configuration Property: %s";

        public MissingValueException(boolean invalid, String property) {
            super(String.format(invalid ? INVALID_MSG : MISSING_MSG, property));
        }
    }
}
