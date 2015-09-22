<#if prop.value??>
{
	"propText":"${prop.text}",
	<#if prop.isDateProp()>
	<#assign date=prop.value>
	"value":<#include "/date.ftl">
	<#elseif prop.isNumProp()>
	<#assign num=prop.value>
	"value":<#include "/num.ftl">
	<#elseif prop.isStrProp()>
	<#assign str_val=prop.value>
	"value":<#include "/str_val.ftl">
	<#elseif prop.isIndexProp()>
	<#assign index=prop.value>
	"value":<#include "/class.ftl">
	</#if>
}
</#if>
