package discord.api.common.exception;

import discord.api.entity.dtos.common.ErrorDto;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

// TODO : 오류 목록에 대해 노션에 정리하기
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(value = {RestApiException.class})
    public ResponseEntity<Object> handleRestApiException(RestApiException e) {
        ErrorCode errorCode = e.getErrorCode();
        log.error("handleRestApiException throw Exception : {}", errorCode);
        return makeResponseEntity(errorCode);
    }

    // JWT 관련 예외 처리
    @ExceptionHandler(value = {
            JwtException.class, UnsupportedJwtException.class,
            MalformedJwtException.class, SignatureException.class,
            ExpiredJwtException.class, IllegalArgumentException.class}
    )
    public ResponseEntity<Object> handleJwtException(Exception e) {
        String exceptionName = e.getClass().getSimpleName();

        ErrorCode errorCode = switch (exceptionName) {
            case "JwtException" -> ((JwtException) e).getErrorCode();

            case "UnsupportedJwtException" -> ErrorCode.JWT_UNSUPPORTED;

            case "MalformedJwtException" -> ErrorCode.JWT_MALFORMED;

            case "SignatureException" -> ErrorCode.JWT_SIGNATURE_INVALID;

            case "ExpiredJwtException" -> ErrorCode.JWT_EXPIRED;

            case "IllegalArgumentException" -> ErrorCode.JWT_ILLEGAL_CLAIM;

            default -> ErrorCode.JWT_EXCEPTION;
        };

        log.error("handleJwtException throw Exception : {}", errorCode);
        return makeResponseEntity(errorCode);
    }

    // 로그인 과정에서 발생하는 예외 처리
    @ExceptionHandler(value = {AuthenticationException.class})
    private ResponseEntity<Object> handleAuthenticationException(AuthenticationException e) {
        ErrorCode errorCode = ErrorCode.LOGIN_FAILED;
        log.error("handleAuthenticationException throw Exception : {}", errorCode);
        return makeResponseEntity(errorCode);
    }

    @ExceptionHandler(value = {Exception.class})
    public ResponseEntity<Object> handleServerException(Exception e) {
        ErrorCode errorCode = ErrorCode.INTERNAL_SERVER_ERROR;
        log.error("handleServerException throw Exception : {}", errorCode);
        e.printStackTrace();
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
