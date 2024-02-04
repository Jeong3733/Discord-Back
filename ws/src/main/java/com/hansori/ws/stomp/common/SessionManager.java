package com.hansori.ws.stomp.common;


public interface SessionManager {

    void put(final String sessionId, final Long userId);
    Long get(final String sessionId);
    void remove(final String sessionId);
}
