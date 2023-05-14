package ru.practicum.comments.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.comments.dto.CommentDto;
import ru.practicum.comments.dto.CommentDtoAdd;
import ru.practicum.comments.mapper.CommentMapper;
import ru.practicum.comments.model.Comment;
import ru.practicum.comments.repository.CommentRepository;
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

    @Override
    @Transactional
    public CommentDto add(Long userId, CommentDtoAdd commentDtoAdd) {
        User author = checkUser(userId);
        Event event = checkEvent(commentDtoAdd.getEventId());
        Comment newComment = CommentMapper.makeComment(commentDtoAdd);
        newComment.setUser(author);
        newComment.setEvent(event);
        log.info("Add new comment user ID {} to eventID {} with text {}", userId, commentDtoAdd.getEventId(),
                commentDtoAdd);
        return CommentMapper.makeCommentDto(commentRepository.save(newComment));
    }

    @Override
    public List<CommentDto> getAllByUser(Long userId) {
        User user = checkUser(userId);
        log.info("Find all comments by user = {}", user);
        return CommentMapper.makeListCommentDto(commentRepository.findAllByUser(user));
    }

    @Override
    public List<CommentDto> getAllByEventId(Long eventId) {
        Event event = checkEvent(eventId);
        log.info("Find all comments by event = {}", event);
        return CommentMapper.makeListCommentDto(commentRepository.findAllByEvent(event));
    }

    @Override
    public CommentDto getById(Long commId) {
            return CommentMapper.makeCommentDto(commentRepository.findById(commId).orElseThrow(()
            -> new NotFoundException("Comment with ID " + commId + " not found")));
    }

    @Override
    @Transactional
    public CommentDto update(Long commId, Long userId, CommentDtoAdd commentDtoAdd) {
        Comment comment = commentRepository.findById(commId).orElseThrow(()
                -> new NotFoundException("Comment with ID " + commId + " not found"));
        checkUser(userId);
        checkEvent(commentDtoAdd.getEventId());
        if (!comment.getUser().getId().equals(userId)) {
            throw new ConflictException("User with ID " + userId + " is not the author of the comment");
        }
        comment.setUpdated(LocalDateTime.now());
        comment.setText(commentDtoAdd.getText());
        return CommentMapper.makeCommentDto(commentRepository.save(comment));
    }

    @Override
    public void delete(Long commId) {
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
