package ru.practicum.event.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.category.exception.CategoryException;
import ru.practicum.category.model.Category;
import ru.practicum.category.repository.CategoryRepository;
import ru.practicum.client.StatClient;
import ru.practicum.comments.model.Comment;
import ru.practicum.comments.repository.CommentRepository;
import ru.practicum.dto.DtoHitIn;
import ru.practicum.dto.DtoStatOut;
import ru.practicum.enums.AdminStateAction;
import ru.practicum.enums.State;
import ru.practicum.enums.Status;
import ru.practicum.enums.UserStateAction;
import ru.practicum.event.dto.*;
import ru.practicum.event.exception.EventException;
import ru.practicum.event.mapper.EventMapper;
import ru.practicum.event.mapper.LocationMapper;
import ru.practicum.event.model.Event;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.event.repository.LocationRepository;
import ru.practicum.exception.ConflictException;
import ru.practicum.request.dto.ParticipationRequestDto;
import ru.practicum.request.dto.RequestResultUpdateDto;
import ru.practicum.request.dto.RequestStatusDtoIn;
import ru.practicum.request.mapper.RequestMapper;
import ru.practicum.request.model.Request;
import ru.practicum.request.repository.RequestRepository;
import ru.practicum.user.exception.UserNotFoundException;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.*;
import static ru.practicum.constant.Constant.DATE_TIME;
import static ru.practicum.enums.Status.CONFIRMED;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class EventServiceImpl implements EventService {
    @Value("${app-name}")
    private String serviceName;
    public static final Sort SORT_BY_ASC = Sort.by(Sort.Direction.ASC, "id");
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final CategoryRepository categoryRepository;
    private final LocationRepository locationRepository;
    private final RequestRepository requestRepository;
    private final CommentRepository commentRepository;
    private final StatClient statClient;

    @Override
    @Transactional
    public EventDtoFull add(EventDtoIn eventDtoIn, Long userId) {
        log.info("Add new event {} with user ID {}", eventDtoIn, userId);
        if (eventDtoIn.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new ConflictException("Дата и время на которые намечено событие не может быть раньше, чем через два часа " +
                    "от текущего момента");
        }
        Event newEvent = EventMapper.makeEvent(eventDtoIn);
        newEvent.setInitiator(checkUser(userId));
        newEvent.setCategory(categoryRepository.findById(eventDtoIn.getCategory()).orElseThrow(() ->
                new CategoryException("Category with id=" + eventDtoIn.getCategory() + " was not found")));
        LocationDto locationDto = eventDtoIn.getLocation();
        newEvent.setLocation(locationRepository.save(LocationMapper.makeLocation(locationDto)));
        return EventMapper.makeEventFullDto(eventRepository.save(newEvent));
    }

    @Override
    public List<EventDto> getByUserId(Long userId, int from, int size) {
        log.info("Get event with user ID {} from {}, size {}", userId, from, size);
        User user = checkUser(userId);
        List<Event> event = eventRepository.findAllByInitiator(user);
        Pageable pageable = PageRequest.of(from / size, size, SORT_BY_ASC);
        loadComments(event);
        return EventMapper.makeListEventDto(eventRepository.findAllByInitiator(user, pageable));
    }

    @Override
    public EventDtoFull getByUserIdByEventId(Long userId, Long eventId) {
        log.info("Get by user ID {} and event ID {}", userId, eventId);
        User user = checkUser(userId);
        Event event = checkEvent(eventId);
        loadComments(List.of(event));
        checkInitiator(userId, event.getInitiator().getId());
        return EventMapper.makeEventFullDto(eventRepository.findAllByInitiatorAndId(user, eventId));
    }

    @Override
    public EventDtoFull updateEventByUser(Long userId, Long eventId, EventUserUpdatedDto dtoUpdate) {
        log.info("Update event {} with user ID {}, event ID {}", dtoUpdate, eventId, userId);
        checkUser(userId);
        Event event = checkEvent(eventId);
        checkInitiator(userId, event.getInitiator().getId());
        if (event.getState().equals(State.PUBLISHED)) {
            throw new ConflictException("Изменить можно только отмененные события или события в состоянии ожидания модерации");
        }
        if (dtoUpdate.getEventDate() != null) {
            if (dtoUpdate.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
                throw new ConflictException("Дата и время на которые намечено событие не может быть раньше, чем через два часа " +
                        "от текущего момента");
            }
        }
        if (dtoUpdate.getCategory() != null) {
            Category category = categoryRepository.findById(dtoUpdate.getCategory()).orElseThrow(()
                    -> new CategoryException("Category with id=" + dtoUpdate.getCategory() + " was not found"));
            event.setCategory(category);
        }
        if (dtoUpdate.getStateAction().equals(UserStateAction.SEND_TO_REVIEW)) {
            event.setState(State.PENDING);
        }
        if (dtoUpdate.getStateAction().equals(UserStateAction.CANCEL_REVIEW)) {
            event.setState(State.CANCELED);
        }
        if (dtoUpdate.getAnnotation() != null && !dtoUpdate.getAnnotation().isBlank()) {
            event.setAnnotation(dtoUpdate.getAnnotation());
        }
        if (dtoUpdate.getDescription() != null && !dtoUpdate.getDescription().isBlank()) {
            event.setDescription(dtoUpdate.getDescription());
        }
        if (dtoUpdate.getTitle() != null && !dtoUpdate.getTitle().isBlank()) {
            event.setTitle(dtoUpdate.getTitle());
        }
        if (dtoUpdate.getRequestModeration() != null) {
            event.setRequestModeration(dtoUpdate.getRequestModeration());
        }
        if (dtoUpdate.getParticipantLimit() != null) {
            event.setParticipantLimit(dtoUpdate.getParticipantLimit());
        }
        if (dtoUpdate.getPaid() != null) {
            event.setPaid(dtoUpdate.getPaid());
        }
        if (dtoUpdate.getLocation() != null) {
            event.setLocation(LocationMapper.makeLocation(dtoUpdate.getLocation()));
        }
        return EventMapper.makeEventFullDto(event);
    }

    @Override
    public List<ParticipationRequestDto> getRequestsOfEvent(Long userId, Long eventId) {
        log.info("Get request with user ID {} and event ID {}", userId, eventId);
        checkUser(userId);
        Event event = checkEvent(eventId);
        if (!event.getInitiator().getId().equals(userId)) {
            throw new ConflictException("Пользователь с ИД: " + userId + " не является инициатором");
        }
        return RequestMapper.makeListDtoOut(requestRepository.findAllByEventId(eventId));
    }

    @Override
    @Transactional
    public RequestResultUpdateDto updateStatusRequest(Long userId, Long eventId, RequestStatusDtoIn requestStatusDtoIn) {
        log.info("Update status request: status {}, user ID {}, event ID {}", requestStatusDtoIn.getStatus(), eventId, eventId);
        checkUser(userId);
        Event event = checkEvent(eventId);
        Long confirmedRequests = requestRepository.countRequestByEventIdAndStatusEquals(eventId, Status.CONFIRMED);

        if (!event.getInitiator().getId().equals(userId)) {
            throw new ConflictException("Only initiator can update requests");
        }
        if (event.getParticipantLimit() != 0 && event.getParticipantLimit().equals(confirmedRequests)) {
            throw new ConflictException("Event has reached participant limit");
        }

        List<Request> requestToSend = new ArrayList<>();
        List<Request> requests = requestRepository.findAllByIdIn(requestStatusDtoIn.getRequestIds());

        if (requests.size() != requestStatusDtoIn.getRequestIds().size()) {
            throw new ConflictException("Events with ids=%s was not found");
        }

        for (Request request : requests) {
            if (!request.getEvent().getId().equals(eventId)) {
                throw new ConflictException("Event and request with id=%d event don't match");
            }
            if (!event.getRequestModeration() || event.getParticipantLimit() == 0) {
                requestToSend.add(request);
            } else {
                if (!request.getStatus().equals(Status.PENDING)) {
                    throw new ConflictException("Request with id=%d not pending");
                }
                if (requestStatusDtoIn.getStatus().equals(Status.CONFIRMED) && event.getParticipantLimit() > confirmedRequests) {
                    request.setStatus(Status.CONFIRMED);
                    requestToSend.add(request);
                } else {
                    requestToSend.add(request);
                    request.setStatus(Status.REJECTED);
                }
            }
        }

        return RequestMapper.makeRequestResultUpdateDto(requestToSend);
    }

    @Override
    public List<EventDtoFull> searchEvent(List<Long> users, List<State> states, List<Long> categories,
                                          LocalDateTime rangeStart, LocalDateTime rangeEnd, Pageable pageable) {
        log.info("Search events with parameters: users ID {}, states {}, categories ID {}, rangeStart {}, rangeEnd {}" +
                "pageable {}", users, states, categories, rangeStart, rangeEnd, pageable);
        List<EventDtoFull> dtoList = new ArrayList<>();
        Specification<Event> specification = (root, query, builder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (users != null && !users.isEmpty()) {
                predicates.add(builder.and(root.get("initiator").get("id").in(users)));
            }
            if (states != null && !states.isEmpty()) {
                predicates.add(builder.and(root.get("state").in(states)));
            }
            if (categories != null && !categories.isEmpty()) {
                predicates.add(builder.and(root.get("category").get("id").in(categories)));
            }
            if (rangeStart != null) {
                predicates.add(builder.and(builder.greaterThan(root.get("eventDate"), rangeStart)));
            }
            if (rangeEnd != null) {
                predicates.add(builder.and(builder.lessThan(root.get("eventDate"), rangeEnd)));
            }
            return builder.and(predicates.toArray(new Predicate[0]));
        };
        for (Event event : eventRepository.findAll(specification, pageable)) {
            dtoList.add(EventMapper.makeEventFullDto(event));
        }
        return dtoList;
    }

    @Override
    @Transactional
    public EventDtoFull updateEventAdmin(Long eventId, EventAdminUpdatedDto eventAdminUpdatedDto) {
        log.info("Update event {} admin {}", eventAdminUpdatedDto, eventId);
        Event event = checkEvent(eventId);
        if (eventAdminUpdatedDto.getStateAction() == AdminStateAction.PUBLISH_EVENT) {
            if (event.getState() != State.PENDING) {
                throw new ConflictException("Не возможно опубликовать событие с ИД: " + eventId);
            }
            LocalDateTime published = LocalDateTime.now();
            event.setPublishedOn(published);
            event.setState(State.PUBLISHED);
        }

        if (eventAdminUpdatedDto.getStateAction() == AdminStateAction.REJECT_EVENT) {
            if (event.getState() == State.PUBLISHED && event.getPublishedOn().isBefore(LocalDateTime.now())) {
                throw new ConflictException("Не возможно опубликовать событие с ИД: " + eventId);
            }
            event.setState(State.CANCELED);
        }
        if (eventAdminUpdatedDto.getAnnotation() != null) {
            event.setAnnotation(eventAdminUpdatedDto.getAnnotation());
        }

        if (eventAdminUpdatedDto.getCategory() != null) {
            event.setCategory(categoryRepository.findById(eventAdminUpdatedDto.getCategory()).orElseThrow(() ->
                    new EventException("Category with id: " + eventAdminUpdatedDto.getCategory() + " not found")));
        }

        if (eventAdminUpdatedDto.getDescription() != null) {
            event.setDescription(eventAdminUpdatedDto.getDescription());
        }

        if (eventAdminUpdatedDto.getEventDate() != null) {
            if (eventAdminUpdatedDto.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
                throw new ConflictException("Не верная дата события");
            }
            event.setEventDate(eventAdminUpdatedDto.getEventDate());
        }
        if (eventAdminUpdatedDto.getPaid() != null) {
            event.setPaid(eventAdminUpdatedDto.getPaid());
        }
        if (eventAdminUpdatedDto.getParticipantLimit() != null) {
            event.setParticipantLimit(eventAdminUpdatedDto.getParticipantLimit());
        }
        if (eventAdminUpdatedDto.getRequestModeration() != null) {
            event.setRequestModeration(eventAdminUpdatedDto.getRequestModeration());
        }
        if (eventAdminUpdatedDto.getTitle() != null) {
            event.setTitle(eventAdminUpdatedDto.getTitle());
        }
        return EventMapper.makeEventFullDto(eventRepository.save(event));
    }

    @Override
    public List<EventDtoFull> getAllPublic(String text, List<Long> categories, Boolean paid, LocalDateTime rangeStart,
                                           LocalDateTime rangeEnd, Boolean onlyAvailable, Pageable pageable, String ip, String url) {
        log.info("Search events public with parameters: text {}, categories ID {}, paid {}, rangeStart {}, rangeEnd {}" +
                        "onlyAvailable {}, pageable {}, ip {}, url {}", text, categories, paid, rangeStart, rangeEnd, onlyAvailable,
                pageable, ip, url);
        createNewHit(ip, url);
        Specification<Event> specification = (root, query, builder) -> {
            List<Predicate> predicates = new ArrayList<>();
            Subquery<Long> subQuery = query.subquery(Long.class);
            Root<Request> requestRoot = subQuery.from(Request.class);
            Join<Request, Event> eventsRequests = requestRoot.join("event");

            predicates.add(builder.equal(root.get("state"), State.PUBLISHED));
            if (text != null && !text.isEmpty()) {
                predicates.add(builder.or(builder.like(builder.lower(root.get("annotation")), "%" + text.toLowerCase() + "%"),
                        builder.like(builder.lower(root.get("description")), "%" + text.toLowerCase() + "%")));
            }
            if (categories != null) {
                predicates.add(builder.and(root.get("category").in(categories)));
            }
            if (paid != null) {
                predicates.add(builder.equal(root.get("paid"), paid));
            }
            if (rangeStart != null) {
                predicates.add(builder.greaterThan(root.get("eventDate"), rangeStart));
            }
            if (rangeEnd != null) {
                predicates.add(builder.lessThan(root.get("eventDate"), rangeEnd));
            }
            if (onlyAvailable != null && onlyAvailable) {
                predicates.add(builder.or(builder.equal(root.get("participantLimit"), 0),
                        builder.and(builder.notEqual(root.get("participantLimit"), 0),
                                builder.greaterThan(root.get("participantLimit"), subQuery.select(builder.count(requestRoot.get("id")))
                                        .where(builder.equal(eventsRequests.get("id"), requestRoot.get("event").get("id")))
                                        .where(builder.equal(requestRoot.get("status"), CONFIRMED))))));

            }
            return builder.and(predicates.toArray(new Predicate[0]));
        };

        List<Event> eventList = eventRepository.findAll(specification, pageable);
        Map<String, Long> eventViewsMap = getEventViewsMap(getEventsViewsList(eventList));
        getConfirmedRequests(eventList);
        loadComments(eventList);

        return EventMapper.makeEventAndViewsDto(eventList, eventViewsMap);
    }

    @Override
    public EventDtoFull getByIdPublic(Long id, String ip, String url) {
        log.info("Get public event with ID {}", id);
        createNewHit(ip, url);
        Event event = getEventByEventIdAndState(id, State.PUBLISHED);
        getConfirmedRequests(List.of(event));
        Map<String, Long> eventViewsMap = getEventViewsMap(getEventsViewsList(List.of(event)));
        loadComments(List.of(event));
        return EventMapper.makeEventAndViewsDto(List.of(event), eventViewsMap).get(0);
    }

    private List<DtoStatOut> getEventsViewsList(List<Event> events) {
        List<String> eventUris = events
                .stream()
                .map(e -> String.format("/events/%s", e.getId()))
                .collect(Collectors.toList());
        LocalDateTime start = events.get(0).getPublishedOn();
        LocalDateTime end = LocalDateTime.now();
        for (Event event : events) {
            if (start.isAfter(event.getPublishedOn())) {
                start = event.getPublishedOn();
            }
        }

        List<DtoStatOut> result = statClient.getStatsEndpoint(start.format(DATE_TIME), end.format(DATE_TIME), eventUris, false);
        log.debug("Получена статистика обращений: {}", result);
        return result;
    }

    private Map<String, Long> getEventViewsMap(List<DtoStatOut> viewStatDtosList) {
        Map<String, Long> eventViews = new HashMap<>();

        for (DtoStatOut viewStat : viewStatDtosList) {
            eventViews.put(viewStat.getUri(), viewStat.getHits());
        }

        return eventViews;
    }

    private void getConfirmedRequests(List<Event> events) {
        Map<Event, Long> requests = requestRepository.findAllByEventInAndStatusEquals(events, Status.CONFIRMED)
                .stream()
                .collect(groupingBy(Request::getEvent, counting()));

        events.forEach(event -> event.setConfirmedRequests(requests.get(event)));
    }

    private Event getEventByEventIdAndState(Long eventId, State state) {
        return eventRepository
                .findEventByIdAndState(eventId, state)
                .orElseThrow(() -> new EventException("Event with id=" + eventId + " was not found"));
    }

    private void createNewHit(String ip, String url) {
        DtoHitIn endpointHitDto = new DtoHitIn(serviceName, url, ip, LocalDateTime.now());

        statClient.createEndpointHit(endpointHitDto);
    }

    private Event checkEvent(Long eventId) {
        return eventRepository.findById(eventId).orElseThrow(()
                -> new EventException("Event with id=" + eventId + " was not found"));
    }

    private User checkUser(Long userId) {
        return userRepository.findById(userId).orElseThrow(()
                -> new UserNotFoundException("Пользователь с ИД " + userId + " не найден"));
    }

    private void checkInitiator(Long userId, Long initiatorId) {
        if (!initiatorId.equals(userId)) {
            throw new EventException("Юзер с ИД: " + userId + " не является инициатором");
        }
    }

    private void loadComments(List<Event> events) {
        Map<Event, Long> comments = commentRepository.findAllByEventIn(events)
                .stream()
                .collect(groupingBy(Comment::getEvent, counting()));

        events.forEach(event -> event.setComments(comments.get(event)));

//        Map<Event, Set<Comment>> comments = commentRepository.findByEventIn(events)
//                .stream()
//                .collect(groupingBy(Comment::getEvent, toSet()));
//
//        events.forEach(event -> event.setComments(comments.getOrDefault(event, Collections.emptySet())));
    }
}
