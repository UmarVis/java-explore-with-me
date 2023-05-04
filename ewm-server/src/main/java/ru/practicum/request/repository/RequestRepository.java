package ru.practicum.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.enums.Status;
import ru.practicum.request.model.Request;
import ru.practicum.user.model.User;

import java.util.List;

public interface RequestRepository extends JpaRepository<Request, Long> {
    List<Request> findAllByRequesterIdAndEventId(Long userId, Long eventId);

    Long countAllByEventId(Long eventId);

    List<Request> findAllByRequester(User user);

    List<Request> findAllByEventId(Long eventId);

    Long countRequestByEventIdAndStatusEquals(Long eventId, Status status);

    List<Request> findAllByIdIn(List<Long> ids);
}
