<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <xsl:output method="xml" version="1.0" encoding="utf-8" indent="yes" cdata-section-elements="DataContent" />
  <xsl:template match="/">
    <beans>
      <xsl:apply-templates />
    </beans>
  </xsl:template>
  <xsl:template match="Config">
    <!-- -->
    <xsl:for-each select="SemanticPattern">
      <xsl:for-each select="Argument">
        <bean class="com.myhexin.qparser.phrase.Argument">
          <xsl:attribute name="id">
            <xsl:value-of select="concat(&#39;Argument&#39;,../@id,&#39;_&#39;,@id)" />
          </xsl:attribute>
          <property class="java.lang.Integer" name="id_">
            <xsl:value-of select="@id" />
          </property>
          <property class="com.myhexin.qparser.phrase.Argument$Type" name="type_">
            <xsl:value-of select="@type" />
          </property>
          <property class="java.lang.String" name="specificIndex_">
            <xsl:value-of select="@SpecificIndex" />
          </property>
          <property class="java.lang.Integer" name="specificIndexGroup_">
            <xsl:value-of select="@SpecificIndexGroup" />
          </property>
          <property class="java.lang.String" name="superClass_">
            <xsl:value-of select="@SuperClass" />
          </property>
          <property class="java.lang.Integer" name="listElementMinCount_">
            <xsl:value-of select="@ListElementMinCount" />
          </property>
          <property class="com.myhexin.qparser.phrase.Argument$ValueType" name="valueType_">
            <xsl:value-of select="@ValueType" />
          </property>
          <property class="java.lang.String" name="defaultIndex_">
            <xsl:value-of select="@DefaultIndex" />
          </property>
          <property class="java.lang.String" name="defaultValue_">
            <xsl:value-of select="@DefaultValue" />
          </property>
        </bean>
      </xsl:for-each>
      <!-- -->
      <container class="java.util.ArrayList">
        <xsl:attribute name="id">
          <xsl:value-of select="concat(&#39;list&#39;,@id)" />
        </xsl:attribute>
        <xsl:for-each select="Argument">
          <value keyRefType="bean">
            <xsl:attribute name="keyRef">
              <xsl:value-of select="concat(&#39;Argument&#39;,../@id,&#39;_&#39;,@id)" />
            </xsl:attribute>
          </value>
        </xsl:for-each>
      </container>
      <!-- -->
      <bean class="com.myhexin.qparser.phrase.Arguments">
        <xsl:attribute name="id">
          <xsl:value-of select="concat(&#39;Arguments&#39;,@id)" />
        </xsl:attribute>
        <property name="arguments_" refType="container">
          <xsl:attribute name="ref">
            <xsl:value-of select="concat(&#39;list&#39;,@id)" />
          </xsl:attribute>
        </property>
      </bean>
      <!-- -->
      <!-- -->
      <!-- -->
      <!-- -->
      <bean class="com.myhexin.qparser.phrase.ArgumentDependency">
        <xsl:attribute name="id">
          <xsl:value-of select="concat(&#39;ArgumentDependency&#39;,@id)" />
        </xsl:attribute>
      </bean>
      <!-- -->
      <bean class="com.myhexin.qparser.phrase.SemanticPattern">
        <xsl:attribute name="id">
          <xsl:value-of select="concat(&#39;SemanticPattern&#39;,@id)" />
        </xsl:attribute>
        <property class="java.lang.String" name="id_">
          <xsl:value-of select="@id" />
        </property>
        <property class="java.lang.String" name="uiRepresentation_">
          <xsl:value-of select="UIRepresentation" />
        </property>
        <property class="java.lang.String" name="chineseRepresentation_">
          <xsl:value-of select="ChineseRepresentation" />
        </property>
        <property class="java.lang.String" name="description_">
          <xsl:value-of select="Description" />
        </property>
        <property name="semanticArguments_" ref="Arguments" refType="bean">
          <xsl:attribute name="ref">
            <xsl:value-of select="concat(&#39;Arguments&#39;,@id)" />
          </xsl:attribute>
        </property>
        <property name="semanticArgumentsDependency_" ref="ArgumentDependency" refType="bean">
          <xsl:attribute name="ref">
            <xsl:value-of select="concat(&#39;ArgumentDependency&#39;,@id)" />
          </xsl:attribute>
        </property>
      </bean>
      <!-- -->
      <container class="java.util.HashMap">
        <xsl:attribute name="id">
          <xsl:value-of select="concat(&#39;map&#39;,@id)" />
        </xsl:attribute>
        <value keyClass="java.lang.String" valueRefType="bean">
          <xsl:attribute name="keyValue">
            <xsl:value-of select="@id" />
          </xsl:attribute>
          <xsl:attribute name="valueRef">
            <xsl:value-of select="concat(&#39;SemanticPattern&#39;,@id)" />
          </xsl:attribute>
        </value>
      </container>
      <!-- -->
      <bean class="com.myhexin.qparser.phrase.KeywordGroupMap">
        <xsl:attribute name="id">
          <xsl:value-of select="concat(&#39;semanticPatternMap_&#39;,@id)" />
        </xsl:attribute>
        <property name="keywordGroupMap_" refType="container">
          <xsl:attribute name="ref">
            <xsl:value-of select="concat(&#39;map&#39;,@id)" />
          </xsl:attribute>
        </property>
      </bean>
      <!-- -->
    </xsl:for-each>
  </xsl:template>
</xsl:stylesheet>
