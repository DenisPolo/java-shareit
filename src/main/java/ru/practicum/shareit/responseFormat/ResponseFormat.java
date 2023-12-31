package ru.practicum.shareit.responseFormat;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Getter
public class ResponseFormat {

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private final LocalDateTime time = LocalDateTime.now();
    private final HttpStatus status;
    private final String message;

    public ResponseFormat(String message, HttpStatus status) {
        this.message = message;
        this.status = status;
    }
}