package com.fun.crawl.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fun.crawl.model.vo.SysUserVo;
import com.fun.crawl.security.UserDetailsImpl;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author: Lan
 * @date: 2019/4/8 14:19
 * @description:
 */
@Component
@ConfigurationProperties(prefix = "jwt")
@Data
public class JwtTokenUtil {

    /**
     * header名称
     */
    private String tokenHeader;

    /**
     * token前缀
     */
    private String tokenPrefix;

    /**
     * 秘钥
     */
    private String secret;

    /**
     * 过期时间
     */
    private Long expiration;

    /**
     * 选择记住后过期时间
     */
    private Long rememberExpiration;

    /**
     * 生成token
     *
     * @param userDTO
     * @return
     */
    public String createToken(UserDetailsImpl userDetails) {
        Long time = expiration;
        Map<String, Object> map = new HashMap<>(1);
        SysUserVo sysUserVo = userDetails.GetSysUserVo();
        map.put("user", sysUserVo);
        return Jwts.builder()
                .setClaims(map)
                .setSubject(sysUserVo.getUsername())
                .setExpiration(new Date(System.currentTimeMillis() + time * 1000))
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
    }

    /**
     * 获取用户名
     *
     * @param token
     * @return
     */
    public String getUserName(String token) {
        return generateToken(token).getSubject();
    }

    /**
     * 解析token
     *
     * @param token
     * @return
     */
    public Claims generateToken(String token) {
        return Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * 获取userDTO
     *
     * @param token
     * @return
     */
    public UserDetailsImpl getUserDTO(String token) {
        Claims claims = generateToken(token);
        Map<String, Object> map = claims.get("user", Map.class);
        String toJSONString = JSONObject.toJSONString(map);
        SysUserVo userDTO = JSONObject.parseObject(toJSONString, SysUserVo.class);
        UserDetailsImpl userDetails=new UserDetailsImpl(userDTO);
        return userDetails;
    }
}