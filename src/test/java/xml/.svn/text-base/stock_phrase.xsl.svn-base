<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <xsl:output method="xml" version="1.0" encoding="utf-8" indent="yes" cdata-section-elements="DataContent" />
  <xsl:template match="/">
    <Config>
      <xsl:apply-templates />
    </Config>
  </xsl:template>
  <xsl:template match="Config">
    <!-- -->
    <xsl:for-each select="SyntacticPattern">
      <SyntacticPattern>
        <xsl:attribute name="id">
          <xsl:value-of select="@id" />
        </xsl:attribute>
        <!-- -->
        <SemanticBind>
            <Description>
              <xsl:value-of select="Description" />
            </Description>
            <ChineseRepresentation>#1</ChineseRepresentation>
          <SemanticBindTo sequence="1">
            <xsl:attribute name="id">
              <xsl:value-of select="./SemanticBind/@BindTo" />
            </xsl:attribute>

            <xsl:for-each select="SyntacticElement">
              <xsl:if test="@type=&#39;argument&#39;">
                <SemanticArgument source="element">
                  <xsl:attribute name="id">
                    <xsl:value-of select="@SemanticBind" />
                  </xsl:attribute>
                  <xsl:attribute name="SyntacticElement">
                    <xsl:value-of select="@sequence" />
                  </xsl:attribute>
                </SemanticArgument>
              </xsl:if>
            </xsl:for-each>
          </SemanticBindTo>
        </SemanticBind>
        <!-- -->
        <xsl:for-each select="SyntacticElement">
          <SyntacticElement>
            <xsl:if test="@sequence!=&#39;&#39;">
              <xsl:attribute name="sequence">
                <xsl:value-of select="@sequence" />
              </xsl:attribute>
            </xsl:if>
            <xsl:if test="@type!=&#39;&#39;">
              <xsl:attribute name="type">
                <xsl:value-of select="@type" />
              </xsl:attribute>
            </xsl:if>
            <xsl:if test="@KeywordGroup!=&#39;&#39;">
              <xsl:attribute name="KeywordGroup">
                <xsl:value-of select="@KeywordGroup" />
              </xsl:attribute>
            </xsl:if>
			<xsl:if test="@Keyword!=&#39;&#39;">
              <xsl:attribute name="Keyword">
                <xsl:value-of select="@Keyword" />
              </xsl:attribute>
            </xsl:if>
            <xsl:if test="@CanAbsent!=&#39;&#39;">
              <xsl:attribute name="CanAbsent">
                <xsl:value-of select="@CanAbsent" />
              </xsl:attribute>
            </xsl:if>
            <xsl:if test="@ValueType!=&#39;&#39;">
              <xsl:attribute name="ValueType">
                <xsl:value-of select="@ValueType" />
              </xsl:attribute>
            </xsl:if>
            <xsl:if test="@SpecificIndex!=&#39;&#39;">
              <xsl:attribute name="SpecificIndex">
                <xsl:value-of select="@SpecificIndex" />
              </xsl:attribute>
            </xsl:if>
            <xsl:if test="@SpecificIndexGroup!=&#39;&#39;">
              <xsl:attribute name="SpecificIndexGroup">
                <xsl:value-of select="@SpecificIndexGroup" />
              </xsl:attribute>
            </xsl:if>
            <xsl:if test="@DefaultIndex!=&#39;&#39;">
              <xsl:attribute name="DefaultIndex">
                <xsl:value-of select="@DefaultIndex" />
              </xsl:attribute>
            </xsl:if>
            <xsl:if test="@DefaultValue!=&#39;&#39;">
              <xsl:attribute name="DefaultValue">
                <xsl:value-of select="@DefaultValue" />
              </xsl:attribute>
            </xsl:if>
          </SyntacticElement>
        </xsl:for-each>
        <!-- -->
        <!-- -->
        <Description>
          <xsl:value-of select="Description" />
        </Description>
      </SyntacticPattern>
    </xsl:for-each>
    <!-- -->
    <!-- -->
  </xsl:template>
</xsl:stylesheet>
