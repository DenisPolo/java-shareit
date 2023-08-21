package ru.practicum.shareit.item.comment.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Entity
@Table(name = "comments", schema = "public")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User author;

    @NotNull
    @Column(name = "item_id", nullable = false)
    private Long itemId;

    @Size(max = 300, message = "Комментарий не должен превышать 300 символов")
    @Column(name = "comment_text", nullable = false)
    private String text;

    @Column(name = "creation_date")
    private LocalDateTime creationDate = LocalDateTime.now();

    public Comment(User author, Long itemId, String text) {
        this.author = author;
        this.itemId = itemId;
        this.text = text;
    }
}