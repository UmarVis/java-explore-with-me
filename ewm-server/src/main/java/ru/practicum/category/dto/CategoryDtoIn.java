package ru.practicum.category.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class CategoryDtoIn {
    @NotBlank(message = "Field: name. Error: must not be blank. Value: null")
    @Size(max = 64)
    String name;
}
