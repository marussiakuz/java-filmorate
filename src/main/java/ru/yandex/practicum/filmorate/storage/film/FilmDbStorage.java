package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Component("filmDbStorage")
public class FilmDbStorage implements FilmStorage {
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
        if (film.getGenres() != null)
            addGenresToTheFilm(film);

        film.setId(Objects.requireNonNull(keyHolder.getKey()).intValue());
        if (film.getDirectors() != null)
            addDirectorToTheFilm(film);
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

        if (film.getDirectors() != null) {
            deleteDirectorsByFilmId(film.getId());
            addDirectorToTheFilm(film);
        }
        if (film.getDirectors() == null) {
            deleteDirectorsByFilmId(film.getId());
        }

        if (film.getGenres() != null) {
            deleteGenresByFilmId(film.getId());
            addGenresToTheFilm(film);
        }
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

        return popularFilms;
    }

    @Override
    public List<Film> getCommonFilms(int user_id, int friend_id) {

        String sqlQuery = "SELECT * FROM film LEFT JOIN (SELECT film_id, COUNT(film_id) AS count_like FROM likes " +
                "GROUP BY film_id) USING (film_id) LEFT JOIN rating ON film.rating_id = rating.rating_id RIGHT " +
                "JOIN  likes AS l1 ON film.film_id=l1.film_id RIGHT JOIN likes AS l2 ON film.film_id=l2.film_id " +
                "WHERE l1.user_id = ? AND l2.user_id = ? ORDER BY count_like DESC";

        List<Film> commonFilms = jdbcTemplate.query(sqlQuery, this::mapRowToFilm, user_id, friend_id);
        if (!commonFilms.isEmpty()) {
            for (Film f : commonFilms) {
                if (!getGenresByFilmId(f.getId()).isEmpty()) {
                    f.setGenres(getGenresByFilmId(f.getId()));
                }
            }
        }
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

    private Film mapRowToFilm(ResultSet resultSet, int rowNum) throws SQLException {

        int filmId = resultSet.getInt("film_id");
        return Film.builder()
                .id(filmId)
                .name(resultSet.getString("title"))
                .description(resultSet.getString("description"))
                .releaseDate(resultSet.getDate("release_date").toLocalDate())
                .duration(Duration.ofMinutes(resultSet.getLong("duration")))
                .mpa(Rating.builder()
                        .id(resultSet.getInt("rating_id"))
                        .name(resultSet.getString("name_rating"))
                        .build())
                .build();
    }

    private Genre mapRowToGenre(ResultSet resultSet, int rowNum) throws SQLException {
        return Genre.builder()
                .id(resultSet.getInt("genre_id"))
                .name(resultSet.getString("name_genre"))
                .build();
    }

    private Director mapRowToDirector(ResultSet resultSet, int rowNum) throws SQLException {
        return Director.builder()
                .id(resultSet.getInt("director_id"))
                .name(resultSet.getString("name_director"))
                .build();
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

    public List<Director> getDirectorsByFilmId(int filmId) {
        String sqlQuery = "SELECT * FROM director RIGHT JOIN (SELECT director_id FROM film_director WHERE film_id = ?) " +
                "USING(director_id)";
        List<Director> directors = jdbcTemplate.query(sqlQuery, this::mapRowToDirector, filmId);
        if (directors == null) {
            return new ArrayList<Director>();
        }
        return directors;
    }

    @Override
    public List<Film> search(Optional<String> query, Optional<List<String>> title) {
        String sql = "";
        if (query.isPresent() && title.isPresent()) {
            if (title.get().size() == 1) {
                if (title.get().contains("title")) {
                    sql = String.format("SELECT * From FILM left join RATING R on FILM.RATING_ID = R.RATING_ID " +
                            " where LOWER (TITLE) LIKE  '%s'", "%" + query.get().toLowerCase() + "%");
                }
            }
            if (title.get().contains("director")) {
                sql = String.format("SELECT * From FILM left join RATING R on FILM.RATING_ID = R.RATING_ID LEFT JOIN FILM_DIRECTOR FD on FILM.FILM_ID = FD.FILM_ID LEFT JOIN DIRECTOR D on FD.DIRECTOR_ID = D.DIRECTOR_ID\n" +
                        "         where  LOWER (NAME_DIRECTOR) LIKE '%s'", "%" + query.get().toLowerCase() + "%");
            }
            if (title.get().size() == 2) {
                if (title.get().contains("title") && title.get().contains("director")) {
                    String sqlTitle = String.format("SELECT * From FILM left join RATING R " +
                            "on FILM.RATING_ID = R.RATING_ID " + " where LOWER (TITLE) LIKE  '%s'", "%" + query.get().toLowerCase() + "%");
                    String sqlDirector = String.format("SELECT * From FILM left join RATING R on FILM.RATING_ID = R.RATING_ID LEFT JOIN FILM_DIRECTOR FD on FILM.FILM_ID = FD.FILM_ID LEFT JOIN DIRECTOR D on FD.DIRECTOR_ID = D.DIRECTOR_ID\n" +
                            "         where  LOWER (NAME_DIRECTOR) LIKE '%s'", "%" + query.get().toLowerCase() + "%");
                    List<Film> searchAll = jdbcTemplate.query(sqlDirector, this::mapRowToFilm);
                    searchAll.addAll(jdbcTemplate.query(sqlTitle, this::mapRowToFilm));
                    searchAll.forEach(film -> film.setGenres(getGenresByFilmId(film.getId())));
                    searchAll.forEach(film -> film.setDirectors(getDirectorsByFilmId(film.getId())));

                    for (Film film : searchAll) {
                        if (film.getGenres().size() == 0) {
                            film.setGenres(null);
                        }
                    }
                    return searchAll;
                }
            }
        }

        List<Film> search = jdbcTemplate.query(sql, this::mapRowToFilm);
        search.forEach(film -> film.setGenres(getGenresByFilmId(film.getId())));
        search.forEach(film -> film.setDirectors(getDirectorsByFilmId(film.getId())));
        for (Film film : search) {
            if (film.getGenres().size() == 0) {
                film.setGenres(null);
            }
        }
        return search;
    }

    @Override
    public void deleteFilmByIdStorage(int filmId) {
        String sqlQuery = "DELETE FROM film WHERE film_id = ?";
        jdbcTemplate.update(sqlQuery, filmId);
    }

    public List<Film> getPopularFilmFoYearFoGenre(Optional<Integer> year, Optional<Integer> genre, Optional<Integer> count) {
        String sql = "";
        int countt = 10;
        if (count.isPresent()) {
            countt = count.get();
        }
        if (genre.isPresent() && year.isEmpty()) {
            sql = String.format("SELECT *FROM FILM LEFT JOIN FILM_GENRE FG on FILM.FILM_ID = FG.FILM_ID" +
                    " LEFT JOIN GENRE G2 on FG.GENRE_ID = G2.GENRE_ID " +
                    "LEFT JOIN RATING R on FILM.RATING_ID = R.RATING_ID LEFT JOIN (SELECT count(USER_ID)," +
                    " FILM_ID as id from LIKES  group by FILM_ID) " +
                    "on FILM.FILM_ID = id WHERE " +
                    " FG.GENRE_ID=%s LIMIT %s", genre.get(), countt);
        }
        if (genre.isEmpty() && year.isPresent()) {
            sql = String.format("SELECT *FROM FILM LEFT JOIN FILM_GENRE FG on FILM.FILM_ID = FG.FILM_ID" +
                    " LEFT JOIN GENRE G2 on FG.GENRE_ID = G2.GENRE_ID " +
                    "LEFT JOIN RATING R on FILM.RATING_ID = R.RATING_ID LEFT JOIN (SELECT count(USER_ID)," +
                    " FILM_ID as id from LIKES  group by FILM_ID) " +
                    "on FILM.FILM_ID = id WHERE  extract(YEAR FROM FILM.RELEASE_DATE)=%s " +
                    "  LIMIT %s", year.get(), 1);
        }
        if (genre.isPresent() && year.isPresent()) {
            sql = String.format("SELECT *FROM FILM LEFT JOIN FILM_GENRE FG on FILM.FILM_ID = FG.FILM_ID" +
                    " LEFT JOIN GENRE G2 on FG.GENRE_ID = G2.GENRE_ID " +
                    "LEFT JOIN RATING R on FILM.RATING_ID = R.RATING_ID LEFT JOIN (SELECT count(USER_ID)," +
                    " FILM_ID as id from LIKES  group by FILM_ID) " +
                    "on FILM.FILM_ID = id WHERE  extract(YEAR FROM FILM.RELEASE_DATE)=%s AND" +
                    " FG.GENRE_ID=%s LIMIT %s", year.get(), genre.get(), countt);
        }
        List<Film> foYear = jdbcTemplate.query(sql, this::mapRowToFilm);
        foYear.forEach(film -> film.setGenres(getGenresByFilmId(film.getId())));
        return foYear;
    }

    @Override
    public List<Film> getRecommendations(int userId) {
        String sqlQuery = "SELECT l2.user_Id " +
                "FROM likes AS l1 " +
                "JOIN likes AS l2 ON l1.FILM_ID = l2.FILM_ID " +
                "WHERE l1.USER_ID = ? AND l1.USER_ID<>l2.USER_ID " +
                "GROUP BY l1.USER_ID , l2.USER_ID " +
                "ORDER BY COUNT(l1.film_id) DESC " +
                "LIMIT 1";

        List<Integer> bestUsersId = jdbcTemplate.queryForList(sqlQuery, Integer.class, userId);

        if (bestUsersId.isEmpty()) {
            return new ArrayList<>();
        }

        sqlQuery = "SELECT * FROM film LEFT JOIN rating ON film.rating_id = rating.rating_id " +
                "RIGHT JOIN (SELECT film_id FROM( " +
                "SELECT film_id FROM likes " +
                "WHERE USER_ID = ? " +
                "EXCEPT " +
                "SELECT film_id FROM likes " +
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
}
