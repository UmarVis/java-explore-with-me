package ru.practicum.event.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.category.mapper.CategoryMapper;
import ru.practicum.enums.State;
import ru.practicum.event.dto.EventDto;
import ru.practicum.event.dto.EventDtoFull;
import ru.practicum.event.dto.EventDtoIn;
import ru.practicum.event.model.Event;
import ru.practicum.user.mapper.UserMapper;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@UtilityClass
public class EventMapper {
    public static Event makeEvent(EventDtoIn eventDtoIn) {
        return Event.builder()
                .annotation(eventDtoIn.getAnnotation())
                .confirmedRequests(0L)
                .createdOn(LocalDateTime.now())
                .description(eventDtoIn.getDescription())
                .eventDate(eventDtoIn.getEventDate())
                .location(eventDtoIn.getLocation() != null ? eventDtoIn.getLocation() : null)
                .paid(eventDtoIn.getPaid())
                .participantLimit(eventDtoIn.getParticipantLimit())
                .publishedOn(LocalDateTime.now())
                .requestModeration(eventDtoIn.getRequestModeration())
                .state(State.PENDING)
                .title(eventDtoIn.getTitle())
                .views(0L)
                .build();
    }

    public static EventDtoFull makeEventFullDto(Event event) {
        return EventDtoFull.builder()
                .annotation(event.getAnnotation())
                .category(CategoryMapper.makeCategoryDto(event.getCategory()))
                .confirmedRequests(event.getConfirmedRequests() != null ? event.getConfirmedRequests() : null)
                .createdOn(event.getCreatedOn())
                .description(event.getDescription())
                .eventDate(event.getEventDate())
                .id(event.getId())
                .initiator(UserMapper.makeUserShotDto(event.getInitiator()))
                .location(LocationMapper.makeLocationDto(event.getLocation()))
                .paid(event.getPaid())
                .participantLimit(event.getParticipantLimit())
                .publishedOn(event.getPublishedOn())
                .requestModeration(event.getRequestModeration())
                .state(event.getState())
                .title(event.getTitle())
                .views(event.getViews())
                .build();
    }

    public static EventDto makeEventDto(Event event) {
        return EventDto.builder()
                .annotation(event.getAnnotation())
                .category(CategoryMapper.makeCategoryDto(event.getCategory()))
                .confirmedRequests(event.getConfirmedRequests() != null ? event.getConfirmedRequests() : null)
                .eventDate(event.getEventDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .id(event.getId())
                .initiator(UserMapper.makeUserShotDto(event.getInitiator()))
                .paid(event.getPaid())
                .title(event.getTitle())
                .views(event.getViews())
                .build();
    }

    public static Set<EventDto> MakeSetEventDto(Set<Event> events) {
        return events.stream().map(EventMapper::makeEventDto).collect(Collectors.toSet());
    }

    public static List<EventDto> makeListEventDto(List<Event> events) {
        return events.stream().map(EventMapper::makeEventDto).collect(Collectors.toList());
    }
}
