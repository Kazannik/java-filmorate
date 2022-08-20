package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();
    private static long key = 1;

    private static long nextIdGenerator() {
        return key++;
    }

    @Override
    public User createUser(User user) {
        long id = nextIdGenerator();
        user = new User(id, user.getEmail(), user.getLogin(), user.getName(), user.getBirthday());
        users.put(user.getId(), user);
        log.debug("Пользователь {} добавлен в коллекцию.", user);
        return user;
    }

    @Override
    public boolean containsUser(long id) {
        return users.containsKey(id);
    }

    @Override
    public User getUser(long id) {
        return users.get(id);
    }

    @Override
    public User updateUser(User user) {
        users.put(user.getId(), user);
        log.debug("Сведения о пользователе {} обновлены.", user);
        return user;
    }

    @Override
    public void removeUser(long id) {
        users.remove(id);
        log.debug("Пользователь {} удален из коллекции.", id);
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }
}
