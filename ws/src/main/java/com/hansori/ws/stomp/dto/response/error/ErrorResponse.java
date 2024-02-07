package com.hansori.ws.stomp.dto.response.error;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Builder
public class ErrorResponse {

    private String message;
    private int code;

}
