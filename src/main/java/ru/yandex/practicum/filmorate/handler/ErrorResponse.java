package ru.yandex.practicum.filmorate.handler;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ErrorResponse {
    private final String description;
    private final String message;
}
