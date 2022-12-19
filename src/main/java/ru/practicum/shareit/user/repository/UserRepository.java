package ru.practicum.shareit.user.repository;

import java.util.List;
import java.util.Optional;

import ru.practicum.shareit.user.User;

public interface UserRepository {

    List<User> findAll();

    Optional<User> findById(long id);

    User add(User user);

    User update(long id, User user);

    boolean isUserWithEmailExist(String email);

    boolean exists(long id);

    void delete(long id);
}
