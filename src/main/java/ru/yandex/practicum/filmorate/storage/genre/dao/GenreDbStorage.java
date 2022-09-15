package ru.yandex.practicum.filmorate.storage.genre.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor
public class GenreDbStorage implements GenreDao {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public boolean containsGenre(int id) {
        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(
                "SELECT count(*) > 0 FROM genres WHERE id=?",
                Boolean.class, id));
    }

    @Override
    public Genre getGenre(int id) {
        SqlRowSet genreRows = jdbcTemplate.queryForRowSet(
                "SELECT * FROM genres WHERE id=?", id);
        if (genreRows.next()) {
            return ofSqlRowSet(genreRows);
        } else {
            log.debug("Жанр фильма с идентификатором {} не найден.", id);
            return null;
        }
    }

    @Override
    public Optional<Genre> findGenreById(int id) {
        SqlRowSet genreRows = jdbcTemplate.queryForRowSet(
                "SELECT * FROM genres WHERE id=?", id);
        if (genreRows.next()) {
            Genre genre = ofSqlRowSet(genreRows);
            log.debug("Найден жанр фильма: {}", genre);
            return Optional.of(genre);
        } else {
            log.debug("Жанр фильма с идентификатором {} не найден.", id);
            return Optional.empty();
        }
    }

    @Override
    public List<Genre> findGenres() {
        return jdbcTemplate.query(
                "SELECT * FROM genres ORDER BY id",
                (rs, rowNum) -> ofResultSet(rs));
    }

    private static Genre ofSqlRowSet(SqlRowSet rs) {
        return new Genre(rs.getInt("id"),
                rs.getString("name"));
    }

    private static Genre ofResultSet(ResultSet rs) throws SQLException {
        return new Genre(rs.getInt("id"),
                rs.getString("name"));
    }
}