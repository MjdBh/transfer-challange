package com.revolut.challenge.exception;

public class SameAccountException extends TransferBaseException {
    public SameAccountException() {
        super(400, "Transfer is invalid for same account", "Bad input data");
    }
}
