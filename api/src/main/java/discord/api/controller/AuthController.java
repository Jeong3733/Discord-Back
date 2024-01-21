package discord.api.controller;

import discord.api.common.exception.ErrorCode;
import discord.api.common.exception.JwtException;
import discord.api.entity.User;
import discord.api.entity.VerificationToken;
import discord.api.entity.dtos.LoginRequestDto;
import discord.api.entity.dtos.SignUpRequestDto;
import discord.api.entity.dtos.TokenResponseDto;
import discord.api.entity.enums.VerificationTokenStatus;
import discord.api.service.AuthService;
import discord.api.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Slf4j
@RestController
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final UserService userService;
    private final AuthenticationManager authenticationManager;

    /**
     * 회원가입
     *
     * @param signUpRequestDto : 회원가입 요청 정보
     * @author Jae Wook Jeong
     */
    @PostMapping("/signUp")
    public ResponseEntity<Boolean> signUp(final @RequestBody SignUpRequestDto signUpRequestDto) {
        authService.signUp(signUpRequestDto);
        return new ResponseEntity<>(true, HttpStatus.OK);
    }

    /**
     * 로그인
     *
     * @param loginRequestDto : 로그인 요청 정보
     * @return TokenResponseDto : Access Token, Refresh Token
     * @author Jae Wook Jeong
     */
    @PostMapping("/login")
    public ResponseEntity<TokenResponseDto> signIn(final @RequestBody LoginRequestDto loginRequestDto) {
        String email = loginRequestDto.getEmail();
        String password = loginRequestDto.getPassword();

        User user = userService.getUserByEmail(email);
        Long id = user.getId();

        authenticate(id, password);
        TokenResponseDto tokenResponseDto = authService.generateTokens(id);
        return new ResponseEntity<>(tokenResponseDto, HttpStatus.OK);
    }


    /**
     * Access Token 이 만료되어 Refresh Token 을 이용해 재발급
     * Filter 를 통과했으므로 Refresh Token 은 유효하다고 가정
     * Filter 에서 SecurityContextHolder 에 저장한 Authentication 을 이용해 email 을 가져옴
     *
     * @param authentication : SecurityContextHolder 에 저장된 Authentication
     * @return TokenResponseDto : Access Token & Refresh Token
     * @author Jae Wook Jeong
     */
    @PostMapping("/refresh")
    public ResponseEntity<TokenResponseDto> refresh(Authentication authentication) {
        long id = Long.parseLong(authentication.getName());
        Boolean validRefreshToken = authService.isRefreshTokenExists(id);

        if (validRefreshToken) {
            TokenResponseDto tokenResponseDto = authService.generateTokens(id);
            return new ResponseEntity<>(tokenResponseDto, HttpStatus.OK);
        }

        throw new JwtException(ErrorCode.JWT_REFRESH_TOKEN_INVALID);
    }

    /**
     * 이메일 인증
     * @param token : 이메일 인증 토큰
     * @return VerificationTokenStatus : 이메일 인증 상태 (INVALID, EXPIRED, VALID)
     *
     * @author Jae Wook Jeong
     */
    @GetMapping("/verify")
    private ResponseEntity<VerificationTokenStatus> verifyEmail(@RequestParam("token") String token) {
        VerificationToken verificationToken = authService.getVerificationToken(token);

        if (verificationToken == null)
            return new ResponseEntity<>(VerificationTokenStatus.INVALID, HttpStatus.BAD_REQUEST);

         else if (verificationToken.getExpiryDate().isBefore(LocalDateTime.now()))
            return new ResponseEntity<>(VerificationTokenStatus.EXPIRED, HttpStatus.BAD_REQUEST);

         else {
            authService.authenticateEmail(verificationToken.getUser());
            return new ResponseEntity<>(VerificationTokenStatus.VALID, HttpStatus.OK);
        }
    }

    /**
     * Login 하는 함수
     *
     * @param id : 사용자 pk
     * @param password : 사용자 비밀번호
     * @throws org.springframework.security.core.AuthenticationException : 인증 실패 시 예외 발생
     * @author Jae Wook Jeong
     */
    private void authenticate(Long id, String password) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(String.valueOf(id), password));
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
