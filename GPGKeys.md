# GPG Keys #

The file located on

` https://mybatis.googlecode.com/svn/committers/KEYS `

contains the MyBatis developers GPG keys. Users can import the keys to verify the artifact signatures.

# Users #

Checkout the file by running

` svn co https://mybatis.googlecode.com/svn/committers `

and run

` gpg --import KEYS `

# Developers #

Checkout the file by running

` svn co https://mybatis.googlecode.com/svn/committers `

and run

` (gpg --list-sigs <your name> && gpg --armor --export <your name>) >> KEYS `

then recommit

` svn ci -m "added simone's gpg key" `