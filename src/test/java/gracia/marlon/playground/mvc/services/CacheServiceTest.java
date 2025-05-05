package gracia.marlon.playground.mvc.services;

import static org.junit.Assert.assertEquals;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.redisson.api.RMapCache;
import org.redisson.api.RedissonClient;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

public class CacheServiceTest {

	private final CacheManager cacheManager;

	private final RedissonClient redissonClient;

	private final RMapCache<String, String> cache;

	private final CacheService cacheService;

	@SuppressWarnings("unchecked")
	public CacheServiceTest() {
		this.cacheManager = Mockito.mock(CacheManager.class);
		this.redissonClient = Mockito.mock(RedissonClient.class);
		this.cache = Mockito.mock(RMapCache.class);
		Mockito.when(this.redissonClient.<String, String>getMapCache(Mockito.anyString())).thenReturn(this.cache);

		this.cacheService = new CacheServiceImpl(this.cacheManager, this.redissonClient);
	}

	@Test
	public void evictCacheSuccessful() {
		Cache returnCache = Mockito.mock(Cache.class);

		Mockito.when(this.cacheManager.getCache(Mockito.anyString())).thenReturn(returnCache);
		Mockito.doNothing().when(cache).clear();

		this.cacheService.evictCache("cache-name");

		Mockito.verify(returnCache).clear();

	}

	@Test
	public void putInSpecialCacheWithTTLSuccessful() {
		Mockito.when(this.cache.put(Mockito.anyString(), Mockito.anyString(), Mockito.anyLong(), Mockito.any()))
				.thenReturn(null);

		this.cacheService.putInSpecialCacheWithTTL("key", "value", 600);

		Mockito.verify(this.cache).put(Mockito.anyString(), Mockito.anyString(), Mockito.anyLong(), Mockito.any());

	}

	@Test
	public void getFromSpecialCacheSuccessful() {
		Mockito.when(this.cache.get(Mockito.anyString())).thenReturn("value");

		String value = this.cacheService.getFromSpecialCache("key");

		assertEquals("value", value);

	}

}
