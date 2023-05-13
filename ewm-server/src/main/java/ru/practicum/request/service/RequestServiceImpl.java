package ru.practicum.request.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.enums.State;
import ru.practicum.enums.Status;
import ru.practicum.event.exception.EventException;
import ru.practicum.event.model.Event;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.ConflictException;
import ru.practicum.request.dto.ParticipationRequestDto;
import ru.practicum.request.exception.RequestException;
import ru.practicum.request.mapper.RequestMapper;
import ru.practicum.request.model.Request;
import ru.practicum.request.repository.RequestRepository;
import ru.practicum.user.exception.UserNotFoundException;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class RequestServiceImpl implements RequestService {
    private final RequestRepository requestRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public ParticipationRequestDto add(Long userId, Long eventId) {
        log.info("Add new request, user ID {}, event ID {}", userId, eventId);
        User user = checkUser(userId);
        Event event = checkEvent(eventId);
        if (event.getInitiator().getId().equals(userId)) {
            throw new ConflictException("Инициатор события не может добавить запрос на участие в своём событии");
        }
        if (!event.getState().equals(State.PUBLISHED)) {
            throw new ConflictException("Нельзя участвовать в неопубликованном событии");
        }
        if (requestRepository.findAllByRequesterIdAndEventId(userId, eventId).size() != 0) {
            throw new ConflictException("Нельзя добавить повторный запрос");
        }
        if (event.getParticipantLimit() != 0 && event.getParticipantLimit() <= requestRepository.countAllByEventId(eventId)) {
            throw new ConflictException("У события достигнут лимит запросов на участие");
        }
        Request request = makeRequest(event, user);
        return RequestMapper.makeDtoOut(requestRepository.save(request));
    }

    @Override
    public List<ParticipationRequestDto> getAll(Long userId) {
        log.info("Get all request with user ID {}", userId);
        User user = checkUser(userId);
        List<Request> requests = requestRepository.findAllByRequester(user);
        return RequestMapper.makeListDtoOut(requests);
    }

    @Override
    public ParticipationRequestDto cancelRequest(Long userId, Long requestId) {
        log.info("Set cancel status request, user ID {}, request ID {}", userId, requestId);
        checkUser(userId);
        Request request = requestRepository.findById(requestId).orElseThrow(()
                -> new RequestException("Request " + requestId + " not found"));
        if (!request.getRequester().getId().equals(userId)) {
            throw new RequestException("Нельзя отменить чужой запрос");
        }
        request.setStatus(Status.CANCELED);
        return RequestMapper.makeDtoOut(requestRepository.save(request));
    }

    private Event checkEvent(Long eventId) {
        return eventRepository.findById(eventId).orElseThrow(()
                -> new EventException("Event with id=" + eventId + " was not found"));
    }

    private User checkUser(Long userId) {
        return userRepository.findById(userId).orElseThrow(()
                -> new UserNotFoundException("Пользователь с ИД " + userId + " не найден"));
    }

    private Request makeRequest(Event event, User user) {
        return Request.builder()
                .created(LocalDateTime.now())
                .requester(user)
                .event(event)
                .status(event.getRequestModeration() ? Status.PENDING : Status.CONFIRMED)
                .build();
    }
}
