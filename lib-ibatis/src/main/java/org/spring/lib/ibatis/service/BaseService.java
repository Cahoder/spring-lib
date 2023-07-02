package org.spring.lib.ibatis.service;

import java.io.Serializable;
import java.util.List;

/**
 * 通用基础操作的Service层接口
 * @author chd (caihongder@gmail.com)
 * @version 1.0
 * @since 2023/6/27
 **/
public interface BaseService<PO, ID extends Serializable> {

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

    /**
     * 批量插入
     * @param poList 待插入po对象集合
     * @return 影响行数
     */
    int batchInsert(List<PO> poList);

    /**
     * 批量插入且获取自增key（useGeneratedKeys=true）
     * @param poList 待插入po对象集合
     * @return 影响行数
     */
    int batchInsertForKey(List<PO> poList);

    /**
     * 批量更新（非null字段不进行更新）
     * @param poList 待插入po对象集合
     * @return 影响行数
     */
    int batchUpdateByIdSelective(List<PO> poList);

    /**
     * 批量删除通过主键id集合
     * @param idList 待删除主键id集合
     * @return 影响行数
     */
    int batchDeleteByIdList(List<ID> idList);

}
