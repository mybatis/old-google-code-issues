/*
 *    Copyright 2010 The myBatis Team
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.mybatis.guice.sample;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.jdbc.ScriptRunner;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.Before;
import org.junit.Test;
import org.mybatis.guice.MyBatisModule;
import org.mybatis.guice.datasource.builtin.PooledDataSourceProvider;
import org.mybatis.guice.datasource.helper.JdbcHelper;
import org.mybatis.guice.sample.dao.UserDao;
import org.mybatis.guice.sample.dao.UserDaoImpl;
import org.mybatis.guice.sample.domain.User;
import org.mybatis.guice.sample.mapper.UserMapper;
import org.mybatis.guice.sample.service.FooService;
import org.mybatis.guice.sample.service.FooServiceDaoImpl;

import com.google.inject.Binder;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.name.Names;

/**
 * Example of MyBatis-Guice basic integration usage.
 *
 * This is the recommended scenario.
 *
 * @version $Id$
 */
public class SampleSqlSessionTest {

    private Injector injector;

    private FooService fooService;

    @Before
    public void setupMyBatisGuice() throws Exception {

        // bindings
        List<Module> modules = this.createMyBatisModule();
        this.injector = Guice.createInjector(modules);

        // prepare the test db
        Environment environment = this.injector.getInstance(SqlSessionFactory.class).getConfiguration().getEnvironment();
        DataSource dataSource = environment.getDataSource();
        ScriptRunner runner = new ScriptRunner(dataSource.getConnection());
        runner.setAutoCommit(true);
        runner.setStopOnError(true);
        runner.runScript(Resources.getResourceAsReader("org/mybatis/guice/sample/db/database-schema.sql"));
        runner.runScript(Resources.getResourceAsReader("org/mybatis/guice/sample/db/database-test-data.sql"));
        runner.closeConnection();

        this.fooService = this.injector.getInstance(FooService.class);
    }

    protected List<Module> createMyBatisModule() {
        List<Module> modules = new ArrayList<Module>();
        modules.add(JdbcHelper.HSQLDB_Embedded);
        modules.add(new MyBatisModule() {

            @Override
            protected void configure() {
                setDataSourceProviderType(PooledDataSourceProvider.class);
                addMapperClass(UserMapper.class);
            }

        });
//        modules.add(new XMLMyBatisModule.Builder()
//            .setEnvironmentId("test")
//            .setClassPathResource("org/mybatis/guice/sample/mybatis-config.xml")
//            .create());
        modules.add(new Module() {
            public void configure(Binder binder) {
                Names.bindProperties(binder, createTestProperties());
                binder.bind(FooService.class).to(FooServiceDaoImpl.class);
                binder.bind(UserDao.class).to(UserDaoImpl.class);
            }
        });

        return modules;
    }

    protected static Properties createTestProperties() {
        final Properties myBatisProperties = new Properties();
        myBatisProperties.setProperty("mybatis.environment.id", "test");
        myBatisProperties.setProperty("JDBC.schema", "mybatis-guice_TEST");
        myBatisProperties.setProperty("derby.create", "true");
        myBatisProperties.setProperty("JDBC.username", "sa");
        myBatisProperties.setProperty("JDBC.password", "");
        myBatisProperties.setProperty("JDBC.autoCommit", "false");
        return myBatisProperties;
    }

    @Test
    public void testFooService(){
        User user = this.fooService.doSomeBusinessStuff("u1");
        assertNotNull(user);
        assertEquals("Pocoyo", user.getName());
    }

}
