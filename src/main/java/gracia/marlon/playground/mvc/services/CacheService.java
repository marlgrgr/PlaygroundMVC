package gracia.marlon.playground.mvc.services;

public interface CacheService {

	void evictCache(String cachename);

	void putInSpecialCacheWithTTL(String key, String value, int ttlInSeconds);

	String getFromSpecialCache(String key);

}
