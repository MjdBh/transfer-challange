package com.revolut.challenge.exception;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class ExceptionDetail {

    @JsonIgnore
    private int status;
    private String title;
    private String message;


    public ExceptionDetail(int status, String message, String title) {
        this.status = status;
        this.message = message;
        this.title = title;
    }

    public ExceptionDetail() {
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
