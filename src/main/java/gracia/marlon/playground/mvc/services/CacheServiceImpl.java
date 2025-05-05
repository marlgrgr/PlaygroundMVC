package gracia.marlon.playground.mvc.services;

import java.util.concurrent.TimeUnit;

import org.redisson.api.RMapCache;
import org.redisson.api.RedissonClient;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

@Service
public class CacheServiceImpl implements CacheService {

	private final CacheManager cacheManager;
	
	private final RMapCache<String, String> cache;

	public CacheServiceImpl(CacheManager cacheManager, RedissonClient redissonClient) {
		this.cacheManager = cacheManager;
		this.cache = redissonClient.getMapCache("specialCache");
	}

	@Override
	public void evictCache(String cachename) {
		this.cacheManager.getCache(cachename).clear();
	}

	@Override
	public void putInSpecialCacheWithTTL(String key, String value, int ttlInSeconds) {
		this.cache.put(key, value, ttlInSeconds, TimeUnit.SECONDS);

	}

	@Override
	public String getFromSpecialCache(String key) {
		return this.cache.get(key);
	}

}
