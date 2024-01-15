package discord.api.service;

import discord.api.common.exception.ErrorCode;
import discord.api.common.exception.RestApiException;
import discord.api.common.security.JwtTokenProvider;
import discord.api.common.utils.RedisUtils;
import discord.api.entity.User;
import discord.api.entity.dtos.SignUpRequestDto;
import discord.api.entity.dtos.TokenResponseDto;
import discord.api.entity.enums.Role;
import discord.api.entity.enums.UserStatus;
import discord.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final RedisUtils redisUtils;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    /**
     * 회원가입
     *
     * @param signUpRequestDto : 회원가입 요청 정보
     * @author Jae Wook Jeong
     * @throws RestApiException : 이메일 중복 시 예외 발생
     */
    public void signUp(SignUpRequestDto signUpRequestDto) {
        String encPwd = bCryptPasswordEncoder.encode(signUpRequestDto.getPassword());

        User user = User.builder()
                .email(signUpRequestDto.getEmail())
                .email_authenticated(false)
                .password(encPwd)
                .nickname(signUpRequestDto.getNickname())
                .birth(signUpRequestDto.getBirth())
                .userStatus(UserStatus.OFFLINE)
                .role(Role.USER)
                .build();

        try {
            userRepository.save(user);
        } catch (DataIntegrityViolationException e) {
            // 이메일 중복 체크
            throw new RestApiException(ErrorCode.EMAIL_DUPLICATION);
        }
    }

    /**
     * 토큰 생성 및 Redis 에 Refresh Token 저장
     *
     * @param email : 사용자 이메일
     * @return TokenResponseDto : Access Token, Refresh Token
     * @author Jae Wook Jeong
     */
    public TokenResponseDto generateTokens(String email) {
        TokenResponseDto tokenResponseDto = jwtTokenProvider.generateToken(email);

        // Redis 에 Refresh Token 저장
        redisUtils.setRefreshToken(email, tokenResponseDto.getRefreshToken());

        return tokenResponseDto;
    }

    /**
     * 유효한 Refresh Token 이 Redis 에  존재하는지 확인
     *
     * @param email : 사용자 이메일
     * @return Boolean : Refresh Token 이 존재하는지 여부 (true: 존재, false: 존재하지 않음)
     * @author Jae Wook Jeong
     */
    public Boolean isRefreshTokenExists(String email) {
        String refreshToken = redisUtils.getRefreshToken(email);

        // Redis 에 Refresh Token 이 존재하지 않음
        // 1. 잘못된 Refresh Token 을 보냄
        // 2. Refresh Token 이 만료됨
        if (refreshToken == null)
            return false;

        return true;
    }
}
