package ru.practicum.comments.controller;

import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.comments.dto.CommentDto;
import ru.practicum.comments.service.CommentService;

import java.util.List;

@RestController
@RequestMapping("/comments/events")
@AllArgsConstructor
@Validated
public class CommentPublicController {
    private final CommentService commentService;

    @GetMapping("/{eventId}")
    public List<CommentDto> getAllByEventId(@PathVariable Long eventId,
                                            @RequestParam(defaultValue = "0") Integer from,
                                            @RequestParam(defaultValue = "10") Integer size) {
        return commentService.getAllByEventId(eventId, from, size);
    }
}
