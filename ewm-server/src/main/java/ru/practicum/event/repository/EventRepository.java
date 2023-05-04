package ru.practicum.event.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.enums.State;
import ru.practicum.event.model.Event;
import ru.practicum.user.model.User;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findAllByInitiator(User user, Pageable pageable);

    Event findAllByInitiatorAndId(User user, Long EventId);

    Page<Event> findAllByInitiator_IdInAndState_InAndCategory_IdInAndEventDateBetween(Collection<Long> initiatorId, Collection<State> state,
                                                                                      Collection<Long> categoryId, LocalDateTime rangeStart, LocalDateTime rangeEnd, Pageable pageable);

    List<Event> findAll(Specification<Event> specification, Pageable pageable);

    List<Event> findAllByCategoryId(Long id);

}
