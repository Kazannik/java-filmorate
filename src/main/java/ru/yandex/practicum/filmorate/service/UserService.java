package ru.yandex.practicum.filmorate.service;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.model.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserService {
    private final UserStorage storage = new InMemoryUserStorage();

    public User create(@Valid @RequestBody User user) throws ValidationException {
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
        return this.storage.createUser(user);
    }

    public User getUser(long id) {
        if (!this.storage.containsUser(id)) {
            log.warn("Попытка доступа к отсутствующему пользователю ({}).", id);
            throw new NotFoundException(String.format("Пользователь (%s) отсутствует в коллекции.", id));
        } else {
            return this.storage.getUser(id);
        }
    }

    public User updateUser(@Valid @NonNull @RequestBody User user) throws ValidationException {
        if (!this.storage.containsUser(user.getId())) {
            log.warn("Попытка обновить сведения об отсутствующем пользователе ({}).", user);
            throw new NotFoundException(String.format("Пользователь (%s) отсутствует в коллекции.", user.getId()));
        } else {
            log.debug("Сведения о пользователе ({}) успешно обновлены.", user);
            return this.storage.updateUser(user);
        }
    }

    public List<User> findAll() {
        return this.storage.findAll();
    }

    public void addFriend(long userId, long friendId) {
        User user = getUser(userId);
        User friend = getUser(friendId);
        user.getFriends().add(friend.getId());
        friend.getFriends().add(user.getId());
        log.debug("Пользователю ({}) добавлен друг ({})", userId, friendId);
    }

    public void removeFriend(long userId, long friendId) {
        User user = getUser(userId);
        User friend = getUser(friendId);
        if (user.getFriends().remove(friend.getId()) && friend.getFriends().remove(user.getId())) {
            log.debug("Пользователь ({}) прекратил дружбу с пользователем ({})", userId, friendId);
        } else {
            log.warn("Пользователь ({}) не являлся другом пользователю ({})", userId, friendId);
            throw new NotFoundException(String.format("Пользователь (%s) не является другом пользователю (%s)", userId, friendId));
        }
    }

    public List<User> getFriends(long userId) {
        User user = getUser(userId);
        return user.getFriends().stream()
                .map(storage::getUser)
                .collect(Collectors.toList());
    }

    public List<User> getCommonFriends(long userId, long otherId) {
        User user = getUser(userId);
        return user.getFriends().stream()
                .map(storage::getUser)
                .filter(o1 -> o1.getFriends().contains(otherId))
                .collect(Collectors.toList());
    }
}