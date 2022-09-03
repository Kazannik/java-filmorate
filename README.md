# java-filmorate
Template repository for Filmorate project.

![Database diagram](../main/filmorate-database.png)

 База данных состоит из 6 таблиц:
 
- films – содержит сведения о фильмах;
- film_category – содержит справочник жанров (категорий) фильмов;
- film_mpa – содержит справочник рейтинга Ассоциации кинокомпаний;
- favorite_films – содержит сведения о лайках фильмам;
- users – содержит сведения о пользователях;
- users_friends – содержит сведения о дружбе пользователей.

## Образцы запросов:

### 1. Выборка всех пользователей:
    SELECT *
    FROM users AS u
    ORDER BY u.id;

### 2. Выборка всех фильмов:  
    SELECT *
    FROM films AS f
    ORDER BY f.id;  
  
### 3. Топ 10 лучших фильмов:  
    SELECT f.id AS id,
           COUNT(ff.film_id) AS top
    FROM films AS f
             LEFT OUTER JOIN favorite_films ff on f.id = ff.film_id
    GROUP BY id
    ORDER BY top DESC, id
    LIMIT 10;

### 4. Выборка общих друзей у пользователей с индексами 1 и 2 с подтвержденным статусом дружбы:
    SELECT t.id_2 AS friend_id
    FROM (SELECT uf.user_id AS id_1,
                 uf.friend_id AS id_2,
                 uf.status AS status
          FROM users_friends AS uf
          UNION
          SELECT uf.friend_id AS id_1,
                 uf.user_id AS id_2,
                 uf.status AS status
          FROM users_friends AS uf
          ORDER BY id_1, id_2) AS t
    WHERE t.id_1 IN (1, 2)
      AND t.status = true
    GROUP BY friend_id
    HAVING count(t.id_2) > 1;
