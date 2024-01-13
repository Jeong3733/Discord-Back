package discord.api.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    // 400 Bad Request


    // 401 Unauthorized


    // 404 Not Found
    EMAIL_NOT_FOUND(HttpStatus.NOT_FOUND, "4040", "이메일이 존재하지 않습니다."),
    PASSWORD_INVALID(HttpStatus.NOT_FOUND, "4041", "부적절한 비밀번호 입니다.");

    // 인증 실패
    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
