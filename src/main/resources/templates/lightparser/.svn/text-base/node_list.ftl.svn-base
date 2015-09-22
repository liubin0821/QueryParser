<@compress single_line=true>
[
<#assign is_first = true>
<#list node_list as node>
	<#if is_first == true>
		<#assign is_first = false>
	<#elseif is_first == false>
		,
	</#if>
	<#-- {"type":"ASTOCK", "id": "300033",  "text":"同花顺"} -->
	{
	<#if node.isStrNode() && node.hasNoAmbiguitySubType("_股票代码")>
	"type":"ASTOCK", <#if node.getId("_股票代码")?? >"id": "${node.getId("_股票代码")}",</#if>
	<#elseif node.isStrNode() && node.hasNoAmbiguitySubType("_a股")>
	"type":"ASTOCK", <#if node.getId("_a股")?? >"id": "${node.getId("_a股")}",</#if>
	<#elseif node.isStrNode() && node.hasNoAmbiguitySubType("_全部基金代码")>
    "type":"FUND", <#if node.getId("_全部基金代码")?? >"id": "${node.getId("_全部基金代码")}",</#if>
    <#elseif node.isStrNode() && node.hasNoAmbiguitySubType("_基金代码")>
    "type":"FUND", <#if node.getId("_基金代码")?? >"id": "${node.getId("_基金代码")}",</#if>
    <#elseif node.isStrNode() && node.hasNoAmbiguitySubType("_全部基金(含未成立、已到期)代码")>
    "type":"FUND", <#if node.getId("_全部基金(含未成立、已到期)代码")?? >"id": "${node.getId("_全部基金(含未成立、已到期)代码")}",</#if>
    <#elseif node.isStrNode() && node.hasNoAmbiguitySubType("_全部基金(含未成立、已到期)简称")>
    "type":"FUND", <#if node.getId("_全部基金(含未成立、已到期)简称")?? >"id": "${node.getId("_全部基金(含未成立、已到期)简称")}",</#if>
    <#elseif node.isStrNode() && node.hasNoAmbiguitySubType("_港股代码")>
    "type":"HKSTOCK", <#if node.getId("_港股代码")?? >"id": "${node.getId("_港股代码")}",</#if>
    <#elseif node.isStrNode() && node.hasNoAmbiguitySubType("_港股")>
    "type":"HKSTOCK", <#if node.getId("_港股")?? >"id": "${node.getId("_港股")}",</#if>
    <#elseif node.isStrNode() && node.hasNoAmbiguitySubType("_港股简称")>
    "type":"HKSTOCK", <#if node.getId("_港股简称")?? >"id": "${node.getId("_港股简称")}",</#if>
    <#elseif node.isStrNode() && node.hasNoAmbiguitySubType("_港股全称")>
    "type":"HKSTOCK", <#if node.getId("_港股全称")?? >"id": "${node.getId("_港股全称")}",</#if>
	<#elseif node.isStrNode() && node.hasSubType("_股票简称")>
	<#if is_organization?? && is_organization==true>"type":"ORGANIZATION", <#else>"type":"ASTOCK", </#if><#if node.getId("_股票简称")?? >"id": "${node.getId("_股票简称")}",</#if>
	<#elseif node.isStrNode() && node.hasSubType("_股票代码")>
	"type":"ASTOCK", <#if node.getId("_股票代码")?? >"id": "${node.getId("_股票代码")}",</#if>
    <#elseif node.isStrNode() && node.hasSubType("_a股")>
    "type":"ASTOCK", <#if node.getId("_a股")?? >"id": "${node.getId("_a股")}",</#if>
	<#elseif node.isStrNode() && node.hasSubType("_基金简称")>
	<#if is_organization?? && is_organization==true>"type":"ORGANIZATION", <#else>"type":"FUND", </#if><#if node.getId("_股票简称")?? >"id": "${node.getId("_股票简称")}",</#if>
	<#elseif node.isStrNode() && node.hasSubType("_基金代码")>
	"type":"FUND", <#if node.getId("_基金代码")?? >"id": "${node.getId("_基金代码")}",</#if>	
	<#elseif node.isStrNode() && node.hasSubType("_全部基金代码")>
    "type":"FUND", <#if node.getId("_全部基金代码")?? >"id": "${node.getId("_全部基金代码")}",</#if> 
    <#elseif node.isStrNode() && node.hasSubType("_全部基金(含未成立、已到期)代码")>
    "type":"FUND", <#if node.getId("_全部基金(含未成立、已到期)代码")?? >"id": "${node.getId("_全部基金(含未成立、已到期)代码")}",</#if>
    <#elseif node.isStrNode() && node.hasSubType("_全部基金(含未成立、已到期)简称")>
    "type":"FUND", <#if node.getId("_全部基金(含未成立、已到期)简称")?? >"id": "${node.getId("_全部基金(含未成立、已到期)简称")}",</#if>   
	<#elseif node.isStrNode() && (node.hasSubType("_b股") || node.hasSubType("BSTOCK"))>
	"type":"BSTOCK", <#if node.getId("_b股")?? >"id": "${node.getId("_b股")}",</#if>
	<#elseif node.isStrNode() && (node.hasSubType("_新三板股") || node.hasSubType("NEWTB"))>
	"type":"NEWTB", <#if node.getId("_新三板股")?? >"id": "${node.getId("_新三板股")}",</#if>
	<#elseif node.isStrNode() && (node.hasSubType("_港股") || node.hasSubType("HKSTOCK"))>
	"type":"HKSTOCK", <#if node.getId("_港股")?? >"id": "${node.getId("_港股")}",</#if>
	<#elseif node.isStrNode() && (node.hasSubType("_港股代码") || node.hasSubType("HKSTOCK"))>
    "type":"HKSTOCK", <#if node.getId("_港股代码")?? >"id": "${node.getId("_港股代码")}",</#if>
    <#elseif node.isStrNode() && (node.hasSubType("_港股简称") || node.hasSubType("HKSTOCK"))>
    "type":"HKSTOCK", <#if node.getId("_港股简称")?? >"id": "${node.getId("_港股简称")}",</#if>
    <#elseif node.isStrNode() && (node.hasSubType("_港股全称") || node.hasSubType("HKSTOCK"))>
    "type":"HKSTOCK", <#if node.getId("_港股全称")?? >"id": "${node.getId("_港股全称")}",</#if>
	<#elseif node.isStrNode() && (node.hasSubType("_美股") || node.hasSubType("USSTOCK"))>
	"type":"USSTOCK", <#if node.getId("_美股")?? >"id": "${node.getId("_美股")}",</#if>
	<#elseif node.isStrNode() && node.hasSubType("_私募")>
	"type":"PRIVATE_EQUITY_COM", <#if node.getId("_私募")?? >"id": "${node.getId("_私募")}",</#if>
	<#elseif node.isStrNode() && node.hasSubType("_基金")>
	"type":"PUBLIC_FUND_COM", <#if node.getId("_基金")?? >"id": "${node.getId("_基金")}",</#if>
	<#elseif node.isStrNode() && node.hasSubType("_保险")>
	"type":"INSURANCE", <#if node.getId("_保险")?? >"id": "${node.getId("_保险")}",</#if>
	<#elseif node.isStrNode() && node.hasSubType("_银行")>
	"type":"BANK", <#if node.getId("_银行")?? >"id": "${node.getId("_银行")}",</#if>
	<#elseif node.isStrNode() && node.hasSubType("_交易所")>
	"type":"EXCHANGE", <#if node.getId("_交易所")?? >"id": "${node.getId("_交易所")}",</#if>
	<#elseif node.isStrNode() && node.hasSubType("_券商")>
	"type":"BROKER", <#if node.getId("_券商")?? >"id": "${node.getId("_券商")}",</#if>
	<#elseif node.isStrNode() && node.hasSubType("_信托")>
	"type":"TRUST_COM", <#if node.getId("_信托")?? >"id": "${node.getId("_信托")}",</#if>
    <#elseif node.isStrNode() && node.hasSubType("_信托产品")>
	"type":"TRUST", <#if node.getId("_信托产品")?? >"id": "${node.getId("_信托产品")}",</#if>
    <#elseif node.isStrNode() && node.hasSubType("_理财产品")>
	"type":"LICAI", <#if node.getId("_理财产品")?? >"id": "${node.getId("_理财产品")}",</#if>
	<#elseif node.isStrNode() && node.hasSubType("_政府机构")>
	"type":"GOVERNMENT", <#if node.getId("_政府机构")?? >"id": "${node.getId("_政府机构")}",</#if>
	<#elseif is_website?? && is_website==true && node.isStrNode() && (node.hasSubType("_所属概念") || node.hasSubType("_泛概念"))>
	"type":"UNKNOWN",
	<#elseif node.isStrNode() && (node.hasSubType("_所属概念") || node.hasSubType("_泛概念"))>
	"type":"CONCEPT", <#if node.getId("_所属概念")?? >"id": "${node.getId("_所属概念")}",</#if>
	<#elseif node.isStrNode() && (node.hasSubType("_主营产品名称") || node.hasSubType("_泛产品"))>
	"type":"PRODUCT",
	<#elseif node.isStrNode() && (node.hasSubType("_所属申万行业"))>
	"type":"INDUSTRY",
    <#elseif node.isStrNode() && (node.hasSubType("_国家主席"))>
	"type":"ZHUXI",
	<#elseif node.isStrNode() && ((node.hasSubType("_国家") || node.hasSubType("_国家和地区")))>
	"type":"GUOJIA",
	<#elseif node.isStrNode() && (node.hasSubType("_姓名")) && (node.hasSubType("_姓") == false)>
	"type":"PEOPLE",
	<#elseif node.isStrNode() && (node.hasSubType("_姓"))>
    "type":"SURNAME",
    <#elseif node.isStrNode() && (node.hasSubType("_疑问词"))>
	"type":"UNKNOWN",
	<#elseif node.isDateNode()>
	"type":"DATE",
	<#elseif node.isNumNode()>
	"type":"NUM",
	<#elseif node.isIndexNode()>
	"type":"INDEX",
	<#elseif node.isUnknownNode()>
	"type":"UNKNOWN",
	<#else>
	"type":"TEXT",
	</#if>
    <#if node.isStrNode()>
	"str_sub_type":"${node.getDefaultIndexSubtype()}",
    "info":"${node.getInfo()}",
    "prop":"${node.getProps()}",
	</#if>
	<#if node.isNumNode()>
	"text":"${node.oldStr_}"
	<#else>
	"text":"${node.text}"
	</#if>
	<#if node.isDateNode() && node.hasStartAndEndFromDataInfo()>
	,"start":"${node.getDateinfo().getFrom()}",
	"end":"${node.getDateinfo().getTo()}"
	</#if>
	,"weight":${node.getNodeTypeWeight()}
	<#if node.hasEvents()>
	,"events":"${node.getEventsSetString()}"
	</#if>
	}
</#list>
]
</@compress>