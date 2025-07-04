package se.omfilm.trivia.stats.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import se.omfilm.trivia.stats.infrastructure.StatsFilesInfrastructure;

@Configuration
@EnableCaching
public class CacheConfiguration {
    @Bean
    public CacheManager createCacheManager() {
        return new ConcurrentMapCacheManager(StatsFilesInfrastructure.GAMES_CACHE_KEY, StatsFilesInfrastructure.ALIAS_CACHE_KEY);
    }
}
