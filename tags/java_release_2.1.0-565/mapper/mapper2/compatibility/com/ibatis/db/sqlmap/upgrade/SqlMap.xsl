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
    doctype-public="-//iBATIS.com//DTD SQL Map 2.0//EN"
    doctype-system="http://www.ibatis.com/dtd/sql-map-2.dtd"
    cdata-section-elements=""
    indent="yes"
    media-type="sqlMapConfig"/>

  <xsl:template match="cache-model">
    <xsl:element name="cacheModel">
      <xsl:if test="@name">
        <xsl:attribute name="id">
          <xsl:value-of select="@name"/>
        </xsl:attribute>
      </xsl:if>

      <xsl:choose>
        <xsl:when test="@reference-type">
          <xsl:attribute name="type">MEMORY</xsl:attribute>
          <property name="reference-type" value="{@reference-type}"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:if test="@implementation">
            <xsl:attribute name="type">
              <xsl:value-of select="@implementation"/>
            </xsl:attribute>
          </xsl:if>
        </xsl:otherwise>
      </xsl:choose>

      <xsl:for-each select="property">
        <property name="{@name}" value="{@value}"/>
      </xsl:for-each>

      <xsl:for-each select="flush-interval">
        <xsl:element name="flushInterval">
          <xsl:if test="@hours">
            <xsl:attribute name="hours">
              <xsl:value-of select="@hours"/>
            </xsl:attribute>
          </xsl:if>
          <xsl:if test="@minutes">
            <xsl:attribute name="minutes">
              <xsl:value-of select="@minutes"/>
            </xsl:attribute>
          </xsl:if>
          <xsl:if test="@seconds">
            <xsl:attribute name="seconds">
              <xsl:value-of select="@seconds"/>
            </xsl:attribute>
          </xsl:if>
          <xsl:if test="@milliseconds">
            <xsl:attribute name="milliseconds">
              <xsl:value-of select="@milliseconds"/>
            </xsl:attribute>
          </xsl:if>
        </xsl:element>
      </xsl:for-each>
      <xsl:for-each select="flush-on-execute">
        <xsl:element name="flushOnExecute">
          <xsl:if test="@statement">
            <xsl:attribute name="statement">
              <xsl:value-of select="@statement"/>
            </xsl:attribute>
          </xsl:if>
        </xsl:element>
      </xsl:for-each>
    </xsl:element>
  </xsl:template>


  <xsl:template match="result-map">
    <xsl:element name="resultMap">
      <xsl:if test="@name">
        <xsl:attribute name="id">
          <xsl:value-of select="@name"/>
        </xsl:attribute>
      </xsl:if>
      <xsl:if test="@class">
        <xsl:attribute name="class">
          <xsl:value-of select="@class"/>
        </xsl:attribute>
      </xsl:if>
      <xsl:if test="@extends">
        <xsl:attribute name="extends">
          <xsl:value-of select="@extends"/>
        </xsl:attribute>
      </xsl:if>
      <xsl:for-each select="property">
        <xsl:element name="result">
          <xsl:if test="@name">
            <xsl:attribute name="property">
              <xsl:value-of select="@name"/>
            </xsl:attribute>
          </xsl:if>
          <xsl:if test="@column">
            <xsl:attribute name="column">
              <xsl:value-of select="@column"/>
            </xsl:attribute>
          </xsl:if>
          <xsl:if test="@columnIndex">
            <xsl:attribute name="columnIndex">
              <xsl:value-of select="@columnIndex"/>
            </xsl:attribute>
          </xsl:if>
          <xsl:if test="@mapped-statement">
            <xsl:attribute name="select">
              <xsl:value-of select="@mapped-statement"/>
            </xsl:attribute>
          </xsl:if>
          <xsl:if test="@type">
            <xsl:attribute name="jdbcType">
              <xsl:value-of select="@type"/>
            </xsl:attribute>
          </xsl:if>
          <xsl:if test="@javaType">
            <xsl:attribute name="javaType">
              <xsl:value-of select="@javaType"/>
            </xsl:attribute>
          </xsl:if>
          <xsl:if test="@null">
            <xsl:attribute name="nullValue">
              <xsl:value-of select="@null"/>
            </xsl:attribute>
          </xsl:if>
        </xsl:element>
      </xsl:for-each>
    </xsl:element>
  </xsl:template>


  <xsl:template match="parameter-map">
    <xsl:element name="parameterMap">
      <xsl:if test="@name">
        <xsl:attribute name="id">
          <xsl:value-of select="@name"/>
        </xsl:attribute>
      </xsl:if>
      <xsl:for-each select="property">
        <xsl:element name="parameter">
          <xsl:if test="@name">
            <xsl:attribute name="property">
              <xsl:value-of select="@name"/>
            </xsl:attribute>
          </xsl:if>
          <xsl:if test="@type">
            <xsl:attribute name="jdbcType">
              <xsl:value-of select="@type"/>
            </xsl:attribute>
          </xsl:if>
          <xsl:if test="@javaType">
            <xsl:attribute name="javaType">
              <xsl:value-of select="@javaType"/>
            </xsl:attribute>
          </xsl:if>
          <xsl:if test="@null">
            <xsl:attribute name="nullValue">
              <xsl:value-of select="@null"/>
            </xsl:attribute>
          </xsl:if>
        </xsl:element>
      </xsl:for-each>
    </xsl:element>
  </xsl:template>

  <xsl:template match="mapped-statement|dynamic-mapped-statement">
    <xsl:element name="statement">
      <xsl:if test="@name">
        <xsl:attribute name="id">
          <xsl:value-of select="@name"/>
        </xsl:attribute>
      </xsl:if>
      <xsl:if test="@parameter-map">
        <xsl:attribute name="parameterMap">
          <xsl:value-of select="@parameter-map"/>
        </xsl:attribute>
      </xsl:if>
      <xsl:if test="@parameter-class">
        <xsl:attribute name="parameterClass">
          <xsl:value-of select="@parameter-class"/>
        </xsl:attribute>
      </xsl:if>
      <xsl:if test="@result-map">
        <xsl:attribute name="resultMap">
          <xsl:value-of select="@result-map"/>
        </xsl:attribute>
      </xsl:if>
      <xsl:if test="@result-class">
        <xsl:attribute name="resultClass">
          <xsl:value-of select="@result-class"/>
        </xsl:attribute>
      </xsl:if>
      <xsl:if test="@cache-model">
        <xsl:attribute name="cacheModel">
          <xsl:value-of select="@cache-model"/>
        </xsl:attribute>
      </xsl:if>

      <xsl:call-template name="copy-statement-nodes"/>

    </xsl:element>
  </xsl:template>

  <xsl:template match="sql-map">
    <xsl:element name="sqlMap">
      <xsl:if test="@name">
        <xsl:attribute name="namespace">
          <xsl:value-of select="@name"/>
        </xsl:attribute>
      </xsl:if>
      <xsl:apply-templates/>
    </xsl:element>
  </xsl:template>

  <xsl:template name="copy-statement-nodes">
    <xsl:for-each select="node()">
      <xsl:copy>
        <xsl:call-template name="copy-dynamic-nodes"/>
      </xsl:copy>
    </xsl:for-each>
  </xsl:template>

  <xsl:template name="copy-dynamic-nodes">
    <xsl:for-each select="@*|node()">
      <xsl:copy>
        <xsl:call-template name="copy-dynamic-nodes"/>
      </xsl:copy>
    </xsl:for-each>
  </xsl:template>


  <xsl:template match="/">
    <xsl:text>
    </xsl:text>
    <xsl:comment>Converted by iBATIS SQL Map Converter 'A' (1.x to 2.x)</xsl:comment>
    <xsl:text>
    </xsl:text>
    <xsl:apply-templates/>
  </xsl:template>

</xsl:stylesheet>
