<#if prop.value??>
	<#if prop.isDateProp()>
	<#assign date=prop.value>
	<#include "/date.ftl">
	<#elseif prop.isNumProp()>
	<#assign num=prop.value>
	<#include "/num.ftl">
	<#elseif prop.isStrProp()>
	<#assign str_val=prop.value>
	<#include "/str_val.ftl">
	<#elseif prop.isIndexProp()>
	<#assign class=prop.value>
	<#include "/class.ftl">
	</#if>
</#if>
