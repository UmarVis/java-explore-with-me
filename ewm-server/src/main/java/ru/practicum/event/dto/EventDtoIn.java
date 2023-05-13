package ru.practicum.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class EventDtoIn {
    @NotBlank
    @Size(max = 2000)
    String annotation;
    @NotNull(message = "Field: category. Error: must not be blank. Value: null")
    Long category;
    @NotBlank
    @Size(max = 7000)
    String description;
    @NotNull(message = "Field: eventDate. Error: должно содержать дату, которая еще не наступила. Value: 2020-12-31T15:10:05")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime eventDate;
    @NotNull
    @Valid
    LocationDto location;
    @NotNull
    boolean paid = false;
    @PositiveOrZero
    long participantLimit = 0L;
    boolean requestModeration = true;
    @NotBlank
    @Size(max = 120)
    String title;
}
