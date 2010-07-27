Abator is a code generator for the iBATIS data mapping framefork.

===============================================================================
Overview
--------
Abator will introspect database tables (through JDBC DatabaseMetaData) and
generate the following objects based on the structure of the tables:

1. Java POJOs that match the table structure including:
   - a class to match the primary key of the table (if there is a primary key)
   - a class to match the non-primary key fields of the table
     (except BLOB fields)
   - a class to include the BLOB fields of a table (if the table has
     BLOB fields)
   - a class to hold indicators for "Query By Example" functionality
   
   There is an inheritance relationship between these classes as
   appropriate.
   
2. iBATIS Compatible SQL Map XML Files.  iBATIS generates SQL for simple
   CRUD functions on each table in a configuration.  The generated SQL
   statements include:
   
   - insert
   - update by primary key
   - delete by primary key
   - delete by example (using a "query by example" strategy)
   - select by primary key
   - select by example (using a "query by example" strategy)
   
   There are different variations of these statements depending on the
   structure of the table (e.g. - if the table doesn't have a primary key,
   then Abator will not generate an update by primary key function, etc.)
   
3. DAO interface and implementation classes that make appropriate use of the
   above objects.  The generation of DAO classes is optional.  Abator will
   generate DAOs of the following types:
   
   - IBATIS: for the iBATIS DAO Framework (an optional part of iBATIS)
   - SPRING: for the SpringFramework
   - GENERIC-CI: for DAOs that only use the iBatis SQL mapping API.  In this
                 DAO the SQlMapClient must be supplied through constructor
                 injection.
   - GENERIC-SI: for DAOs that only use the iBatis SQL mapping API.  In this
                 DAO the SQlMapClient must be supplied through setter
                 injection.

===============================================================================
Limitations
-----------
1. Abator by itself does not attempt to merge newly created Java files with
   existing files.  This function must be supplied by another environment.  In
   the Eclipse plugin, Abator uses the Eclipse AST for manipulating Java source
   code in a syntactically correct manner.  So outside of such an environment,
   Abator will only overwrite or ignore existing Java files - according to the
   wishes of the user.  It may be possible to build something like Eclipse's
   AST with a tool like ANTLR, but that is for the future.


