package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;

public interface UserStorage {

    User createUser(User user);

    boolean containsUser(long id);

    User getUser(long id);

    Optional<User> findUserById(long id);

    User updateUser(User user);

    void removeUser(long id);

    List<User> findAll();

    void addFriend(long userId, long friendId);

    void removeFriend(long userId, long friendId);

    List<User> getFriends(long userId);

    List<User> getCommonFriends(long userId, long otherId);
}
