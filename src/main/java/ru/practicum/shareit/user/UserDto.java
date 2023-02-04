package ru.practicum.shareit.user;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    long id;
    @NotBlank(
            groups = Marker.OnCreate.class,
            message = "User's name cannot be empty")
    String name;
    @NotBlank(
            groups = Marker.OnCreate.class,
            message = "User's email cannot be empty")
    @Email(
            groups = {Marker.OnCreate.class, Marker.OnUpdate.class},
            message = "User's email is incorrect"
    )
    String email;
}
