package ru.practicum.shareit.user;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.Getter;

@Getter
public class UserDto {

    private long id;

    @NotBlank
    private String name;

    @NotNull
    @Email
    private String email;
}
