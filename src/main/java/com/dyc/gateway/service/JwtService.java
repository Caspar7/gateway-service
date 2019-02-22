package com.dyc.gateway.service;

import io.jsonwebtoken.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.util.Date;

@Service
public class JwtService {

    private static final Logger LOGGER = LoggerFactory.getLogger(JwtService.class);
    // 私钥
    private static final String SECRET_KEY = "caspar";

    public String createJWT(String userId, String userName) {

        // 生成token
        String jwtToken = Jwts.builder()
                // 头部
                .setHeaderParam("typ", "JWT")
                // jwt 标注中的申明
                .setIssuedAt(new Date()) // 签发时间
                .setExpiration(new Date(new Date().getTime() + 10000L))// 过期时间
                .setSubject("9527")// jwt面向的客户
                .setIssuer("caspar")// jwt的签发者
                // 公共申明和私有申明
                .claim("userId", userId)
                .claim("userName",userName)
                .claim("phone", "18988888888")
                .claim("age", 35)
                .claim("sex", "男")
                // 签证
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY.getBytes())
                .compact();

        LOGGER.info("生成的 jwt token 如下:" + jwtToken);
        return jwtToken;
    }

    public void parseJWT(String jwtToken) {
        // 验证jwt
        Jws<Claims> claimsJws = Jwts.parser()
                // 验证签发者字段iss 必须是 huan
                .require("iss", "caspar")
                // 设置私钥
                .setSigningKey(SECRET_KEY.getBytes())
                // 解析jwt字符串
                .parseClaimsJws(jwtToken);

        // 获取头部信息
        JwsHeader header = claimsJws.getHeader();
        // 获取载荷信息
        Claims payload = claimsJws.getBody();

        LOGGER.info("解析过来的jwt的header如下:" + header.toString());
        LOGGER.info("解析过来的jwt的payload如下:" + payload.toString());
    }
}
