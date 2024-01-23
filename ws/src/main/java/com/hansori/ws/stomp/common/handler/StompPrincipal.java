package com.hansori.ws.stomp.common.handler;

import lombok.AllArgsConstructor;

import java.security.Principal;

@AllArgsConstructor
public final class StompPrincipal implements Principal {

    private  final String name;

    @Override
    public String getName() {
        return name;
    }


}
