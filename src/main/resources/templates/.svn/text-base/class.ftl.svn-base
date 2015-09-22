<#if index.isNestedSemanticNode()>
${(index.text)!}
<#elseif index.isFocusNode()>
{
	"index": {
		"text":"${(index.getIndex().text)!}",
		"props":[
			<#assign is_first = true>
			<#list index.getIndex().getAllProps() as prop>
				<#if prop.value??>                      
					<#if is_first == false>
						,
					</#if>
					<#include "/prop.ftl">
                    <#assign is_first = false>
				</#if>
			</#list>
		]
	}
}
<#else>
{
	"index": {
		"text":"${(index.text)!}",
		"props":[
			<#assign is_first = true>
			<#list index.getAllProps() as prop>
				<#if prop.value??>
					<#if is_first == true>
					<#assign is_first = false>
					<#elseif is_first == false>
						,
					</#if>
					<#include "/prop.ftl">
				</#if>
			</#list>
		]
	}
}
</#if>