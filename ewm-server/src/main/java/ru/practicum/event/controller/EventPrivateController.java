package ru.practicum.event.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.event.dto.EventDto;
import ru.practicum.event.dto.EventDtoFull;
import ru.practicum.event.dto.EventDtoIn;
import ru.practicum.event.dto.EventUserUpdatedDto;
import ru.practicum.event.service.EventService;
import ru.practicum.request.dto.ParticipationRequestDto;
import ru.practicum.request.dto.RequestResultUpdateDto;
import ru.practicum.request.dto.RequestStatusDtoIn;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping("/users")
@AllArgsConstructor
@Validated
public class EventPrivateController {
    private final EventService eventService;

    @PostMapping("/{userId}/events")
    @ResponseStatus(HttpStatus.CREATED)
    public EventDtoFull add(@Validated @RequestBody EventDtoIn eventDtoIn,
                            @PathVariable @Positive Long userId) {
        return eventService.add(eventDtoIn, userId);
    }

    @GetMapping("/{userId}/events")
    public List<EventDto> getByUserId(@PathVariable @Positive Long userId,
                                      @PositiveOrZero @RequestParam(defaultValue = "0") int from,
                                      @Positive @RequestParam(defaultValue = "10") int size) {
        return eventService.getByUserId(userId, from, size);
    }

    @GetMapping("/{userId}/events/{eventId}")
    public EventDtoFull getByUserIdByEventId(@PathVariable @Positive Long userId,
                        @PathVariable @Positive Long eventId) {
        return eventService.getByUserIdByEventId(userId, eventId);
    }

    @PatchMapping("/{userId}/events/{eventId}")
    public EventDtoFull updateEventByUser(@PathVariable @Positive Long userId,
                                                 @PathVariable @Positive Long eventId,
                                                 @RequestBody @Validated EventUserUpdatedDto dtoUpdate) {
        return eventService.updateEventByUser(userId, eventId, dtoUpdate);
    }

    @GetMapping("/{userId}/events/{eventId}/requests")
    public List<ParticipationRequestDto> getRequestsOfEvent(@PathVariable @Positive Long userId,
                                                      @PathVariable @Positive Long eventId) {
        return eventService.getRequestsOfEvent(userId, eventId);
    }

    @PatchMapping("/{userId}/events/{eventId}/requests")
    public RequestResultUpdateDto updateStatusRequest(@PathVariable Long userId,
                                                      @PathVariable Long eventId,
                                                      @RequestBody RequestStatusDtoIn requestStatusDtoIn) {
        return eventService.updateStatusRequest(userId, eventId, requestStatusDtoIn);
    }
}
