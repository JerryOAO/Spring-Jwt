package com.example.utils;

import jakarta.annotation.Resource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class FlowUtils {
    @Resource
    StringRedisTemplate stringRedisTemplate;
    //笔记：limitOnceCheck方法用于限制某个操作的频率，比如限制某个操作一分钟内只能执行一次 key为操作的标识 limit为限制的时间
    public boolean limitOnceCheck(String key, int limit) {
        //笔记：如果key存在，则返回false
        if(Boolean.TRUE.equals(stringRedisTemplate.hasKey(key))){
            return false;
        }else{
            //笔记：如果key不存在，则设置key并设置过期时间
            stringRedisTemplate.opsForValue().set(key, "", limit, TimeUnit.SECONDS);
            return true;
        }
    }
}
