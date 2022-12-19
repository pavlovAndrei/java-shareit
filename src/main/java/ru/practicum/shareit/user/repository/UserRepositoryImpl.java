package ru.practicum.shareit.user.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static java.util.Objects.nonNull;
import static java.util.Optional.ofNullable;

import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;
import ru.practicum.shareit.user.User;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    private final Map<Long, User> users = new HashMap<>();
    private int counter = 1;

    @Override
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public Optional<User> findById(long id) {
        return ofNullable(users.get(id));
    }

    @Override
    public User add(User user) {
        user.setId(counter);
        counter++;
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User update(long id, User user) {
        user.setId(id);
        users.put(id, user);
        return user;
    }

    @Override
    public void delete(long id) {
        users.remove(id);
    }

    @Override
    public boolean isUserWithEmailExist(String email) {
        return users.values().stream()
                .anyMatch(user -> user.getEmail().equals(email));
    }

    public boolean exists(long id) {
        return nonNull(users.get(id));
    }
}
