package ru.practicum.shareit.user;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.SneakyThrows;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.service.UserService;

@WebMvcTest({UserController.class})
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    MockMvc mockMvc;

    @MockBean
    UserService userService;

    UserDto userDto;

    @BeforeEach
    void setUp() {
        userDto = UserDto.builder()
                .id(1L)
                .name("Test user")
                .email("andrei@yandex.ru")
                .build();
    }

    @SneakyThrows
    @Test
    void addUser() {
        when(userService.add(userDto)).
                thenReturn(userDto);

        mockMvc.perform(post("/users")
                        .content(new ObjectMapper().writeValueAsString(userDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userDto.getId()))
                .andExpect(jsonPath("$.name").value(userDto.getName()))
                .andExpect((jsonPath("$.email").value(userDto.getEmail())));
    }

    @SneakyThrows
    @Test
    void addUser_whenInvalidEmail_thenThrowBadRequestException() {
        userDto.setEmail("invalidEmail");

        mockMvc.perform(post("/users")
                        .content(new ObjectMapper().writeValueAsString(userDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void getAllUsers() {
        when(userService.findAll())
                .thenReturn(List.of(userDto));

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(List.of(userDto))));
    }

    @SneakyThrows
    @Test
    void deleteUserById() {
        mockMvc.perform(delete("/users/{id}", userDto.getId()))
                .andExpect(status().isOk());

        verify(userService, times(1)).delete(userDto.getId());
    }

    @SneakyThrows
    @Test
    void getUserById() {
        when(userService.getById(userDto.getId()))
                .thenReturn(userDto);

        mockMvc.perform(get("/users/{id}", userDto.getId()))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(userDto)))
                .andExpect(jsonPath("$.id").value(userDto.getId()))
                .andExpect(jsonPath("$.email").value(userDto.getEmail()))
                .andExpect(jsonPath("$.name").value((userDto.getName())));
    }

    @SneakyThrows
    @Test
    void getUserById_whenUserNotFound_thenThrowNotFoundException() {
        var wrongUserId = "222";

        when(userService.getById(anyLong()))
                .thenThrow(NotFoundException.class);

        mockMvc.perform(get("/users/{id}", wrongUserId))
                .andExpect(status().isNotFound());
    }

    @SneakyThrows
    @Test
    void updateUser() {
        when(userService.update(userDto.getId(), userDto))
                .thenReturn(userDto);

        mockMvc.perform(patch("/users/{id}", userDto.getId())
                        .content(objectMapper.writeValueAsString(userDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userDto.getId()))
                .andExpect(jsonPath("$.name").value(userDto.getName()))
                .andExpect((jsonPath("$.email").value(userDto.getEmail())));
    }

    @SneakyThrows
    @Test
    void updateUser_whenUserNotExist_thenThrowBadRequestException() {
        when(userService.update(anyLong(), any()))
                .thenThrow(BadRequestException.class);

        mockMvc.perform(patch("/users/{id}", userDto.getId())
                        .content(objectMapper.writeValueAsString(userDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}
