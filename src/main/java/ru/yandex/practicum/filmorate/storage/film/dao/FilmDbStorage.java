package ru.yandex.practicum.filmorate.storage.film.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.genre.dao.GenreDao;
import ru.yandex.practicum.filmorate.storage.mpa.dao.MpaDao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Repository()
@RequiredArgsConstructor
@Primary
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    private final GenreDao genreDao;
    private final MpaDao mpaDao;

    @Override
    public Film createFilm(Film film) {
        jdbcTemplate.update(
                "INSERT INTO films (name, description, releaseDate, duration, mpa_id, rate) VALUES(?,?,?,?,?,?)",
                film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(), film.getMpa().getId(),
                film.getRate());
        long filmId = jdbcTemplate.queryForObject("SELECT max(id) FROM films", Long.class);
        filmGenresUpdate(filmId, film.getGenres());
        Film createdFilm = getFilm(filmId);
        log.debug("Фильм {} добавлен в коллекцию.", createdFilm);
        return createdFilm;
    }

    @Override
    public boolean containsFilm(long id) {
        return jdbcTemplate.queryForObject(
                "SELECT count(*) > 0 FROM films WHERE id=?", Boolean.class, id);
    }

    @Override
    public Film getFilm(long id) {
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet(
                "SELECT * FROM films WHERE id=?", id);
        if (filmRows.next()) {
            Film film = filmOfSqlRowSet(filmRows);
            film.getGenres().addAll(getFilmGenresIdCollection(film.getId()).stream()
                    .map(genreDao::getGenre).collect(Collectors.toList()));
            return film;
        } else {
            log.debug("Фильм с идентификатором {} не найден.", id);
            return null;
        }
    }

    @Override
    public Optional<Film> findFilmById(long id) {
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet(
                "SELECT * FROM films WHERE id=?", id);
        if (filmRows.next()) {
            Film film = filmOfSqlRowSet(filmRows);
            log.debug("Найден фильм: {}", film);
            return Optional.of(film);
        } else {
            log.debug("Фильм с идентификатором {} не найден.", id);
            return Optional.empty();
        }
    }

    @Override
    public Film updateFilm(Film film) {
        jdbcTemplate.update(
                "UPDATE films SET name=?, description=?, releaseDate=?, duration=?, mpa_id=?, rate=? WHERE id=?",
                film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(), film.getMpa().getId(),
                film.getRate(), film.getId());
        filmGenresUpdate(film.getId(), film.getGenres());
        log.debug("Сведения о фильме {} обновлены.", film);
        return getFilm(film.getId());
    }

    @Override
    public void removeFilm(long id) {
        jdbcTemplate.update("DELETE FROM films WHERE id=?", id);
        jdbcTemplate.update("DELETE FROM favorite_films WHERE film_id=?", id);
        jdbcTemplate.update("DELETE FROM films_generes WHERE film_id=?", id);
        log.debug("Фильм {} удален из коллекции.", id);
    }

    @Override
    public List<Film> findAll() {
        return jdbcTemplate.query(
                "SELECT * FROM films ORDER BY id",
                (rs, rowNum) -> filmOfResultSet(rs));
    }

    @Override
    public void addLike(long filmId, long userId) {
        jdbcTemplate.update(
                "INSERT INTO favorite_films (film_id, user_id) VALUES(?,?)", filmId, userId);
        log.debug("Создана запись об отметке фильму {} пользователем {}.", filmId, userId);
    }

    @Override
    public void removeLike(long filmId, long userId) {
        jdbcTemplate.update(
                "DELETE FROM favorite_films WHERE film_id=? AND user_id=?", filmId, userId);
        log.debug("Удалена запись об отметке фильму {} пользователем {}.", filmId, userId);
    }

    private void filmGenresUpdate(long filmId, List<Genre> genres) {
        jdbcTemplate.update(
                "DELETE FROM films_genres WHERE film_id=?", filmId);
        if (genres != null) {
            for (Genre genre : genres) {
                jdbcTemplate.update(
                        "INSERT INTO films_genres (film_id, genre_id) VALUES(?,?)", filmId, genre.getId());
            }
        }
    }

    @Override
    public List<Film> getPopular(int count) {
        return jdbcTemplate.query(
                "SELECT F.* FROM films AS F " +
                        "LEFT OUTER JOIN favorite_films AS FF ON F.id = FF.film_id " +
                        "GROUP BY F.id " +
                        "ORDER BY Count(FF.film_id) DESC " +
                        "LIMIT ?",
                (rs, rowNum) -> filmOfResultSet(rs), count);
    }

    private List<Integer> getFilmGenresIdCollection(long filmId) {
        return jdbcTemplate.queryForList(
                "SELECT DISTINCT genre_id FROM films_genres WHERE film_id=? ORDER BY genre_id",
                Integer.class, filmId);
    }

    private Film filmOfSqlRowSet(SqlRowSet rs) {
        Film film = new Film(rs.getLong("id"),
                rs.getString("name"),
                rs.getDate("releaseDate").toLocalDate(),
                rs.getString("description"),
                rs.getInt("duration"),
                rs.getInt("rate"),
                mpaDao.getMpa(rs.getInt("mpa_id")),
                new ArrayList<>());
        return film;
    }

    private Film filmOfResultSet(ResultSet rs) throws SQLException {
        long filmId = rs.getLong("id");
        Film film = new Film(filmId,
                rs.getString("name"),
                rs.getDate("releaseDate").toLocalDate(),
                rs.getString("description"),
                rs.getInt("duration"),
                rs.getInt("rate"),
                mpaDao.getMpa(rs.getInt("mpa_id")),
                getFilmGenresIdCollection(filmId).stream()
                        .map(genreDao::getGenre)
                        .collect(Collectors.toList()));
        return film;
    }
}