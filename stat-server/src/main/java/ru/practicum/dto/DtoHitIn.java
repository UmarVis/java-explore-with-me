package ru.practicum.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sun.istack.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.time.LocalDateTime;

import static ru.practicum.dto.DataTimePattern.DATA_TIME_FORMAT;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DtoHitIn {
    @NonNull
    private String app;
    @NonNull
    private String uri;
    @NonNull
    private String ip;
    @NotNull
    @JsonFormat(pattern = DATA_TIME_FORMAT)
    private LocalDateTime timestamp;
}
