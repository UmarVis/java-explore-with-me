package ru.practicum.comments.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.comments.model.Comment;
import ru.practicum.event.model.Event;
import ru.practicum.user.model.User;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findAllByUser(User user, Pageable pageable);

    List<Comment> findAllByEvent(Event event, Pageable pageable);

    List<Comment> findAllByEventIn(List<Event> events);
}
