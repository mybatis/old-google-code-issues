<?xml version="1.0" encoding="UTF-8"?>
<!--
    Copyright 2010 The myBatis Team

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

<!--
    version: $Id$
-->
<xsl:stylesheet
        version="1.0"
        xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
        xmlns:regexp="http://xml.apache.org/xalan/java/org.mybatis.i2m.RegexpReplacer">

    <xsl:output method="xml" encoding="UTF-8" />

    <xsl:template match="/">
        <xsl:apply-templates select="sqlMap" />
    </xsl:template>

    <xsl:template match="sqlMap">
        <xsl:text disable-output-escaping="yes" >
            <![CDATA[<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">]]>
        </xsl:text>
        <xsl:element name="mapper">
            <xsl:attribute name="namespace"><xsl:value-of select="@namespace" /></xsl:attribute>
            <xsl:apply-templates />
        </xsl:element>
    </xsl:template>

    <xsl:template match="select">
        <xsl:element name="select">
            <xsl:attribute name="id"><xsl:value-of select="@id" /></xsl:attribute>

            <xsl:if test="@parameterClass">
                <xsl:attribute name="parameterType"><xsl:value-of select="@parameterClass" /></xsl:attribute>
            </xsl:if>

            <xsl:if test="@parameterMap">
                <xsl:attribute name="parameterMap"><xsl:value-of select="@parameterMap" /></xsl:attribute>
            </xsl:if>

            <xsl:if test="@resultClass">
                <xsl:attribute name="resultType"><xsl:value-of select="@resultClass" /></xsl:attribute>
            </xsl:if>

            <xsl:if test="@resultMap">
                <xsl:attribute name="resultMap"><xsl:value-of select="@resultMap" /></xsl:attribute>
            </xsl:if>

            <xsl:if test="@fetchSize">
                <xsl:attribute name="fetchSize"><xsl:value-of select="@fetchSize" /></xsl:attribute>
            </xsl:if>

            <xsl:if test="@resultSetType">
                <xsl:attribute name="resultSetType"><xsl:value-of select="@resultSetType" /></xsl:attribute>
            </xsl:if>

            <xsl:if test="@timeout">
                <xsl:attribute name="timeout"><xsl:value-of select="@timeout" /></xsl:attribute>
            </xsl:if>

            <xsl:apply-templates />
        </xsl:element>
    </xsl:template>

    <xsl:template match="insert">
        <xsl:element name="insert">
            <xsl:attribute name="id"><xsl:value-of select="@id" /></xsl:attribute>

            <xsl:if test="@parameterClass">
                <xsl:attribute name="parameterType"><xsl:value-of select="@parameterClass" /></xsl:attribute>
            </xsl:if>

            <xsl:if test="@parameterMap">
                <xsl:attribute name="parameterMap"><xsl:value-of select="@parameterMap" /></xsl:attribute>
            </xsl:if>

            <xsl:apply-templates />

          </xsl:element>
    </xsl:template>

    <xsl:template match="update">
        <xsl:element name="update">
            <xsl:attribute name="id"><xsl:value-of select="@id" /></xsl:attribute>

            <xsl:if test="@parameterClass">
                <xsl:attribute name="parameterType"><xsl:value-of select="@parameterClass" /></xsl:attribute>
            </xsl:if>

            <xsl:if test="@parameterMap">
                <xsl:attribute name="parameterMap"><xsl:value-of select="@parameterMap" /></xsl:attribute>
            </xsl:if>

           <xsl:apply-templates />
        </xsl:element>
    </xsl:template>

    <xsl:template match="procedure">
        <xsl:element name="update">
            <xsl:attribute name="id"><xsl:value-of select="@id" /></xsl:attribute>

            <xsl:if test="@parameterClass">
                <xsl:attribute name="parameterType"><xsl:value-of select="@parameterClass" /></xsl:attribute>
            </xsl:if>

            <xsl:if test="@parameterMap">
                <xsl:attribute name="parameterMap"><xsl:value-of select="@parameterMap" /></xsl:attribute>
            </xsl:if>

            <xsl:attribute name="statementType">CALLABLE</xsl:attribute>

            <xsl:apply-templates />
        </xsl:element>
    </xsl:template>

    <xsl:template match="delete">
        <xsl:element name="delete">
            <xsl:attribute name="id"><xsl:value-of select="@id" /></xsl:attribute>

            <xsl:if test="@parameterClass">
                <xsl:attribute name="parameterType"><xsl:value-of select="@parameterClass" /></xsl:attribute>
            </xsl:if>

            <xsl:if test="@parameterMap">
                <xsl:attribute name="parameterMap"><xsl:value-of select="@parameterMap" /></xsl:attribute>
            </xsl:if>

            <xsl:apply-templates />
        </xsl:element>
    </xsl:template>

    <xsl:template match="resultMap">
        <xsl:element name="resultMap">
            <xsl:attribute name="id"><xsl:value-of select="@id" /></xsl:attribute>

            <xsl:if test="@class">
                <xsl:attribute name="type"><xsl:value-of select="@class" /></xsl:attribute>
            </xsl:if>

            <xsl:if test="@extends">
                <xsl:attribute name="extends">
                    <xsl:value-of select="@extends" />
                </xsl:attribute>
            </xsl:if>

            <xsl:apply-templates />
        </xsl:element>
    </xsl:template>

    <xsl:template match="parameterMap">
        <xsl:element name="parameterMap">
            <xsl:attribute name="id"><xsl:value-of select="@id" /></xsl:attribute>

            <xsl:if test="@class">
                <xsl:attribute name="type"><xsl:value-of select="@class" /></xsl:attribute>
            </xsl:if>

          <xsl:apply-templates />
      </xsl:element>
    </xsl:template>

    <xsl:template match="parameterMap/parameter">
        <xsl:copy-of select="." />
    </xsl:template>

    <xsl:template match="resultMap/result">
        <xsl:choose>
            <xsl:when test="@resultMap">
                <xsl:element name="collection">
                    <xsl:attribute name="property"><xsl:value-of select="@property" /></xsl:attribute>
                    <xsl:attribute name="resultMap"><xsl:value-of select="@resultMap" /></xsl:attribute>
                </xsl:element>

                <xsl:apply-templates />
            </xsl:when>

            <xsl:when test="@select">
                <xsl:element name="association">
                    <xsl:attribute name="property"><xsl:value-of select="@property" /></xsl:attribute>
                    <xsl:attribute name="select"><xsl:value-of select="@select" /></xsl:attribute>
                    <xsl:if test="@column">
                        <xsl:attribute name="column"><xsl:value-of select="@column" /></xsl:attribute>
                    </xsl:if>
                    <xsl:if test="@javaType">
                        <xsl:attribute name="javaType"><xsl:value-of select="@javaType" /></xsl:attribute>
                    </xsl:if>
                </xsl:element>

                <xsl:apply-templates />
            </xsl:when>

            <xsl:otherwise>
                <xsl:element name="result">
                    <xsl:if test="@property">
                        <xsl:attribute name="property"><xsl:value-of select="@property" /></xsl:attribute>
                    </xsl:if>
                    <xsl:if test="@column">
                        <xsl:attribute name="column"><xsl:value-of select="@column" /></xsl:attribute>
                    </xsl:if>
                    <xsl:if test="@javaType">
                        <xsl:attribute name="javaType"><xsl:value-of select="@javaType" /></xsl:attribute>
                    </xsl:if>
                    <xsl:if test="@jdbcType">
                        <xsl:attribute name="jdbcType"><xsl:value-of select="@jdbcType" /></xsl:attribute>
                    </xsl:if>
                    <xsl:if test="@typeHandler">
                        <xsl:attribute name="typeHandler"><xsl:value-of select="@typeHandler" /></xsl:attribute>
                    </xsl:if>
                </xsl:element>

                <xsl:if test="@nullValue">
                    <xsl:comment>Sorry, I can't migrate nullValue <xsl:value-of select="@nullValue" /></xsl:comment>
                </xsl:if>
                <xsl:if test="@columnIndex">
                    <xsl:comment>Sorry, I can't migrate columnIndex <xsl:value-of select="@columnIndex" /></xsl:comment>
                </xsl:if>
                <xsl:if test="@notNullColumn">
                    <xsl:comment>Sorry, I can't migrate notNullColumn <xsl:value-of select="@notNullColumn" /></xsl:comment>
                </xsl:if>
                <xsl:if test="@columnIndex">
                    <xsl:comment>Sorry, I can't migrate columnIndex <xsl:value-of select="@columnIndex" /></xsl:comment>
                </xsl:if>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <xsl:template match="sql">
        <xsl:element name="sql">
            <xsl:attribute name="id"><xsl:value-of select="@id" /></xsl:attribute>

            <xsl:apply-templates />
        </xsl:element>
    </xsl:template>

    <xsl:template match="include">
        <xsl:element name="include">
            <xsl:attribute name="refid"><xsl:value-of select="@refid" /></xsl:attribute>

            <xsl:apply-templates />
        </xsl:element>
    </xsl:template>

    <xsl:template match="isNull">
        <xsl:element name="if">
            <xsl:attribute name="test">
            <xsl:if test="substring-before(@property, '.')">
                <xsl:value-of select="substring-before(@property, '.')" /><xsl:text> == null or </xsl:text>
            </xsl:if>
            <xsl:value-of select="@property" /><xsl:text> == null</xsl:text></xsl:attribute>
            <xsl:value-of select="@prepend" />

            <xsl:apply-templates />
        </xsl:element>
    </xsl:template>

    <xsl:template match="isNotNull">
        <xsl:element name="if">
            <xsl:attribute name="test">
            <xsl:if test="substring-before(@property, '.')">
                <xsl:value-of select="substring-before(@property, '.')" /><xsl:text> != null and </xsl:text>
            </xsl:if>
            <xsl:value-of select="@property" /><xsl:text> != null</xsl:text></xsl:attribute>
            <xsl:value-of select="@prepend" />

            <xsl:apply-templates />
        </xsl:element>
    </xsl:template>

    <xsl:template match="isGreaterThan">
        <xsl:element name="if">
            <xsl:attribute name="test">
                <xsl:value-of select="@property" />
                <xsl:text><![CDATA[ > ]]></xsl:text>
                <xsl:value-of select="@compareProperty" />
                <xsl:if test="@compareValue = 'Y' or @compareValue = 'N'"><xsl:text><![CDATA["]]></xsl:text></xsl:if>
                    <xsl:value-of select="@compareValue" />
                <xsl:if test="@compareValue = 'Y' or @compareValue = 'N'"><xsl:text><![CDATA["]]></xsl:text></xsl:if>
            </xsl:attribute>

            <xsl:value-of select="@prepend" />

            <xsl:apply-templates />
        </xsl:element>
    </xsl:template>

    <xsl:template match="isLessThan">
        <xsl:element name="if">
            <xsl:attribute name="test">
                <xsl:value-of select="@property" />
                <xsl:text><![CDATA[ < ]]></xsl:text>
                <xsl:value-of select="@compareProperty" />
                <xsl:if test="@compareValue = 'Y' or @compareValue = 'N'"><xsl:text><![CDATA["]]></xsl:text></xsl:if>
                    <xsl:value-of select="@compareValue" />
                <xsl:if test="@compareValue = 'Y' or @compareValue = 'N'"><xsl:text><![CDATA["]]></xsl:text></xsl:if>
            </xsl:attribute>

            <xsl:value-of select="@prepend" />

            <xsl:apply-templates />
        </xsl:element>
    </xsl:template>

    <xsl:template match="isEqual">
        <xsl:element name="if">
            <xsl:attribute name="test">
                <xsl:if test="substring-before(@property, '.')">
                    <xsl:value-of select="substring-before(@property, '.')" /><xsl:text> != null and </xsl:text>
                </xsl:if>
                <xsl:value-of select="@property" />
                <xsl:text><![CDATA[ == ]]></xsl:text>
                <xsl:value-of select="@compareProperty" />
                <xsl:if test="@compareValue = 'Y' or @compareValue = 'N'"><xsl:text><![CDATA["]]></xsl:text></xsl:if>
                    <xsl:value-of select="@compareValue" />
                <xsl:if test="@compareValue = 'Y' or @compareValue = 'N'"><xsl:text><![CDATA["]]></xsl:text></xsl:if>
            </xsl:attribute>

            <xsl:value-of select="@prepend" />

            <xsl:apply-templates />
        </xsl:element>
    </xsl:template>

    <xsl:template match="isLessEqual">
        <xsl:element name="if">
            <xsl:attribute name="test">
                <xsl:value-of select="@property" />
                <xsl:text><![CDATA[ <= ]]></xsl:text>
                <xsl:value-of select="@compareProperty" />
                <xsl:if test="@compareValue = 'Y' or @compareValue = 'N'"><xsl:text><![CDATA["]]></xsl:text></xsl:if>
                    <xsl:value-of select="@compareValue" />
                <xsl:if test="@compareValue = 'Y' or @compareValue = 'N'"><xsl:text><![CDATA["]]></xsl:text></xsl:if>
            </xsl:attribute>

            <xsl:value-of select="@prepend" />

            <xsl:apply-templates />
        </xsl:element>
    </xsl:template>

    <xsl:template match="iterate">
        <xsl:value-of select="@prepend" /><xsl:text> </xsl:text>

        <xsl:element name="foreach">
            <xsl:attribute name="collection"><xsl:value-of select="@property" /></xsl:attribute>
            <xsl:attribute name="item">item</xsl:attribute>
            <xsl:if test="@conjunction">
                <xsl:attribute name="separator"><xsl:value-of select="@conjunction" /></xsl:attribute>
            </xsl:if>
            <xsl:if test="@close">
                <xsl:attribute name="close"><xsl:value-of select="@close" /></xsl:attribute>
            </xsl:if>
            <xsl:if test="@open">
                <xsl:attribute name="open"><xsl:value-of select="@open" /></xsl:attribute>
            </xsl:if>

            <xsl:apply-templates />
        </xsl:element>
    </xsl:template>

    <xsl:template match="selectKey">
        <xsl:element name="selectKey">
            <xsl:attribute name="keyProperty"><xsl:value-of select="@keyProperty" /></xsl:attribute>
            <xsl:if test="@resultClass">
                <xsl:attribute name="resultType"><xsl:value-of select="@resultClass" /></xsl:attribute>
            </xsl:if>
            <xsl:if test="@type = 'pre'">
                <xsl:attribute name="order">BEFORE</xsl:attribute>
            </xsl:if>
            <xsl:if test="@type = 'post'">
                <xsl:attribute name="order">AFTER</xsl:attribute>
            </xsl:if>

            <xsl:apply-templates />
        </xsl:element>
    </xsl:template>

    <xsl:template match="typeAlias|statement|cacheModel|dynamic|isPropertyAvailable|isNotPropertyAvailable|isNotParameterPresent|isParameterPresent">
        <xsl:comment>
            Sorry, I can't migrate <xsl:value-of select="@*" />
            See console output for further details 
        </xsl:comment>
        <xsl:message>
            Sorry, I can't migrate: <xsl:copy-of select="." />
        </xsl:message> 
    </xsl:template>

    <xsl:for-each select="*/text()">
        <xsl:variable name="currentText" select="." />
        <xsl:value-of select="regexp:replace($currentText)" />
    </xsl:for-each>

</xsl:stylesheet>
