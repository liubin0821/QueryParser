<#if index.isIndexNode()>
{
	"indexID": "${(index.getIndex().getIndexID())!}",
	"indexText": "${(index.getIndex().getText())!}",
	"indexSrc": "${(index.getIndex().getDataSrc())!}",
	"isAdd": false,
	"params": [
		<#assign is_first_prop = true>
		<#list index.getIndex().getAllProps() as prop>
			<#if prop.value??>
				<#if is_first_prop == true>
				<#assign is_first_prop = false>
				<#elseif is_first_prop == false>
					,
				</#if>
				<#include "/prop.ftl">
			</#if>
		</#list>
	],
	"innerName": "${(index.getIndex().getUniqId())!}"
}
<#elseif index.isClassNode()>
{
	"indexID": "${(index.getIndexID())!}",
    "indexText": "${(index.getText())!}",
    "indexSrc": "${(index.getDataSrc())!}",
    "isAdd": false,
    "params": [
		<#assign is_first_prop = true>
		<#list index.getAllProps() as prop>
			<#if prop.value??>
				<#if is_first_prop == true>
				<#assign is_first_prop = false>
				<#elseif is_first_prop == false>
					,
				</#if>
				<#include "/prop.ftl">
			</#if>
		</#list>
	],
    "innerName": "${(index.getUniqId())!}"
}
</#if>