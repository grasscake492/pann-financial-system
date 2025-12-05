package org.util;

import io.jsonwebtoken.*;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtil {

    // 从配置文件中读取，这里先写死
    private final String SECRET_KEY = "pann-financial-system-secret-key-2025";
    private final long EXPIRATION_TIME = 86400000; // 24小时

    /**
     * 生成JWT令牌
     */
    public String generateToken(Long userId, String studentNumber, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("studentNumber", studentNumber);
        claims.put("role", role);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userId.toString())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }

    /**
     * 从令牌中获取用户ID
     */
    public Long getUserIdFromToken(String token) {
        Claims claims = getAllClaimsFromToken(token);
        return Long.parseLong(claims.getSubject());
    }

    /**
     * 从令牌中获取学号
     */
    public String getStudentNumberFromToken(String token) {
        Claims claims = getAllClaimsFromToken(token);
        return claims.get("studentNumber", String.class);
    }

    /**
     * 从令牌中获取角色
     */
    public String getRoleFromToken(String token) {
        Claims claims = getAllClaimsFromToken(token);
        return claims.get("role", String.class);
    }

    /**
     * 获取令牌的过期时间
     */
    public Date getExpirationDateFromToken(String token) {
        Claims claims = getAllClaimsFromToken(token);
        return claims.getExpiration();
    }

    /**
     * 验证令牌是否有效
     */
    public boolean validateToken(String token, Long userId) {
        try {
            Long tokenUserId = getUserIdFromToken(token);
            return tokenUserId.equals(userId) && !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 验证令牌是否过期
     */
    private boolean isTokenExpired(String token) {
        Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    /**
     * 从令牌中获取所有声明
     */
    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token)
                .getBody();
    }
}