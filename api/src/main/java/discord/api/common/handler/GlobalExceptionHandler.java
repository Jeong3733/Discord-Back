package discord.api.common.handler;

import discord.api.common.enums.ErrorCode;
import discord.api.dtos.ErrorDto;
import discord.api.common.exception.RestApiException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(value = {RestApiException.class})
    public ResponseEntity<Object> handleApiException(RestApiException e) {
        ErrorCode errorCode = e.getErrorCode();
        log.error("handleApiException throw RestApiException : {}", errorCode);
        return makeResponseEntity(errorCode);
    }

    private ResponseEntity<Object> makeResponseEntity(ErrorCode errorCode) {
        return ResponseEntity.status(errorCode.getHttpStatus())
                .body(ErrorDto.builder()
                        .code(errorCode.getCode())
                        .message(errorCode.getMessage())
                        .build());
    }
}
