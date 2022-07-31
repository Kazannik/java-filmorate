package ru.yandex.practicum.filmorate.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class ValidationException extends ResponseStatusException {
    public ValidationException(final String message) {
        this(HttpStatus.BAD_REQUEST, message);
    }

    public ValidationException(final HttpStatus status, final String message) {
        super(status);
        this.message=message;
    }

    private final String message;

    @Override
    public String getMessage(){
        return message;
    }

}
