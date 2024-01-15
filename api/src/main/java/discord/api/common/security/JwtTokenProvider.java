package discord.api.common.security;

import discord.api.common.GlobalConstant;
import discord.api.common.exception.ErrorCode;
import discord.api.common.exception.JwtException;
import discord.api.entity.dtos.TokenResponseDto;
import discord.api.entity.enums.TokenType;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenProvider implements InitializingBean {

    @Value("${app.jwt.secret}")
    private String secretKey;
    private Key key;

    /**
     * 빈 초기화시 key 미리 생성
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        byte[] secretKeyBytes = secretKey.getBytes();
        key = Keys.hmacShaKeyFor(secretKeyBytes);
    }

    /**
     * Access 토큰 생성하는 메서드
     *
     * @param email : 이메일
     * @return String : 토큰
     * @throws io.jsonwebtoken.security.InvalidKeyException : 키가 유효하지 않을 때 예외 발생
     * @author Jae Wook Jeong
     */
    public TokenResponseDto generateToken(String email) {
        log.info("JwtTokenProvider.generateToken() working");

        Date now = new Date();

        String accessToken = Jwts.builder()
                .signWith(key, SignatureAlgorithm.HS512)
                .setExpiration(new Date(now.getTime() + GlobalConstant.ACCESS_EXP_TIME))
                .setSubject(email)
                .claim("type", TokenType.ACCESS)
                .compact();

        String refreshToken = Jwts.builder()
                .signWith(key, SignatureAlgorithm.HS512)
                .setExpiration(new Date(now.getTime() + GlobalConstant.REFRESH_EXP_TIME))
                .setSubject(email)
                .claim("type", TokenType.REFRESH)
                .compact();

        return TokenResponseDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    /**
     * 토큰 검증 & 클레임 추출
     *
     * @param token : 토큰
     * @return Claims : 클레임
     * @author Jae Wook Jeong
     */
    public Claims validateAndGetClaims(String token) {
        log.info("JwtTokenProvider.validateAndGetClaims() working");
        byte[] secretKeyBytes = secretKey.getBytes();

       return Jwts.parserBuilder()
                .setSigningKey(secretKeyBytes)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Request Header 에서 토큰 추출하는 메서드
     *
     * @param request : HttpServletRequest
     * @return String : 토큰 / null (Authorization Header 가 없을 때)
     * @author Jae Wook Jeong
     */
    public String resolve(HttpServletRequest request) {
        log.info("JwtTokenProvider.resolve() working");
        String authorization = request.getHeader(GlobalConstant.TOKEN_HEADER);

        // 토큰이 없으면 null 반환
        if (authorization == null) {
            return null;
        }

        if (authorization.startsWith(GlobalConstant.TOKEN_PREFIX))
            return authorization.replace(GlobalConstant.TOKEN_PREFIX, "");

        throw new JwtException(ErrorCode.JWT_AUTHORIZATION_HEADER_INVALID);
    }
}
