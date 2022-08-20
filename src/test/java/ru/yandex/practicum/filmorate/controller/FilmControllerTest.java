package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class FilmControllerTest {
    @Autowired
    FilmController controller;

    @Test
    void releaseDateTest() {
        final ValidationException filmReleaseDateValidationException = assertThrows(ValidationException.class,
                () -> controller.create(new Film(0,"Название фильма", "Описание фильма",
                        LocalDate.of(1895,12,27),45)));
        Assertions.assertEquals("Дата релиза фильма не раньше 28 декабря 1895 года.",
                filmReleaseDateValidationException.getMessage());
    }
}