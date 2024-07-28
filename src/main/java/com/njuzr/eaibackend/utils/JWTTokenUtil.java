package com.njuzr.eaibackend.utils;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Slf4j
@Component
public class JWTTokenUtil {

    @Value("${jwt.secret}")
    private String secret; // token生成秘钥
    @Value("${jwt.issuer}")
    private String issuer; // 发布人
    @Value("${jwt.expiration}")
    private String expiration; // 过期时间


    /**
     * 根据用户信息生成token
     * @param username
     * @return token令牌
     */
    public String generateToken(String username) {
        String uuid = UUID.randomUUID().toString(); // token id
        Date exprireDate = Date.from(Instant.now().plusSeconds(Long.parseLong(expiration))); // 过期时间
        return Jwts.builder()
                // 设置头部信息header
                .header()
                .add("typ", "JWT")
                .add("alg", "HS256")
                .and()
                // 设置自定义负载信息payload
                .claim("username", username)
                // 令牌ID
                .id(uuid)
                // 过期日期
                .expiration(exprireDate)
                // 签发时间
                .issuedAt(new Date())
                // 主题
                .subject(username)
                // 签发者
                .issuer(issuer)
                // 签名
                .signWith(Keys.hmacShaKeyFor(secret.getBytes()), Jwts.SIG.HS256)
                .compact();
    }

    /**
     * 解析token
     * @param token token
     * @return Jws<Claims>
     */
    public Jws<Claims> parseClaim(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(Keys.hmacShaKeyFor(secret.getBytes()))
                    .build()
                    .parseSignedClaims(token);
        } catch (ExpiredJwtException e) {
            // 令牌过期
            throw new CredentialsExpiredException("Token has expired", e);
        } catch (JwtException e) {
            // 其他JWT解析错误
            throw new AuthenticationServiceException("Error parsing JWT", e);
        }
    }

    public String parseToken(String token) {
        return parseClaim(token).getPayload().getSubject();
    }


    /**
     * 判断token是否未失效
     * true表示未失效
     */
    public boolean isTokenNotExpired(String token) {
        Date expiredDate = parseClaim(token).getPayload().getExpiration();
        return expiredDate.after(new Date());
    }
}
