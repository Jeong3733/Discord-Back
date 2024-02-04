package com.hansori.ws.stomp.common;

import com.hansori.ws.stomp.dto.response.error.CustomException;
import com.hansori.ws.stomp.dto.response.error.ErrorCode;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

@Component
public class HashSessionManager implements SessionManager{

    private final ConcurrentHashMap<String, Long> userMap = new ConcurrentHashMap<>();

    @Override
    public void put(final String sessionId, final Long userId) {
        userMap.put(sessionId, userId);
    }

    @Override
    public Long get(final String sessionId) {
        return userMap.get(sessionId);
    }

    @Override
    public void remove(final String sessionId) {
        userMap.remove(sessionId);
        for (String s : userMap.keySet()) {
            System.out.println(s);
        }
    }



}
