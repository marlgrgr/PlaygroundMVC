package gracia.marlon.playground.mvc.configuration;

import org.redisson.api.RedissonClient;
import org.redisson.spring.cache.CacheConfig;
import org.redisson.spring.cache.RedissonSpringCacheManager;

public class CustomRedissonSpringCacheManager extends RedissonSpringCacheManager {

	CacheConfig defaultConfig = null;

	public CustomRedissonSpringCacheManager(RedissonClient redisson) {
		super(redisson);
	}

	public CacheConfig getDefaultConfig() {
		return defaultConfig;
	}

	public void setDefaultConfig(CacheConfig defaultConfig) {
		this.defaultConfig = defaultConfig;
	}

	@Override
	protected CacheConfig createDefaultConfig() {
		return this.defaultConfig == null ? new CacheConfig() : this.defaultConfig;
	}

}
