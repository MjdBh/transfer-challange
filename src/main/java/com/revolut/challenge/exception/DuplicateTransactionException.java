package com.revolut.challenge.exception;

public class DuplicateTransactionException extends TransferBaseException {
       public DuplicateTransactionException()

    {
        super(409, "Transaction id is duplicated", "duplicate transaction");
    }
}

