package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class FilmControllerTest {
    @Autowired
    FilmController controller;

    @Test
    void createFilmWithNullNameTest() {
        final NullPointerException nullFilmNameException = assertThrows(NullPointerException.class,
                () -> controller.create(new Film(0, null, "Описание фильма",
                        LocalDate.of(2022, 7, 31), 45)));
        Assertions.assertEquals("name is marked non-null but is null", nullFilmNameException.getMessage());
    }

    @Test
    void createFilmWithEmptyNameTest() {
        final ValidationException filmNameValidationException = assertThrows(ValidationException.class,
                () -> controller.create(new Film(0, " ", "Описание фильма",
                        LocalDate.of(2022, 7, 31), 45)));
        Assertions.assertEquals("Название не может быть пустым.", filmNameValidationException.getMessage());
    }

    @Test
    void createFilmWithBigDescriptionTest() {
        final ValidationException filmDescriptionValidationException = assertThrows(ValidationException.class,
                () -> controller.create(new Film(0, "Название фильма", new String(new char[201]),
                        LocalDate.of(2022, 7, 31), 45)));
        Assertions.assertEquals("Максимальная длина описания фильма не может превышать 200 символов.",
                filmDescriptionValidationException.getMessage());
    }

    @Test
    void createFilmWithBadReleaseDateTest() {
        final ValidationException filmReleaseDateValidationException = assertThrows(ValidationException.class,
                () -> controller.create(new Film(0, "Название фильма", "Описание фильма",
                        LocalDate.of(1895, 12, 27), 45)));
        Assertions.assertEquals("Дата релиза фильма не раньше 28 декабря 1895 года.",
                filmReleaseDateValidationException.getMessage());
    }

    @Test
    void createFilmWithReleaseDateTest() {
        try {
            controller.create(new Film(0, "Название фильма", "Описание фильма",
                    LocalDate.of(1895, 12, 28), 45));
        } catch (ValidationException ex) {
            assertNull(ex);
        }
    }

    @Test
    void createFilmWithDurationTest() {
        final ValidationException filmDurationValidationException = assertThrows(ValidationException.class,
                () -> controller.create(new Film(0, "Название фильма", "Описание фильма",
                        LocalDate.of(2022, 7, 31), 0)));
        Assertions.assertEquals("Продолжительность фильма должна быть положительной.",
                filmDurationValidationException.getMessage());
    }

    @Test
    void createFilmTest() {
        try {
            controller.create(new Film(1, "Название фильма", "Описание фильма",
                    LocalDate.of(2022, 7, 31), 1));
        } catch (ValidationException ex) {
            assertNull(ex);
        }
    }

    @Test
    void putFilmNotFoundTest() {
        final NotFoundException putNewFilmValidationException = assertThrows(NotFoundException.class,
                () -> controller.put(new Film(3, "Название фильма", "Описание фильма",
                        LocalDate.of(2022, 7, 31), 45)));
        Assertions.assertEquals("Фильм (3) отсутствует в коллекции.",
                putNewFilmValidationException.getMessage());
    }
}