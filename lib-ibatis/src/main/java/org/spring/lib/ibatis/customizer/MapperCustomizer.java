package org.spring.lib.ibatis.customizer;

import org.spring.lib.ibatis.context.DaoMapperContext;

/**
 * Dao层Mapper增强定制器
 * @author chd (caihongder@gmail.com)
 * @version 1.0
 * @since 2023/6/30
 **/
public interface MapperCustomizer {

    /**
     * 针对Mapper进行增强
     * @param daoMapperContext mapper上下文必需信息
     */
    void process(DaoMapperContext daoMapperContext);

}
