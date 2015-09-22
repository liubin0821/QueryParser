"indexConds": [
	<#assign is_first_index = true>
	<#list args as index>
		<#if index.isIndexNode()>
			<#if is_first_index == true>
			<#assign is_first_index = false>
			<#elseif is_first_index == false>
				,
			</#if>
			<#include "/index.ftl">
		</#if>
	</#list>
]