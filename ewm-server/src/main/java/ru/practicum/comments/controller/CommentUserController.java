package ru.practicum.comments.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.comments.dto.CommentDto;
import ru.practicum.comments.dto.CommentDtoAdd;
import ru.practicum.comments.service.CommentService;

import javax.validation.constraints.Positive;

@RestController
@RequestMapping("/comments")
@AllArgsConstructor
@Validated
public class CommentUserController {
    private final CommentService commentService;

    @PostMapping("/user/{userId}")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto add(@PathVariable @Positive Long userId,
                          @RequestBody @Validated CommentDtoAdd commentDtoAdd) {
        return commentService.add(userId, commentDtoAdd);
    }

    @PatchMapping("/{commId}/user/{userId}")
    public CommentDto update(@PathVariable Long commId, @PathVariable Long userId,
                             @Validated @RequestBody CommentDtoAdd commentDtoAdd) {
        return commentService.update(commId, userId, commentDtoAdd);
    }

    @DeleteMapping("/{commId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long commId) {
        commentService.delete(commId);
    }
}
