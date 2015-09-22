<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>QueryParser</title>
   <script language="javascript">
    function checkupfile()
    {
        var item = document.getElementsByName("upload_text")[0].value.trim();
        if (item == "")
        {
            alert("请选择上传文件");
            return false;
        }
        if (item != "stock_onto.xml" && item != "stock_phrase.xml")
        {
            alert("仅允许上传stock_phrase.xml和stock_onto.xml");
            return false;
        }
        var ff = document.getElementsByName("upload")[0];
        ff.method = "post";
        ff.encoding = "multipart/form-data";
        ff.submit();
        return true;
    }
    </script>
</head>

<body>
<table width="80%" align="center" style="border-style:solid; border:thin">
 <tr>
    <td><div align="center">
      <h2><strong>上传配置文件</strong></h2>
    </div></td>
  </tr>
  <tr>
    <td align="center">
      <form id="upload" name="upload" method="post" encoding="multipart/form-data">
              <div name="frmUpload">
                <input id="ehdel_upload_text" type="text" name="upload_text" style="display:none"/>
                <input type="file" onchange="ehdel_upload_text.value=this.value" class="ehdel_upload" size="50" name="fileinput"/>
                <input type="submit" id="filerupload" value="上传" onclick="return checkupfile();"/>
              </div>
      </form>   
    </td>
  </tr>
</table> 
</body>
</html>