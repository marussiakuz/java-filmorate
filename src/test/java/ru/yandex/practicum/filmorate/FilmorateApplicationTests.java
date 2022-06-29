package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.jdbc.JdbcTestUtils;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Sql({"/schema.sql", "/test-data.sql"})
class FilmorateApplicationTests {
	private final UserDbStorage userDbStorage;
	private final FilmDbStorage filmDbStorage;
	private final JdbcTemplate jdbcTemplate;

	@Test
	public void addUpdateAndGetByIdUser() {
		assertEquals(5, JdbcTestUtils.countRowsInTable(jdbcTemplate, "users"));

		User user = User.builder()
				.name("Name")
				.login("login")
				.email("email@gmail.com")
				.birthday(LocalDate.of(1980, 12, 12))
				.build();

		userDbStorage.add(user);

		assertEquals(6, JdbcTestUtils.countRowsInTable(jdbcTemplate, "users"));

		User updatedUser = User.builder()
				.id(user.getId())
				.name("")
				.login("UpdatedLogin")
				.email("updatedEmail@gmail.ru")
				.birthday(LocalDate.of(2022, 6, 15))
				.build();

		userDbStorage.update(updatedUser);

		assertEquals(6, JdbcTestUtils.countRowsInTable(jdbcTemplate, "users"));

		Optional<User> userOptional = userDbStorage.getUserById(user.getId());

		assertThat(userOptional)
				.isPresent()
				.hasValueSatisfying(userCheck ->
						assertThat(userCheck).hasFieldOrPropertyWithValue("id", user.getId()));

		User receivedUser = userOptional.get();

		assertThat(receivedUser).hasFieldOrPropertyWithValue("name", "UpdatedLogin");
		assertThat(receivedUser).hasFieldOrPropertyWithValue("login", "UpdatedLogin");
		assertThat(receivedUser).hasFieldOrPropertyWithValue("email", "updatedEmail@gmail.ru");
		assertThat(receivedUser).hasFieldOrPropertyWithValue("birthday", LocalDate.of(2022, 6, 15));
	}

	@Test
	public void getAllUsers() {
		List<User> users = userDbStorage.getAllUsers();

		assertEquals(5, users.size());

		AtomicInteger userId = new AtomicInteger(1);

		users.forEach(user -> {
			assertEquals(user, Stream.of(userDbStorage.getUserById(userId.getAndIncrement()))
					.filter(Optional::isPresent)
					.map(Optional::get)
					.findFirst()
					.orElse(null));
		});
	}

	@Test
	public void addAndDeleteFriend() {
		List<User> friends = userDbStorage.getAllFriends(3);

		assertTrue(friends.isEmpty());

		userDbStorage.addFriend(3, 1);
		userDbStorage.addFriend(3, 2);
		friends = userDbStorage.getAllFriends(3);

		assertEquals(2, friends.size());

		userDbStorage.deleteFriend(3, 2);
		friends = userDbStorage.getAllFriends(3);

		assertEquals(1, friends.size());

		userDbStorage.deleteFriend(3, 1);
		friends = userDbStorage.getAllFriends(3);

		assertTrue(friends.isEmpty());
	}

	@Test
	public void getAllAndCommonFriends() {
		List<User> friends = userDbStorage.getAllFriends(1);

		assertTrue(friends.isEmpty());

		userDbStorage.addFriend(1, 2);
		userDbStorage.addFriend(1, 3);
		userDbStorage.addFriend(1,4);
		userDbStorage.addFriend(1, 5);
		friends = userDbStorage.getAllFriends(1);

		assertEquals(4, friends.size());

		userDbStorage.addFriend(2, 4);
		userDbStorage.addFriend(2, 5);

		List<User> commonFriends = userDbStorage.getCommonFriends(1, 2);

		assertEquals(2, commonFriends.size());

		AtomicInteger userId = new AtomicInteger(4);

		commonFriends.forEach(user -> {
			assertEquals(user, Stream.of(userDbStorage.getUserById(userId.getAndIncrement()))
					.filter(Optional::isPresent)
					.map(Optional::get)
					.findFirst()
					.orElse(null));
		});
	}

