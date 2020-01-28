package com.revolut.challenge.exception;

public class InsufficientBalanceException extends TransferBaseException {

    public InsufficientBalanceException() {
        super(400, "Balance is insufficient", "Insufficient Balance");
    }


}
