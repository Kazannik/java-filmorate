package ru.yandex.practicum.filmorate.service;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FilmService {
    private final FilmStorage storage;
    private final UserService userService;
    @Autowired
    public FilmService(InMemoryFilmStorage storage, UserService userService) {
        this.storage = storage;
        this.userService = userService;
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
        return storage.createFilm(film);
    }

    public Film getFilm(long id) {
        if (!storage.containsFilm(id)) {
            log.warn("Попытка доступа к отсутствующему фильму ({}).", id);
            throw new NotFoundException(String.format("Фильм (%s) отсутствует в коллекции.", id));
        } else {
            return storage.getFilm(id);
        }
    }

    public Film updateFilm(@Valid @NonNull @RequestBody Film film) throws ValidationException {
        if (!storage.containsFilm(film.getId())) {
            log.warn("Попытка обновить сведения об отсутствующем фильме ({}).", film);
            throw new NotFoundException(String.format("Фильм (%s) отсутствует в коллекции.", film.getId()));
        } else {
            log.debug("Сведения о фильме ({}) успешно обновлены.", film);
            return storage.updateFilm(film);
        }
    }

    public List<Film> findAll() {
        return storage.findAll();
    }

    public void addLike(long filmId, long userId) {
        Film film = getFilm(filmId);
        User user = userService.getUser(userId);
        film.getLikes().add(user.getId());
        log.debug("Пользователь ({}) поставил лайк фильму ({})", userId, filmId);
    }

    public void removeLike(long filmId, long userId) {
        Film film = getFilm(filmId);
        User user = userService.getUser(userId);
        if (film.getLikes().remove(user.getId())) {
            log.debug("Пользователь ({}) удалил лайк у фильма ({})", userId, filmId);
        } else {
            log.warn("Пользователь ({}) не ставил лайк фильму ({})", userId, filmId);
            throw new NotFoundException(String.format("Пользователь %s не ставил лайк фильму %s", userId, filmId));
        }
    }

    public List<Film> getPopular(int count) {
        return findAll().stream()
                .sorted((o1, o2) -> o2.getLikes().size() - o1.getLikes().size())
                .limit(count)
                .collect(Collectors.toList());
    }
}
