package ru.practicum.compilation.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.event.dto.EventDto;

import java.util.Set;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CompilationDto {
    Set<EventDto> events;
    Long id;
    Boolean pinned;
    String title;
}
