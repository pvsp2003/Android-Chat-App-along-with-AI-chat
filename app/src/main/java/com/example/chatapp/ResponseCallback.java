package com.example.chatapp;
public interface ResponseCallback {
    void onResponse(String response);
    void onError(Throwable throwable);
}