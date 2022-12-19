package ru.practicum.shareit.user.service;

import java.util.List;

import ru.practicum.shareit.user.UserDto;

public interface UserService {

    List<UserDto> findAll();

    UserDto getById(long id);

    UserDto add(UserDto userDto);

    UserDto update(long id, UserDto userDto);

    void delete(long id);
}
