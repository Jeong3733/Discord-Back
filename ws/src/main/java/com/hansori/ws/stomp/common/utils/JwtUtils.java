package com.hansori.ws.stomp.common.utils;

import com.hansori.ws.stomp.common.constant.JwtConstant;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public final class JwtUtils {

    public static boolean validateToken(final String token) {

        try {


            Jwts.parserBuilder()
                    .setSigningKey(JwtConstant.SECRET_KEY.getBytes())
                    .build()
                    .parseClaimsJws(replaceTokenPrefix(token));

            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            log.error("잘못된 JWT 서명입니다.");
        } catch (io.jsonwebtoken.ExpiredJwtException e) {
            log.error("만료된 JWT 토큰입니다.");
        } catch (io.jsonwebtoken.UnsupportedJwtException e) {
            log.error("지원되지 않는 JWT 토큰입니다.");
        } catch (Exception e) {
            log.error("JWT 토큰이 잘못되었습니다.");
        }

        log.info("is not valid token");
        return false;
    }

    public static Claims parseToken(final String token) {

        return Jwts.parserBuilder()
                .setSigningKey(JwtConstant.SECRET_KEY.getBytes())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public static long getUserIdFromToken(final String token) {
        final Claims claims = parseToken(replaceTokenPrefix(token));
        return claims.get("id", Long.class);
    }

    public static String replaceTokenPrefix(final String token) {
        return token.replace(JwtConstant.TOKEN_PREFIX, "");
    }


}
