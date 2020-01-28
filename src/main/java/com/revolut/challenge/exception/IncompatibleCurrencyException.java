package com.revolut.challenge.exception;

public class IncompatibleCurrencyException extends TransferBaseException {

    public IncompatibleCurrencyException() {
        super(400, "Currency of accounts is incompatible", "Incompatible Currency");
    }
}
