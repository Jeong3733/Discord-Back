package com.hansori.ws.stomp.dto.response.error;

import lombok.Getter;
import lombok.ToString;

@Getter
public enum ErrorCode {

    USER_NOT_FOUND(1001),
    USER_NOT_AUTHORIZED(1005),
    INTERNAL_SERVER_ERROR(5001);



    private final int code;


    ErrorCode(int code) {
        this.code = code;
    }


}
