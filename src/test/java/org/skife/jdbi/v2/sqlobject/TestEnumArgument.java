package org.skife.jdbi.v2.sqlobject;

import junit.framework.TestCase;
import org.h2.jdbcx.JdbcConnectionPool;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;
import org.skife.jdbi.v2.tweak.HandleCallback;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

public class TestEnumArgument extends TestCase
{
    private JdbcConnectionPool ds;
    private DBI                dbi;

    @Before
    public void setUp() throws Exception
    {
        ds = JdbcConnectionPool.create("jdbc:h2:mem:test",
                                       "username",
                                       "password");
        dbi = new DBI(ds);
        dbi.withHandle(new HandleCallback<Object>()
        {
            public Object withHandle(Handle handle) throws Exception
            {
                handle.execute("create table something (id identity primary key, name varchar(32))");
                return null;
            }
        });
    }

    @After
    public void tearDown() throws Exception
    {
        ds.dispose();
    }

    enum NameEnum{
        cemo
    }

    @Test
    public void testEnum() throws Exception
    {
        DAO dao = dbi.open(DAO.class);
        Object cemo = NameEnum.cemo;
        test(cemo);
        long cemo_id = dao.insert(NameEnum.cemo.name());

        assertThat(dao.findNameById(cemo_id), equalTo(NameEnum.cemo.name()));

    }

    public void test(Object o) {
        System.out.println("TestEnumArgument.test");
    }
    public void test(Enum o) {
        System.out.println("TestEnumArgument.test");
    }

    public static interface DAO
    {
        @SqlUpdate("insert into something (name) values (:it)")
        public long insert(@Bind String name);

        @SqlQuery("select name from something where id = :it")
        public String findNameById(@Bind long id);
    }
}
