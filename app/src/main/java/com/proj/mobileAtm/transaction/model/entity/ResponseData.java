package com.proj.mobileAtm.transaction.model.entity;

import lombok.Data;

@Data
public class ResponseData <T>{

    boolean success;
    String message;
    T data;

    public ResponseData(boolean success, String message) {
        this.success = success;
        this.message = message;
    }
}
