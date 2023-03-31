package org.spring.lib.cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spring.lib.cache.loader.AbstractCacheLoader;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * 默认缓存加载器
 *
 * @author chd (caihongder@gmail.com)
 * @version 1.0
 * @since 2023/3/31
 **/
@Component
public class DefaultCacheLoader implements ApplicationListener<ContextRefreshedEvent> {

    private static final Logger log = LoggerFactory.getLogger(DefaultCacheLoader.class);

    @Resource
    private List<AbstractCacheLoader> cacheLoaders;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        log.info("start loading the static configuration of caches");
        cacheLoaders.forEach(AbstractCacheLoader::initLoadCache);
        log.info("complete loading the static configuration of caches");
    }

    //@Scheduled(cron = "${load.cache.task.loadTestCacheCron}")
    public void scheduledLoadTestCache() {
        log.info("start scheduled loading the specific cache");
        cacheLoaders.forEach(AbstractCacheLoader::loadCache);
        log.info("end scheduled loading the specific cache");
    }

}
