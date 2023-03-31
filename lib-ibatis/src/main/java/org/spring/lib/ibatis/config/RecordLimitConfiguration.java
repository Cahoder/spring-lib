package org.spring.lib.ibatis.config;

import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

/**
 * 查询限量应用配置方式
 * 注意:jdbc连接串增加参数 &useCursorFetch=true
 *
 * @author chd (caihongder@gmail.com)
 * @version 1.0
 * @since 2023/2/14
 **/
@Component
public class RecordLimitConfiguration implements BeanPostProcessor, Ordered {

    private static final Logger log = LoggerFactory.getLogger(RecordLimitConfiguration.class);

    @Value("${db.query.record.fetch.size:2000}")
    private int defaultFetchSize;

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof SqlSessionFactory) {
            Configuration configuration = ((SqlSessionFactory) bean).getConfiguration();
            if(configuration.getDefaultFetchSize() == null || configuration.getDefaultFetchSize() == 0) {
                configuration.setDefaultFetchSize(defaultFetchSize);
                if(log.isDebugEnabled()) {
                    log.debug("设置defaultFetchSize: {}", defaultFetchSize);
                }
            } else {
                if(log.isDebugEnabled()) {
                    log.debug("已有设置defaultFetchSize: {}", configuration.getDefaultFetchSize());
                }
            }
        }
        return bean;
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE - 10;
    }

}
