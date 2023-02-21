package com.ttech.aws.plugin.s3p.controller;

public class ApiException extends Exception{

    public ApiException(String message, ApiException e) {
        super(message, e);
    }
}
