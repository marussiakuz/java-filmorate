package ru.yandex.practicum.filmorate.storage.user;


import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

@Component
public interface UserStorage {    // управление списком пользователей

    public List<User> getAllUsers();
    public void add(User user);
    public void update(User user);
    public User getUserById(int id);
    public boolean doesUserExist(Integer userId);
}
