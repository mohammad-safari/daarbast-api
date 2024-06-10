package ce.web.daarbast.redis;

import java.io.IOException;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import redis.embedded.RedisServer;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

@Configuration
@ConditionalOnProperty(value = "embedded.redis.enabled", havingValue = "true")
@EnableConfigurationProperties({ RedisProperties.class })
public class EmbeddedRedis {
    private RedisServer redisServer;

    public EmbeddedRedis(RedisProperties redisProperties) throws IOException {
        this.redisServer = new RedisServer(redisProperties.port());
    }

    @PostConstruct
    public void postConstruct() throws IOException {
        redisServer.start();
    }

    @PreDestroy
    public void preDestroy() throws IOException {
        redisServer.stop();
    }

    @Bean
    public RedisServer redisServer() {
        return redisServer;
    }
}
