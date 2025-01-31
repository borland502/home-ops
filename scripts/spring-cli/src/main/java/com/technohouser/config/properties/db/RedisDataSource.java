package com.technohouser.config.properties.db;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext.SerializationPair;
import java.time.Duration;

@Configuration
public class RedisDataSource {

  @Bean
  public JedisConnectionFactory redisConnectionFactory() {
    return new JedisConnectionFactory();
  }

  @Bean
  public RedisCacheConfiguration cacheConfiguration() {
    return RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofMinutes(60))
        .disableCachingNullValues().serializeValuesWith(
            SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()));
  }

}
