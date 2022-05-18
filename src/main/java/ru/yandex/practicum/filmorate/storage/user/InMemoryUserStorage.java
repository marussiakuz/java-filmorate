package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
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
        checkName(user);
        checkId(user);
        users.put(user.getId(), user);
    }

    @Override
    public void update(User user) {
        if (!doesUserExist(user.getId())) throw new UserNotFoundException();
        checkName(user);
        users.put(user.getId(), user);
    }

    @Override
    public User getUserById(int userId) {
        System.out.println("!doesUserExist(userId)" + !doesUserExist(userId));
        if (!doesUserExist(userId)) throw new UserNotFoundException();
        return users.get(userId);
    }

    @Override
    public boolean doesUserExist(Integer userId) {
        return users.containsKey(userId);
    }

    private void checkName(User user) {    // проверяет -> name == null и пустое ли, и если да присваивает логин
        if (user.getName() == null || user.getName().isBlank()) user.setName(user.getLogin());
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
