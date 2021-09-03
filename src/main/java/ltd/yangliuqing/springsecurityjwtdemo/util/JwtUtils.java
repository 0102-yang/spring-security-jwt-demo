package ltd.yangliuqing.springsecurityjwtdemo.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author yang
 */
public class JwtUtils {

    private static final Key KEY;

    private static final String ISS = "yang-0FBC";

    static {
        // Yang ==> SHA-256
        byte[] keyBytes = "FEF1114F2D1A5BF56F5D8F442CACE951A500462C337A4F3992A9767128BF31A5".getBytes();
        KEY = Keys.hmacShaKeyFor(keyBytes);
    }

    public static String createToken(String subject, Map<String, Object> claims, long timeout, TimeUnit timeUnit) {
        String jti = UUID.randomUUID().toString();
        long now = System.currentTimeMillis();
        long expiredTime = TimeUnit.MILLISECONDS.convert(timeout, timeUnit);
        Date iat = new Date();
        Date exp = new Date(now + expiredTime);

        JwtBuilder builder = Jwts.builder().setClaims(claims).setId(jti).setIssuer(JwtUtils.ISS).setIssuedAt(iat).setNotBefore(iat).setExpiration(exp).setSubject(subject).signWith(JwtUtils.KEY);
        return builder.compact();
    }

    public static String createToken(String subject, long timeout, TimeUnit timeUnit) {
        Map<String, Object> claims = Collections.emptyMap();
        return JwtUtils.createToken(subject, claims, timeout, timeUnit);
    }

    public static Optional<Claims> getClaims(String token) {
        // 获取payload
        JwtParser parser = Jwts.parserBuilder().requireIssuer(JwtUtils.ISS).setSigningKey(JwtUtils.KEY).build();
        Optional<Claims> claims;
        try {
            Jws<Claims> c = parser.parseClaimsJws(token);
            claims = Optional.of(c.getBody());
        } catch (JwtException e) {
            claims = Optional.empty();
        }

        // 验证是否过期
        if (claims.isPresent()) {
            Date now = new Date();
            Claims test = claims.get();
            Date exp = test.getExpiration();
            if (exp.before(now)) {
                // 已经过期
                claims = Optional.empty();
            }
        }

        return claims;
    }

}
