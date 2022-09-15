package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.time.LocalDate;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class FilmControllerTest {
    @Autowired
    FilmController controller;

    @Test
    void createFilmWithNullNameTest() {
        final NullPointerException nullFilmNameException = assertThrows(NullPointerException.class,
                () -> controller.create(new Film(0L, null, LocalDate.of(2022, 7, 31),
                        "Описание фильма",45,1, new Mpa(1), new ArrayList<>())));
        Assertions.assertEquals("name is marked non-null but is null", nullFilmNameException.getMessage());
    }

    @Test
    void createFilmWithEmptyNameTest() {
        final ValidationException filmNameValidationException = assertThrows(ValidationException.class,
                () -> controller.create(new Film(0L, " ", LocalDate.of(2022, 7, 31),
                        "Описание фильма", 45,1, new Mpa(1), new ArrayList<>())));
        Assertions.assertEquals("Название не может быть пустым.", filmNameValidationException.getMessage());
    }

    @Test
    void createFilmWithBigDescriptionTest() {
        final ValidationException filmDescriptionValidationException = assertThrows(ValidationException.class,
                () -> controller.create(new Film(0L, "Название фильма",
                        LocalDate.of(2022, 7, 31),new String(new char[201]),
                         45,1, new Mpa(1), new ArrayList<>())));
        Assertions.assertEquals("Максимальная длина описания фильма не может превышать 200 символов.",
                filmDescriptionValidationException.getMessage());
    }

    @Test
    void createFilmWithBadReleaseDateTest() {
        final ValidationException filmReleaseDateValidationException = assertThrows(ValidationException.class,
                () -> controller.create(new Film(0L, "Название фильма",
                        LocalDate.of(1895, 12, 27),"Описание фильма",
                         45,1, new Mpa(1), new ArrayList<>())));
        Assertions.assertEquals("Дата релиза фильма не раньше 28 декабря 1895 года.",
                filmReleaseDateValidationException.getMessage());
    }

    @Test
    void createFilmWithReleaseDateTest() {
        try {
            controller.create(new Film(0L, "Название фильма", LocalDate.of(1895, 12, 28),
                    "Описание фильма",45,1, new Mpa(1), new ArrayList<>()));
        } catch (ValidationException ex) {
            assertNull(ex);
        }
    }

    @Test
    void createFilmWithDurationTest() {
        final ValidationException filmDurationValidationException = assertThrows(ValidationException.class,
                () -> controller.create(new Film(0L, "Название фильма",
                        LocalDate.of(2022, 7, 31),"Описание фильма",
                         0,1, new Mpa(1), new ArrayList<>())));
        Assertions.assertEquals("Продолжительность фильма должна быть положительной.",
                filmDurationValidationException.getMessage());
    }

    @Test
    void createFilmTest() {
        try {
            controller.create(new Film(1L, "Название фильма", LocalDate.of(2022, 7, 31),
                    "Описание фильма", 1,1, new Mpa(1), new ArrayList<>()));
        } catch (ValidationException ex) {
            assertNull(ex);
        }
    }

    @Test
    void putFilmNotFoundTest() {
        final NotFoundException putNewFilmValidationException = assertThrows(NotFoundException.class,
                () -> controller.put(new Film(3L, "Название фильма",
                        LocalDate.of(2022, 7, 31), "Описание фильма",
                        45,1, new Mpa(1), new ArrayList<>())));
        Assertions.assertEquals("Фильм (3) отсутствует в коллекции.",
                putNewFilmValidationException.getMessage());
    }
}