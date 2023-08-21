package ru.practicum.shareit.item.comment;

import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.model.Comment;

public class CommentMapper {

    public static CommentDto mapToCommentDto(Comment comment) {

        return new CommentDto(comment.getId(), comment.getAuthor().getName(), comment.getText(), comment.getCreationDate());
    }
}