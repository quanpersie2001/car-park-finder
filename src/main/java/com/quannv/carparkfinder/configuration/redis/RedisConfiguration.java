package com.quannv.carparkfinder.configuration.redis;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfiguration {

	@Value("${redis.host}")
	private String redisHost;

	@Value("${redis.port}")
	private int redisPort;

	@Value("${redis.password}")
	private String redisPass;

	@Bean
	JedisConnectionFactory jedisConnectionFactory() {
		RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration(redisHost, redisPort);
		// redisStandaloneConfiguration.setPassword(RedisPassword.of(redisPass));
		return new JedisConnectionFactory(redisStandaloneConfiguration);
	}

	@Bean
	public <T> RedisTemplate<String, T> redisTemplate() {
		RedisTemplate<String, T> template = new RedisTemplate<>();
		template.setConnectionFactory(jedisConnectionFactory());

		// Use String serializer for keys
		template.setKeySerializer(new StringRedisSerializer());

		// Use JSON serializer for values
		template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
		return template;
	}
}
