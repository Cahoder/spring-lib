package org.spring.lib.ibatis.dao;

import java.io.Serializable;

/**
 * 标识接口-仅对继承该接口的Dao进行增强
 * @author chd (caihongder@gmail.com)
 * @version 1.0
 * @since 2023/6/27
 **/
public interface MapperAware<PO, ID extends Serializable> {
}
