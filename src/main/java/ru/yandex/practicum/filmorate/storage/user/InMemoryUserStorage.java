package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

@Component
public class InMemoryUserStorage implements UserStorage {
    private final HashMap<Integer, User> users = new HashMap<>();

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public void add(User user) {
        checkId(user);
        users.put(user.getId(), user);
    }

    @Override
    public void update(User user) {
        users.put(user.getId(), user);
    }

    @Override
    public User getUserById(int userId) {
        return users.get(userId);
    }

    @Override
    public boolean doesUserExist(int userId) {
        return users.containsKey(userId);
    }

    private void checkId(User user) {
        if (user.getId() == 0) {
            if (users.isEmpty()) user.setId(1);
            else {
                int maxId = users.keySet().stream().max(Comparator.naturalOrder()).get();
                user.setId(++maxId);
            }

        }
    }
}
