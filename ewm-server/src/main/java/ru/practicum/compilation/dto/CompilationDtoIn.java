package ru.practicum.compilation.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class CompilationDtoIn {
    List<Long> events;
    @NotNull
    Boolean pinned;
    @NotBlank(message = "Field: title. Error: must not be blank. Value: null")
    String title;
}
