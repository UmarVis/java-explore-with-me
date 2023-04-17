package ru.practicum.server.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.dto.DtoHitIn;
import ru.practicum.server.model.Hit;

@UtilityClass
public class MapperHit {
    public static Hit makeHit(DtoHitIn dtoHitIn) {
        return Hit.builder()
                .app(dtoHitIn.getApp())
                .uri(dtoHitIn.getUri())
                .ip(dtoHitIn.getIp())
                .timestamp(dtoHitIn.getTimestamp())
                .build();
    }
}
