package ru.practicum.shareit.exception;

import lombok.extern.slf4j.Slf4j;
import org.postgresql.util.PSQLException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.responseFormat.ResponseFormat;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestControllerAdvice
public class ExceptionHandler {

    @org.springframework.web.bind.annotation.ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseFormat methodArgumentNotValidExceptionHandle(MethodArgumentNotValidException e) {
        String defaultMessage;

            List<String> messages = new ArrayList<>(List.of(e.getMessage().split(";")));
            defaultMessage = messages.get(messages.size() - 1).replaceAll(".*\\[|\\].*", "");
        log.warn(defaultMessage);
        return new ResponseFormat(defaultMessage, HttpStatus.BAD_REQUEST);
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(BadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseFormat runtimeExceptionHandle(BadRequestException e) {
        log.warn(e.getMessage());
        return new ResponseFormat(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @org.springframework.web.bind.annotation.ExceptionHandler({AlreadyExistsException.class, PSQLException.class,
            Exception.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponseFormat alreadyExistsExceptionHandle(Exception e) {
        log.warn(e.getMessage());
        if (e.getMessage().contains("QueryState.")) {
            String[] splitMessage = e.getMessage().split("\\.");
            return new ErrorResponseFormat("Unknown state: " + splitMessage[splitMessage.length - 1],
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ErrorResponseFormat(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseFormat notFoundExceptionHandle(NotFoundException e) {
        log.warn(e.getMessage());
        return new ResponseFormat(e.getMessage(), HttpStatus.NOT_FOUND);
    }
}