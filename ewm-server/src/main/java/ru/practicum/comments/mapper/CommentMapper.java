package ru.practicum.comments.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.comments.dto.CommentDto;
import ru.practicum.comments.dto.CommentDtoAdd;
import ru.practicum.comments.model.Comment;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.constant.Constant.DATE_TIME;

@UtilityClass
public class CommentMapper {
    public Comment makeComment(CommentDtoAdd commentDtoAdd) {
        return Comment.builder()
                .created(LocalDateTime.now())
                .updated(LocalDateTime.now())
                .text(commentDtoAdd.getText())
                .build();
    }

    public CommentDto makeCommentDto(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .created(comment.getCreated().format(DATE_TIME))
                .updated(comment.getUpdated().format(DATE_TIME))
                .text(comment.getText())
                .eventId(comment.getEvent().getId())
                .userId(comment.getUser().getId())
                .build();
    }

    public List<CommentDto> makeListCommentDto(List<Comment> comments) {
        return comments.stream().map(CommentMapper::makeCommentDto).collect(Collectors.toList());
    }
}
