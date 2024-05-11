package com.example.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Component     //这个注解是用来标识这个类是一个组件类，这个类是用来被其他类调用的 Component的用法目的是为了让Spring容器来管理这个类
//JWT工具类
public class JwtUtils {
    @Value("${spring.security.jwt.key}")
    String key;

    @Value("${spring.security.jwt.expire}")
    int expire;

    @Resource
    StringRedisTemplate template;
    public boolean invalidateJwt(String headerToken){
        String token = this.convertToken(headerToken);
        if (token == null) return false;
        Algorithm algorithm = Algorithm.HMAC256(key);
        JWTVerifier jwtVerifier = JWT.require(algorithm).build();
        try {
            DecodedJWT jwt = jwtVerifier.verify(token);
            String id = jwt.getId();
            return deleteToken(id,jwt.getExpiresAt());
        }
        catch (JWTVerificationException e){
            return false;
        }
    }

    //deleteToken
    private boolean deleteToken(String uuid,Date time){
        if(this.isInvalidToken(uuid)) return false;
        Date now = new Date();
        long expire = Math.max(time.getTime() - now.getTime(),0);
        //这里是将token放入黑名单中
        template.opsForValue().set(Const.JWT_BLACK_LIST + uuid,"",expire, TimeUnit.MILLISECONDS);
        return true;
    }

    //isInvalidToken 方法是用来判断token是否有效的 传入的是一个uuid 返回的是一个boolean类型的值
    private boolean isInvalidToken(String uuid){
        //这里是判断token是否在黑名单中
        return Boolean.TRUE.equals(template.hasKey(Const.JWT_BLACK_LIST + uuid));
    }

    //初学者笔记：这个方法是用来解析JWT的 传入的是一个token 返回的是一个DecodedJWT对象
    public DecodedJWT resolveJwt(String headerToken){
        String token = this.convertToken(headerToken);
        if (token == null) return null;
        Algorithm algorithm = Algorithm.HMAC256(key);
        JWTVerifier jwtVerifier = JWT.require(algorithm).build();
        try {
            DecodedJWT verify = jwtVerifier.verify(token);
            //这里是判断token是否有效 如果token无效返回null
            if(this.isInvalidToken(verify.getId())) return null;
            Date expiresAt = verify.getExpiresAt();
            return new Date().after(expiresAt) ? null : verify;
        }
        catch (JWTVerificationException e){
            return null;
        }
    }

    public String createJwt(UserDetails details,int id,String username){
        //创建算法 对称加密 HMAC256 传入密钥key 新手怎么理解这个key呢？这个key就是一个密钥，这个密钥是自己定义的，这个密钥就是用来加密和解密的
        Algorithm algorithm = Algorithm.HMAC256(key);
        //获取过期时间
        Date expire = this.expireTime();
        return JWT.create()
                .withJWTId(UUID.randomUUID().toString())
                //设置负载
                .withClaim("id",id)
                //设置用户名
                .withClaim("name",username)
                //设置角色
                .withClaim("authorities",details.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList())
                //设置过期时间
                .withExpiresAt(expire)
                //设置签发时间
                .withIssuedAt(new Date())
                //签名 签名的作用是防止数据被篡改
                .sign(algorithm);

    }
    //获取过期时间
    public Date expireTime(){
        //获取日历对象 Calendar的用法和Date类似
        Calendar calendar = Calendar.getInstance();
        //设置过期时间
        calendar.add(Calendar.HOUR,expire * 24);
        //返回过期时间
        return calendar.getTime();
    }
    public UserDetails toUser(DecodedJWT jwt){
        Map<String, Claim> claims = jwt.getClaims();
        return User
                .withUsername(claims.get("name").asString())
                .password("******")
                .authorities(claims.get("authorities").asArray(String.class))
                .build();
    }

    public Integer toId(DecodedJWT jwt){
        Map<String,Claim> claims = jwt.getClaims();
        return claims.get("id").asInt();
    }

    //convertToken方法是用来转换token的 传入的是一个headerToken 返回的是一个token
    private String convertToken(String headerToken){
        if (headerToken == null || !headerToken.startsWith("Bearer "))
            return null;
        return headerToken.substring(7);
    }
}
