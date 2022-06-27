package ru.yandex.practicum.filmorate.controller.error;

public class ErrorResponse {
    private String message;

    public ErrorResponse(String message) {
        this.message = message;
    }

    public String getError() {
        return message;
    }
}