===============================================================================
Quick Start Guide
----------------
1. Create an Abator configuration file
2. Fill out the configuration file appropriately.  At a minimum, you must
   specify:
   
   - jdbcConnection information to specify how to connect to the target
     database
   - A target package, and target project for the javaModelGenerator
   - A target package, and target project for the sqlMapGenerator
   - A target package, target project, and type for the daoGenerator (or you
     can remove the daoGenerator element if you don't wish to generate DAOs).
     The type can be one of the types described above
   - At least one database table
3. Create a build.xml file for Ant as descibed below
4. Use Ant to run Abator


===============================================================================
Specifying the Configuration in XML
-----------------------------------
In the normal use case, Abator is driven by an XML configuration file.
The configuration file tells Abator:

- How to connect to the database
- What objects to generate, and how to generate them
- What tables should be used for object generation

The best reference for the XML configuration file is the annotated DTD.
The following is an example abator configuration file:

<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE abatorConfiguration PUBLIC
  "-//Apache Software Foundation//DTD Abator for iBATIS Configuration 1.0//EN"
  "http://ibatis.apache.org/dtd/abator-config_1_0.dtd">

<abatorConfiguration>
  <abatorContext id="DB2Tables">
    <jdbcConnection driverClass="COM.ibm.db2.jdbc.app.DB2Driver"
        connectionURL="jdbc:db2:TEST"
        userId="db2admin"
        password="db2admin">
      <classPathEntry location="/Program Files/IBM/SQLLIB/java/db2java.zip" />
    </jdbcConnection>
  
    <javaTypeResolver >
      <property name="forceBigDecimals" value="false" />
    </javaTypeResolver>
  
    <javaModelGenerator targetPackage="test.model"
        targetProject="\AbatorOutput\src">
      <property name="enableSubPackages" value="true" />
      <property name="trimStrings" value="true" />
    </javaModelGenerator>
  
    <sqlMapGenerator targetPackage="test.xml"
        targetProject="\AbatorOutput\src">
      <property name="enableSubPackages" value="true" />
    </sqlMapGenerator>
  
    <daoGenerator type="IBATIS" targetPackage="test.dao"
        targetProject="\AbatorOutput\src">
      <property name="enableSubPackages" value="true" />
    </daoGenerator>
  
    <table schema="DB2ADMIN" tableName="ALLTYPES" domainObjectName="Customer">
      <property name="useActualColumnNames" value="true"/> 
      <generatedKey column="ID" sqlStatement="DB2" identity="true" />
      <columnOverride column="DATE_FIELD" property="startDate" />
      <ignoreColumn column="FRED" />
      <columnOverride column="LONG_VARCHAR_FIELD" jdbcType="VARCHAR" />
    </table>
  
  </abatorContext>
</abatorConfiguration>

Important notes about this file follow:

1. The file specifies that the legacy DB2 CLI driver will be used to connect to
   the database, and also specifies where the driver can be found.
2. The Java Type Resolver should not force the use of Bigdecimal fields - this
   means that integral types (Short, Integer, Long, etc.) will be substituted
   if possible.  This feature is an attempt to make database DECIMAL and
   NUMERIC columns easier to deal with.
3. The Java model generator should use sub-packages.  This means that the
   generated model objects will be placed in a package called
   "test.model.db2admin" in this case (because the table is in the "DB2ADMIN"
   schema).  If the "enableSubPackages" attribute was set to "false", then the
   package would be "test.model".  The Java model generator should also trim
   strings.  This means that the setters for any String property will call the
   "trim" function - this is useful if your database will return blank
   characters at the end of character columns (id CHAR is used instead of
   VARCHAR for example).
4. The SQL Map generator should use sub-packages.  This means that the
   generated XML files will be placed in a package called "test.xml.db2admin"
   in this case (because the table is in the "DB2ADMIN" schema).  If the
   "enableSubPackages" attribute was set to "false", then the package would be
   "test.xml".
5. The DAO generator should use sub-packages.  This means that the generated
   DAO classes will be placed in a package called "test.dao.db2admin" in this
   case (because the table is in the "DB2ADMIN" schema).  If the
   "enableSubPackages" attribute was set to "false", then the package would be
   "test.dao".  The DAO generator should generate DAO classes that conform to
   the iBATIS DAO framework.  The DAO generator can generate DAO classes in
   the following formats:
   
   - IBATIS - for the iBATIS DAO Framework
   - SPRING - for the Spring Framework
   - GENRIC-CI - for DAOs that only depend on iBATIS' SQL Map API.  The
                 SqlMapClient instance is supplied through constructor
                 injection
   - GENRIC-SI - for DAOs that only depend on iBATIS' SQL Map API.  The
                 SqlMapClient instance is supplied through setter injection

6. The file specifies only one table will be introspected, but many more could
   be specified.  Important notes about that table are as follows:
   
   - The generated objects will be based on the name "Customer" (CustomerKey,
     Customer, CustomerDAO, etc.) - rather than on the table name.
   - Actual column names will be used as properties.  If this property were
     set to "false" (or not specified), then Abator would attempt to camel
     case the column names.  In either case, the name can be overridden by
     the <columnOverride> element
   - The column has a generated key, it is an identity column, and the database
     type is DB2.  This will cause Abator to generate the proper <selectKey>
     element in the generated <insert> statement so that the newly generated
     key can be returned.  Valid values for the "sqlStatement" attribute
     include DB2, MYSQL, SQLSERVER, DERBY, CLOUDSCAPE and HSQLDB (only
     if the column is an identity column).  Or you can specify the SQL proper
     statement for other databases.
   - The column "DATE_FIELD" will be mapped to a property called "startDate".
     This will override the default property which would be "DATE_FIELD" in
     this case, or "dateField" if the "useActualColumnNames" property was
     set to "false".
   - The column "FRED" will be ignored.  No SQL will list the field, and no
     Java property will be generated.
   - The column "LONG_VARCHAR_FIELD" will be treated as a VARCHAR field,
     regardless of the actual datatype.

7. Multiple contexts could be configured if you want Abator to run against
   different databases during the same run, or if you want to use different
   configuration options for different tables.

===============================================================================
Using Abator
------------
Abator can be run in the following ways:

- As an Ant taks with an XML configuration
- From another Java program with an XML configuration
- From another Java program with a Java based configuration

I'll discuss each method briefly.

1. Running Abator from Ant

  Abator includes a simple Ant task.  The task must be defined in your
  build.xml file, and the task has two parameters.  Here is an example
  build.xml file:

   <project default="genfiles" basedir=".">
     <target name="genfiles" description="Generate the files">
       <taskdef name="abator.genfiles" 
                classname="org.apache.ibatis.abator.ant.AbatorAntTask" 
                classpath="abator060.jar" />
       <abator.genfiles overwrite="true" configfile="abatorConfig.xml" />
     </target>
   </project>
   
   Notes:
   
   - The classpath on the <taskdef> is used to tell Ant where the implementing
     code is.  This is optional if you add Abator to the Ant classpath on one
     of the other ways described in the Ant manual
   - The name of the task can be anything you desire, "abator.genfiles" is
     simply an example
   - The "overwrite" attribute is optional.  If "true", "yes", etc. then Abator
     will overwrite any existing Java files.  If "false", "no", etc. then
     Abator will not touch existing Java files - the newly generated Java file
     will be saved with a unique filename instead.  The default is "false".
   - The "configfile" attribute is required - it must specify a valid Abator
     configuration XML file.
     
   When running in Ant, Abator interprets the project/package attributes in all
   XML configurations as follows:
   
   - Project is assumed to ba an existing directory structure.  The task will
     fail if this directory structure does not exist.
   - Package is assumed to be a subdirectory structure of the  project
     directory structure.  The task will create these directories if necessary.
  

2. Running Abator from Java with an XML Configuration file

   The following code sample shows ow to call Abator from Java.  It does not
   show exception handling, but that should be obvious from the compiler
   errors :)

   List warnings = new ArrayList();  // Abator will add Strings to this list
   boolean overwrite = true;
   File configFile = new File("abatorConfig.xml");   
   AbatorConfigurationParser cp = new AbatorConfigurationParser(warnings);
   AbatorConfiguration config = cp.parseAbatorConfiguration(configFile);
   DefaultShellCallback callback = new DefaultShellCallback(overwrite);
   Abator abator = new Abator(config, callback, warnings);
   abator.generate(null);
   
