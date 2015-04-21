# Introduction #

The MyBatis community supports as well 3rd part cache implementations, follow below which one are currently supported in the 1.0.0 release:


## Ehcache ##

[Ehcache](http://ehcache.org/) is a widely used java distributed cache for general purpose caching, Java EE and light-weight containers.

The Ehcache integration is built on top of the `ehcache-core` and comes without any Ehcache 3rd part applications. Please refeer to official Ehcache documentation if you need Ehcache [plugins](http://ehcache.org/documentation/index.html).

Users that want to use Ehcache into their applications, have to download the zip bundle, decompress it and add the jars in the classpath; Apache Maven users instead can simply add in the `pom.xm`l the following dependency:

```
<dependencies>
    ...
    <dependency>
        <groupId>org.mybatis.caches</groupId>
        <artifactId>mybatis-ehcache</artifactId>
        <version>x.x.x</version>
    </dependency>
    ...
</dependencies>
```

then, just configure it in the mapper XML:

```
<mapper namespace="org.acme.FooMapper">

    <cache type="org.mybatis.caches.ehcache.EhcacheCache"/>

    ...

</mapper>
```

If users need to log cache operations, they can plug the Cache logging version:

```
<mapper namespace="org.acme.FooMapper">

    <cache type="org.mybatis.caches.ehcache.LoggingEhcacheCache"/>

    ...

</mapper>
```

Users that need to configure Ehcache through XML configuration file, have to put in the classpath the `/ehcache.xml` resource; please refeer to the official Ehcache [documentation](http://ehcache.org/documentation/configuration.html) to know more details.

For further info read the [manual](http://mybatis.github.io/ehcache-cache/)

## Hazelcast ##

[Hazelcast](http://www.hazelcast.com) is an open source clustering and highly scalable data distribution platform for Java.

Users that want to use Hazelcast into their applications, have to download the zip bundle, decompress it and add the jars in the classpath; Apache Maven users instead can simply add in the `pom.xm`l the following dependency:

```
<dependencies>
    ...
    <dependency>
        <groupId>org.mybatis.caches</groupId>
        <artifactId>mybatis-hazelcast</artifactId>
        <version>x.x.x</version>
    </dependency>
    ...
</dependencies>
```

then, just configure it in the mapper XML:

```
<mapper namespace="org.acme.FooMapper">

    <cache type="org.mybatis.caches.hazelcast.HazelcastCache"/>

    ...

</mapper>
```

If users need to log cache operations, they can plug the Cache logging version:

```
<mapper namespace="org.acme.FooMapper">

    <cache type="org.mybatis.caches.hazelcast.LoggingHazelcastCache"/>

    ...

</mapper>
```

Please refer to the official Hazelcast Near Cache [documentation](http://www.hazelcast.com/documentation.jsp#MapNearCache) to know more details how to configure Hazelcast as Cache.

For further info read the [manual](http://mybatis.github.io/hazelcast-cache/)

## OSCache ##

[OSCache](http://www.opensymphony.com/oscache/) is a high performances caching solution developed and maintained by [Open Symphony](http://www.opensymphony.com/) and easily integrated in MyBatis since the iBATIS version 2.X.

Users that want to use OSCache into their applications, have to download the zip bundle, decompress it and add the jars in the classpath; Apache Maven users instead can simply add in the `pom.xm` the following dependency:

```
<dependencies>
    ...
    <dependency>
        <groupId>org.mybatis.caches</groupId>
        <artifactId>mybatis-oscace</artifactId>
        <version>x.x.x</version>
    </dependency>
    ...
</dependencies>
```

then, just configure it in the mapper XML:

```
<mapper namespace="org.acme.FooMapper">

    <cache type="org.mybatis.caches.oscache.OSCache"/>

    ...

</mapper>
```

If users need to log cache operations, they can plug the Cache logging version:

```
<mapper namespace="org.acme.FooMapper">

    <cache type="org.mybatis.caches.oscache.LoggingOSCache"/>

    ...

</mapper>
```

For proper OSCache configuration please read the official [reference](http://wiki.opensymphony.com/display/CACHE/Configuration)

For further info read the [manual](http://mybatis.github.io/oscache-cache/)