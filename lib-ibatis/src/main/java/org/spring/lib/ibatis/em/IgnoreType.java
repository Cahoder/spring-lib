package org.spring.lib.ibatis.em;

/**
 * 需要忽略字段的场景类型枚举
 * @author chd (caihongder@gmail.com)
 * @version 1.0
 * @since 2023/6/28
 **/
public enum IgnoreType {

    /**
     * 插入时忽略字段
     */
    INSERT,
    /**
     * 更新时忽略字段
     */
    UPDATE,
    /**
     * 查询时忽略字段
     */
    SELECT

}
