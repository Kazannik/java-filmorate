package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {

    User createUser(User user);

    boolean containsUser(long id);

    User getUser(long id);

    User updateUser(User user);

    void removeUser(long id);

    List<User> findAll();
}
