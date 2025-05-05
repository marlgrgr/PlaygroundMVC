package gracia.marlon.playground.mvc.configuration;

import java.io.File;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.codec.JsonJacksonCodec;
import org.redisson.config.Config;
import org.redisson.spring.cache.CacheConfig;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import lombok.extern.slf4j.Slf4j;

@Configuration
@EnableCaching
@Slf4j
public class CacheConfiguration {

	private final String redissonConfigPath;

	private final long redisMaxIdleTime;

	private final long redisTTL;

	private final long DURATION_MULTIPLIER = 60_000;

	public CacheConfiguration(Environment env) {
		this.redissonConfigPath = env.getProperty("redis.config.path", "");
		this.redisMaxIdleTime = Long.parseLong(env.getProperty("redis.config.maxIdleTime", "0"));
		this.redisTTL = Long.parseLong(env.getProperty("redis.config.redisTTL", "0"));
	}

	@Bean
	RedissonClient redissonClient() {
		RedissonClient redissonClient = null;
		try {
			final Config config = Config.fromYAML(new File(this.redissonConfigPath));
			config.setCodec(new JsonJacksonCodec());
			redissonClient = Redisson.create(config);
		} catch (Exception e) {
			log.error("An error occurred while creating the redisson client", e);
		}
		return redissonClient;
	}

	@Bean
	CacheManager cacheManager(RedissonClient redissonClient) {

		final CacheConfig defaultCacheConfig = new CacheConfig();
		defaultCacheConfig.setTTL(redisTTL * DURATION_MULTIPLIER);
		defaultCacheConfig.setMaxIdleTime(redisMaxIdleTime * DURATION_MULTIPLIER);

		final CustomRedissonSpringCacheManager redissonCacheManager = new CustomRedissonSpringCacheManager(
				redissonClient);
		redissonCacheManager.setDefaultConfig(defaultCacheConfig);

		return redissonCacheManager;
	}
}
