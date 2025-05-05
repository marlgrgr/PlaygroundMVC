package gracia.marlon.playground.mvc.configuration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.jupiter.api.Test;
import org.redisson.api.RedissonClient;
import org.redisson.codec.JsonJacksonCodec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;

public class CacheConfigurationIT extends AbstractIntegrationBase {

	@Autowired
	private RedissonClient redissonClient;

	@Autowired
	private CustomRedissonSpringCacheManager customRedissonSpringCacheManager;

	@Test
	void redissonClientSuccessful() {
		assertNotNull(redissonClient);
		assertNotNull(customRedissonSpringCacheManager);
		assertTrue(redissonClient.getConfig().getCodec() instanceof JsonJacksonCodec);
		assertEquals(600000, customRedissonSpringCacheManager.getDefaultConfig().getMaxIdleTime());

		Cache cache = customRedissonSpringCacheManager.getCache("new-cache");
		assertEquals("new-cache", cache.getName());

		customRedissonSpringCacheManager.setDefaultConfig(null);
		cache = customRedissonSpringCacheManager.getCache("other-cache");
		assertEquals("other-cache", cache.getName());
	}

}
