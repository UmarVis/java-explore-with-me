package ru.practicum.comments.controller;

import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.comments.dto.CommentDto;
import ru.practicum.comments.service.CommentService;

import java.util.List;

@RestController
@RequestMapping("/comments/admin")
@AllArgsConstructor
@Validated
public class CommentAdminController {
    private final CommentService commentService;

    @GetMapping("/{commId}")
    public CommentDto getById(@PathVariable Long commId) {
        return commentService.getById(commId);
    }

    @GetMapping("/user/{userId}")
    public List<CommentDto> getAllByUser(@PathVariable Long userId) {
        return commentService.getAllByUser(userId);
    }

    @GetMapping("/events/{eventId}")
    public List<CommentDto> getAllByEventId(@PathVariable Long eventId) {
        return commentService.getAllByEventId(eventId);
    }
}
