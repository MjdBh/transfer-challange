package com.revolut.challenge.exception;

public class TransferBaseException extends RuntimeException {

    private int status=500;
    private String message="Transfer exception";
    private String title="General Detail";

    public TransferBaseException(int status, String message, String title) {
        super(message);
        this.status = status;
        this.message = message;
        this.title = title;
    }

    public TransferBaseException() {
    }

    public int getStatus() {
        return status;
    }

    public ExceptionDetail getDetail(){
        return new ExceptionDetail(status,message,title);
    }


}
