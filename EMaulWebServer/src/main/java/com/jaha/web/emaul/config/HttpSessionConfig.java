package com.jaha.web.emaul.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.session.data.redis.RedisOperationsSessionRepository;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

@EnableRedisHttpSession
public class HttpSessionConfig {

    @Value("${server.session.timeout}")
    private Integer maxInactiveIntervalInSeconds;

    @Bean
    public RedisOperationsSessionRepository sessionRepository(RedisConnectionFactory factory) {
        RedisOperationsSessionRepository sessionRepository = new RedisOperationsSessionRepository(factory);
        sessionRepository.setDefaultMaxInactiveInterval(this.maxInactiveIntervalInSeconds);
        // System.out.println("######################################################### 레디스 세션타임아웃: " + this.maxInactiveIntervalInSeconds);
        return sessionRepository;
    }

}