3. Running Abator from Java with a Java Based Configuration

   The following code sample shows ow to call Abator from Java only.  It does
   not show exception handling, but that should be obvious from the compiler
   errors :)

   List warnings = new ArrayList();  // Abator will add Strings to this list
   boolean overwrite = true;
   AbatorConfiguration config = new AbatorConfiguration();
   
   //   ... fill out the config object as appropriate...
   
   DefaultShellCallback callback = new DefaultShellCallback(overwrite);
   Abator abator = new Abator(config, callback, warnings);
   abator.generate(null);


===============================================================================
Extending Abator
----------------
Abator provides interfaces for all major functions and allows you to provide
your own implementations to the core tool.  The public API for Abator is
contained in the package org.apache.ibatis.abator.api.  I'll briefly cover each
major API below.

1. org.apache.ibatis.abator.api.JavaModelGenerator

   Abator calls methods in this interface to generate the POJOs described
   above.  The default implementation of this interface is 
   org.apache.ibatis.abator.internal.java.JavaModelGeneratorDefaultImpl.  You
   can provide your own implementation, and the default implementation has
   been designed for extensibility.
   
   To provide your own implementation, specify the fully qualified class name
   in the XML configuration like this:
      <javaModelGenerator type="mypackage.MyImplementation">
        ...
      </javaModelGenerator>
      
   The type element is optional, or can be set to "DEFAULT".  Either case will
   cause Abator to use the default implementation.
      
2. org.apache.ibatis.abator.api.SqlMapGenerator

   Abator calls methods in this interface to generate the SQL Maps described
   above.  The default implementation of this interface is 
   org.apache.ibatis.abator.internal.sqlmap.SqlMapGeneratorDefaultImpl.  You
   can provide your own implementation, and the default implementation has
   been designed for extensibility.
   
   To provide your own implementation, specify the fully qualified class name
   in the XML configuration like this:
      <sqlMapGenerator type="mypackage.MyImplementation">
        ...
      </sqlMapGenerator>
      
   The type element is optional, or can be set to "DEFAULT".  Either case will
   cause Abator to use the default implementation.

