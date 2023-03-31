package org.spring.lib.ibatis.interceptor;

import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.Properties;

/**
 * 查询限量拦截插件方式
 *
 * @author chd (caihongder@gmail.com)
 * @version 1.0
 * @since 2023/2/14
 **/
@Intercepts({
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class, CacheKey.class, BoundSql.class}),
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class})
})
public class RecordLimitInterceptor implements Interceptor {

    private static final Logger log = LoggerFactory.getLogger(RecordLimitInterceptor.class);

    private int recordLimit;

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        RowBounds rowBounds = (RowBounds) invocation.getArgs()[2];
        if (Objects.nonNull(rowBounds)) {
            if (rowBounds.getLimit() == RowBounds.NO_ROW_LIMIT) {
                if(log.isDebugEnabled()) {
                    log.debug("查询语句未设置limit 补充设置为默认值 {},sql:{}", recordLimit, ((MappedStatement)invocation.getArgs()[0]).getId());
                }
                invocation.getArgs()[2] = new RowBounds(rowBounds.getOffset(), recordLimit);
            }
        } else {
            if (log.isDebugEnabled()) {
                log.debug("查询语句无设置limit 补充设置为默认值 {},sql:{}", recordLimit, ((MappedStatement) invocation.getArgs()[0]).getId());
            }
            invocation.getArgs()[2] = new RowBounds(RowBounds.NO_ROW_OFFSET, recordLimit);
        }
        return invocation.proceed();
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {
        this.recordLimit = Integer.parseInt(properties.getProperty("db.query.record.limit", "2000"));
    }

}
