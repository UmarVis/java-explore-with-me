package ru.practicum.comments.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class CommentDtoAdd {
    @NotNull
    Long event_id;
    @NotBlank(message = "Нельзя оставить пустой комментарий")
    @Size(max = 2000)
    String text;
}
