package ru.practicum.comments.service;

import ru.practicum.comments.dto.CommentDto;
import ru.practicum.comments.dto.CommentDtoAdd;

import java.util.List;

public interface CommentService {
    CommentDto add(Long userId, CommentDtoAdd commentDtoAdd);

    List<CommentDto> getAllByUser(Long userId);

    List<CommentDto> getAllByEventId(Long eventId);

    CommentDto getById(Long commId);

    CommentDto update(Long commId, Long userId, CommentDtoAdd commentDtoAdd);

    void delete(Long commId);
}
