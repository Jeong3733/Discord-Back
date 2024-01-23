package com.hansori.ws.stomp.dto.response.error;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class CustomException extends RuntimeException{

    private ErrorCode errorCode;

}
