package ru.yandex.practicum.filmorate.storage.user.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor
@Primary
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public User createUser(User user) {
        String sql = "INSERT INTO users (email, login, name, birthday) VALUES(?,?,?,?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement stmt = con.prepareStatement(sql, new String[]{"id"});
            stmt.setString(1, user.getEmail());
            stmt.setString(2, user.getLogin());
            stmt.setString(3, user.getName());
            stmt.setDate(4, Date.valueOf(user.getBirthday()));
            return stmt;
        }, keyHolder);
        long userId = keyHolder.getKey().longValue();
        User createdUser = getUser(userId);
        log.debug("Пользователь {} добавлен в коллекцию.", createdUser);
        return createdUser;
    }

    @Override
    public boolean containsUser(long id) {
        return jdbcTemplate.queryForObject(
                "SELECT count(*) > 0 FROM users WHERE id=?", Boolean.class, id);
    }

    @Override
    public User getUser(long id) {
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("SELECT * FROM users WHERE id=?", id);
        if (userRows.next()) {
            return ofSqlRowSet(userRows);
        } else {
            log.debug("Пользователь с идентификатором {} не найден.", id);
            return null;
        }
    }

    @Override
    public Optional<User> findUserById(long id) {
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("SELECT * FROM users WHERE id=?", id);
        if (userRows.next()) {
            User user = ofSqlRowSet(userRows);
            log.debug("Найден пользователь: {}", user);
            return Optional.of(user);
        } else {
            log.debug("Пользователь с идентификатором {} не найден.", id);
            return Optional.empty();
        }
    }

    @Override
    public User updateUser(User user) {
        jdbcTemplate.update(
                "UPDATE users SET email=?, login=?, name=?, birthday=? WHERE id=?",
                user.getEmail(), user.getLogin(), user.getName(), user.getBirthday(), user.getId());
        log.debug("Сведения о пользователе {} обновлены.", user);
        return user;
    }

    @Override
    public void removeUser(long id) {
        jdbcTemplate.update("DELETE FROM users WHERE id=?", id);
        jdbcTemplate.update("DELETE FROM users_friends WHERE user_id=? OR friend_id=?", id);
        jdbcTemplate.update("DELETE FROM favorite_films WHERE user_id=?", id);
        log.debug("Пользователь {} удален из коллекции.", id);
    }

    @Override
    public List<User> findAll() {
        String sql = "SELECT * FROM users";
        return jdbcTemplate.query(sql, (rs, rowNum) -> ofResultSet(rs));
    }

    @Override
    public void addFriend(long userId, long friendId) {
        jdbcTemplate.update("INSERT INTO users_friends (user_id, friend_id) VALUES(?,?)",
                userId, friendId);
        log.debug("Создана запись о поступлении заявки на дружбу от пользователя {} к пользователю {}.",
                friendId, userId);
    }

    @Override
    public void removeFriend(long userId, long friendId) {
        jdbcTemplate.update(
                "DELETE FROM users_friends WHERE user_id=? AND friend_id=?", userId, friendId);
        log.debug("Запись о заявке от пользователя {} к пользователю {} удалена из коллекции.",
                friendId, userId);
    }

    @Override
    public List<User> getFriends(long userId) {
        String sql = "SELECT U.* FROM users_friends AS UF " +
                "LEFT OUTER JOIN users AS U ON UF.friend_id=U.id " +
                "WHERE UF.user_id=?";
        return jdbcTemplate.query(sql, (rs, rowNum) -> ofResultSet(rs), userId);
    }

    @Override
    public List<User> getCommonFriends(long userId, long otherId) {
        String sql = "SELECT * FROM users WHERE id IN " +
                "(SELECT t.id_2 AS friend_id FROM " +
                "(SELECT UF.user_id AS id_1, UF.friend_id AS id_2 " +
                "FROM users_friends AS UF " +
                "UNION " +
                "SELECT UF.friend_id AS id_1, UF.user_id AS id_2 " +
                "FROM users_friends AS UF " +
                "ORDER BY id_1, id_2) AS t " +
                "WHERE t.id_1 IN (?, ?) " +
                "GROUP BY friend_id " +
                "HAVING count(t.id_2) > 1);";
        return jdbcTemplate.query(sql, (rs, rowNum) -> ofResultSet(rs), userId, otherId);
    }

    private User ofSqlRowSet(SqlRowSet rs) {
        User user = new User(rs.getLong("id"),
                rs.getString("email"),
                rs.getString("login"),
                rs.getString("name"),
                rs.getDate("birthday").toLocalDate());
        return user;
    }

    private User ofResultSet(ResultSet rs) throws SQLException {
        User user = new User(rs.getLong("id"),
                rs.getString("email"),
                rs.getString("login"),
                rs.getString("name"),
                rs.getDate("birthday").toLocalDate());
        return user;
    }
}
