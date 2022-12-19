package ru.practicum.shareit.user;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class User {

    private long id;
    @NotBlank
    private String name;
    @NotBlank
    @Email
    private String email;
}
