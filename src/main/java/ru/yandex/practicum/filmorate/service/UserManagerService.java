package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;

import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
public class UserManagerService extends ManagerService<User> {    // управление списком пользователей
    private final HashMap<Integer, User> users = new HashMap<>();

    @Override
    public List<User> get() {
        return new ArrayList<>(users.values());
    }

    @Override
    public void add(User user) {
        users.put(user.getId(), user);
    }

    @Override
    public void update(User user) {
        users.put(user.getId(), user);
    }

    @Override
    public User get(int id) {
        return users.get(id);
    }
}
