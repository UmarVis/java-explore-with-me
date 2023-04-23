package ru.practicum.server.service;

import ru.practicum.dto.DtoHitIn;
import ru.practicum.dto.DtoStatOut;

import java.time.LocalDateTime;
import java.util.List;

public interface StatService {
    void addHit(DtoHitIn dtoHitIn);

    List<DtoStatOut> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique);
}
