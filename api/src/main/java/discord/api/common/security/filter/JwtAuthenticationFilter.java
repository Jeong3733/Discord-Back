package discord.api.common.security.filter;

import discord.api.common.security.userdetails.CustomUserDetails;
import discord.api.common.security.userdetails.CustomUserDetailsService;
import discord.api.common.security.JwtTokenProvider;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;

@Slf4j
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtTokenProvider jwtTokenProvider;
    private final CustomUserDetailsService customUserDetailsService;
    private final HandlerExceptionResolver resolver;

    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider, CustomUserDetailsService customUserDetailsService, @Qualifier("handlerExceptionResolver") HandlerExceptionResolver resolver) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.customUserDetailsService = customUserDetailsService;
        this.resolver = resolver;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        log.info("===== JwtAuthenticationFilter working =====");

        try {
            String token = jwtTokenProvider.resolve(request);

            // Authorization 토큰이 비었을 경우는 아래의 로직을 진행하지 않고 그냥 진행
            if (token != null) {
                Claims claims = jwtTokenProvider.validateAndGetClaims(token);

                String email = claims.getSubject();
                CustomUserDetails userDetails = customUserDetailsService.loadUserByUsername(email);
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities()
                );

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }

            filterChain.doFilter(request, response);

        } catch (Exception e) {
            log.error("JwtAuthenticationFilter working : {}", e.getMessage());
            resolver.resolveException(request, response, null, e);
        }
    }
}
