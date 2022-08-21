package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Long, Film> films = new HashMap<>();
    private static long key = 1;

    private static long nextIdGenerator() {
        return key++;
    }

    @Override
    public Film createFilm(Film film) {
        long id = nextIdGenerator();
        film = new Film(id, film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration());
        films.put(film.getId(), film);
        log.debug("Фильм {} добавлен в коллекцию.", film);
        return film;
    }

    @Override
    public boolean containsFilm(long id) {
        return films.containsKey(id);
    }

    @Override
    public Film getFilm(long id) {
        return films.get(id);
    }

    @Override
    public Film updateFilm(Film film) {
        films.put(film.getId(), film);
        log.debug("Сведения о фильме {} обновлены.", film);
        return film;
    }

    @Override
    public void removeFilm(long id) {
        films.remove(id);
        log.debug("Фильм {} удален из коллекции.", id);
    }

    @Override
    public List<Film> findAll() {
        return new ArrayList<>(films.values());
    }
}