	@Test
	public void addUpdateAndGetByIdFilm() {
		assertEquals(5, JdbcTestUtils.countRowsInTable(jdbcTemplate, "film"));

		Film film = Film.builder()
				.name("Title")
				.description("about.. ")
				.duration(Duration.ofMinutes(100))
				.releaseDate(LocalDate.of(1976, 6, 6))
				.mpa(Rating.builder()
						.name("G")
						.id(1)
						.build())
				.build();

		filmDbStorage.add(film);

		assertEquals(6, JdbcTestUtils.countRowsInTable(jdbcTemplate, "film"));

		Film updatedFilm = Film.builder()
				.id(film.getId())
				.name("Another")
				.description("very strange.. ")
				.duration(Duration.ofMinutes(120))
				.releaseDate(LocalDate.of(2022, 6, 3))
				.mpa(Rating.builder()
						.name("NC-17")
						.id(5)
						.build())
				.build();

		filmDbStorage.update(updatedFilm);

		assertEquals(6, JdbcTestUtils.countRowsInTable(jdbcTemplate, "film"));

		Optional<Film> filmOptional = filmDbStorage.getFilmById(film.getId());

		assertThat(filmOptional)
				.isPresent()
				.hasValueSatisfying(filmCheck ->
						assertThat(filmCheck).hasFieldOrPropertyWithValue("id", film.getId()));

		Film receivedFilm = filmOptional.get();

		assertThat(receivedFilm).hasFieldOrPropertyWithValue("name", "Another");
		assertThat(receivedFilm).hasFieldOrPropertyWithValue("description", "very strange.. ");
		assertThat(receivedFilm).hasFieldOrPropertyWithValue("duration", Duration.ofMinutes(120));
		assertThat(receivedFilm).hasFieldOrPropertyWithValue("releaseDate", LocalDate.of(2022, 6, 3));
		assertThat(receivedFilm.getMpa()).hasFieldOrPropertyWithValue("id", 5);
		assertThat(receivedFilm.getMpa().getName()).isEqualTo("NC-17");
	}

	@Test
	public void getAllFilms() {
		List<Film> films = filmDbStorage.getAllFilms();

		assertEquals(5, films.size());

		AtomicInteger filmId = new AtomicInteger(1);

		films.forEach(film -> {
			assertEquals(film, Stream.of(filmDbStorage.getFilmById(filmId.getAndIncrement()))
					.filter(Optional::isPresent)
					.map(Optional::get)
					.findFirst()
					.orElse(null));
		});
	}

	@Test
	public void addAndDeleteLikeAndGetMostPopular() {
		filmDbStorage.addLike(5, 1);
		filmDbStorage.addLike(5, 2);
		filmDbStorage.addLike(5, 3);
		filmDbStorage.addLike(5, 4);
		filmDbStorage.addLike(5, 5);

		filmDbStorage.addLike(4, 1);
		filmDbStorage.addLike(4, 2);
		filmDbStorage.addLike(4, 3);
		filmDbStorage.addLike(4, 4);

		filmDbStorage.addLike(3, 1);
		filmDbStorage.addLike(3, 2);
		filmDbStorage.addLike(3, 3);

		filmDbStorage.addLike(2, 1);
		filmDbStorage.addLike(2, 2);

		List<Film> popular = filmDbStorage.getMostPopularFilms(3);

		assertEquals(3, popular.size());

		assertEquals(filmDbStorage.getFilmById(5).get(), popular.get(0));
		assertEquals(filmDbStorage.getFilmById(4).get(), popular.get(1));
		assertEquals(filmDbStorage.getFilmById(3).get(), popular.get(2));

		filmDbStorage.deleteLike(5, 1);
		filmDbStorage.deleteLike(5, 2);
		filmDbStorage.deleteLike(5, 3);

		popular = filmDbStorage.getMostPopularFilms(2);

		assertEquals(2, popular.size());
		assertEquals(filmDbStorage.getFilmById(4).get(), popular.get(0));
		assertEquals(filmDbStorage.getFilmById(3).get(), popular.get(1));
	}
	@Test
	public void deleteUserByIdTest() {
		assertEquals(5, JdbcTestUtils.countRowsInTable(jdbcTemplate, "users"));

		User userDel = User.builder()
				.name("NameDel")
				.login("loginDel")
				.email("emailDel@gmail.com")
				.birthday(LocalDate.of(1980, 12, 12))
				.build();

		userDbStorage.add(userDel);

		assertEquals(6, JdbcTestUtils.countRowsInTable(jdbcTemplate, "users"));

		userDbStorage.deleteUserByIdStorage(userDel.getId());

		assertEquals(5, JdbcTestUtils.countRowsInTable(jdbcTemplate, "users"));
	}

	@Test
	public void deleteFilmByIdTest() {
		assertEquals(5, JdbcTestUtils.countRowsInTable(jdbcTemplate, "film"));

		Film filmDel = Film.builder()
				.name("TitleDel")
				.description("aboutDel.. ")
				.duration(Duration.ofMinutes(100))
				.releaseDate(LocalDate.of(1976, 6, 6))
				.mpa(Rating.builder()
						.name("G")
						.id(1)
						.build())
				.build();

		filmDbStorage.add(filmDel);

		assertEquals(6, JdbcTestUtils.countRowsInTable(jdbcTemplate, "film"));

		filmDbStorage.deleteFilmByIdStorage(filmDel.getId());

		assertEquals(5, JdbcTestUtils.countRowsInTable(jdbcTemplate, "film"));

	}




}