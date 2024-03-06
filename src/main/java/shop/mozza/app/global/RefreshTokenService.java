package shop.mozza.app.global;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class RefreshTokenService {

    private static final long REFRESH_TOKEN_EXPIRE_TIME = 14; // 리프레시 토큰 유효 기간 (일)

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    public void saveRefreshToken(String userId, String refreshToken) {

        log.info("Redis Save : User id " + userId);
        log.info("Redis Save : Refresh toekn  " + refreshToken);

        ValueOperations<String, String> values = redisTemplate.opsForValue();
        values.set(userId, refreshToken, REFRESH_TOKEN_EXPIRE_TIME, TimeUnit.DAYS);
    }

    public String findRefreshTokenByUserId(String userId) {

        log.info("Redis find : User id " + userId);
        return redisTemplate.opsForValue().get(userId);
    }

    public void deleteRefreshToken(String userId) {

        log.info("Redis delete : User id " + userId);
        redisTemplate.delete(userId);
    }

    public boolean validateRefreshToken(String refreshToken) {
        // Redis에서 refreshToken으로 저장된 username 조회
        String username = redisTemplate.opsForValue().get(refreshToken);
        return username != null && !username.isEmpty();
    }

}