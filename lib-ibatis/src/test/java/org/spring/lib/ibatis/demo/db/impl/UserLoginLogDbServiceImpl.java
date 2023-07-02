package org.spring.lib.ibatis.demo.db.impl;

import org.spring.lib.ibatis.demo.dao.UserLoginLogDao;
import org.spring.lib.ibatis.demo.po.UserLoginLogPo;
import org.spring.lib.ibatis.demo.db.UserLoginLogDbService;
import org.spring.lib.ibatis.service.impl.BaseServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 用户登录日志表
 *
 * @author groovy script
 * @version 1.0
 * @since 2023-7-2
 */
@Service
public class UserLoginLogDbServiceImpl extends BaseServiceImpl<UserLoginLogPo, Long> implements UserLoginLogDbService {

    @Resource
    private UserLoginLogDao userLoginLogDao;

}