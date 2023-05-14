package ru.practicum.comments.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class CommentDto {
    Long id;

    String created;

    String updated;

    String text;

    Long event_id;

    Long user_id;
}
