package org.spring.lib.ibatis.demo.db;

import org.spring.lib.ibatis.demo.po.UserLoginLogPo;
import org.spring.lib.ibatis.service.BaseService;

/**
 * 用户登录日志表
 *
 * @author groovy script
 * @version 1.0
 * @since 2023-7-2
 */
public interface UserLoginLogDbService extends BaseService<UserLoginLogPo, Long> {
}