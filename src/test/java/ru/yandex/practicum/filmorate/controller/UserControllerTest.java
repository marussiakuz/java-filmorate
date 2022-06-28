package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.model.User;

import ru.yandex.practicum.filmorate.service.user.UserService;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.time.Month;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.liquibase.enabled=false",
        "spring.flyway.enabled=false"
})

@AutoConfigureMockMvc
class UserControllerTest {

    private static User user;

    @Autowired
    private UserController userController;

    @Autowired
    private UserService userService;

    @Qualifier("userDbStorage")
    @Autowired
    private UserStorage userStorage;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    private void beforeEach() {
        user = new User();
        user.setLogin("Login");
        user.setName("Name");
        user.setEmail("email@yandex.ru");
        user.setBirthday(LocalDate.of(1990, Month.NOVEMBER, 17));
    }

    @Test
    void addValidUserIsOk() throws Exception {
        mockMvc.perform(post("/users")
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(user)))
                .andExpect(status().isOk());
    }

    @Test
    void addUserWithInvalidLogin() throws Exception {
        user.setLogin("");
        mockMvc.perform(post("/users")
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(user)))
                .andExpect(status().isBadRequest());

        user.setLogin("Login login");
        mockMvc.perform(post("/users")
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(user)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addUserWithInvalidEmail() throws Exception {
        user.setEmail("email");
        mockMvc.perform(post("/users")
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(user)))
                .andExpect(status().isBadRequest());

        user.setEmail("");
        mockMvc.perform(post("/users")
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(user)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addUserWithInvalidBirthday() throws Exception {
        user.setBirthday(LocalDate.of(2990, Month.NOVEMBER, 17));
        mockMvc.perform(post("/users")
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(user)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateValidUserIsOk() throws Exception {
        user = new User();
        user.setLogin("NewUser");
        user.setName("Family Name");
        user.setEmail("new@yandex.ru");
        user.setBirthday(LocalDate.of(1999, Month.DECEMBER, 29));
        mockMvc.perform(post("/users")
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(user)))
                        .andExpect(status().isOk());

        user.setName("Name Family");
        user.setId(2);
        System.out.println("user "+ user);
        mockMvc.perform(put("/users")
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(user)))
                .andExpect(status().isOk());

        Optional<User> optionalUser = userStorage.getUserById(2);

        assertTrue(optionalUser.isPresent());

        User updatedUser = optionalUser.get();

        assertThat(updatedUser.getName()).isEqualTo("Name Family");
    }

    @Test
    void updateUserWithInvalidLogin() throws Exception {
        mockMvc.perform(post("/users")
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(user)))
                .andExpect(status().isOk());

        user.setLogin("");
        mockMvc.perform(put("/users")
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(user)))
                .andExpect(status().isBadRequest());

        user.setLogin("Login login");
        mockMvc.perform(put("/users")
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(user)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateUserWithInvalidEmail() throws Exception {
        mockMvc.perform(post("/users")
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(user)))
                .andExpect(status().isOk());

        user.setEmail("email");
        mockMvc.perform(put("/users")
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(user)))
                .andExpect(status().isBadRequest());

        user.setEmail("");
        mockMvc.perform(put("/users")
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(user)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateUserWithInvalidBirthday() throws Exception {
        mockMvc.perform(post("/users")
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(user)))
                .andExpect(status().isOk());

        user.setBirthday(LocalDate.of(2990, Month.NOVEMBER, 17));
        mockMvc.perform(put("/users")
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(user)))
                .andExpect(status().isBadRequest());
    }
}