package ru.practicum.server.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.DtoHitIn;
import ru.practicum.dto.DtoStatOut;
import ru.practicum.server.service.StatService;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class StatController {
    private final StatService statService;

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public void addHit(@Valid @RequestBody DtoHitIn dtoHitIn) {
        log.info("Информация сохранена {}", dtoHitIn);
        statService.addHit(dtoHitIn);
    }

    @GetMapping("/stats")
    public List<DtoStatOut> getStats(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime start,
                                     @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime end,
                                     @RequestParam(value = "uris", defaultValue = "") List<String> uris,
                                     @RequestParam(value = "unique", defaultValue = "false") Boolean unique) {
        log.info("Получение статистики: start {}, end {}", start, end);
        return statService.getStats(start, end, uris, unique);
    }
}
