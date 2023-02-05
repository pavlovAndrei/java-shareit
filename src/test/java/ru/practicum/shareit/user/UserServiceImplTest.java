package ru.practicum.shareit.user;

import java.util.List;
import java.util.Optional;

import static java.lang.String.format;
import static java.util.stream.Collectors.toList;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserServiceImpl;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @InjectMocks
    private UserServiceImpl userService;
    @Mock
    private UserMapper userMapper;
    @Mock
    private UserRepository userRepository;

    @Test
    void getAllUsers() {
        var users = List.of(new User());

        when(userRepository.findAll())
                .thenReturn(users);

        var expectedList = users.stream()
                .map(userMapper::toUserDto)
                .collect(toList());
        var actualList = userService.findAll();

        assertThat(actualList).isEqualTo(expectedList);
    }

    @Test
    void getUserById() {
        long userId = 1L;
        var user = new User();

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));

        var expectedUserDto = userMapper.toUserDto(user);
        var actualUserDto = userService.getById(userId);

        assertThat(actualUserDto).isEqualTo(expectedUserDto);
    }

    @Test
    void getUserById_whenUserNotExist_throwNotFoundException() {
        long userId = 1L;

        when(userRepository.findById(userId))
                .thenReturn(Optional.empty());

        var exception = assertThrows(NotFoundException.class,
                () -> userService.getById(userId));

        assertThat(exception.getMessage())
                .isEqualTo(format("User with id '%d' does not exist", userId));
        verify(userMapper, never()).toUserDto(any(User.class));
    }

    @Test
    void addUser() {
        UserDto userDtoForSave = new UserDto();

        User userForSave = new User();
        when(userMapper.toUser(any(UserDto.class)))
                .thenReturn(userForSave);
        User user = userMapper.toUser(userDtoForSave);
        when(userRepository.save(any(User.class)))
                .thenReturn(user);
        UserDto expectedUserDto = new UserDto();
        when(userMapper.toUserDto(any(User.class)))
                .thenReturn(expectedUserDto);

        UserDto actualUserDto = userService.add(userDtoForSave);

        assertThat(actualUserDto).isEqualTo(expectedUserDto);
    }

    @Test
    void updateUser() {
        long userId = 1L;
        var oldUser = new User();
        var expectedUserDto = UserDto.builder().build();

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(oldUser));
        when(userMapper.toUserDto(oldUser))
                .thenReturn(expectedUserDto);

        UserDto actualUserDto = userService.update(userId, expectedUserDto);

        assertThat(actualUserDto).isEqualTo(expectedUserDto);
    }

    @Test
    void deleteUser() {
        long userId = 1L;

        when(userRepository.existsById(anyLong()))
                .thenReturn(true);

        userService.delete(userId);

        verify(userRepository).deleteById(userId);
    }
}
