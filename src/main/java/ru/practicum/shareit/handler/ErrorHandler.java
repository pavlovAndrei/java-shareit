package ru.practicum.shareit.handler;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.shareit.exception.BadRequestException;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {
//    @ExceptionHandler
//    @ResponseStatus(NOT_FOUND)
//    public ErrorResponse handleNotFoundException(final NotFoundException e) {
//        log.info("Error 404 {}", e.getMessage());
//        return new ErrorResponse(e.getMessage());
//    }
//    @ExceptionHandler
//    @ResponseStatus(NOT_FOUND)
//    public ErrorResponse handleOwnerException(final OwnerException e) {
//        log.info("Error 404 {}", e.getMessage());
//        return new ErrorResponse(e.getMessage());
//    }

//    @ExceptionHandler
//    @ResponseStatus(HttpStatus.BAD_REQUEST)
//    public ErrorResponse handleValidationException(final ValidationException e) {
//        log.info("Error 400 {}", e.getMessage());
//        return new ErrorResponse(e.getMessage());
//    }
//    @ExceptionHandler
//    @ResponseStatus(HttpStatus.CONFLICT)
//    public ErrorResponse handleEmailException(final EmailException e) {
//        log.info("Error 409 {}", e.getMessage());
//        return new ErrorResponse(e.getMessage());
//    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(BadRequestException.class)
    public ErrorResponse handleBadRequestException(BadRequestException e) {
        return new ErrorResponse(e.getMessage());
    }

//    @ResponseStatus(HttpStatus.BAD_REQUEST)
//    @ExceptionHandler(IllegalArgumentExceptionCustom.class)
//    public ErrorResponse handleIllegalArgumentExceptionCustom(IllegalArgumentExceptionCustom e) {
//        String error = "Unknown state: UNSUPPORTED_STATUS";
//        return new ErrorResponse(error);
//    }

    @Getter
    @RequiredArgsConstructor
    static class ErrorResponse {
        private final String error;
    }
}
