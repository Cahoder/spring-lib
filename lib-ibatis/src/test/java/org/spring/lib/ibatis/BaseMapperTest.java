package org.spring.lib.ibatis;

import org.junit.BeforeClass;
import org.junit.Test;
import org.spring.lib.ibatis.demo.db.UserLoginLogDbService;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import javax.annotation.Resource;

/**
 * 提供基础的增删改查接口测试
 * @see org.spring.lib.ibatis.dao.base.curd.CurdMapper
 * @see org.spring.lib.ibatis.dao.base.batch.BatchMapper
 * @author chd (caihongder@gmail.com)
 * @version 1.0
 * @since 2023/7/2
 **/
@SpringBootTest
@ContextConfiguration({"classpath:mybatis/*","classpath:dao/*.xml"})
public class BaseMapperTest extends AbstractJUnit4SpringTest {

    /*@Resource
    private UserLoginLogDbService dbService;*/

    @BeforeClass
    public static void before() {

    }

    @Test
    public void demo() throws Exception {
        //List<UserLoginLogPo> userLoginLogPoList = dbService.listByCondition(new UserLoginLogPo());

    }

}
