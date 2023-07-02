package org.spring.lib.ibatis.config;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * jar包自动装配入口
 * @author chd (caihongder@gmail.com)
 * @version 1.0
 * @since 2023/6/27
 **/
@Configuration
@ConditionalOnClass({SqlSessionFactory.class, SqlSessionFactoryBean.class})
@ComponentScan("org.spring.lib.ibatis.config")
public class IbatisBeanConfiguration {

}
