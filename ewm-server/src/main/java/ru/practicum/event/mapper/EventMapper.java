package ru.practicum.event.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.category.mapper.CategoryMapper;
import ru.practicum.comments.dto.CommentDto;
import ru.practicum.comments.mapper.CommentMapper;
import ru.practicum.comments.model.Comment;
import ru.practicum.enums.State;
import ru.practicum.event.dto.EventDto;
import ru.practicum.event.dto.EventDtoFull;
import ru.practicum.event.dto.EventDtoIn;
import ru.practicum.event.model.Event;
import ru.practicum.user.mapper.UserMapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static ru.practicum.constant.Constant.DATE_TIME;

@UtilityClass
public class EventMapper {
    public Event makeEvent(EventDtoIn eventDtoIn) {
        return Event.builder()
                .annotation(eventDtoIn.getAnnotation())
                .confirmedRequests(0L)
                .createdOn(LocalDateTime.now())
                .description(eventDtoIn.getDescription())
                .eventDate(eventDtoIn.getEventDate())
                .location(eventDtoIn.getLocation() != null ? LocationMapper.makeLocation(eventDtoIn.getLocation()) : null)
                .paid(eventDtoIn.isPaid())
                .participantLimit(eventDtoIn.getParticipantLimit())
                .publishedOn(LocalDateTime.now())
                .requestModeration(eventDtoIn.isRequestModeration())
                .state(State.PENDING)
                .title(eventDtoIn.getTitle())
                .views(0L)
                .build();
    }

    public EventDtoFull makeEventFullDto(Event event) {
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
                .comments(event.getComments() != null ? makeSetComments(event.getComments()) : null)
                .build();
    }

    public EventDto makeEventDto(Event event) {
        return EventDto.builder()
                .annotation(event.getAnnotation())
                .category(CategoryMapper.makeCategoryDto(event.getCategory()))
                .confirmedRequests(event.getConfirmedRequests() != null ? event.getConfirmedRequests() : null)
                .eventDate(event.getEventDate().format(DATE_TIME))
                .id(event.getId())
                .initiator(UserMapper.makeUserShotDto(event.getInitiator()))
                .paid(event.getPaid())
                .title(event.getTitle())
                .views(event.getViews())
                .build();
    }

    public EventDtoFull makeEventAndViewsFullDto(Event event, Long views) {
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
                .views(views)
                .comments(event.getComments() != null ? makeSetComments(event.getComments()) : null)
                .build();
    }

    public Set<EventDto> makeSetEventDto(Set<Event> events) {
        return events.stream().map(EventMapper::makeEventDto).collect(Collectors.toSet());
    }

    public List<EventDto> makeListEventDto(List<Event> events) {
        return events.stream().map(EventMapper::makeEventDto).collect(Collectors.toList());
    }

    public List<EventDtoFull> makeEventAndViewsDto(List<Event> events, Map<String, Long> eventViews) {
        return events.stream().map(event -> {
            Long views = eventViews.get(String.format("/events/%s", event.getId()));

            return makeEventAndViewsFullDto(event, views != null ? views : 0);
        }).collect(Collectors.toList());
    }

    public Set<CommentDto> makeSetComments(Set<Comment> comments) {
        return comments.stream().map(CommentMapper::makeCommentDto).collect(Collectors.toSet());
    }
}
