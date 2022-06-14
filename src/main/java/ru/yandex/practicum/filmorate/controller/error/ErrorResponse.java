package ru.yandex.practicum.filmorate.controller;

public class ErrorResponse {
    private String message;

    public ErrorResponse(String message) {
        this.message = message;
    }

    public String getError() {
        return message;
    }
}
