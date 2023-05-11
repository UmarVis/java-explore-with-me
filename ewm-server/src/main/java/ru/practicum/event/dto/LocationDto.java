package ru.practicum.event.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotNull;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LocationDto {
    @NotNull
    Float lat;
    @NotNull
    Float lon;
}
