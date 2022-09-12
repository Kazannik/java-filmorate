# java-filmorate
Template repository for Filmorate project.

![Database diagram](../add-diagram/filmorate-database.png)

 База данных состоит из 7 таблиц:
 
- films – содержит сведения о фильмах;
- films_genres - содержит связи между фильмами и жанрами;
- genres – содержит справочник жанров фильмов;
- mpa – содержит справочник рейтинга Ассоциации кинокомпаний;
- favorite_films – содержит сведения о лайках фильмам;
- users – содержит сведения о пользователях;
- users_friends – содержит сведения о дружбе пользователей.

## Образцы запросов:
	
### 1. Проверка наличия фильма по индексу 1:  
    SELECT count(*) > 0 
    FROM films
    WHERE id=1;

### 2. Получение фильма по индексу 1:  
    SELECT *
    FROM films
    WHERE id=1;

### 3. Выборка всех фильмов:  
    SELECT *
    FROM films
    ORDER BY id;

### 4. Топ 10 лучших фильмов:  
    SELECT F.*
    FROM films AS F
    LEFT OUTER JOIN favorite_films AS FF ON F.id = FF.film_id
    GROUP BY F.id
    ORDER BY Count(FF.film_id) DESC
    LIMIT 10;

### 5. Выборка друзей у пользователя с индексом 1:  
    SELECT U.*
    FROM users_friends AS UF
    LEFT OUTER JOIN users AS U ON UF.friend_id=U.id
    WHERE UF.user_id=1;

### 6. Выборка общих друзей у пользователей с индексами 1 и 2 с подтвержденным статусом дружбы:
    SELECT *
    FROM users
    WHERE id IN
      (SELECT t.id_2 AS friend_id
         FROM
          (SELECT UF.user_id AS id_1, UF.friend_id AS id_2
           FROM users_friends AS UF
           UNION
           SELECT UF.friend_id AS id_1, UF.user_id AS id_2
           FROM users_friends AS UF
           ORDER BY id_1, id_2) AS t
       WHERE t.id_1 IN (1, 2)
       GROUP BY friend_id
       HAVING count(t.id_2) > 1);
