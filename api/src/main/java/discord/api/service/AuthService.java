package discord.api.service;

import discord.api.common.event.OnSignUpCompleteEvent;
import discord.api.common.exception.ErrorCode;
import discord.api.common.exception.RestApiException;
import discord.api.common.security.JwtTokenProvider;
import discord.api.common.utils.RedisUtils;
import discord.api.entity.User;
import discord.api.entity.VerificationToken;
import discord.api.entity.dtos.SignUpRequestDto;
import discord.api.entity.dtos.TokenResponseDto;
import discord.api.entity.enums.Role;
import discord.api.entity.enums.UserStatus;
import discord.api.repository.UserRepository;
import discord.api.repository.VerificationTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final VerificationTokenRepository verificationTokenRepository;
    private final RedisUtils redisUtils;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final MailService mailService;
    private final ApplicationEventPublisher eventPublisher;

    /**
     * 회원가입
     *
     * @param signUpRequestDto : 회원가입 요청 정보
     * @author Jae Wook Jeong
     * @throws RestApiException : 이메일 중복 시 예외 발생
     */
    @Transactional
    public void signUp(SignUpRequestDto signUpRequestDto) {
        String encPwd = bCryptPasswordEncoder.encode(signUpRequestDto.getPassword());
        String email = signUpRequestDto.getEmail();

        User user = User.builder()
                .email(email)
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

        // 이벤트 발생
        eventPublisher.publishEvent(new OnSignUpCompleteEvent(user));
    }

    /**
     * 토큰 생성 및 Redis 에 Refresh Token 저장
     *
     * @param email : 사용자 이메일
     * @return TokenResponseDto : Access Token, Refresh Token
     * @author Jae Wook Jeong
     */
    @Transactional
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
    @Transactional
    public Boolean isRefreshTokenExists(String email) {
        String refreshToken = redisUtils.getRefreshToken(email);

        // Redis 에 Refresh Token 이 존재하지 않음
        // 1. 잘못된 Refresh Token 을 보냄
        // 2. Refresh Token 이 만료됨
        if (refreshToken == null)
            return false;

        return true;
    }


    /**
     * Email 인증을 위한 Verification Token 생성
     *
     * @param user : 사용자 정보
     * @param token : Verification Token (UUID)
     * @return VerificationToken : 생성된 Verification Token 정보
     * @throws RestApiException : 토큰 중복 시 예외 발생
     * @author Jae Wook Jeong
     */
    @Transactional
    public VerificationToken createVerificationToken(User user, String token) {
        VerificationToken verificationToken = VerificationToken.builder()
                .user(user)
                .token(token)
                .build();
        try {
            verificationTokenRepository.save(verificationToken);
        } catch (DataIntegrityViolationException e) {
            // 토큰 중복 체크
            throw new RestApiException(ErrorCode.VERIFICATION_TOKEN_DUPLICATION);
        }

        return verificationToken;
    }

    /**
     * 이메일 인증 토큰을 통해서 VerificationToken 정보 가져오기
     *
     * @param token : 이메일 인증 토큰
     * @return VerificationToken : 이메일 인증 토큰 정보
     * @throws RestApiException : 토큰이 존재하지 않을 시 예외 발생
     * @author Jae Wook Jeong
     */
    @Transactional
    public VerificationToken getVerificationToken(String token) {
        return verificationTokenRepository.findByToken(token)
                .orElseThrow(() -> {
                    throw new RestApiException(ErrorCode.VERIFICATION_TOKEN_NOT_FOUND);
                });
    }

    /**
     * 이메일 완증 완료시 사용자의 이메일 인증 상태를 인증 완료로 변경
     *
     * @param user : 사용자
     * @author Jae Wook Jeong
     */
    @Transactional
    public void authenticateEmail(User user) {
        user.authenticate();
    }
}