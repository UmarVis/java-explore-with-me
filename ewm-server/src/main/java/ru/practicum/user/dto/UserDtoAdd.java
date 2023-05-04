package ru.practicum.user.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UserDtoAdd {
    @NotBlank(message = "Field: name. Error: must not be blank. Value: null")
    @Email
    String email;
    @NotBlank
    String name;
}
