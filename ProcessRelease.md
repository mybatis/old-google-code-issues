## Introduction ##

This page will guide the development team with setting up their environment to perform a release.


## Prerequisites ##

  * Install/Configure GPG - The artifacts that are deployed to the central maven repositories need to be signed.  To do this you will need to have a public and private keypair.  There is a very good [guide](http://www.sonatype.com/people/2010/01/how-to-generate-pgp-signatures-with-maven/) that will walk you though this.

  * Install Maven 3

## Configuration ##

### Maven ###
As of Maven 2.1.0 you can now encrypt your servers passwords.  We highly recommend that you follow this [guide](http://maven.apache.org/guides/mini/guide-encryption.html) to set your master password and use it to encrypt your Sonatype password in the next section.

### Sonatype ###
Using the instructions from the previous step encrypt your Sonatype password and add the following servers to your `~/.m2/settings.xml` file.  You may already have other servers in this file.  If not just create the file.
```
<?xml version="1.0" encoding="UTF-8"?>
<settings>
    <servers>
        <server>
            <id>sonatype-nexus-snapshots</id>
            <username>nmaves</username>
            <password>{jSMOWnoPFgsHVpMvz5VrIt5kRbzGpI8u+9EF1iFQyJQ=}</password>
        </server>

        <server>
            <id>sonatype-nexus-staging</id>
            <username>nmaves</username>
            <password>{jSMOWnoPFgsHVpMvz5VrIt5kRbzGpI8u+9EF1iFQyJQ=}</password>
        </server>
    </servers>
</settings>
```

### Google Code ###
Using the instructions from the previous step encrypt your Google Code password and add the following server to your `~/.m2/settings.xml` file.  You may already have other servers in this file.  If not just create the file.
```
<?xml version="1.0" encoding="UTF-8"?>
<settings>
    <servers>
        <server>
            <id>googlecode</id>
            <username>nmaves</username>
            <password>{JoOt7XAPsdYHo2uAC2p4bWN7kOVrLglffMw19Z9ETz0=}</password>
        </server>
    </servers>
</settings>
```

### Configure the MyBatis parent pom ###
If you're stared a new subproject, please use the already configured _parent pom_ to avoid replicate the same meta inf in every project:
```
<parent>
    <groupId>org.mybatis</groupId>
    <artifactId>mybatis-parent</artifactId>
    <version>14</version>
</parent>
```

Even if follows below a guide that illustrates how to simplify the development, please take a look at the source code to see what has been already declared and don't invest time on something already done.

The parent pom defines:

  * the inception year;
  * the organization;
  * the license
  * the issue management;
  * the mailing lists;
  * the developers list;
  * commons useful reporting plugins (not published, but useful during development)
  * commons useful build plugins (everybody agrees the compiler settings is always the same)

Users that intend use the parent pom have to configure the properties below:

  * `findbugs.onlyAnalyze` the package name (usually followed by .`*`) under findbug analysis
  * `clirr.comparisonVersion` the version to compare the current code against, default value is: `${project.version}`

When creating a new subproject, make sure following resources are present at same level of `src` dir:

  * `LICENSE`
  * `NOTICE`

Moreover there are predefined, not active by default, profiles for a common behavior of the release process.

  * `docbook`: useful to generate pdf users guide, it expects `src/docbkx/index.xml` file and will produce a `${project.build.directory}/docbkx/pdf/${project.artifactId}-${project.version}-reference.pdf`; this profile can be activated using `-Pdocbook` option when launching Maven on shell;
  * `bundle`: it creates the 99% typical distribution `${project.build.directory}/${project.artifactId}-${project.version}-bundle.zip` package to be uploaded on MyBatis space on Google Code (requires `release` and `docbook` profiles active); this profile can be activated using `-Pbundle` option when launching Maven on shell;
  * `gupload`: upload the zip bundle on MyBatis space on Google Code, adding `${project.description} ${project.version} release` as summary and `Featured,Type-Archive` as labels (requires `release`, `docbook` and `bundle` profiles active); this profile can be activated using `-Pgupload` option when launching Maven on shell;

To activate profiles during the release process, it is strongly suggested to configure the release plugin:
```
<build>
    <plugins>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-release-plugin</artifactId>
            <configuration>
                <arguments>-Prelease,docbook,bundle,gupload</arguments>
            </configuration>
        </plugin>
    </plugins>
</build>
```

**NOTE**
Users that have the need to customize the above behaviors can reconfigure the plugins in their POM, please don't take them as a dogma, they have been added only because they match with the 99% of the cases (see subprojects)

## Release ##

### Release the parent pom ###

To release the parent pom developers have to enable the `parent-release` profile:

  * Prepare the release
` mvn release:prepare -Pparent-release `
  * Perform
` mvn release:perform -Pparent-release -Dgpg.passphrase=thephrase `

or just

` mvn release:perform -Pparent-release `

and type the gpg passphrase when prompted

### Release a MyBatis module ###

The release plugin for maven is already configured in the MyBatis parent pom file so all you need to do is execute the following two steps to complete the release.  The first step will create the release tag and update the pom with the correct release and snapshot versions.  The second step will sign and deploy the artifacts to the Sonatype open source repository.  This repository is synced every hour to the central Maven repositories.  If you don't supply the optional gpg.passphrase then you will be prompted for it.

  * Prepare the release
` mvn release:prepare `
  * Perform
` mvn release:perform -Dgpg.passphrase=thephrase`

or just

` mvn release:perform -Pparent-release `

and type the gpg passphrase when prompted

Now you can checkout the new release ( or just use the one left over from the release in target/checkout) and issue the following to build the artifacts for the site.

`mvn package`

## Managing Sonatype's Nexus ##
The mybatis-parent v4 is configured to work with Sonatype's Nexus to simplify the Staging Repository close and promote operation, without logging in on Nexus using the browser.
First of all, add in the `~/.m2/settings.xml`the following snippet:

```
<pluginGroups>
    <pluginGroup>org.sonatype.plugins</pluginGroup>
</pluginGroups>
```

then in the project dir you can use the nexus goals described on the [plugin page](http://www.sonatype.com/books/nexus-book/reference/staging-sect-managing-plugin.html)