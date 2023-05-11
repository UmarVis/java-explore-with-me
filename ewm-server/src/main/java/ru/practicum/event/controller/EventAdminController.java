package ru.practicum.event.controller;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.enums.State;
import ru.practicum.event.dto.EventAdminUpdatedDto;
import ru.practicum.event.dto.EventDtoFull;
import ru.practicum.event.service.EventService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/admin/events")
@AllArgsConstructor
@Validated
public class EventAdminController {
    private final EventService eventService;

    @GetMapping
    public List<EventDtoFull> searchEvent(@RequestParam(defaultValue = "") List<Long> users,
                                          @RequestParam(defaultValue = "") List<String> states,
                                          @RequestParam(defaultValue = "") List<Long> categories,
                                          @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
                                          @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
                                          @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                          @Positive @RequestParam(defaultValue = "10") Integer size) {
        return eventService.searchEvent(new ArrayList<>(users), states.stream().map(State::valueOf).collect(Collectors.toList()),
                new ArrayList<>(categories), rangeStart, rangeEnd, PageRequest.of(from, size));
    }

    @PatchMapping("/{eventId}")
    public EventDtoFull updateEventAdmin(@PathVariable Long eventId,
                                         @RequestBody EventAdminUpdatedDto eventAdminUpdatedDto) {
        return eventService.updateEventAdmin(eventId, eventAdminUpdatedDto);
    }
}
