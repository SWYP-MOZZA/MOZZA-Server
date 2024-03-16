package shop.mozza.app.global;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.concurrent.TimeUnit;


@Service
@Slf4j
public class TokenService {

    @Value("${jwt.refresh-token.expire-length}")
    private long refreshTokenValidityInSeconds;

    @Value("${jwt.access-token.expire-length}")
    private long accessTokenValidityInSeconds;


    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    public void saveRefreshToken(Long id, String refreshToken) {

        log.info("Redis Save : User id " + id);
        log.info("Redis Save : Refresh token  " + refreshToken);
        refreshToken = refreshToken.replace("Bearer ", "");
        ValueOperations<String, String> values = redisTemplate.opsForValue();
//        values.set(username, refreshToken, refreshTokenValidityInSeconds, TimeUnit.SECONDS);
        values.set(id.toString(), refreshToken);

    }

    public String findRefreshTokenByUserId(Long id) {

        log.info("Redis find : User id " + id.toString());
        return redisTemplate.opsForValue().get(id.toString());
    }

    public void deleteRefreshToken(Long id) {
        log.info("Redis delete : User id " + id);
        redisTemplate.delete(id.toString());
    }

    public boolean validateRefreshToken(Long id, String refreshToken) {
        // Redis에서 refreshToken으로 저장된 username 조회
        String redisRefreshToken = redisTemplate.opsForValue().get(id.toString());
        // 저장된 refreshToken과 입력받은 refreshToken이 동일한지 확인
        return refreshToken.equals(redisRefreshToken);
    }

    public void addToBlacklist(String token) {
        log.info("Adding token to blacklist: " + token);
        ValueOperations<String, String> values = redisTemplate.opsForValue();
        values.set("BL_" + token, "blacklisted", refreshTokenValidityInSeconds, TimeUnit.SECONDS);
    }

    public boolean isTokenBlacklisted(String token) {
        String value = redisTemplate.opsForValue().get("BL_" + token);
        return value != null;
    }

}