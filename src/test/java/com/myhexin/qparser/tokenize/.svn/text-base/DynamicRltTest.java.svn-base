package com.myhexin.qparser.tokenize;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import com.myhexin.qparser.Param;
import com.myhexin.qparser.ApplicationContext.ApplicationContextHelper;
import com.myhexin.qparser.define.EnumDef.UrlReqType;
import com.myhexin.qparser.phrase.PhraseParser;
import com.myhexin.qparser.util.RequestItem;
import com.myhexin.qparser.util.ResponseItem;
import com.myhexin.qparser.util.URLReader;

public class DynamicRltTest {
	public static PhraseParser parser;// = new PhraseParser("./conf/qparser.conf", "./data");
	
	static {
		// 加载xml文件，初始spring容器
		ApplicationContextHelper.loadApplicationContext();
		
		// phraseParser配置于qphrase.xml文件
		parser = (PhraseParser) ApplicationContextHelper.getBean("phraseParser");
	}
	
	/** 检查返回的结果是否符合分词要求 */
	private static boolean checkRlt(String resp) {
		if (resp.length() <= 0)
			return false;
		String[] lines = resp.split("\n");
		for (String token : lines[0].split("\t")) {
			int pos = token.lastIndexOf('/');
			if (pos < 0) {
				System.out.println(String.format("动态词典格式错误[%s]", resp));
				return false;
			}
		}
		return true;
	}
	
	public static String addDynamicRlt(String ltpRlt) {
        String logStr = null;
        RequestItem reqItem = new RequestItem(UrlReqType.TKN_DYNAMIC_LTP);
        try {
            String postBodyStr = "q="
                    + URLEncoder.encode(String.format("%s", ltpRlt), "UTF-8");
            postBodyStr = postBodyStr + "&p=1&rp=1";
            if(Param.USE_DYNAMIC_LTP_MIXED){
                postBodyStr = postBodyStr + "&cs=1";
            }
            reqItem.setPostBodyStr(postBodyStr);
        } catch (UnsupportedEncodingException e) {
            return ltpRlt;
        }
        String seggerUrl = Param.DYNAMIC_SERV_URL;
        URLReader urlReader = new URLReader(seggerUrl);
        ResponseItem rspItem = urlReader.run(reqItem);
        
        if( !rspItem.getRspOK() ){
            logStr = rspItem.getRspLogsStr("\n");
            System.out.println(logStr);
            return ltpRlt;
        }
        String rspStr = rspItem.getRspRltsStr("\n");
        if(checkRlt(rspStr)){
            return rspStr;
        }else{
            return ltpRlt;
        }
    }
	
	public static void main(String[] args) {
		System.out.println(addDynamicRlt("朱子健/"));
	}
}
