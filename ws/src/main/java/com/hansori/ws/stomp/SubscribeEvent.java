package com.hansori.ws.stomp;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class SubscribeEvent {
    private long userId;
    private long roomId;
}
