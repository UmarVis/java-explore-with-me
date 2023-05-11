package ru.practicum.event.controller;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.event.dto.EventDtoFull;
import ru.practicum.event.service.EventService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/events")
@AllArgsConstructor
@Validated
public class EventPublicController {
    private final EventService eventService;

    @GetMapping
    public List<EventDtoFull> getAllPublic(@RequestParam(required = false) String text,
                                           @RequestParam(required = false) List<Long> categories,
                                           @RequestParam(required = false) Boolean paid,
                                           @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
                                           @RequestParam(required = false) @FutureOrPresent @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
                                           @RequestParam(defaultValue = "false") boolean onlyAvailable,
                                           @RequestParam(required = false) String sort,
                                           @PositiveOrZero @RequestParam(defaultValue = "0") int from,
                                           @Positive @RequestParam(defaultValue = "10") int size,
                                           HttpServletRequest request) {
        String ip = request.getRemoteAddr();
        String url = request.getRequestURI();

        Pageable pageable = PageRequest.of(from, size);
        if (sort != null) {
            Sort sorting;
            switch (sort) {
                case "EVENT_DATE":
                    sorting = Sort.by(Sort.Direction.DESC, "eventDate");
                    break;
                case "VIEWS":
                    sorting = Sort.by(Sort.Direction.DESC, "views");
                    break;
                default:
                    sorting = Sort.by(Sort.Direction.DESC, "id");
            }
            pageable = PageRequest.of(from, size, sorting);

        }
        return eventService.getAllPublic(text, categories, paid, rangeStart,
                rangeEnd, onlyAvailable, pageable, ip, url);
    }

    @GetMapping("/{id}")
    public EventDtoFull getByIdPublic(@PathVariable Long id, HttpServletRequest request) {
        String ip = request.getRemoteAddr();
        String url = request.getRequestURI();
        return eventService.getByIdPublic(id, ip, url);
    }
}
