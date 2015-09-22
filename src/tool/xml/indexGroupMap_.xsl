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
    <xsl:for-each select="IndexGroup">
      <container class="java.util.ArrayList">
        <xsl:attribute name="id">
          <xsl:value-of select="concat(&#39;list&#39;,@id)" />
        </xsl:attribute>
        <xsl:for-each select="Index">
          <value keyClass="java.lang.String">
            <xsl:attribute name="keyValue">
              <xsl:value-of select="." />
            </xsl:attribute>
          </value>
        </xsl:for-each>
      </container>
      <!-- -->
      <bean class="com.myhexin.qparser.phrase.IndexGroup">
        <xsl:attribute name="id">
          <xsl:value-of select="concat(&#39;IndexGroup&#39;,@id)" />
        </xsl:attribute>
        <property name="_id" class="java.lang.String">
          <xsl:value-of select="@id" />
        </property>
        <property name="_description" class="java.lang.String">
          <xsl:value-of select="Description" />
        </property>
        <property name="_indexs" refType="container">
          <xsl:attribute name="ref">
            <xsl:value-of select="concat(&#39;list&#39;,@id)" />
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
            <xsl:value-of select="concat(&#39;IndexGroup&#39;,@id)" />
          </xsl:attribute>
        </value>
      </container>
      <!-- -->
      <bean class="com.myhexin.qparser.phrase.IndexGroupMap">
        <xsl:attribute name="id">
          <xsl:value-of select="concat(&#39;IndexGroupMap_&#39;,@id)" />
        </xsl:attribute>
        <property name="IndexGroupMap_" refType="container">
          <xsl:attribute name="ref">
            <xsl:value-of select="concat(&#39;map&#39;,@id)" />
          </xsl:attribute>
        </property>
      </bean>
    </xsl:for-each>
  </xsl:template>
</xsl:stylesheet>
