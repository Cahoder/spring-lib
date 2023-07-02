package org.spring.lib.ibatis.demo.dao;

import org.spring.lib.ibatis.demo.po.UserLoginLogPo;
import org.spring.lib.ibatis.dao.base.BaseDao;

/**
 * 用户登录日志表
 *
 * @author groovy script
 * @version 1.0
 * @since 2023-7-2
 */
public interface UserLoginLogDao extends BaseDao<UserLoginLogPo, Long> {
}