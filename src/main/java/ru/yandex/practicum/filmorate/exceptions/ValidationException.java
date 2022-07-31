package ru.yandex.practicum.filmorate.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class ValidationException extends ResponseStatusException {
    public ValidationException(final String message) {
        super(HttpStatus.BAD_REQUEST);
        this.message=message;
    }

    private final String message;

    @Override
    public String getMessage(){
        return message;
    }

}
