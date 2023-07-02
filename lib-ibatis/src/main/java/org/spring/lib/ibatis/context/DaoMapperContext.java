package org.spring.lib.ibatis.context;

import org.apache.ibatis.session.Configuration;
import org.spring.lib.ibatis.dao.MapperAware;
import org.spring.lib.ibatis.entity.SqlEntityFactory;

import java.io.Serializable;

/**
 * @author chd (caihongder@gmail.com)
 * @version 1.0
 * @since 2023/6/29
 **/
public class DaoMapperContext {

    private Configuration configuration;
    private SqlEntityFactory sqlEntityFactory;
    private Class<? extends MapperAware<?,? extends Serializable>> mapperInterface;

    public DaoMapperContext() {
    }

    public DaoMapperContext(Configuration configuration, SqlEntityFactory sqlEntityFactory,
                            Class<? extends MapperAware<?, ? extends Serializable>> mapperInterface) {
        this.configuration = configuration;
        this.sqlEntityFactory = sqlEntityFactory;
        this.mapperInterface = mapperInterface;
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }

    public SqlEntityFactory getSqlEntityFactory() {
        return sqlEntityFactory;
    }

    public void setSqlEntityFactory(SqlEntityFactory sqlEntityFactory) {
        this.sqlEntityFactory = sqlEntityFactory;
    }

    public void setMapperInterface(Class<? extends MapperAware<?, ? extends Serializable>> mapperInterface) {
        this.mapperInterface = mapperInterface;
    }

    public Class<? extends MapperAware<?, ? extends Serializable>> getMapperInterface() {
        return mapperInterface;
    }
}
