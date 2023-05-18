package ru.practicum.event.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.enums.State;
import ru.practicum.event.model.Event;
import ru.practicum.user.model.User;

import java.util.List;
import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findAllByInitiator(User user, Pageable pageable);

    Event findAllByInitiatorAndId(User user, Long eventId);

    List<Event> findAll(Specification<Event> specification, Pageable pageable);

    Boolean existsAllByCategoryId(Long id);

    Optional<Event> findEventByIdAndState(Long id, State state);

    List<Event> findAllByInitiator(User user);
}
