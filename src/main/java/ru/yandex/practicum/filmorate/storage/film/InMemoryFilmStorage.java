package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Repository()
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Long, Film> films = new HashMap<>();
    private final Map<Long, List<Long>> likes = new HashMap<>();
    private long key = 1;

    private long nextIdGenerator() {
        return key++;
    }

    @Override
    public Film createFilm(Film film) {
        long id = nextIdGenerator();
        film = new Film(id, film.getName(), film.getReleaseDate(), film.getDescription(),  film.getDuration(),
                film.getRate(), film.getMpa(), film.getGenres());
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
    public Optional<Film> findFilmById(long id) {
        Film film = films.get(id);
        if (film != null) {
            log.debug("Найден фильм: {}", film);
            return Optional.of(film);
        } else {
            log.debug("Фильм с идентификатором {} не найден.", id);
            return Optional.empty();
        }
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

    public void addLike(long filmId, long userId) {
        likes.put(filmId, new ArrayList<>());
    }


    public void removeLike(long filmId, long userId) {

    }


    @Override
    public List<Film> getPopular(int count) {
        return findAll().stream()
                .sorted((o1, o2) -> o2.getRate() - o1.getRate())
                .limit(count)
                .collect(Collectors.toList());
    }
}