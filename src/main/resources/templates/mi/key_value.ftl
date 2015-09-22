<#if args[1].isDateNode()>
	<#assign date=args[1]>
	{
		"cmpOper": "=",
		"cmpVal": "${date.getFrom()}-${date.getTo()}",
		"operType": "filt",
		"innerName": "filt[${(args[0].getIndex().getUniqId())!}][=][${date.getFrom()}-${date.getTo()}]"
	}
<#elseif args[1].isNumNode()>
	<#assign num=args[1]>
	{
		"cmpOper": "=",
		"cmpVal": "${num.getTo()}",
		"operType": "filt",
		"innerName": "filt[${(args[0].getIndex().getUniqId())!}][=][${num.getTo()}]"
	}
<#elseif args[1].isStrNode()>
	<#assign str_val=args[1]>
	{
		"cmpOper": "contain",
		"cmpVal": "${str_val.getText()}",
		"operType": "filt",
		"innerName": "filt[${(args[0].getIndex().getUniqId())!}][contain][${str_val.getText()}]"
	}
</#if>