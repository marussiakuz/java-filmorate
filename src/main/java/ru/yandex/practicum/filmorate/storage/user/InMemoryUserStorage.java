package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;
import java.util.stream.Collectors;

@Component("inMemoryUserStorage")
public class InMemoryUserStorage implements UserStorage {
    private final HashMap<Integer, User> users = new HashMap<>();

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public void add(User user) {
        if (user.getId() == null || user.getId() == 0) {
            if (users.isEmpty()) user.setId(1);
            else {
                int maxId = users.keySet().stream().max(Comparator.naturalOrder()).get();
                user.setId(++maxId);
            }

        }
        users.put(user.getId(), user);
    }

    @Override
    public void update(User user) {
        users.put(user.getId(), user);
    }

    @Override
    public Optional<User> getUserById(int userId) {
        return Optional.ofNullable(users.get(userId));
    }

    @Override
    public void addFriend(int userId, int friendId) {
        users.get(userId).addFriend(friendId);
        users.get(friendId).addFriend(userId);
    }

    @Override
    public void deleteFriend(int userId, int friendId) {
        users.get(userId).deleteFriend(friendId);
        users.get(friendId).deleteFriend(userId);
    }

    @Override
    public List<User> getAllFriends(int userId) {
        return users.get(userId).getFriends().stream().map(users::get).collect(Collectors.toList());
    }

    @Override
    public List<User> getCommonFriends(int userId, int otherUserId) {
        List<Integer> friends = new ArrayList<>(users.get(userId).getFriends());
        friends.retainAll(users.get(otherUserId).getFriends());
        return friends.stream().map(users::get).collect(Collectors.toList());
    }

    @Override
    public boolean doesUserExist(int userId) {
        return users.containsKey(userId);
    }

    @Override
    public boolean doesFriendExist(int userId, int friendId) {
        return users.get(userId).getFriends().contains(friendId);
    }
}
