package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;

import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.MapperToFilm;

import java.sql.PreparedStatement;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Component("filmDbStorage")
public class FilmDbStorage implements FilmStorage, MapperToFilm {
    private final JdbcTemplate jdbcTemplate;

    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Film> getAllFilms() {
        String sqlQuery = "SELECT * FROM film LEFT JOIN rating USING(rating_id)";
        List<Film> films = jdbcTemplate.query(sqlQuery, this::mapRowToFilm);
        return films;
    }

    @Override
    public Film add(Film film) {
        String sqlQuery = "INSERT INTO film(title, description, release_date, duration, rating_id) SELECT ?, ?, ?, ?, ?";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"film_id"});
            stmt.setString(1, film.getName());
            stmt.setString(2, film.getDescription());
            stmt.setDate(3, java.sql.Date.valueOf(film.getReleaseDate()));
            stmt.setLong(4, film.getDuration().toMinutes());
            stmt.setInt(5, film.getMpa().getId());
            return stmt;
        }, keyHolder);

        film.setId(Objects.requireNonNull(keyHolder.getKey()).intValue());
        return film;
    }

    @Override
    public void update(Film film) {
        String sqlQuery = "UPDATE film SET title = ?, description = ?, release_date = ?, duration = ?, rating_id = ? " +
                "WHERE film_id = ?";

        jdbcTemplate.update(sqlQuery,
                film.getName(),
                film.getDescription(),
                java.sql.Date.valueOf(film.getReleaseDate()),
                film.getDuration().toMinutes(),
                film.getMpa().getId(),
                film.getId());

    }

    @Override
    public Optional<Film> getFilmById(int id) {
        String sqlQuery = "SELECT * FROM film INNER JOIN rating USING (rating_id) WHERE film_id = ?";
        Film film = jdbcTemplate.queryForObject(sqlQuery, this::mapRowToFilm, id);


        return Optional.ofNullable(film);
    }

    @Override
    public void addLike(int filmId, int userId) {
        String sqlQuery = "INSERT INTO likes (user_id, film_id) VALUES (?, ?)";
        jdbcTemplate.update(sqlQuery, userId, filmId);
    }

    @Override
    public void deleteLike(int filmId, int userId) {
        String sqlQuery = "DELETE FROM likes WHERE user_id = ? AND film_id = ?";
        jdbcTemplate.update(sqlQuery, userId, filmId);
    }

    @Override
    public List<Film> getMostPopularFilms(int count) {
        String sqlQuery = "SELECT * FROM film LEFT JOIN (SELECT film_id, COUNT(film_id) AS count_like FROM likes " +
                "GROUP BY film_id) USING (film_id) LEFT JOIN rating ON film.rating_id = rating.rating_id " +
                "ORDER BY count_like DESC limit ?";

        return jdbcTemplate.query(sqlQuery, this::mapRowToFilm, count);
    }

    @Override
    public List<Film> getCommonFilms(int user_id, int friend_id) {  // получить список общих с другим пользователем фильмов
        String sqlQuery = "SELECT * FROM film LEFT JOIN (SELECT film_id, COUNT(film_id) AS count_like FROM likes " +
                "GROUP BY film_id) USING (film_id) LEFT JOIN rating ON film.rating_id = rating.rating_id RIGHT " +
                "JOIN  likes AS l1 ON film.film_id=l1.film_id RIGHT JOIN likes AS l2 ON film.film_id=l2.film_id " +
                "WHERE l1.user_id = ? AND l2.user_id = ? ORDER BY count_like DESC";

        return jdbcTemplate.query(sqlQuery, this::mapRowToFilm, user_id, friend_id);
    }

    @Override
    public boolean doesFilmExist(int filmId) {
        String sql = "SELECT COUNT(*) FROM film WHERE film_id = ?";

        int count = jdbcTemplate.queryForObject(sql, new Object[]{filmId}, Integer.class);
        return count > 0;
    }

    @Override
    public boolean doesLikeExist(int filmId, int userId) {
        String sql = "SELECT COUNT(*) FROM likes WHERE user_id = ? AND film_id = ?";

        int count = jdbcTemplate.queryForObject(sql, new Object[]{userId, filmId}, Integer.class);

        return count > 0;
    }


    @Override
    public List<Film> search(String query, List<String> title) {  // поиск по названию фильмов и по режиссёру
        String sql = null;

        String format = String.format("SELECT * FROM film LEFT JOIN rating r ON film.rating_id = r.rating_id " +
                "LEFT JOIN film_director fd on film.film_id = fd.film_id LEFT JOIN director d " +
                "ON fd.director_id = d.director_id WHERE LOWER (name_director) LIKE '%s'", "%" + query.toLowerCase() + "%");
        String s = String.format("SELECT * FROM film LEFT JOIN rating r ON film.rating_id = r.rating_id " +
                "WHERE LOWER (title) LIKE  '%s'", "%" + query.toLowerCase() + "%");
        if (title.size() == 1) {
            if (title.contains("title")) {
                String sqlTitle = s;
                return jdbcTemplate.query(sqlTitle, this::mapRowToFilm);
            }
            if (title.contains("director")) {
                String sqlDirector = format;
                return jdbcTemplate.query(sqlDirector, this::mapRowToFilm);
            }
        }

        if (title.size() == 2) {
            if (title.contains("title") && title.contains("director")) {
                String sqlTitle = s;

                String sqlDirector = format;

                List<Film> searchAll = jdbcTemplate.query(sqlDirector, this::mapRowToFilm);
                searchAll.addAll(jdbcTemplate.query(sqlTitle, this::mapRowToFilm));
                return searchAll;
            }
        }
        return null;
    }

    @Override
    public void deleteFilmById(int filmId) {
        String sqlQuery = "DELETE FROM film WHERE film_id = ?";
        jdbcTemplate.update(sqlQuery, filmId);
    }

    // получить список самых популярных фильмов по году и по жанру
    @Override
    public List<Film> getPopularFilmFoYearFoGenre(int year, int genreId, int count) {
        String sqlQuery = "SELECT * FROM film LEFT JOIN film_genre fg ON film.film_id = fg.film_id LEFT JOIN genre g " +
                "ON fg.genre_id = g.genre_id LEFT JOIN rating r ON film.rating_id = r.rating_id LEFT JOIN " +
                "(SELECT COUNT(user_id), film_id FROM likes GROUP BY film_id ON film.film_id WHERE " +
                "EXTRACT(YEAR FROM film.release_date) = ? AND fg.genre_id = ? LIMIT ?";

        return jdbcTemplate.query(sqlQuery, this::mapRowToFilm, year, genreId, count);
    }

    // получить список самых популярных фильмов по году
    @Override
    public List<Film> getPopularFilmFoYear(int year, int count) {
        String sqlQuery = "SELECT * FROM film LEFT JOIN film_genre fg ON film.film_id = fg.film_id LEFT JOIN genre g " +
                "ON fg.genre_id = g.genre_id LEFT JOIN rating r ON film.rating_id = r.rating_id LEFT JOIN " +
                "(SELECT COUNT(user_id), film_id FROM likes GROUP BY film_id ON film.film_id " +
                "WHERE EXTRACT(YEAR FROM film.release_date) = ? LIMIT ?";

        return jdbcTemplate.query(sqlQuery, this::mapRowToFilm, year, count);
    }

    // получить список самых популярных фильмов по жанру
    @Override
    public List<Film> getPopularFilmFoGenre(int genreId, int count) {
        String sqlQuery = "SELECT * FROM film LEFT JOIN film_genre fg ON film.film_id = fg.film_id LEFT JOIN genre g " +
                "ON fg.genre_id = g.genre_id LEFT JOIN rating r ON film.rating_id = r.rating_id LEFT JOIN " +
                "(SELECT COUNT(user_id), film_id FROM likes GROUP BY film_id) ON film.film_id = likes.film_id " +
                "WHERE fg.genre_id = ? LIMIT ?";

        return jdbcTemplate.query(sqlQuery, this::mapRowToFilm, genreId, count);
    }

    // Возвращает рекомендации по фильмам для просмотра
    @Override
    public List<Film> getRecommendations(int userId) {
        String sqlQuery = "SELECT l2.user_Id FROM likes AS l1 JOIN likes AS l2 ON l1.FILM_ID = l2.FILM_ID " +
                "WHERE l1.user_Id = ? AND l1.user_Id<>l2.user_Id GROUP BY l1.user_Id , l2.user_Id " +
                "ORDER BY COUNT(l1.film_id) DESC LIMIT 1";

        List<Integer> bestUsersId = jdbcTemplate.queryForList(sqlQuery, Integer.class, userId);

        if (bestUsersId.isEmpty()) {
            return new ArrayList<>();
        }

        sqlQuery = "SELECT * FROM film LEFT JOIN rating ON film.rating_id = rating.rating_id RIGHT JOIN " +
                "(SELECT film_id FROM(SELECT film_id FROM likes WHERE USER_ID = ? EXCEPT SELECT film_id FROM likes " +
                "WHERE USER_ID = ?)) AS r ON r.film_id = film.film_id";

        return jdbcTemplate.query(sqlQuery, this::mapRowToFilm, bestUsersId.get(0), userId);

    }


}
