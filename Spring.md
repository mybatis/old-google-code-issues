## Introduction to MyBatis-Spring ##

MyBatis-Spring helps you integrate your MyBatis code seamlessly with Spring. Using the classes in this library, Spring will load the necessary MyBatis factory and session classes for you. This library also provides an easy way to inject MyBatis data mappers into your service beans. Finally, MyBatis-Spring will handle transactions and translate MyBatis exceptions into Spring DataAccessExceptions.

## Quick Setup ##

To use MyBatis with Spring you need at least two things defined in the Spring application context: an SqlSessionFactory and at least one data mapper class.
In MyBatis-Spring, an SqlSessionFactoryBean is used to create an SqlSessionFactory. To configure the factory bean, put the following in the Spring XML configuration file:

```
<bean id="sqlSessionFactory" class="org.mybatis.spring.SqlSessionFactoryBean">
  <property name="dataSource" ref="dataSource" />
</bean>
```

Notice that the SqlSessionFactory requires a DataSource. This can be any DataSource and should be configured just like any other Spring database connection.

Assume you have a data mapper class defined like the following:

```
public interface UserMapper {
  @Select("SELECT * FROM user WHERE id = #{userId}")
  User getUser(@Param("userId") String userId);
}
```

This interface is added to Spring using a MapperFactoryBean like the following:

```
<bean id="userMapper" class="org.mybatis.spring.mapper.MapperFactoryBean">
  <property name="mapperInterface" value="org.mybatis.spring.sample.mapper.UserMapper" />
  <property name="sqlSessionFactory" ref="sqlSessionFactory" />
</bean>
```

Note that the mapper class specified must be an interface, not an actual implementation class. In this example, annotations are used to specify the SQL, but a MyBatis mapper XML file could also be used.

Once configured, you can inject mappers directly into your business/service objects in the same way you inject any other Spring bean. The MapperFactoryBean handles creating an SqlSession as well as closing it. If there is a Spring transaction in progress, the session will also be committed or rolled back when the transaction completes. Finally, any exceptions will be translated into Spring DataAccessExceptions.

Calling MyBatis data methods is now only one line of code:

```
public class FooServiceImpl implements FooService {

    private UserMapper userMapper;

    public void setUserMapper(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    public User doSomeBusinessStuff(String userId) {
        return this.userMapper.getUser(userId);
    }

}
```

## More info? ##

Have a look at the full doc in
http://www.mybatis.org/spring

And have a look at JPetStore 6 that is built on top of MyBatis, Spring and Stripes:
http://code.google.com/p/mybatis/downloads/list?can=3&q=Product%3DSample

## Installation ##

MyBatis-Spring code and detailed documentation is available at the downloads section.

To use the MyBatis-Spring module, you just need to include the mybatis-spring jar file and its dependencies in the classpath.

If you are using Maven just add the following dependency to your pom.xml:

```
<dependency>
  <groupId>org.mybatis</groupId>
  <artifactId>mybatis-spring</artifactId>
  <version>1.1.0</version>
</dependency>
```