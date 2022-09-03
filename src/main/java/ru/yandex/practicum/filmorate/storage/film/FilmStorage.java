package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {
    Film createFilm(Film film);

    boolean containsFilm(long id);

    Film getFilm(long id);

    Film updateFilm(Film film);

    void removeFilm(long id);

    List<Film> findAll();
}
