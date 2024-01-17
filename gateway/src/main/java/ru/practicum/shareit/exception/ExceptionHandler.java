package ru.practicum.shareit.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestControllerAdvice
public class ExceptionHandler {

    @org.springframework.web.bind.annotation.ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponseFormat methodArgumentNotValidExceptionHandle(MethodArgumentNotValidException e) {
        String defaultMessage;

        List<String> messages = new ArrayList<>(List.of(e.getMessage().split(";")));
        defaultMessage = messages.get(messages.size() - 1).replaceAll(".*\\[|\\].*", "");
        log.warn(defaultMessage);
        return new ErrorResponseFormat(defaultMessage, HttpStatus.BAD_REQUEST);
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponseFormat exceptionHandle(Exception e) {
        log.warn(e.getClass() + "; " + e.getMessage());
        if (e.getMessage().contains("QueryState.")) {
            String[] splitMessage = e.getMessage().split("\\.");
            return new ErrorResponseFormat("Unknown state: " + splitMessage[splitMessage.length - 1],
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ErrorResponseFormat(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}