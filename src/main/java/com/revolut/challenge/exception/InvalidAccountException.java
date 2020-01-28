package com.revolut.challenge.exception;

public class InvalidAccountException extends TransferBaseException {
    public InvalidAccountException() {
        super(400, "Invalid Account", "Invalid data");
    }

    public InvalidAccountException(String accountNumber) {
        super(400, "Invalid Account number "+accountNumber, "Invalid data");
    }

}
