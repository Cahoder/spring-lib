package org.spring.lib.ibatis;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

/**
 * useCursorFetch=true配合流式查询
 *
 * @author chd (caihongder@gmail.com)
 * @version 1.0
 * @since 2023/2/14
 **/
public class StreamQueryTest extends AbstractJUnit4SpringTest {

    private static final Logger log = LoggerFactory.getLogger(StreamQueryTest.class);

    final AtomicLong tmAl = new AtomicLong();
    final String tableName = "test";

    @Test
    public void demo() throws Exception {
        Class.forName("com.mysql.jdbc.Driver");
        Properties props = new Properties();
        props.setProperty("user", "root");
        props.setProperty("password", "123456");
        this.execute(props, "jdbc:mysql://localhost/testdb?useSSL=false&useCursorFetch=true");
        Assert.assertTrue(true);
    }

    private void execute(Properties props, String url) {
        ExecutorService threadPool = Executors.newCachedThreadPool();
        CountDownLatch cdl = new CountDownLatch(1);
        long start = System.currentTimeMillis();
        for (int i = 0; i < 1; i++) {
            QueryRunner queryRunner = new QueryRunner(props, cdl, url);
            threadPool.submit(queryRunner);
        }
        try {
            cdl.await();
            long end = System.currentTimeMillis();
            System.out.println("Test end,total cost:" + (end - start) + "ms");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static class QueryRunner implements Runnable {
        private final Properties props;
        private final CountDownLatch countDownLatch;
        private final String url;

        public QueryRunner(Properties props, CountDownLatch cdl, String url) {
            this.props = props;
            this.countDownLatch = cdl;
            this.url = url;
        }

        public void run() {
            Connection connection = null;
            PreparedStatement ps = null;
            Statement st = null;
            ResultSet rstmp = null;
            long start = System.currentTimeMillis();
            try {
                connection = DriverManager.getConnection(url, props);
                connection.setAutoCommit(false);
                st = connection.createStatement();

                //st.setFetchSize(500);
                st.setFetchSize(Integer.MIN_VALUE);  //仅修改此处即可

                st.executeQuery("SELECT SUM(k) FROM sbtest1 GROUP BY k");
                rstmp = st.getResultSet();
                while (rstmp.next()) {
                    System.out.println(rstmp.getRow());
                }
            } catch (Exception e) {
                System.out.println(System.currentTimeMillis() - start);
                System.out.println(new java.util.Date().toString());
                e.printStackTrace();
            } finally {
                this.close(connection, ps, st, rstmp);
                this.countDownLatch.countDown();
            }
        }

        private void close(Connection connection, PreparedStatement ps, Statement st, ResultSet rstmp) {
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
            }
            if (st != null) {
                try {
                    st.close();
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
            }
            if (rstmp != null) {
                try {
                    rstmp.close();
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

}
