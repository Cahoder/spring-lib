package org.spring.lib.ibatis.dao.base.batch;

import org.apache.ibatis.annotations.Param;
import org.spring.lib.ibatis.annotation.SqlProvider;
import org.spring.lib.ibatis.dao.MapperAware;

import java.io.Serializable;
import java.util.List;

/**
 * 通用批量操作的Dao层接口
 * @author chd (caihongder@gmail.com)
 * @version 1.0
 * @since 2023/6/27
 **/
@SqlProvider(BatchSqlProvider.class)
public interface BatchMapper<PO, ID extends Serializable> extends MapperAware<PO, ID> {

    /**
     * 批量插入
     * @param poList 待插入po对象集合
     * @return 影响行数
     */
    int batchInsert(@Param("list") List<PO> poList);

    /**
     * 批量插入且获取自增key（useGeneratedKeys=true）
     * @param poList 待插入po对象集合
     * @return 影响行数
     */
    int batchInsertForKey(@Param("list") List<PO> poList);

    /**
     * 批量更新（非null字段不进行更新）
     * @param poList 待插入po对象集合
     * @return 影响行数
     */
    int batchUpdateByIdSelective(@Param("list") List<PO> poList);

    /**
     * 批量删除通过主键id集合
     * @param idList 待删除主键id集合
     * @return 影响行数
     */
    int batchDeleteByIdList(@Param("list") List<ID> idList);

}
