package ru.practicum.mapper;

import ru.practicum.dto.DtoHitIn;
import ru.practicum.model.Hit;

public class MapperHit {
    public static Hit makeHit(DtoHitIn dtoHitIn) {
        Hit hit = new Hit(dtoHitIn.getApp(),dtoHitIn.getUri(),dtoHitIn.getIp(),dtoHitIn.getTimestamp());
        return hit;
    }
}
