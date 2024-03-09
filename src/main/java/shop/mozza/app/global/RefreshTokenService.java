package shop.mozza.app.global;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;


@Service
@Slf4j
public class RefreshTokenService {

    @Value("${jwt.refresh-token.expire-length}")
    private long refreshTokenValidityInSeconds;


    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    public void saveRefreshToken(String username, String refreshToken) {

        log.info("Redis Save : User id " + username);
        log.info("Redis Save : Refresh toekn  " + refreshToken);
        refreshToken = refreshToken.replace("Bearer ", "");
        ValueOperations<String, String> values = redisTemplate.opsForValue();
//        values.set(username, refreshToken, refreshTokenValidityInSeconds, TimeUnit.SECONDS);
        values.set(username, refreshToken);

    }

    public String findRefreshTokenByUserId(String username) {

        log.info("Redis find : User id " + username);
        return redisTemplate.opsForValue().get(username);
    }

    public void deleteRefreshToken(String username) {
        log.info("Redis delete : User id " + username);
        redisTemplate.delete(username);
    }

    public boolean validateRefreshToken(String username, String refreshToken) {
        // Redis에서 refreshToken으로 저장된 username 조회
        String redisRefreshToken = redisTemplate.opsForValue().get(username);
        // 저장된 refreshToken과 입력받은 refreshToken이 동일한지 확인
        return refreshToken.equals(redisRefreshToken);
    }
}