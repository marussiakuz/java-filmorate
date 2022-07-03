DROP TABLE IF EXISTS film_director;
DROP TABLE IF EXISTS film_genre;
DROP TABLE IF EXISTS genre;
DROP TABLE IF EXISTS friendship;
DROP TABLE IF EXISTS likes;
DROP TABLE IF EXISTS events;
DROP TABLE IF EXISTS review_usefulness;
DROP TABLE IF EXISTS review;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS film;
DROP TABLE IF EXISTS director;
DROP TABLE IF EXISTS rating;


CREATE TABLE IF NOT EXISTS users
(
    user_id  INT PRIMARY KEY AUTO_INCREMENT,
    name     VARCHAR(30),
    email    VARCHAR(30),
    login    VARCHAR(30) NOT NULL,
    birthday DATE
);

CREATE TABLE IF NOT EXISTS friendship
(
    user_id      INT,
    friend_id    INT,
    is_confirmed BOOLEAN,
    FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE CASCADE,
    FOREIGN KEY (friend_id) REFERENCES users (user_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS rating
(
    rating_id   INT PRIMARY KEY AUTO_INCREMENT,
    name_rating VARCHAR(30)
);
CREATE TABLE IF NOT EXISTS director
(
    director_id   INT PRIMARY KEY AUTO_INCREMENT,
    name_director VARCHAR(30)
);

CREATE TABLE IF NOT EXISTS film
(
    film_id      INT PRIMARY KEY AUTO_INCREMENT,
    title        VARCHAR(30),
    description  VARCHAR(30),
    release_date DATE,
    duration     INT,
    rating_id    INT,
    FOREIGN KEY (rating_id) REFERENCES rating (rating_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS likes
(
    user_id INT,
    film_id INT,
    FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE CASCADE,
    FOREIGN KEY (film_id) REFERENCES film (film_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS genre
(
    genre_id   INT PRIMARY KEY AUTO_INCREMENT,
    name_genre VARCHAR(30)
);

CREATE TABLE IF NOT EXISTS film_genre
(
    film_id  INT,
    genre_id INT,
    FOREIGN KEY (film_id) REFERENCES film (film_id) ON DELETE CASCADE,
    FOREIGN KEY (genre_id) REFERENCES genre (genre_id) ON DELETE CASCADE
);
CREATE TABLE IF NOT EXISTS film_director
(
    film_id     INT,
    director_id INT,
    FOREIGN KEY (film_id) REFERENCES film (film_id) ON DELETE CASCADE,
    FOREIGN KEY (director_id) REFERENCES director (director_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS review
(
    review_id      INT PRIMARY KEY AUTO_INCREMENT,
    review_content TEXT,
    is_positive    BOOLEAN,
    user_id        INT,
    film_id        INT,
    FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE CASCADE,
    FOREIGN KEY (film_id) REFERENCES film (film_id) ON DELETE CASCADE
);

-- в таблице review_usefulness хранятся лайки / дизлайки (лайк = 1, дизлайк = -1)
CREATE TABLE IF NOT EXISTS review_usefulness
(
    review_id    INT,
    user_id      INT,
    like_dislike INT NOT NULL DEFAULT 0,
    FOREIGN KEY (review_id) REFERENCES review (review_id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS events
(
    event_id   INT PRIMARY KEY AUTO_INCREMENT,
    user_id    INT,
    entity_id  INT,
    event_type VARCHAR(6),
    operation  VARCHAR(6),
    time_stamp BIGINT,
    FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE CASCADE
);