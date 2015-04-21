# Introduction #

MyBatis 3.2+ supports pluggable scripting engines so that you can build your dinamic queries using your preferred language.

## Velocity ##

[Velocity](http://velocity.apache.org) is a template engine that can be used for many purposes.

If you are not familiar with apache velocity, you can learn it from its documentation site:

To install the plugin in your project, add the dependency:

```
<dependencies>
    ...
    <dependency>
        <groupId>org.mybatis.scripting</groupId>
        <artifactId>mybatis-velocity</artifactId>
        <version>xxx</version>
    </dependency>
    ...
</dependencies>
```

Register the language driver alias in your mybatis configuration file:

```
<configuration>
  ...
  <typeAliases>
    <typeAlias alias="velocity" type="org.mybatis.scripting.velocity.Driver"/>
  </typeAliases>
  ...
</configuration>
```

Optional: Set the velocity as your default scripting language:

```
<configuration>
  ...
  <settings>
    <setting name="defaultScriptingLanguage" value="velocity"/>
  </settings>
  ...
</configuration>
```

Now you can write dynamic statements like this one:

```
<select id="findPerson" lang="velocity">
  #set( $pattern = $_parameter.name + '%' )
  SELECT *
  FROM person
  WHERE name LIKE @{pattern, jdbcType=VARCHAR}
</select>
```

For further info read the [manual](http://mybatis.github.io/velocity-scripting/)