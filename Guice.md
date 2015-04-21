## Introduction to MyBatis-Guice ##

In our daily work we've been strongly using both MyBatis Sql Mapper and Google Guice frameworks and once noticed we'd been continuosly repeating the same code snippets in different projects, according to the DRY _don't repeat yourself principle_, we started realizing something that alleviate us the task to create our stuff.

Indeed, this small library intends to create the missing perfect glue between the two popular frameworks, reducing the boilerplate and redundant code that users have to write to configure and use MyBatis into a Google Guice context.

## Quick Look ##

Core components are contained in the org.mybatis.guice package, providing a set of reusable Google Guice `com.google.inject.Providers` and `com.google.inject.Modules` that alleviate users the task to create MyBatis objects.

The core component of the Guice approach is represented by the org.mybatis.guice.MyBatisModule that's able to create, through a serie of required and optional `com.google.inject.Providers`, the core MyBatis, SqlSessionFactory, SqlSessionManager and the user defined Mappers.

```
Class<? extends Provider<DataSource>> dataSourceProviderClass = [...];
Class<? extends Provider<TransactionFactory>> txFactoryProviderClass = [...];
Injector injector = Guice.createInjector(new MyBatisModule() {

            @Override
            protected void initialize() {
                bindDataSourceProviderType(dataSourceProviderClass);
                bindTransactionFactoryType(txFactoryProviderClass);
                addMapperClass(ContactMapper.class);
                handleType(CustomType.class).with(CustomLongTypeHandler.class);
                handleType(Address.class).with(AddressTypeHandler.class);
                addInterceptorClass(CountUpdateInterceptor.class);
                addSimpleAlias(com.acme.Foo.class);
                addAlias("MyBar", com.acme.Bar.class);
                handleType(com.acme.Foo.class).with(com.acme.dao.FooHandler.class);
                handleType(com.acme.Bar.class).with(com.acme.dao.BarHandler.class);
                addMapperClass(com.acme.dao.FooMapper.class);
                addMapperClass(com.acme.dao.BarMapper.class);
                lazyLoadingEnabled(true);
                bindObjectFactoryType(com.acme.MyObjectFactoryProvider.class);
            }

        });
```

Once the MyBatis module has been set-up, clients are ready to request MyBatis components injections.

For example, users can request directly the configured Mapper interfaces:

```
@Singleton
public final class MyMappersClient {

    @Inject
    private FooMapper fooMapper;

    @Inject
    private BarMapper barMapper;
  
    // setters here

    @Transactional(
        executorType = ExecutorType.BATCH,
        isolationLevel = TransactionIsolationLevel.READ_UNCOMMITTED,
        rethrowExceptionsAs = MyCustomException.class,
        exceptionMessage = "Something went wrong while foobaring {0} argument"
    )
    public void doFooBar(String arg) {
        this.fooMapper.doFoo(arg);
        this.barMapper.doBar(arg);
    }

```

Please refer to the reference manual to know all about MyBatis-Guice features

## Installation ##

Installing the MyBatis-Guice module it is very easy, just put the mybatis-guice-3.2.jar and related dependencies in the classpath!

Apache Maven users instead can easily adding the following dependency in their POMs :

```
<dependency>
  <groupId>org.mybatis</groupId>
  <artifactId>mybatis-guice</artifactId>
  <version>3.2</version>
</dependency>
```