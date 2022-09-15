package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Optional;

public interface FilmStorage {
    Film createFilm(Film film);

    boolean containsFilm(long id);

    Film getFilm(long id);

    Optional<Film> findFilmById(long id);

    Film updateFilm(Film film);

    void removeFilm(long id);

    List<Film> findAll();

    void addLike(long filmId, long userId);

    void removeLike(long filmId, long userId);

    List<Film> getPopular(int count);

}
