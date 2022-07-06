package ru.yandex.practicum.filmorate.service.user;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import ru.yandex.practicum.filmorate.exceptions.entityNotFoundExceptions.FriendNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.entityAlreadyExcistsExceptions.UserAlreadyExistsException;
import ru.yandex.practicum.filmorate.exceptions.entityNotFoundExceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.enums.EventType;
import ru.yandex.practicum.filmorate.service.genre.GenreService;
import ru.yandex.practicum.filmorate.storage.event.EventStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class UserService {
    private final UserStorage userStorage;
    private final EventStorage eventStorage;
    private final FilmStorage filmStorage;
    private final GenreService genreService;

    @Autowired
    public UserService(@Qualifier("userDbStorage") UserStorage userStorage,
                       @Qualifier("eventDbStorage") EventStorage eventStorage,
                       @Qualifier("filmDbStorage") FilmStorage filmStorage, GenreService genreService) {
        this.userStorage = userStorage;
        this.eventStorage = eventStorage;
        this.filmStorage = filmStorage;
        this.genreService = genreService;
    }

    public List<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    public User add(User user) {
        if (user.getId() != null && userStorage.doesUserExist(user.getId()))
            throw new UserAlreadyExistsException(String.format("User with id=%s already exists", user.getId()));
        checkName(user);

        userStorage.add(user);
        log.debug(String.format("new user with id=%s added successfully", user.getId()));

        return user;
    }

    public User update(User user) {
        validate(user.getId());
        checkName(user);

        userStorage.update(user);
        log.debug(String.format("user data with id=%s has been successfully updated", user.getId()));

        return user;
    }

    public User getUserById(int userId) {
        validate(userId);

        Optional<User> optionalUser = userStorage.getUserById(userId);
        if (optionalUser.isEmpty()) {
            throw new UserNotFoundException(String.format("User with id=%s not found", userId));
        }

        return optionalUser.get();
    }

    public void addFriend(int userId, int friendId) {
        validate(userId);
        validate(friendId);

        userStorage.addFriend(userId, friendId);
        log.debug(String.format("The user with id=%s has added the user with id=%s to friends", userId, friendId));

        eventStorage.addAddEvent(userId, friendId, EventType.FRIEND);
        log.debug(String.format("the add friend event was completed successfully"));
    }

    public void deleteFriend(int userId, int friendId) {
        validateFriendship(userId, friendId);

        userStorage.deleteFriend(userId, friendId);
        log.debug(String.format("The user with id=%s has removed the user with id=%s from friends", userId, friendId));

        eventStorage.addRemoveEvent(userId, friendId, EventType.FRIEND);
        log.debug(String.format("the remove friend event was completed successfully"));
    }

    public List<User> getAllFriends(int userId) {
        validate(userId);
        return userStorage.getAllFriends(userId);
    }

    public List<User> getCommonFriends(int userId, int otherUserId) {
        validate(userId);
        validate(otherUserId);

        return userStorage.getCommonFriends(userId, otherUserId);
    }

    public List<Event> getEvents(int userId) {
        validate(userId);

        return eventStorage.getEvents(userId);
    }

    private void validate(int userId) {
        if (!userStorage.doesUserExist(userId))
            throw new UserNotFoundException(String.format("User with id=%s not found", userId));
    }

    private void checkName(User user) {    // проверяет -> name == null и пустое ли, и если да присваивает логин
        if (user.getName() == null || user.getName().isBlank())
            user.setName(user.getLogin());
    }

    private void validateFriendship(int userId, int friendId) {
        if (!userStorage.doesFriendExist(userId, friendId))
            throw new FriendNotFoundException(String.format("the user with id=%s does not have a friend with user id=%s",
                    userId, friendId));
    }

    public void deleteUserByIdService(int userId) {
        validate(userId);
        userStorage.deleteUserById(userId);
        log.debug(String.format("the user with id=%s was deleted", userId));
    }

    public List<Film> getRecommendations(int userId) {
        List<Film> recommendationsFilms = filmStorage.getRecommendations(userId);
        if (!recommendationsFilms.isEmpty()) {
            for (Film f : recommendationsFilms) {
                if (!genreService.fillGenre(f.getId()).isEmpty()) {
                    f.setGenres(genreService.fillGenre(f.getId()));
                }
            }
        }
        return recommendationsFilms;
    }
}
