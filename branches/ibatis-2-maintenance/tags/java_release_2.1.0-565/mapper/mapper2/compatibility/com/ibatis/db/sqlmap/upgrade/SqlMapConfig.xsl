<?xml version="1.0" encoding="UTF-8" ?>

<!--
   Copyright 2004 Clinton Begin

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 -->

<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:fo="http://www.w3.org/1999/XSL/Format">

  <xsl:output
    method="xml"
    version="1.0"
    encoding="UTF-8"
    omit-xml-declaration="no"
    standalone="no"
    doctype-public="-//iBATIS.com//DTD SQL Map Config 2.0//EN"
    doctype-system="http://www.ibatis.com/dtd/sql-map-config-2.dtd"
    cdata-section-elements=""
    indent="yes"
    media-type="sqlMapConfig"/>

  <xsl:template match="properties">
    <properties resource="{@resource}"/>
  </xsl:template>

  <xsl:template match="settings">
    <xsl:element name="settings">
      <xsl:if test="@useFullyQualifiedStatementNames">
        <xsl:attribute name="useStatementNamespaces">
          <xsl:value-of select="@useFullyQualifiedStatementNames"/>
        </xsl:attribute>
      </xsl:if>
      <xsl:if test="@cacheModelsEnabled">
        <xsl:attribute name="cacheModelsEnabled">
          <xsl:value-of select="@cacheModelsEnabled"/>
        </xsl:attribute>
      </xsl:if>
      <xsl:if test="@useBeansMetaClasses">
        <xsl:attribute name="enhancementEnabled">
          <xsl:value-of select="@useBeansMetaClasses"/>
        </xsl:attribute>
      </xsl:if>
      <xsl:if test="@maxTransactions">
        <xsl:attribute name="maxTransactions">
          <xsl:value-of select="@maxTransactions"/>
        </xsl:attribute>
      </xsl:if>
      <xsl:if test="@maxExecute">
        <xsl:attribute name="maxRequests">
          <xsl:value-of select="@maxExecute"/>
        </xsl:attribute>
      </xsl:if>
    </xsl:element>
  </xsl:template>

  <xsl:template match="datasource">
    <xsl:element name="transactionManager">
      <xsl:choose>
        <xsl:when test="//settings/@useGlobalTransactions='true'">
          <xsl:attribute name="type">JTA</xsl:attribute>
          <xsl:element name="property">
            <xsl:attribute name="name">UserTransaction</xsl:attribute>
            <xsl:attribute name="value">
              <xsl:value-of select="//settings/@userTransactionJndiName"/>
            </xsl:attribute>
          </xsl:element>
        </xsl:when>
        <xsl:otherwise>
          <xsl:attribute name="type">JDBC</xsl:attribute>
        </xsl:otherwise>
      </xsl:choose>
      <xsl:text>
      </xsl:text>
      <xsl:element name="dataSource">
        <xsl:if test="@factory-class='com.ibatis.db.sqlmap.datasource.SimpleDataSourceFactory'">
          <xsl:attribute name="type">SIMPLE</xsl:attribute>
        </xsl:if>
        <xsl:if test="@factory-class='com.ibatis.db.sqlmap.datasource.DbcpDataSourceFactory'">
          <xsl:attribute name="type">DBCP</xsl:attribute>
        </xsl:if>
        <xsl:if test="@factory-class='com.ibatis.db.sqlmap.datasource.JndiDataSourceFactory'">
          <xsl:attribute name="type">JNDI</xsl:attribute>
        </xsl:if>
        <xsl:for-each select="property[@name and @value]">
          <xsl:text>
          </xsl:text>
          <property name="{@name}" value="{@value}"/>
          <xsl:text>
          </xsl:text>
        </xsl:for-each>
      </xsl:element>
      <xsl:text>
      </xsl:text>
    </xsl:element>
  </xsl:template>

  <xsl:template match="sql-map">
    <sqlMap resource="{@resource}"/>
  </xsl:template>

  <xsl:template match="/">
    <xsl:text>
    </xsl:text>
    <xsl:comment>Converted by iBATIS SQL Map Converter 'A' (1.x to 2.x)</xsl:comment>
    <xsl:text>
    </xsl:text>
    <sqlMapConfig>
      <xsl:apply-templates/>
    </sqlMapConfig>
  </xsl:template>

</xsl:stylesheet>
