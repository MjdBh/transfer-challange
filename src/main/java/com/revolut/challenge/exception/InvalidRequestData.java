package com.revolut.challenge.exception;

public class InvalidRequestData  extends TransferBaseException {
    public InvalidRequestData() {
        super(400, "Invalid body", "Invalid data in request body");
    }
}
