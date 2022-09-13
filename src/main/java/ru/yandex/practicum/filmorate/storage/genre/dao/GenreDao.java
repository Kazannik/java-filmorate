package ru.yandex.practicum.filmorate.storage.genre.dao;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Optional;

public interface GenreDao {

    boolean containsGenre(int id);

    Genre getGenre(int id);

    Optional<Genre> findGenreById(int id);

    List<Genre> findGenres();
}
