package com.hansori.ws;

import com.hansori.ws.stomp.common.constant.JwtConstant;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.util.Date;



public final class JwtTokenProvider {

    private JwtTokenProvider() {
        throw new IllegalStateException("Utility class");
    }

    public static String generateToken(Long id) {

        byte[] bytes = JwtConstant.SECRET_KEY.getBytes();

        Date date = new Date();
        return JwtConstant.TOKEN_PREFIX + Jwts.builder()
                .signWith(Keys.hmacShaKeyFor(bytes), SignatureAlgorithm.HS512)
                .setExpiration(new Date(date.getTime() + 30000000))
                .claim("id", id)
                .compact();
    }
}
