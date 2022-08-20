package ru.yandex.practicum.filmorate.model.exceptions;

public class NotFoundException extends RuntimeException {
    public NotFoundException(final String message) {
        super(message);
    }
}
