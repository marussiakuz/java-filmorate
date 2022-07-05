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
        films.forEach(film -> film.setGenres(getGenresByFilmId(film.getId())));
        films.forEach(film -> film.setDirectors(getDirectorsByFilmId(film.getId())));

        return films;
    }

    @Override
    public void add(Film film) {
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
        if (film.getGenres() != null) addGenresToTheFilm(film);

        film.setId(Objects.requireNonNull(keyHolder.getKey()).intValue());
        if (film.getDirectors() != null) addDirectorToTheFilm(film);
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

        updateDirectorsAndGenres(film);
    }

    @Override
    public Optional<Film> getFilmById(int id) {
        String sqlQuery = "SELECT * FROM film INNER JOIN rating USING (rating_id) WHERE film_id = ?";

        Film film = jdbcTemplate.queryForObject(sqlQuery, this::mapRowToFilm, id);

        if (film != null) {
            List<Genre> genres = getGenresByFilmId(id);
            film.setGenres(genres.isEmpty() ? null : genres);

            List<Director> directors = getDirectorsByFilmId(id);
            film.setDirectors(directors);
        }

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

        List<Film> popularFilms = jdbcTemplate.query(sqlQuery, this::mapRowToFilm, count);

        popularFilms.forEach(film -> film.setGenres(getGenresByFilmId(film.getId())));
        popularFilms.forEach(film->film.setDirectors(getDirectorsByFilmId(film.getId())));

        return popularFilms;
    }

    @Override
    public List<Film> getCommonFilms(int user_id, int friend_id) {  // получить список общих с другим пользователем фильмов
        String sqlQuery = "SELECT * FROM film LEFT JOIN (SELECT film_id, COUNT(film_id) AS count_like FROM likes " +
                "GROUP BY film_id) USING (film_id) LEFT JOIN rating ON film.rating_id = rating.rating_id RIGHT " +
                "JOIN  likes AS l1 ON film.film_id=l1.film_id RIGHT JOIN likes AS l2 ON film.film_id=l2.film_id " +
                "WHERE l1.user_id = ? AND l2.user_id = ? ORDER BY count_like DESC";

        List<Film> commonFilms = jdbcTemplate.query(sqlQuery, this::mapRowToFilm, user_id, friend_id);

        commonFilms.stream()
                .filter(film -> !getGenresByFilmId(film.getId()).isEmpty())
                .forEach(film -> film.setGenres(getGenresByFilmId(film.getId())));

        return commonFilms;
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
    public List<Director> getDirectorsByFilmId(int filmId) {  // получить список режиссеров по id фильма
        String sqlQuery = "SELECT * FROM director RIGHT JOIN (SELECT director_id FROM film_director WHERE film_id = ?) " +
                "USING(director_id)";

        return jdbcTemplate.query(sqlQuery, this::mapRowToDirector, filmId);
    }

    @Override
    public List<Film> search(String query, List<String> title) {  // поиск по названию фильмов и по режиссёру
        String sql = null;

        if (title.size() == 1) {
            if (title.contains("title")) {
                sql = "SELECT * FROM film LEFT JOIN rating r ON film.rating_id = r.rating_id WHERE LOWER (TITLE) " +
                        "LIKE '%s', \"%?%\"";
            }
        }

        if (title.contains("director")) {
            sql = "SELECT * FROM film LEFT JOIN rating R ON FILM.RATING_ID = r.rating_id LEFT JOIN film_director fd " +
                    "ON film.film_id = fd.film_id LEFT JOIN director d ON fd.director_id = d.director_id " +
                    "WHERE LOWER (name_director) LIKE '%s', \"%?%\"";
        }

        if (title.size() == 2) {
            if (title.contains("title") && title.contains("director")) {
                String sqlTitle = "SELECT * FROM film LEFT JOIN rating r ON film.rating_id = r.rating_id " +
                        "WHERE LOWER (title) LIKE  '%s', \"%?%\"";

                String sqlDirector = "SELECT * FROM film LEFT JOIN rating r ON film.rating_id = r.rating_id " +
                        "LEFT JOIN film_director fd on film.film_id = fd.film_id LEFT JOIN director d " +
                        "ON fd.director_id = d.director.idbWHERE LOWER (name_director) LIKE '%s', \"%?%\"";

                List<Film> searchAll = jdbcTemplate.query(sqlDirector, this::mapRowToFilm, query.toLowerCase());
                searchAll.addAll(jdbcTemplate.query(sqlTitle, this::mapRowToFilm));
                searchAll.forEach(film -> film.setGenres(getGenresByFilmId(film.getId())));
                searchAll.forEach(film -> film.setDirectors(getDirectorsByFilmId(film.getId())));
                searchAll.stream().filter(film -> film.getGenres().size() == 0).forEach(film -> film.setGenres(null));

                return searchAll;
            }
        }

        assert sql != null;
        List<Film> search = jdbcTemplate.query(sql, this::mapRowToFilm);
        search.forEach(film -> film.setGenres(getGenresByFilmId(film.getId())));
        search.forEach(film -> film.setDirectors(getDirectorsByFilmId(film.getId())));
        search.stream().filter(film -> film.getGenres().size() == 0).forEach(film -> film.setGenres(null));
                
        return search;
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

        List<Film> foYearFoGenre = jdbcTemplate.query(sqlQuery, this::mapRowToFilm, year, genreId, count);
        foYearFoGenre.forEach(film -> film.setGenres(getGenresByFilmId(film.getId())));

        return foYearFoGenre;
    }

    // получить список самых популярных фильмов по году
    @Override
    public List<Film> getPopularFilmFoYear(int year, int count) {
        String sqlQuery = "SELECT * FROM film LEFT JOIN film_genre fg ON film.film_id = fg.film_id LEFT JOIN genre g " +
                "ON fg.genre_id = g.genre_id LEFT JOIN rating r ON film.rating_id = r.rating_id LEFT JOIN " +
                "(SELECT COUNT(user_id), film_id FROM likes GROUP BY film_id ON film.film_id " +
                "WHERE EXTRACT(YEAR FROM film.release_date) = ? LIMIT ?";

        List<Film> foYear = jdbcTemplate.query(sqlQuery, this::mapRowToFilm, year, count);
        foYear.forEach(film -> film.setGenres(getGenresByFilmId(film.getId())));

        return foYear;
    }

    // получить список самых популярных фильмов по жанру
    @Override
    public List<Film> getPopularFilmFoGenre(int genreId, int count) {
        String sqlQuery = "SELECT * FROM film LEFT JOIN film_genre fg ON film.film_id = fg.film_id LEFT JOIN genre g " +
                "ON fg.genre_id = g.genre_id LEFT JOIN rating r ON film.rating_id = r.rating_id LEFT JOIN " +
                "(SELECT COUNT(user_id), film_id FROM likes GROUP BY film_id) ON film.film_id = likes.film_id " +
                "WHERE fg.genre_id = ? LIMIT ?";

        List<Film> foGenre = jdbcTemplate.query(sqlQuery, this::mapRowToFilm, genreId, count);
        foGenre.forEach(film -> film.setGenres(getGenresByFilmId(film.getId())));

        return foGenre;
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

        List<Film> recommendationsFilms = jdbcTemplate.query(sqlQuery, this::mapRowToFilm, bestUsersId.get(0), userId);

        if (!recommendationsFilms.isEmpty()) {
            for (Film f : recommendationsFilms) {
                if (!getGenresByFilmId(f.getId()).isEmpty()) {
                    f.setGenres(getGenresByFilmId(f.getId()));
                }
            }
        }
        return recommendationsFilms;
    }

    private void updateDirectorsAndGenres(Film film) {
        deleteDirectorsByFilmId(film.getId());
        if (film.getDirectors() != null) addDirectorToTheFilm(film);

        if (film.getGenres() != null) {
            deleteGenresByFilmId(film.getId());
            addGenresToTheFilm(film);
        }
    }

    private List<Genre> getGenresByFilmId(int filmId) {
        String sqlQuery = "SELECT * FROM genre RIGHT JOIN (SELECT genre_id FROM film_genre WHERE film_id = ?) " +
                "USING(genre_id)";

        return jdbcTemplate.query(sqlQuery, this::mapRowToGenre, filmId);
    }

    private void addGenresToTheFilm(Film film) {
        if (film.getGenres() == null || film.getGenres().isEmpty())
            return;

        String sqlQuery = "INSERT INTO film_genre(film_id, genre_id) SELECT ?, ?";

        film.getGenres().stream()
                .mapToInt(Genre::getId)
                .distinct()
                .forEach(genreId -> jdbcTemplate.update(sqlQuery, film.getId(), genreId));

        film.setGenres(getGenresByFilmId(film.getId()));
    }

    private void addDirectorToTheFilm(Film film) {
        if (film.getDirectors() == null || film.getDirectors().isEmpty())
            return;

        String sqlQuery = "INSERT INTO film_director(film_id, director_id) SELECT ?, ?";

        film.getDirectors().stream()
                .mapToInt(Director::getId)
                .distinct()
                .forEach(directorId -> jdbcTemplate.update(sqlQuery, film.getId(), directorId));

        film.setDirectors(getDirectorsByFilmId(film.getId()));
    }

    private void deleteGenresByFilmId(int filmId) {
        String sqlQuery = "DELETE FROM film_genre WHERE film_id = ?";

        jdbcTemplate.update(sqlQuery, filmId);
    }


    private void deleteDirectorsByFilmId(int filmId) {
        String sqlQuery = "DELETE FROM FILM_DIRECTOR WHERE film_id = ?";

        jdbcTemplate.update(sqlQuery, filmId);
    }
}
