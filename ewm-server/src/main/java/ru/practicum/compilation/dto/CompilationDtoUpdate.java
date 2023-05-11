package ru.practicum.compilation.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class CompilationDtoUpdate {
    List<Long> events;
    @NotNull
    Boolean pinned;
    @NotBlank(message = "Field: title. Error: must not be blank. Value: null")
    @Size(max = 512)
    String title;
}
