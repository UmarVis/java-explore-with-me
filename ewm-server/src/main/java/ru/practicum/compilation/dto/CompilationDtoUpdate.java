package ru.practicum.compilation.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

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
    Boolean pinned;
    @Size(max = 512)
    String title;
}
