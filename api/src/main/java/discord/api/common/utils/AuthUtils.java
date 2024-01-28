package discord.api.common.utils;

import discord.api.common.security.userdetails.CustomUserDetails;
import discord.api.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class AuthUtils {
    private final AuthenticationManager authenticationManager;

    /**
     * Login 하는 함수
     *
     * @param id : 사용자 pk
     * @param password : 사용자 비밀번호
     * @throws org.springframework.security.core.AuthenticationException : 인증 실패 시 예외 발생
     * @author Jae Wook Jeong
     */
    public void authenticate(Long id, String password) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(String.valueOf(id), password));
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    public User  getUserFromAuthentication(Authentication authentication) {
        return ((CustomUserDetails) authentication.getPrincipal()).getUser();
    }
}
