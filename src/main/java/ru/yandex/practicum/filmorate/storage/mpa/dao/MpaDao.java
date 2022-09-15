package ru.yandex.practicum.filmorate.storage.mpa.dao;

import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;
import java.util.Optional;

public interface MpaDao {
    boolean containsMpa(int id);

    Mpa getMpa(int id);

    Optional<Mpa> findMpaById(int id);

    List<Mpa> findMpa();
}
