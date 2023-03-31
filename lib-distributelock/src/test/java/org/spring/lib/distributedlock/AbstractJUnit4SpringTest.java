package org.spring.lib.distributedlock;

import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * <p>单元测试统一父类</p>
 *
 * @author chd (caihongder@gmail.com)
 * @version 1.0
 * @since 2022/9/7
 **/
@RunWith(SpringJUnit4ClassRunner.class)
public abstract class AbstractJUnit4SpringTest extends AbstractJUnit4SpringContextTests {

    @BeforeClass
    public static void before() {
        System.setProperty("spring.profiles.active", "development");
    }

}
