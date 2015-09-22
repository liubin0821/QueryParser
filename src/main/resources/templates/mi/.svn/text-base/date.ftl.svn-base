{
	"paramText": "${prop.getText()}",
	"paramValue": 
    <#if date.isDateNode()>
    "${date.getFrom()}-${date.getTo()}"
    <elseif date.isTimeNode()>
    "${date.getText()}"
    <#else>
    </#if>
    ,
	"paramValType": "DATE"
}