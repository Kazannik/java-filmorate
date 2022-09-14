package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Repository()
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();
    private final Map<Long, List<Long>> friends = new HashMap<>();
    private long key = 1;

    private long nextIdGenerator() {
        return key++;
    }

    @Override
    public User createUser(User user) {
        long id = nextIdGenerator();
        user = new User(id, user.getEmail(), user.getLogin(), user.getName(), user.getBirthday());
        friends.put(id, new ArrayList<>());
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
    public Optional<User> findUserById(long id) {
        User user = users.get(id);
        if (user != null) {
            log.debug("Найден пользователь: {}", user);
            return Optional.of(user);
        } else {
            log.debug("Пользователь с идентификатором {} не найден.", id);
            return Optional.empty();
        }
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

    @Override
    public void addFriend(long userId, long friendId) {
        if (!friends.get(userId).contains(friendId)) {
            friends.get(userId).add(friendId);
        }
    }

    @Override
    public void removeFriend(long userId, long friendId) {
        if (!friends.get(userId).contains(friendId)) {
            friends.get(userId).remove(friendId);
        }
    }


    @Override
    public List<User> getFriends(long userId) {
        return friends.get(userId).stream()
                .map(this::getUser)
                .collect(Collectors.toList());
    }

    @Override
    public List<User> getCommonFriends(long userId, long otherId) {
        return friends.get(userId).stream()
                .map(this::getUser)
                .filter(user -> friends.get(userId).contains(otherId))
                .collect(Collectors.toList());
    }
}