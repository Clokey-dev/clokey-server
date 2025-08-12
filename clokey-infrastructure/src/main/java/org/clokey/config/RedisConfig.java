package org.clokey.config;

import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.clokey.properties.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;

@Configuration
@RequiredArgsConstructor
public class RedisConfig {

    private final RedisProperties redisProperties;

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        RedisStandaloneConfiguration redisStandaloneConfig =
                new RedisStandaloneConfiguration(redisProperties.host(), redisProperties.port());
        if (!redisProperties.password().isBlank()) {
            redisStandaloneConfig.setPassword(redisProperties.password());
        }

        LettuceClientConfiguration lettuceClientConfig =
                LettuceClientConfiguration.builder()
                        .commandTimeout(Duration.ofSeconds(1))
                        .shutdownTimeout(Duration.ZERO)
                        .build();

        return new LettuceConnectionFactory(redisStandaloneConfig, lettuceClientConfig);
    }

    @Bean
    public RedisCacheManager appleSecretClientManager(RedisConnectionFactory cf) {
        var keySer = new org.springframework.data.redis.serializer.StringRedisSerializer();
        var valSer = new org.springframework.data.redis.serializer.StringRedisSerializer();

        var baseCfg =
                org.springframework.data.redis.cache.RedisCacheConfiguration.defaultCacheConfig()
                        .serializeKeysWith(
                                org.springframework.data.redis.serializer.RedisSerializationContext
                                        .SerializationPair.fromSerializer(keySer))
                        .serializeValuesWith(
                                org.springframework.data.redis.serializer.RedisSerializationContext
                                        .SerializationPair.fromSerializer(valSer));

        var caches =
                new java.util.HashMap<
                        String, org.springframework.data.redis.cache.RedisCacheConfiguration>();
        caches.put("appleClientSecret", baseCfg.entryTtl(Duration.ofDays(179)));

        return org.springframework.data.redis.cache.RedisCacheManager.builder(cf)
                .cacheDefaults(baseCfg)
                .withInitialCacheConfigurations(caches)
                .build();
    }
}
