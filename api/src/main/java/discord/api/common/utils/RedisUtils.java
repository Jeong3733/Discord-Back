package discord.api.common.utils;

import discord.api.common.GlobalConstant;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@Component
public class RedisUtils {
    private final RedisTemplate<String, String> redisTemplate;
    public void setRefreshToken(Long id, String refreshToken) {
        // Redis 에 저장 - 만료 시간 설정을 통해 자동 삭제 처리
        redisTemplate.opsForValue().set(
                String.valueOf(id),
                refreshToken,
                GlobalConstant.REFRESH_EXP_TIME,
                TimeUnit.MILLISECONDS
        );
    }

    public String getRefreshToken(Long id) {
        return redisTemplate.opsForValue().get(String.valueOf(id));
    }

    public void deleteRefreshToken(Long id) {
        redisTemplate.delete(String.valueOf(id));
    }
}
