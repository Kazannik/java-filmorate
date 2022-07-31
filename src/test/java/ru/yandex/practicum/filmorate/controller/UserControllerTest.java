package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class UserControllerTest {
    static UserController controller;

    @BeforeAll
    public static void beforeAll() {
        controller = new UserController();
    }

    @Test
    void create() throws ValidationException {
        final NullPointerException nullEmailException = assertThrows(NullPointerException.class,
                () -> controller.create(new User(0, null, "Логин", "Имя пользователя",
                        LocalDate.of(2022, 7, 31))));
        Assertions.assertEquals("email is marked non-null but is null", nullEmailException.getMessage());

        final NullPointerException nullLoginException = assertThrows(NullPointerException.class,
                () -> controller.create(new User(0, "kazannik.m@mail.ru", null, "Имя пользователя",
                        LocalDate.of(2022, 7, 31))));
        Assertions.assertEquals("login is marked non-null but is null", nullLoginException.getMessage());

        final ValidationException emailValidationException = assertThrows(ValidationException.class,
                () -> controller.create(new User(0, "kazannik.mAmail.ru", "Логин", "Имя пользователя",
                        LocalDate.of(2022, 7, 31))));
        Assertions.assertEquals("Адрес электронной почты некорректен.", emailValidationException.getMessage());

        final ValidationException loginValidationException = assertThrows(ValidationException.class,
                () -> controller.create(new User(0, "kazannik.m@mail.ru", "Логин пользователя", "Имя пользователя",
                        LocalDate.of(2022, 7, 31))));
        Assertions.assertEquals("Логин пользователя не может быть пустым или содержать пробелы.", loginValidationException.getMessage());

        controller.create(new User(0, "kazannik.m@mail.ru", "Логин", null,
                LocalDate.of(2022, 07, 31)));
        Assertions.assertEquals(controller.findAll().get(0).getName(), controller.findAll().get(0).getLogin());

        final ValidationException birthdateValidationException = assertThrows(ValidationException.class,
                () -> controller.create(new User(0, "kazannik.m@mail.ru", "Логин", "Имя пользователя",
                        LocalDate.now().plusDays(1))));
        Assertions.assertEquals("Дата рождения пользователя некорректна.", birthdateValidationException.getMessage());

        try {
            controller.create(new User(1, "kazannik.m@mail.ru", "Логин", "Имя пользователя", LocalDate.now()));
        } catch (ValidationException ex) {
            assertNull(ex);
        }
    }

    @Test
    void put() {
        final ValidationException putNewUserValidationException = assertThrows(ValidationException.class,
                () -> controller.put(new User(3, "kazannik.m@mail.ru", "Логин", "Имя пользователя",
                        LocalDate.of(2022, 7, 31))));
        Assertions.assertEquals("Пользователь с заданным идентификатором не зарегистрирован.",
                putNewUserValidationException.getMessage());
    }
}