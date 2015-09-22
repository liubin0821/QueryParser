{
    "operation": {
        "operator": "contain",
        "operands": [
            <#assign index=arg1>
			<#include "/class.ftl">
			,
			<#if arg2.isDateNode()>
			<#assign date=arg2>
			<#include "/date.ftl">
			<#elseif arg2.isNumNode()>
			<#assign num=arg2>
			<#include "/num.ftl">
			<#elseif arg2.isStrNode()>
			<#assign str_val=arg2>
			<#include "/str_val.ftl">
			</#if>
        ]
    }
}