package ru.practicum.comments.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.comments.dto.CommentDto;
import ru.practicum.comments.service.CommentService;

import java.util.List;

@RestController
@RequestMapping("/comments/admin")
@AllArgsConstructor
public class CommentAdminController {
    private final CommentService commentService;

    @GetMapping("/{commId}")
    public CommentDto getById(@PathVariable Long commId) {
        return commentService.getById(commId);
    }

    @GetMapping("/user/{userId}")
    public List<CommentDto> getAllByUser(@PathVariable Long userId,
                                         @RequestParam(defaultValue = "0") Integer from,
                                         @RequestParam(defaultValue = "10") Integer size) {
        return commentService.getAllByUser(userId, from, size);
    }

    @DeleteMapping("/{commId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void adminDelete(@PathVariable Long commId) {
        commentService.adminDelete(commId);
    }
}
