package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users")
public class UserController {
    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    private final Map<Integer, User> users = new HashMap<>();

    @GetMapping
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    @PostMapping
    public User create(@Valid @RequestBody User user) throws ValidationException {
        int id = getNextId();
        user = new User(id, user.getEmail(), user.getLogin(), user.getName(), user.getBirthday());
        if (user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            log.warn("Адрес электронной почты некорректен.", user);
            throw new ValidationException("Адрес электронной почты некорректен.");
        }
        if (user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            log.warn("Логин пользователя некорректен.", user);
            throw new ValidationException("Логин пользователя не может быть пустым или содержать пробелы.");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            user = new User(user.getId(), user.getEmail(), user.getLogin(), user.getLogin(), user.getBirthday());
            log.debug("Имя пользователя не указано и подменено логином.", user);
        }
        if (user.getBirthday() == null || user.getBirthday().isAfter(LocalDate.now())) {
            log.warn("Дата рождения пользователя некорректна.", user);
            throw new ValidationException("Дата рождения пользователя некорректна.");
        }

        users.put(user.getId(), user);
        log.debug("Пользователь успешно зарегистрирован.", user);
        return user;
    }

    @PutMapping
    public User put(@Valid @RequestBody User user) throws ValidationException {
        if (!users.containsKey(user.getId())) {
            log.warn("Попытка обновить сведения о незарегистрированном пользователе.", user);
            throw new ValidationException(HttpStatus.INTERNAL_SERVER_ERROR, "Пользователь с заданным идентификатором не зарегистрирован.");
        } else {
            users.put(user.getId(), user);
            log.debug("Сведения о пользователе успешно обновлены.", user);
            return user;
        }
    }

    private int getNextId() {
        List<Integer> list = getAllId();
        int key = 1;
        while (list.contains(key)) {
            key++;
        }
        return key;
    }

    private List<Integer> getAllId() {
        return users.values().stream()
                .map(User::getId)
                .sorted()
                .collect(Collectors.toList());
    }
}
