package com.gmail.subnokoii78.gpcore.database.sqlite;

public class SqliteDatabaseException extends RuntimeException {
    protected SqliteDatabaseException(String message, Throwable cause) {
        super(message, cause);
    }

    protected SqliteDatabaseException(String message) {
        super(message);
    }
}
