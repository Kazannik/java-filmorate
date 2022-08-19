package ru.yandex.practicum.filmorate.controller;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    private final Map<Integer, Film> films = new HashMap<>();

    @GetMapping
    public List<Film> findAll() {
        return new ArrayList<>(films.values());
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) throws ValidationException {
        int id = getNextId();
        film = new Film(id, film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration());
        if (film.getName().isBlank()) {
            log.warn("Некорректное название фильма.", film);
            throw new ValidationException("Название не может быть пустым.");
        }
        if (film.getDescription().length() > 200) {
            log.warn("Превышена максимальная длина описания фильма.", film);
            throw new ValidationException("Максимальная длина описания фильма не может превышать 200 символов.");
        }
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            log.warn("Некорректная дата релиза фильма.", film);
            throw new ValidationException("Дата релиза фильма не раньше 28 декабря 1895 года.");
        }
        if (film.getDuration() <= 0) {
            log.warn("Некорректная продолжительность фильма.", film);
            throw new ValidationException("Продолжительность фильма должна быть положительной.");
        }

        films.put(film.getId(), film);
        log.debug("Фильм успешно добавлен в коллекцию.", film);
        return film;
    }

    @PutMapping
    public Film put(@Valid @RequestBody @NonNull Film film) throws ValidationException {
        if (!films.containsKey(film.getId())) {
            log.warn("Попытка обновить сведения об отсутствующем фильме.", film);
            throw new ValidationException("Заданный фильм отсутствует в коллекции.");
        } else {
            films.put(film.getId(), film);
            log.debug("Сведения о фильме успешно обновлены.", film);
            return film;
        }
    }

    private int getNextId() {
        List<Integer> list = getAllId();
        int key = 1;
        while (list.contains(key)) {
            key++;
        }
        return key;
    }

    private List<Integer> getAllId() {
        return films.values().stream()
                .map(Film::getId)
                .sorted()
                .collect(Collectors.toList());
    }
}
