package ru.practicum.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.event.model.Location;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class EventDtoIn {
    @NotBlank
    String annotation;
    @NotNull(message = "Field: category. Error: must not be blank. Value: null")
    Long category;
    @NotBlank
    String description;
    @NotNull(message = "Field: eventDate. Error: должно содержать дату, которая еще не наступила. Value: 2020-12-31T15:10:05")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime eventDate;
    @NotNull
    Location location;
    @NotNull
    Boolean paid = false;
    @PositiveOrZero
    Long participantLimit = 0L;
    @NotNull
    Boolean requestModeration = true;
    @NotBlank
    String title;
}
