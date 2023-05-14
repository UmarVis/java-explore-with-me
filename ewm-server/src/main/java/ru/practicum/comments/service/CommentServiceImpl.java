package ru.practicum.comments.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.comments.dto.CommentDto;
import ru.practicum.comments.dto.CommentDtoAdd;
import ru.practicum.comments.dto.CommentDtoUpdate;
import ru.practicum.comments.mapper.CommentMapper;
import ru.practicum.comments.model.Comment;
import ru.practicum.comments.repository.CommentRepository;
import ru.practicum.enums.State;
import ru.practicum.event.model.Event;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    public static final Sort SORT_BY_ASC = Sort.by(Sort.Direction.ASC, "id");

    @Override
    @Transactional
    public CommentDto add(Long userId, CommentDtoAdd commentDtoAdd) {
        User author = checkUser(userId);
        Event event = checkEvent(commentDtoAdd.getEventId());
        if (!event.getState().equals(State.PUBLISHED)) {
            throw new ConflictException("Комментарий можно оставить только к опубликованному событию");
        }
        Comment newComment = CommentMapper.makeComment(commentDtoAdd);
        newComment.setUser(author);
        newComment.setEvent(event);
        log.info("Add new comment user ID {} to eventID {} with text {}", userId, commentDtoAdd.getEventId(),
                commentDtoAdd);
        return CommentMapper.makeCommentDto(commentRepository.save(newComment));
    }

    @Override
    public List<CommentDto> getAllByUser(Long userId, int from, int size) {
        User user = checkUser(userId);
        Pageable pageable = PageRequest.of(from / size, size, SORT_BY_ASC);
        log.info("Find all comments by user = {}", user);
        return CommentMapper.makeListCommentDto(commentRepository.findAllByUser(user, pageable));
    }

    @Override
    public List<CommentDto> getAllByEventId(Long eventId, int from, int size) {
        Event event = checkEvent(eventId);
        if (!event.getState().equals(State.PUBLISHED)) {
            throw new ConflictException("Получить комментарии можно только для опубликованного события");
        }
        Pageable pageable = PageRequest.of(from / size, size, SORT_BY_ASC);
        log.info("Find all comments by event = {}", event);
        return CommentMapper.makeListCommentDto(commentRepository.findAllByEvent(event, pageable));
    }

    @Override
    public CommentDto getById(Long commId) {
        return CommentMapper.makeCommentDto(commentRepository.findById(commId).orElseThrow(()
                -> new NotFoundException("Comment with ID " + commId + " not found")));
    }

    @Override
    @Transactional
    public CommentDto update(Long commId, Long userId, CommentDtoUpdate commentDtoUpdate) {
        Comment comment = commentRepository.findById(commId).orElseThrow(()
                -> new NotFoundException("Comment with ID " + commId + " not found"));
        checkUser(userId);
        checkEvent(comment.getId());
        if (!comment.getUser().getId().equals(userId)) {
            throw new ConflictException("User with ID " + userId + " is not the author of the comment");
        }
        if (comment.getCreated().isAfter(LocalDateTime.now().plusHours(1))) {
            throw new ConflictException("Редактирование комментария доступно только в течение часа после создания");
        }
        comment.setUpdated(LocalDateTime.now());
        comment.setText(commentDtoUpdate.getText());
        return CommentMapper.makeCommentDto(commentRepository.save(comment));
    }

    @Override
    public void deleteByUser(Long commId, Long userId) {
        checkUser(userId);
        Comment comment = commentRepository.findById(commId).orElseThrow(()
                -> new NotFoundException("Comment with ID " + commId + " not found"));
        if (!userId.equals(comment.getUser().getId())) {
            throw new ConflictException("User with ID " + userId + " is not the author of the comment");
        }
        commentRepository.delete(comment);
    }

    @Override
    public void adminDelete(Long commId) {
        Comment comment = commentRepository.findById(commId).orElseThrow(()
                -> new NotFoundException("Comment with ID " + commId + " not found"));
        commentRepository.delete(comment);
    }

    private Event checkEvent(Long eventId) {
        return eventRepository.findById(eventId).orElseThrow(()
                -> new NotFoundException("Event with ID " + eventId + " not found"));
    }

    private User checkUser(Long userId) {
        return userRepository.findById(userId).orElseThrow(()
                -> new NotFoundException("User with ID " + userId + " not found"));
    }
}
