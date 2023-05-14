package ru.practicum.comments.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommentDtoUpdate {
    @NotBlank(message = "Нельзя оставить пустой комментарий")
    @Size(max = 2000)
    String text;
}
