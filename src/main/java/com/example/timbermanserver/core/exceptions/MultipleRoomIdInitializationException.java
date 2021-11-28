package com.example.timbermanserver.core.exceptions;

public class MultipleRoomIdInitializationException extends Exception {

    public MultipleRoomIdInitializationException() {}

    @Override
    public String getMessage() {
        return "Room id cannot be initialized twice";
    }
}
