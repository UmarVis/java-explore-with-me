package ru.practicum.user.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UserDtoAdd {
    @NotBlank(message = "Field: name. Error: must not be blank. Value: null")
    @Email
    @Size(max = 50)
    String email;
    @NotBlank
    @Size(max = 512)
    String name;
}
