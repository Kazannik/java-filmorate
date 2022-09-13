package ru.yandex.practicum.filmorate.service;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(@Qualifier("UserDbStorage") UserStorage storage) {
        this.userStorage = storage;
    }

    public User create(@Valid @RequestBody User user) throws ValidationException {
        if (user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            log.warn("Адрес электронной почты некорректен ({}).", user);
            throw new ValidationException("Адрес электронной почты некорректен.");
        }
        if (user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            log.warn("Логин пользователя некорректен ({}).", user);
            throw new ValidationException("Логин пользователя не может быть пустым или содержать пробелы.");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            user = new User(user.getId(), user.getEmail(), user.getLogin(), user.getLogin(), user.getBirthday());
            log.debug("Имя пользователя не указано и подменено логином ({}).", user);
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            log.warn("Дата рождения пользователя некорректна ({}).", user);
            throw new ValidationException("Дата рождения пользователя некорректна.");
        }
        return userStorage.createUser(user);
    }

    public User getUser(long id) {
        if (!userStorage.containsUser(id)) {
            log.warn("Попытка доступа к отсутствующему пользователю ({}).", id);
            throw new NotFoundException(String.format("Пользователь (%s) отсутствует в коллекции.", id));
        } else {
            return userStorage.getUser(id);
        }
    }

    public User updateUser(@Valid @NonNull @RequestBody User user) throws ValidationException {
        if (!userStorage.containsUser(user.getId())) {
            log.warn("Попытка обновить сведения об отсутствующем пользователе ({}).", user);
            throw new NotFoundException(String.format("Пользователь (%s) отсутствует в коллекции.", user.getId()));
        } else {
            return userStorage.updateUser(user);
        }
    }

    public List<User> findAll() {
        return userStorage.findAll();
    }

    public void addFriend(long userId, long friendId) {
        if (!userStorage.containsUser(userId)) {
            log.warn("Попытка добавить друзей пользователю с недействительным индексом ({}).", userId);
            throw new NotFoundException(String.format("Пользователь с индексом (%s) отсутствует в коллекции.", friendId));
        } else if (!userStorage.containsUser(friendId)) {
            log.warn("Попытка добавить в друзья пользователя с недействительным индексом ({}).", friendId);
            throw new NotFoundException(String.format("Пользователь с индексом (%s) отсутствует в коллекции.", friendId));
        }
        else {
            userStorage.addFriend(userId, friendId);
            log.debug("Пользователь ({}) оставил заявку пользователю ({}).", userId, friendId);
        }
    }

    public void removeFriend(long userId, long friendId) {
        if (!userStorage.containsUser(userId)) {
            log.warn("Попытка удалить друзей у пользователя с недействительным индексом ({}).", userId);
            throw new NotFoundException(String.format("Пользователь с индексом (%s) отсутствует в коллекции.", friendId));
        } else if (!userStorage.containsUser(friendId)) {
            log.warn("Попытка удалить из друзей пользователя с недействительным индексом ({}).", friendId);
            throw new NotFoundException(String.format("Пользователь с индексом (%s) отсутствует в коллекции.", friendId));
        }
        else {
            userStorage.removeFriend(userId, friendId);
            log.debug("Пользователь ({}) прекратил дружбу с пользователем ({})", userId, friendId);
        }
    }

    public List<User> getFriends(long userId) {
        return userStorage.getFriends(userId);
    }

    public List<User> getCommonFriends(long userId, long otherId) {
        return userStorage.getCommonFriends(userId, otherId);
    }
}