package ru.yandex.practicum.filmorate.storage.mpa.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component("MpaDbStorage")
public class MpaDbStorage implements MpaDao {

    private final JdbcTemplate jdbcTemplate;

    public MpaDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public boolean containsMpa(int id) {
        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(
                "SELECT count(*) > 0 FROM mpa WHERE id=?",
                Boolean.class, id));
    }

    @Override
    public Mpa getMpa(int id) {
        SqlRowSet mpaRows = jdbcTemplate.queryForRowSet(
                "SELECT * FROM mpa WHERE id=?", id);
        if (mpaRows.next()) {
            return mpaOfSqlRowSet(mpaRows);
        } else {
            log.debug("Категория фильма с идентификатором {} не найдена.", id);
            return null;
        }
    }

    @Override
    public Optional<Mpa> findMpaById(int id) {
        SqlRowSet mpaRows = jdbcTemplate.queryForRowSet(
                "SELECT * FROM mpa WHERE id=?", id);
        if (mpaRows.next()) {
            Mpa mpa = mpaOfSqlRowSet(mpaRows);
            log.debug("Найдена категория фильма: {}", mpa);
            return Optional.of(mpa);
        } else {
            log.debug("Категория фильма с идентификатором {} не найдена.", id);
            return Optional.empty();
        }
    }

    @Override
    public List<Mpa> findMpa() {
        return jdbcTemplate.query(
                "SELECT * FROM mpa ORDER BY id",
                (rs, rowNum) -> mpaOfResultSet(rs));
    }

    private static Mpa mpaOfSqlRowSet(SqlRowSet rs) {
        return new Mpa(rs.getInt("id"),
                rs.getString("name"));
    }

    private static Mpa mpaOfResultSet(ResultSet rs) throws SQLException {
        return new Mpa(rs.getInt("id"),
                rs.getString("name"));
    }
}