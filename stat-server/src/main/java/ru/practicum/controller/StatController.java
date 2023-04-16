package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.DtoHitIn;
import ru.practicum.dto.DtoStatOut;
import ru.practicum.service.StatService;

import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.dto.DataTimePattern.DATA_TIME_FORMAT;

@RestController
@RequiredArgsConstructor
@Slf4j
public class StatController {
    private final StatService statService;

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public void addHit (@RequestBody DtoHitIn dtoHitIn) {
        log.info("Информация сохранена {}", dtoHitIn);
        statService.addHit(dtoHitIn);
    }

    @GetMapping("/stats")
    public List<DtoStatOut> getStats (@RequestParam @DateTimeFormat(pattern = DATA_TIME_FORMAT) LocalDateTime start,
                                @RequestParam @DateTimeFormat(pattern = DATA_TIME_FORMAT) LocalDateTime end,
                                @RequestParam(required = false) List<String> uris,
                                @RequestParam(defaultValue = "false") Boolean unique) {
        log.info("Получение статистики: start {}, end {}", start, end);
        return statService.getStats(start, end, uris, unique);
    }
}
