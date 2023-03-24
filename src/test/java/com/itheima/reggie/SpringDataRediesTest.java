package com.itheima.reggie;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * @author
 * @create 2023-03-17 15:56
 */
@SpringBootTest
public class SpringDataRediesTest {
    @Autowired
    private RedisTemplate redisTemplate;

    @Test
    public void testString(){
        redisTemplate.opsForValue().set("hh",123);
        Object hh = redisTemplate.opsForValue().get("hh");
        System.out.println(hh);

    }
}
