package ru.practicum.shareit.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.ToString;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Getter
public class ErrorResponseFormat {

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private final LocalDateTime time = LocalDateTime.now();
    private final HttpStatus status;

    private final String error;

    public ErrorResponseFormat(String message, HttpStatus status) {
        this.error = message;
        this.status = status;
    }
}