package ru.yandex.practicum.filmorate.model.exceptions;

public class ValidationException extends RuntimeException {
    public ValidationException(final String message) {
        super(message);
    }
}

