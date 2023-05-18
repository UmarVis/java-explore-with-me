package ru.practicum.comments.service;

import ru.practicum.comments.dto.CommentDto;
import ru.practicum.comments.dto.CommentDtoAdd;
import ru.practicum.comments.dto.CommentDtoUpdate;

import java.util.List;

public interface CommentService {
    CommentDto add(Long userId, CommentDtoAdd commentDtoAdd);

    List<CommentDto> getAllByUser(Long userId, int from, int size);

    List<CommentDto> getAllByEventId(Long eventId, int from, int size);

    CommentDto getById(Long commId);

    CommentDto update(Long commId, Long userId, CommentDtoUpdate commentDtoUpdate);

    void deleteByUser(Long commId, Long userId);

    void adminDelete(Long commId);
}
