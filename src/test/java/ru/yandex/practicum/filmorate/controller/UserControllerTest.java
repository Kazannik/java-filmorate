package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class UserControllerTest {
    @Autowired
    UserController controller;

    @Test
    void createUserWithNullEmailTest() {
        final NullPointerException nullEmailException = assertThrows(NullPointerException.class,
                () -> controller.create(new User(0L, null, "Логин", "Имя пользователя",
                        LocalDate.of(2022, 7, 31))));
        Assertions.assertEquals("email is marked non-null but is null", nullEmailException.getMessage());
    }

    @Test
    void createUserWithNullLoginTest() {
        final NullPointerException nullLoginException = assertThrows(NullPointerException.class,
                () -> controller.create(new User(0L, "kazannik.m@mail.ru", null, "Имя пользователя",
                        LocalDate.of(2022, 7, 31))));
        Assertions.assertEquals("login is marked non-null but is null", nullLoginException.getMessage());
    }

    @Test
    void createUserWithBadEmailTest() {
        final ValidationException emailValidationException = assertThrows(ValidationException.class,
                () -> controller.create(new User(0L, "kazannik.mAmail.ru", "Логин", "Имя пользователя",
                        LocalDate.of(2022, 7, 31))));
        Assertions.assertEquals("Адрес электронной почты некорректен.", emailValidationException.getMessage());
    }

    @Test
    void createUserWithBadLoginTest() {
        final ValidationException loginValidationException = assertThrows(ValidationException.class,
                () -> controller.create(new User(0L, "kazannik.m@mail.ru", "Логин пользователя", "Имя пользователя",
                        LocalDate.of(2022, 7, 31))));
        Assertions.assertEquals("Логин пользователя не может быть пустым или содержать пробелы.", loginValidationException.getMessage());
    }

    @Test
    void createUserWithNullNameTest() {
        controller.create(new User(0L, "kazannik.m@mail.ru", "Логин", null,
                LocalDate.of(2022, 7, 31)));
        Assertions.assertEquals(controller.findAll().get(0).getName(), controller.findAll().get(0).getLogin());
    }

    @Test
    void createUserWithBadBirthdayTest() {
        final ValidationException birthdateValidationException = assertThrows(ValidationException.class,
                () -> controller.create(new User(0L, "kazannik.m@mail.ru", "Логин", "Имя пользователя",
                        LocalDate.now().plusDays(1))));
        Assertions.assertEquals("Дата рождения пользователя некорректна.", birthdateValidationException.getMessage());
    }

    @Test
    void createUserTest() {
        try {
            controller.create(new User(1L, "kazannik.m@mail.ru", "Логин", "Имя пользователя", LocalDate.now()));
        } catch (ValidationException ex) {
            assertNull(ex);
        }
    }

    @Test
    void putUserNotFoundTest() {
        final NotFoundException putNewUserValidationException = assertThrows(NotFoundException.class,
                () -> controller.put(new User(99L, "kazannik.m@mail.ru", "Логин", "Имя пользователя",
                        LocalDate.of(2022, 7, 31))));
        Assertions.assertEquals("Пользователь (99) отсутствует в коллекции.",
                putNewUserValidationException.getMessage());
    }
}