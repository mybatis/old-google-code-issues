## Introduction ##

MyBatis is a direct fork of the iBATIS code.  At the time of this writing, the code is identical, but there are no
known plans to continue the iBATIS project within the Apache software foundation. Therefore it is somewhat less of a
fork, and more like a bend in the road.  As with a corner when driving, there is a slight adjustment to be made.

## Questions ##

  * **Do I have to change my code?**

> No.  The MyBatis releases will be fully backward compatible.  We have
> no intention to ever change the package name (maybe in version 4 someday).  The only think that may need to be
> changed in the not to distant future is the DTD location.  Instructions are below.  Don't worry though, your
> code won't explode if you don't.  It will just mean you can't use some of the newer features in the future until
> you do.

  * **Did the license change?**

> No.  The copyright for future code will change.  But the license is still the Apache License 2.0.

  * **Which versions will be supported?**

> iBATIS 2 and 3 will be supported, with both 2.3.5 and 3.0.1 GA releases becoming available immediately.
> iBATIS.NET 1.x and 3.x will be supported, with a new release coming available soon.

  * **What will happen to the Apache iBATIS project?**

> Nothing really.  Apache won't delete any existing resources.  They'll be available in a read-only form.
> The Apache project will be archived in the "Apache Attic" when it becomes stale


## Steps ##

There are only a few simple steps to follow, some to do now, and some to do when you're ready.

  1. Sign up for new mailing lists **(now)**
  1. Use the new issue tracker and wiki **(now)**
  1. Change bookmarks / links to our site **(now)**
  1. Download new build (when you need your next build)
  1. Change DTD/Doctype in XML Headers (at your next opportunity)

### 1. Mailing Lists ###

First, unsubscribe from the old mailing lists by sending an email to each one that you are signed up for.

Most users of the Java framework will only be signed up for the user list.

  * [mailto:user-java-unsubscribe@ibatis.apache.org](mailto:user-java-unsubscribe@ibatis.apache.org) - Java User List
  * [mailto:user-cs-unsubscribe@ibatis.apache.org](mailto:user-cs-unsubscribe@ibatis.apache.org) - .NET User List
  * [mailto:dev-unsubscribe@ibatis.apache.org](mailto:dev-unsubscribe@ibatis.apache.org) - Developer List
  * [mailto:commits-unsubscribe@ibatis.apache.org](mailto:commits-unsubscribe@ibatis.apache.org) - Commits List

There are no replacements for the developer or commits lists.  Commits are easy to find with subversion or on the
[Source/Changes](http://code.google.com/p/mybatis/source/list) tab of the Google Projects page

Now you're ready to sign up to the new MyBatis Google Group.  If you already have a Google Account, you won't
have much to do...

  * Log into Google (GMail, YouTube, etc.) or sign up if you don't have an account.
  * Then join one or both of these groups:

  * **Java**
    * http://groups.google.com/group/mybatis-user

  * **.NET**
    * http://groups.google.com/group/mybatisnet-user

You can leave and re-join the group any time using the same page.  You can also search the group right there on the discussion
page.

### 2. Issue Tracking and Wiki ###

The Google Code project site has a new unified wiki and issue tracker.  Please do not use the old Jira and Confluence
wiki pages.  We won't necessarily see any issues or comments posted there (recently due to an infrastructure change at the
ASF, all comments were lost on Confluence pages).

We've moved all of the open issues (all new featurs) for iBATIS 3 to the new Google Code issue tracker.

You can find the new Issue Tracker and Wiki here:

Java
  * [Issue Tracker](http://code.google.com/p/mybatis/issues/list)
  * [Wiki](http://code.google.com/p/mybatis/wiki/Welcome)

.NET
  * [Issue Tracker](http://code.google.com/p/mybatisnet/issues/list)
  * [Wiki](http://code.google.com/p/mybatisnet/wiki/Welcome)


### 3. Bookmarks and Links ###

Of course you don't want to be left with outdated or dead links.  So be sure to update your browser bookmarks
and any links to our site:

  * http://www.mybatis.org - Homepage
  * http://code.google.com/p/mybatis/ - Java Google Code Project
  * http://code.google.com/p/mybatisnet/ - .NET Google Code Project

### 4. Update to the Latest Build (Java only at this time) ###

There's no need to do this immediately.  But when you're ready to upgrade to the latest release or the 3.0 GA release
of MyBatis, you should do so from this site.  There are no known plans to release any further from the Apache site.

When the time comes, you'll find the new releases summarized here:

  * http://mybatis.org/java.html - software download page at mybatis.org

All future releases will be distributed via Google Code and archived here:

  * http://code.google.com/p/mybatis/downloads/list - Google Code project download page

For Maven users, your new dependency coordinates are:

```
  <dependency>
    <groupId>org.mybatis</groupId>
    <artifactId>mybatis</artifactId>
    <version>3.0.1</version>
  </dependency>
```

### 5. Update your DTD/Doctype Headers (Java only at this time) ###

**After** you've updated to a build **later** than "3.0 GA Candidate", you can update your DOCTYPE headers in your Configuration and Mapper XML files.

```
#
# DOCTYPE header for MyBatis XML Configuration file.
#

<!DOCTYPE configuration
    PUBLIC "-//mybatis.org//DTD Config 3.0//EN"
    "http://mybatis.org/dtd/mybatis-3-config.dtd">

#
# DOCTYPE header for MyBatis XML Mapper files.
#

<!DOCTYPE mapper
    PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
    "http://mybatis.org/dtd/mybatis-3-mapper.dtd"> 
```