3. org.apache.ibatis.abator.api.DAOGenerator

   Abator calls methods in this interface to generate the different types of
   DAOs described above.  Abator supplies four implementations of this
   interface to match the different types of DAOs described above.  The
   implementations are:
   
   - org.apache.ibatis.abator.internal.java.DAOGeneratorGenericConstructorInjectionImpl
   - org.apache.ibatis.abator.internal.java.DAOGeneratorGenericSetterInjectionImpl
   - org.apache.ibatis.abator.internal.java.DAOGeneratorIbatisImpl
   - org.apache.ibatis.abator.internal.java.DAOGeneratorSpringImpl
   
   Each of these implementations extends the base abstract implementation
   org.apache.ibatis.abator.internal.java.DAOGeneratorBaseImpl
   
   The four different DAO implementations are "configured" through the use of
   a template described in org.apache.ibatis.abator.internal.java.DAOGeneratorTemplate.
   It should be fairly simple to provide a new template for a different type
   of DAO if needed.
   
   The DAO generators are also designed for extensibility.  To provide your own
   implementation, specify the fully qualified class name in the XML
   configuration like this:
      <daoGenerator type="mypackage.MyImplementation">
        ...
      </daoGenerator>
      
   The type element is required.  It can also be set to one of the following
   special values to use one of the four supplied implementations:
   
   - IBATIS
   - SPRING
   - GENERIC-CI
   - GENERIC-SI

4. org.apache.ibatis.abator.api.JavaTypeResolver

   Abator calls methods in this interface to map JDBC types to Java types
   during database introspection.  The default implementation of this
   interface is org.apache.ibatis.abator.internal.types.JavaTypeResolverDefaultImpl.
   You can provide your own implementation, and the default implementation has
   been designed for extensibility.
   
   To provide your own implementation, specify the fully qualified class name
   in the XML configuration like this:
      <javaTypeResolver type="mypackage.MyImplementation">
        ...
      </javaTypeResolver>
      
   The type element is optional, or can be set to "DEFAULT".  Either case will
   cause Abator to use the default implementation.
   
5. org.apache.ibatis.abator.api.ShellCallback

   Abator calls methods in this interface to perform functions that it cannot
   do on its own.  The most important of these functions are:
   
   - Translating project/package into a directory structure
   - Merging Java source files in the event that an existing Java file of
     the same name/package exists.
   
   The default implementation of this interface is 
   org.apache.ibatis.abator.internal.DefaultShellCallback.  The default
   implementation simply concatenates project and package together and creates
   the necessary directory if needed.  The default implementation does not
   support merging of Java files, and will either overwrite or ignore files.
   
   You can provide your own implementation.  This would be the most important
   class to write if you want to integrate Abator into some other environment.
   The Eclipse plugin provides an implementation of this interface that
   supports Java file merging when running in the Eclipse environment.
   
   To provide your own implementation, supply an instance of the interface
   on the constructor to the org.apache.ibatis.abator.api.Abator
   object.

6. org.apache.ibatis.abator.api.ProgressCallback

   Abator calls methods in this interface to report progress during the
   generation of files (a long running process).  The default implementation
   of this interface is org.apache.ibatis.abator.internal.NullProgressCallback
   which simply ignores all progress messages.  You can provide an
   implementation of this interface to support progress notifications and
   cancellation of file generation.
   
   Implementing this class would also be important when integrating Abator
   into other IDE environments.
   
   To provide your own implementation, supply an instance of the interface
   in the org.apache.ibatis.abator.api.Abator.generate() method call.

   
===============================================================================
Dependencies
------------
Abator has no dependencies beyond the JRE.  Abator does require JRE 1.4 or
above.  Abator also requires that the JDBC driver implements the
DatabaseMetaData interface, especially the "getColumns" and "getPrimaryKeys"
methods.


===============================================================================
Support
-------
Support for Abator is provided through the iBATIS user mailing list.  Mail
questions or bug reports to:

  user-java@ibatis.apache.org
