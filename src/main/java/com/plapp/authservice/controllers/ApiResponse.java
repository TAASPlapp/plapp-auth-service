package com.plapp.authservice.controllers;

public class ApiResponse {
    private Boolean success;
    private String message;

    public ApiResponse(Boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public ApiResponse(Boolean success) {
        this(success, null);
    }

    public ApiResponse() {
        this(true);
    }


    public Boolean getSuccess() { return success; }
    public String getMessage() { return message; }
}
