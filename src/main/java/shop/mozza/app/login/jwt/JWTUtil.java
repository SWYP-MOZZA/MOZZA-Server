package shop.mozza.app.login.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import shop.mozza.app.login.user.domain.GuestUser;
import shop.mozza.app.login.user.domain.User;
import shop.mozza.app.login.user.dto.UserDto;


import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JWTUtil {

    private Key key;

    // 액세스 토큰 유효 시간 (예: 10분)
    @Value("${jwt.access-token.expire-length}")
    private int accessTokenValidityInSeconds;

    @Value("${jwt.refresh-token.expire-length}")
    private int refreshTokenValidityInSeconds;


    public JWTUtil(@Value("${jwt.token.secret-key}")String secret) {


        byte[] byteSecretKey = Decoders.BASE64.decode(secret);
        key = Keys.hmacShaKeyFor(byteSecretKey);
    }

    public String getUsername(String token) {

        return Jwts.parser().setSigningKey(key).build().parseClaimsJws(token).getBody().get("username", String.class);
    }

    public String getRole(String token) {

        return Jwts.parser().setSigningKey(key).build().parseClaimsJws(token).getBody().get("role", String.class);
    }

    public Boolean isExpired(String token) {

        return Jwts.parser().setSigningKey(key).build().parseClaimsJws(token).getBody().getExpiration().before(new Date());
    }


    public String createAccessToken(String username, String role) {
        return createJwt(username, role, accessTokenValidityInSeconds);
    }



    public String createAccessToken() {
        return createJwt(accessTokenValidityInSeconds);
    }


    public String createRefreshToken(String username) {
        return createJwt(username, null, refreshTokenValidityInSeconds);
    }

    public String createJwt(String username, String role, int expiredSeconds) {

        Map<String, Object> claims = new HashMap<>();
        claims.put("username", username);
        if (role != null) {
            claims.put("role", role);
        }
        return "Bearer "+Jwts.builder()
                .setClaims(claims)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiredSeconds))
                .signWith(key)
                .compact();
    }

    public String createJwt(int expiredSeconds) {
        Map<String, Object> claims = new HashMap<>();
        return "Bearer "+Jwts.builder()
                .setClaims(claims)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiredSeconds))
                .signWith(key)
                .compact();
    }



    public String getUsernameFromRefreshToken(String token) {
        Claims claims = Jwts.parser().setSigningKey(key).build().parseClaimsJws(token).getBody();
        return claims.getSubject();
    }
    public boolean validateToken(String token) {
        try {
            // Parse the token and extract claims
            Jws<Claims> claimsJws = Jwts.parser()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);

            // Check if the token expiration time has passed
            Date expiration = claimsJws.getBody().getExpiration();
            return expiration != null && expiration.after(new Date());
        } catch (Exception e) {
            // Token validation failed
            return false;
        }
    }



}
