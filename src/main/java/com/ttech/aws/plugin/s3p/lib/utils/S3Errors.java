package com.ttech.aws.plugin.s3p.lib.utils;

import com.cloudops.mc.plugin.sdk.error.ErrorCode;

public enum S3Errors implements ErrorCode {
    CONNECTION_TEST_FAILED("The connection test failed"),
    MISSING_URL_PARAMETER("URL parameter cannot be empty"),
    MISSING_ACCESS_ID_PARAMETER("Access ID parameter cannot be empty"),
    MISSING_SECRET_KEY_PARAMETER("Secret Key paramter cannot be empty"),
    ENVIRONMENT_NAME_NOT_UNIQUE("The name you entered for the environment already exists as a bucket in S3"),
    URL_NOT_VALID("The URL you entered is nto a valid file URL");

    private String message;

    S3Errors(String message){
        this.message = message;
    }

    @Override
    public String message() {
        return this.message;
    }
}
