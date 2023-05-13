package ru.practicum.event.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.enums.State;
import ru.practicum.event.dto.*;
import ru.practicum.request.dto.ParticipationRequestDto;
import ru.practicum.request.dto.RequestResultUpdateDto;
import ru.practicum.request.dto.RequestStatusDtoIn;

import java.time.LocalDateTime;
import java.util.List;

public interface EventService {
    EventDtoFull add(EventDtoIn eventDtoIn, Long userId);

    List<EventDto> getByUserId(Long userId, int from, int size);

    EventDtoFull getByUserIdByEventId(Long userId, Long eventId);

    EventDtoFull updateEventByUser(Long userId, Long eventId, EventUserUpdatedDto dtoUpdate);

    List<ParticipationRequestDto> getRequestsOfEvent(Long userId, Long eventId);

    RequestResultUpdateDto updateStatusRequest(Long userId, Long eventId, RequestStatusDtoIn requestStatusDtoIn);

    List<EventDtoFull> searchEvent(List<Long> users, List<State> states, List<Long> categories,
                                   LocalDateTime rangeStart, LocalDateTime rangeEnd, Pageable pageable);

    EventDtoFull updateEventAdmin(Long eventId, EventAdminUpdatedDto eventAdminUpdatedDto);

    List<EventDtoFull> getAllPublic(String text, List<Long> categories, Boolean paid, LocalDateTime rangeStart,
                                LocalDateTime rangeEnd, Boolean onlyAvailable, Pageable pageable, String ip, String url);

    EventDtoFull getByIdPublic(Long id, String ip, String url);
}
