package shop.mozza.app.login.jwt;


import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import shop.mozza.app.user.domain.User;
import shop.mozza.app.user.dto.UserDto;

import java.security.Key;
import java.time.ZonedDateTime;
import java.util.Date;


@Slf4j
@Component
public class JwtUtil {

//    private final Key key;
//    private final long accessTokenExpTime;
//
//    public JwtUtil(
//            @Value("${jwt.secret}") String secretKey,
//            @Value("${jwt.expiration_time}") long accessTokenExpTime
//    ) {
//        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
//        this.key = Keys.hmacShaKeyFor(keyBytes);
//        this.accessTokenExpTime = accessTokenExpTime;
//    }
//
//    public String createAccessToken(UserDto user) {
//        return createToken(user, accessTokenExpTime);
//    }
//
//    private String createToken(UserDto user, long expireTime) {
//        Claims claims = (Claims) Jwts.claims();
//        claims.put("id", user.getId());
//        claims.put("name", user.getName());
//
//
//        ZonedDateTime now = ZonedDateTime.now();
//        ZonedDateTime tokenValidity = now.plusSeconds(expireTime);
//
//
//        return Jwts.builder()
//                .setClaims(claims)
//                .setIssuedAt(Date.from(now.toInstant()))
//                .setExpiration(Date.from(tokenValidity.toInstant()))
//                .signWith(key, SignatureAlgorithm.HS256)
//                .compact();
//    }
//
//
//    /**
//     * Token에서 User ID 추출
//     * @param token
//     * @return User ID
//     */
//    public Long getUserId(String token) {
//        return parseClaims(token).get("memberId", Long.class);
//    }
//
//
//    /**
//     * JWT 검증
//     * @param token
//     * @return IsValidate
//     */
//    public boolean validateToken(String token) {
//        try {
//            Jwts.parser().setSigningKey(key).build().parseClaimsJws(token);
//            return true;
//        } catch (SecurityException | MalformedJwtException e) {
//            log.info("Invalid JWT Token", e);
//        } catch (ExpiredJwtException e) {
//            log.info("Expired JWT Token", e);
//        } catch (UnsupportedJwtException e) {
//            log.info("Unsupported JWT Token", e);
//        } catch (IllegalArgumentException e) {
//            log.info("JWT claims string is empty.", e);
//        }
//        return false;
//    }
//
//
//    /**
//     * JWT Claims 추출
//     * @param accessToken
//     * @return JWT Claims
//     */
//    public Claims parseClaims(String accessToken) {
//        try {
//            return Jwts.parser().setSigningKey(key).build().parseClaimsJws(accessToken).getBody();
//        } catch (ExpiredJwtException e) {
//            return e.getClaims();
//        }
//    }

}
