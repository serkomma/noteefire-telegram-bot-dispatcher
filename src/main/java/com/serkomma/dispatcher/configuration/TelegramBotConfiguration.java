package com.serkomma.dispatcher.configuration;

import com.serkomma.dispatcher.entities.CachedNotificationEntity;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisKeyValueTemplate;
import org.springframework.data.redis.core.RedisTemplate;

@Configuration
@ComponentScan(basePackages = "com.serkomma.dispatcher")
@EnableFeignClients(basePackages = "com.serkomma.dispatcher")
public class TelegramBotConfiguration {
    @Bean
    JedisConnectionFactory jedisConnectionFactory(){
        return new JedisConnectionFactory();
    }
    @Bean
    public RedisTemplate<Long, CachedNotificationEntity> redisTemplate(RedisConnectionFactory connectionFactory){
        RedisTemplate<Long, CachedNotificationEntity> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        return template;
    }
}

