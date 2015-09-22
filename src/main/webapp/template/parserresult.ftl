<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>QueryParser</title>
   <script language="javascript">
    function check()
    {
        var item = document.getElementsByName("question")[0];
        if (item.value == "")
        {
            alert("问句不能为空");
        }
    }
    function viewPhaseData()
    {
        window.open("/phraseview/?all=1", "_blank");
    }
    function viewRelatedData()
    {
        var item = document.getElementsByName("question")[0];
        if (item.value == "")
        {
            alert("问句为空，无相关配置");
            return;
        }
        window.open("/phraseview/?all=0&query="+item.value, "_blank");
    }
    function reload()
    {
        var item = document.getElementsByName("question")[0];
        window.open("/parser/?reload=1&q="+item.value, "_self");
    }
    function upload()
    {
        window.open("/upload", "_blank");
    }
    function test()
    {
        window.open("/test", "_blank");
    }
    </script>
</head>

<body>
<table width="80%" align="center" style="border-style:solid; border:thin">
 <tr>
    <td><div align="center">
      <h2><strong>QueryParser</strong></h2>
    </div></td>
  </tr>
  <tr>
    <td align="center">
      <form id="search" name="search" method="get" action="/parserresult">
            <label>请输入问句：</label>
              <input type="text" value="${question}" name='question' size="90"/>
              <select name="qType" id="qType">   
		        <option value="stock" <#if qType?? && qType=="stock">selected</#if>>stock</option>   
		        <option value="hkstock" <#if qType?? && qType=="hkstock">selected</#if>>hkstock</option>   
		        <option value="fund" <#if qType?? && qType=="fund">selected</#if>>fund</option>   
		        <option value="hghy" <#if qType?? && qType=="hghy">selected</#if>>hghy</option>
		        <option value="search" <#if qType?? && qType=="search">selected</#if>>search</option>
		        <option value="all" <#if qType?? && qType=="all">selected</#if>>all</option>   
		      </select> 
            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
            <input type="submit" name="submit" onclick="return check();"/>
      </form>
    </td>
  </tr>
  <tr>
    <td><div align="center" style="color:blue;">
      <h3><strong></strong></h3>
    </div></td>
  </tr>
</table>
<table width="80%" align="center" style="border-style:solid; border:thin">
  <tr>
    <td><div align="center" style="color:blue;">
      <h3><strong></strong></h3>
    </div></td>
  </tr>
  <tr>
    <td align="center" width="100%">

          <table width="100%" border="1" cellpadding='5'>

              <#list representations as resp>
              	  <tr>
                      <td width="70%">
                          	解析结果: <a title="点击执行选股" href="http://x.10jqka.com.cn/stockpick/search?preParams=&ts=1&f=1&qs=1&querytype=&tid=stockpick&queryarea=all&w=${outputs[resp_index]!}" target="_blank">${outputs[resp_index]!}</a><br>
                      </td>
                  </tr>
              	  <tr><td width="70%">自评分数: ${scores[resp_index]!}<br></td></tr>
              	  <tr><td width="70%">句式语义: ${syntSmeanIdsList[resp_index]!}<br></td></tr>
                  <tr><td width="70%">${jsonresults[resp_index]!}<br></td></tr>
              </#list>
              ${logresult!}
          </table>
    </td>
  </tr>
</table>
</body>
</html>