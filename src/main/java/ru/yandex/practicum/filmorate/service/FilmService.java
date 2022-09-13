package ru.yandex.practicum.filmorate.service;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.genre.dao.GenreDao;
import ru.yandex.practicum.filmorate.storage.mpa.dao.MpaDao;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final GenreDao genresDao;
    private final MpaDao mpaDao;
    private final UserService userService;

    @Autowired
    public FilmService(@Qualifier("FilmDbStorage") FilmStorage storage, UserService userService,
                       GenreDao genres, MpaDao mpa) {
        this.filmStorage = storage;
        this.userService = userService;
        this.genresDao = genres;
        this.mpaDao = mpa;
    }

    public Film createFilm(@Valid @NonNull @RequestBody Film film) throws ValidationException {
        if (film.getName().isBlank()) {
            log.warn("Некорректное название фильма ({}).", film);
            throw new ValidationException("Название не может быть пустым.");
        }
        if (film.getDescription().length() > 200) {
            log.warn("Превышена максимальная длина описания фильма ({}).", film);
            throw new ValidationException("Максимальная длина описания фильма не может превышать 200 символов.");
        }
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            log.warn("Некорректная дата релиза фильма ({}).", film);
            throw new ValidationException("Дата релиза фильма не раньше 28 декабря 1895 года.");
        }
        if (film.getDuration() <= 0) {
            log.warn("Некорректная продолжительность фильма ({}).", film);
            throw new ValidationException("Продолжительность фильма должна быть положительной.");
        }
        if (film.getMpa() == null) {
            log.warn("Не задан МРАА рейтинг фильма ({}).", film);
            throw new ValidationException("Не задан МРАА рейтинг фильма.");
        }
        return filmStorage.createFilm(film);
    }

    public Film getFilm(long id) {
        if (!filmStorage.containsFilm(id)) {
            log.warn("Попытка доступа к отсутствующему фильму ({}).", id);
            throw new NotFoundException(String.format("Фильм (%s) отсутствует в коллекции.", id));
        } else {
            return filmStorage.getFilm(id);
        }
    }

    public Film updateFilm(@Valid @NonNull @RequestBody Film film) throws ValidationException {
        if (!filmStorage.containsFilm(film.getId())) {
            log.warn("Попытка обновить сведения об отсутствующем фильме ({}).", film);
            throw new NotFoundException(String.format("Фильм (%s) отсутствует в коллекции.", film.getId()));
        } else {
            return filmStorage.updateFilm(film);
        }
    }

    public List<Film> findAll() {
        return filmStorage.findAll();
    }

    public void addLike(long filmId, long userId) {
        if (!filmStorage.containsFilm(filmId)) {
            log.warn("Попытка поставить лайк отсутствующему фильму ({}).", filmId);
            throw new NotFoundException(String.format("Фильм (%s) отсутствует в коллекции.", filmId));
        } else if (userService.getUser(userId) == null) {
            log.warn("Попытка поставить лайк отсутствующим пользователем ({}).", userId);
            throw new NotFoundException(String.format("Пользователь (%s) отсутствует в коллекции.", userId));
        } else {
            filmStorage.addLike(filmId, userId);
            log.debug("Пользователь ({}) поставил лайк фильму ({})", userId, filmId);
        }
    }

    public void removeLike(long filmId, long userId) {
        if (!filmStorage.containsFilm(filmId)) {
            log.warn("Попытка удалить лайк отсутствующему фильму ({}).", filmId);
            throw new NotFoundException(String.format("Фильм (%s) отсутствует в коллекции.", filmId));
        } else if (userService.getUser(userId) == null) {
            log.warn("Попытка удалить лайк отсутствующим пользователем ({}).", userId);
            throw new NotFoundException(String.format("Пользователь (%s) отсутствует в коллекции.", userId));
        } else {
            filmStorage.removeLike(filmId, userId);
            log.debug("Пользователь ({}) удалил лайк у фильма ({})", userId, filmId);
        }
    }

    public List<Film> getPopular(int count) {
        return filmStorage.getPopular(count);
    }

    public Genre getGenre(int id) {
        if (!genresDao.containsGenre(id)) {
            log.warn("Попытка доступа к отсутствующему жанру фильма ({}).", id);
            throw new NotFoundException(String.format("Жанр фильма (%s) отсутствует в коллекции.", id));
        } else {
            return genresDao.getGenre(id);
        }
    }

    public List<Genre> findGenres() {
        return genresDao.findGenres();
    }

    public Mpa getMpa(int id) {
        if (!mpaDao.containsMpa(id)) {
            log.warn("Попытка доступа к отсутствующей категории фильма ({}).", id);
            throw new NotFoundException(String.format("Категория фильма (%s) отсутствует в коллекции.", id));
        } else {
            return mpaDao.getMpa(id);
        }
    }

    public List<Mpa> findMpa() {
        return mpaDao.findMpa();
    }
}
