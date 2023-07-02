package org.spring.lib.ibatis.dao.base.curd;

import org.spring.lib.ibatis.annotation.SqlProvider;
import org.spring.lib.ibatis.dao.MapperAware;

import java.io.Serializable;
import java.util.List;

/**
 * 通用CURD的Dao层接口
 * @author chd (caihongder@gmail.com)
 * @version 1.0
 * @since 2023/6/27
 **/
@SqlProvider(CurdSqlProvider.class)
public interface CurdMapper<PO, ID extends Serializable> extends MapperAware<PO, ID> {

    /**
     * 根据主键id查询
     * @param id 主键id
     * @return po对象
     */
    PO getById(ID id);

    /**
     * 插入，为null的字段会直接使用数据库默认字段（等同于insertSelective）
     * @param po po对象
     * @return 影响行数
     */
    int insert(PO po);

    /**
     * 插入，并获取自增key且获取自增key（useGeneratedKeys=true）
     * @param po po对象
     * @return 自增主键id值
     */
    int insertForKey(PO po);

    /**
     * 根据id更新po对象中非null的值
     * @param po po对象
     * @return 影响行数
     */
    int updateByIdSelective(PO po);

    /**
     * 根据id删除
     * @param id 主键id
     * @return 影响行数
     */
    int deleteById(ID id);

    /**
     * 根据条件（PO构造，非null字段相等）查询，返回List
     * @param po po条件对象
     * @return 符合条件po集合,保证集合不为null
     */
    List<PO> listByCondition(PO po);

    /**
     * 根据条件（PO构造，非null字段相等）查询，返回单个PO
     * @param po po条件对象
     * @return 符合条件po对象,不存在返回null
     */
    PO getByCondition(PO po);

}
