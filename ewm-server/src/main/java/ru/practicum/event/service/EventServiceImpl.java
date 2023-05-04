package ru.practicum.event.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.category.exception.CategoryException;
import ru.practicum.category.mapper.CategoryMapper;
import ru.practicum.category.model.Category;
import ru.practicum.category.repository.CategoryRepository;
import ru.practicum.client.StatClient;
import ru.practicum.dto.DtoHitIn;
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
import ru.practicum.exception.NotFoundException;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static ru.practicum.enums.Status.CONFIRMED;

@Service
@AllArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class EventServiceImpl implements EventService {
    public static final Sort SORT_BY_ASC = Sort.by(Sort.Direction.ASC, "id");
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final CategoryRepository categoryRepository;
    private final LocationRepository locationRepository;
    private final RequestRepository requestRepository;
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
        newEvent.setLocation(locationRepository.save(eventDtoIn.getLocation()));
        return EventMapper.makeEventFullDto(eventRepository.save(newEvent));
    }

    @Override
    public List<EventDto> getByUserId(Long userId, int from, int size) {
        log.info("Get event with user ID {} from {}, size {}", userId, from, size);
        User user = checkUser(userId);
        Pageable pageable = PageRequest.of(from / size, size, SORT_BY_ASC);
        return EventMapper.makeListEventDto(eventRepository.findAllByInitiator(user, pageable));
    }

    @Override
    public EventDtoFull getByUserIdByEventId(Long userId, Long eventId) {
        log.info("Get by user ID {} and event ID {}", userId, eventId);
        User user = checkUser(userId);
        Event event = checkEvent(eventId);
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
        if (dtoUpdate.getAnnotation() != null) {
            event.setAnnotation(dtoUpdate.getAnnotation());
        }
        if (dtoUpdate.getDescription() != null) {
            event.setDescription(dtoUpdate.getDescription());
        }
        if (dtoUpdate.getTitle() != null) {
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
        for (Event event : eventRepository.findAllByInitiator_IdInAndState_InAndCategory_IdInAndEventDateBetween(users,
                states, categories, rangeStart, rangeEnd, pageable)) {
            dtoList.add(EventMapper.makeEventFullDto(event));
        }
        System.out.println(dtoList);
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
    public List<EventDto> getAllPublic(String text, List<Long> categories, Boolean paid, LocalDateTime rangeStart,
                                       LocalDateTime rangeEnd, Boolean onlyAvailable, Pageable pageable, String ip, String url) {
        log.info("Search events public with parameters: text {}, categories ID {}, paid {}, rangeStart {}, rangeEnd {}" +
                        "onlyAvailable {}, pageable {}, ip {}, url {}", text, categories, paid, rangeStart, rangeEnd, onlyAvailable,
                pageable, ip, url);
        createNewHit(ip, url);
        List<EventDto> eventDtoList = new ArrayList<>();
        LocalDateTime startDate = Objects.requireNonNullElseGet(rangeStart, () -> LocalDateTime.now().plusYears(30));
        LocalDateTime endDate = Objects.requireNonNullElseGet(rangeEnd, () -> LocalDateTime.now().minusYears(30));
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
            predicates.add(builder.greaterThan(root.get("eventDate"), startDate));
            predicates.add(builder.lessThan(root.get("eventDate"), endDate));
            if (onlyAvailable != null && onlyAvailable) {
                predicates.add(builder.or(builder.equal(root.get("participantLimit"), 0),
                        builder.and(builder.notEqual(root.get("participantLimit"), 0),
                                builder.greaterThan(root.get("participantLimit"), subQuery.select(builder.count(requestRoot.get("id")))
                                        .where(builder.equal(eventsRequests.get("id"), requestRoot.get("event").get("id")))
                                        .where(builder.equal(requestRoot.get("status"), CONFIRMED))))));

            }
            return builder.and(predicates.toArray(new Predicate[0]));
        };
        for (Event event : eventRepository.findAll(specification, pageable)) {
            eventDtoList.add(makeEventDtoOut(event));
        }
        return eventDtoList;
    }

    @Override
    public EventDtoFull getByIdPublic(Long id, String ip, String url) {
        log.info("Get public event with ID {}", id);
        createNewHit(ip, url);
        return EventMapper.makeEventFullDto(checkEvent(id));
    }


    private void createNewHit(String ip, String url) {
        String serviceName = "ewm-server";
        DtoHitIn endpointHitDto = new DtoHitIn(serviceName, url, ip, LocalDateTime.now());

        statClient.createEndpointHit(endpointHitDto);
    }

    private EventDto makeEventDtoOut(Event event) {
        EventDto dtoOut = EventMapper.makeEventDto(event);
        dtoOut.setCategory(CategoryMapper.makeCategoryDto(categoryRepository.findById(event.getCategory().getId()).orElseThrow(() ->
                new NotFoundException("Category with id: " + event.getCategory().getId() + " not found"))));
        return dtoOut;
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
}
