INSERT INTO rating (name_rating)
VALUES ('G');
INSERT INTO rating (name_rating)
VALUES ('PG');
INSERT INTO rating (name_rating)
VALUES ('PG-13');
INSERT INTO rating (name_rating)
VALUES ('R');
INSERT INTO rating (name_rating)
VALUES ('NC-17');

INSERT INTO genre (name_genre)
VALUES ('Комедия');
INSERT INTO genre (name_genre)
VALUES ('Драма');
INSERT INTO genre (name_genre)
VALUES ('Мультфильм');
INSERT INTO genre (name_genre)
VALUES ('Триллер');
INSERT INTO genre (name_genre)
VALUES ('Документарный');
INSERT INTO genre (name_genre)
VALUES ('Боевик');

INSERT INTO users (name, email, login, birthday)
VALUES ('UserOne', 'one@yandex.ru', 'one', '1990-01-01');
INSERT INTO users (name, email, login, birthday)
VALUES ('UserTwo', 'two@yandex.ru', 'two', '1993-03-13');
INSERT INTO users (name, email, login, birthday)
VALUES ('UserThree', 'three@yandex.ru', 'three', '1992-02-02');
INSERT INTO users (name, email, login, birthday)
VALUES ('UserFour', 'four@yandex.ru', 'four', '1999-09-19');
INSERT INTO users (name, email, login, birthday)
VALUES ('UserFive', 'five@yandex.ru', 'five', '2000-11-11');

INSERT INTO film (title, description, release_date, duration, rating_id)
VALUES ('first', 'first description', '2022-01-01', 120, 5);
INSERT INTO film (title, description, release_date, duration, rating_id)
VALUES ('second', 'second description', '2022-02-02', 90, 4);
INSERT INTO film (title, description, release_date, duration, rating_id)
VALUES ('third', 'third description', '2022-03-03', 150, 3);
INSERT INTO film (title, description, release_date, duration, rating_id)
VALUES ('fourth', 'fourth description', '2022-04-04', 120, 2);
INSERT INTO film (title, description, release_date, duration, rating_id)
VALUES ('fifth', 'fifth description', '2022-05-05', 70, 1);

INSERT INTO film_genre (film_id, genre_id)
VALUES (1, 1);
INSERT INTO film_genre (film_id, genre_id)
VALUES (1, 6);
INSERT INTO film_genre (film_id, genre_id)
VALUES (2, 2);
INSERT INTO film_genre (film_id, genre_id)
VALUES (3, 3);
INSERT INTO film_genre (film_id, genre_id)
VALUES (3, 6);
INSERT INTO film_genre (film_id, genre_id)
VALUES (3, 2);
INSERT INTO film_genre (film_id, genre_id)
VALUES (4, 1);
INSERT INTO film_genre (film_id, genre_id)
VALUES (5, 5);