package org.spring.lib.ibatis.dao.base;

import org.spring.lib.ibatis.dao.base.batch.BatchMapper;
import org.spring.lib.ibatis.dao.base.curd.CurdMapper;

import java.io.Serializable;

/**
 * 通用基础操作的Dao层接口
 * @author chd (caihongder@gmail.com)
 * @version 1.0
 * @since 2023/6/27
 **/
public interface BaseDao<PO, ID extends Serializable> extends CurdMapper<PO, ID>, BatchMapper<PO, ID> {
}
