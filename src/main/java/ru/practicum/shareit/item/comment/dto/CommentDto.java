package ru.practicum.shareit.item.comment.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@AllArgsConstructor
public class CommentDto {
    private Long id;
    private String authorName;
    private String text;
    private LocalDateTime created;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CommentDto that = (CommentDto) o;
        return id.equals(that.id)
                && authorName.equals(that.authorName)
                && text.equals(that.text);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, authorName, text);
    }
}