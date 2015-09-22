package com.myhexin.qparser.phrase.parsePrePlugins;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import com.myhexin.qparser.Param;

import com.myhexin.qparser.define.EnumDef.UrlReqType;
import com.myhexin.qparser.phrase.ParserAnnotation;
import com.myhexin.qparser.phrase.util.Consts;
import com.myhexin.qparser.util.RequestItem;
import com.myhexin.qparser.util.ResponseItem;
import com.myhexin.qparser.util.URLReader;

public class PhraseParserPrePluginWordSegmentDynamic extends PhraseParserPrePluginAbstract {
	private final static org.slf4j.Logger logger_ = org.slf4j.LoggerFactory.getLogger(PhraseParserPrePluginWordSegmentDynamic.class.getName());
	
    public PhraseParserPrePluginWordSegmentDynamic() {
        super("Word_Segment_Dynamic");
    }
    
    @Override
    public void processSingle(ParserAnnotation annotation) {
    	String query = annotation.getSegmentedText();
    	String result = addDynamicRlt(query); // 获得动态分词结果
        // 如果queryList为空，返回原问句
    	//.trim()滥用,在取得该result的函数中trim
    	if (result != null && result.length() > 0) {
    		annotation.setSegmentedText(result);
    	}
    }
    
    /*
     * 返回动态分词结果
     */
    /**
     * 获取动态词典的结果
     * @param ltpRlt
     * @return
     */
    public String addDynamicRlt(String ltpRlt) {
        RequestItem reqItem = new RequestItem(UrlReqType.TKN_DYNAMIC_LTP);
        //System.out.println(ltpRlt);
        try {
        	//reqItem.setPostBodyStr("q="+URLEncoder.encode(String.format("%s", ltpRlt), "UTF-8"));
            String postBodyStr = "q=" + URLEncoder.encode(String.format("%s", ltpRlt), Consts.CHARSET_UTF_8);
            postBodyStr = postBodyStr + "&rp=1";//"&p=1&rp=1"
            //postBodyStr = postBodyStr + "&cs=1";
            if(Param.USE_DYNAMIC_LTP_MIXED){
                postBodyStr = postBodyStr + "&cs=1";
            }
            reqItem.setPostBodyStr(postBodyStr);
        } catch (UnsupportedEncodingException e) {
            return "";
        }
        String seggerUrl = Param.DYNAMIC_SERV_URL;
        URLReader urlReader = new URLReader(seggerUrl);
        ResponseItem rspItem = null;
        for (int i = 0; i < Param.MAX_CONNECT_TIMES; i++) {
        	rspItem = urlReader.run(reqItem, Param.READ_CONTENT_TIME_OUT_DYNAMIC_RLT);
        	if( !rspItem.getRspOK() ){
        		logger_.info(seggerUrl + "&q=" + ltpRlt);
        		logger_.info("try " + (i+1));
                logger_.info(rspItem.getRspLogsStr("\n"));
        	} else {
        		break;
        	}
        }
        if( !rspItem.getRspOK() ){
        	logger_.error(seggerUrl + "&q=" + ltpRlt);
        	logger_.error("try " + Param.MAX_CONNECT_TIMES);
        	logger_.error(rspItem.getRspLogsStr(Consts.STR_NEW_LINE));
            return "";
        }
        String rspStr = rspItem.getRspRltsStr(Consts.STR_NEW_LINE);
        if(checkRlt(rspStr)){
            return rspStr;
        }else{
            return "";
        }
    }
    
    /** 检查返回的结果是否符合分词要求 */
	private boolean checkRlt(String resp) {
		if (resp.length() <= 0)
			return false;
		String[] lines = resp.split(Consts.STR_NEW_LINE);
		if(lines!=null && lines.length>0){
			String[] tokens = lines[0].split(Consts.STR_TAB);
			if(tokens!=null && tokens.length>0) {
				for (String token : tokens) {
					int pos = token.lastIndexOf(Consts.CHAR_SLASH);
					if (pos < 0) {
						logger_.error(String.format("动态词典格式错误[%s]", resp));
						return false;
					}
				}
			}
		}
		
		return true;
	}
	
	@Override
    public String getLogResult(ParserAnnotation annotation ) {
		String queryText = annotation.getQueryText();
    	String querySegText = annotation.getSegmentedText();
    	if(queryText!=null)
    		return String.format("[%s] 动态分词结果 : %s\n", queryText, querySegText);
    	else
    		return null;
    }
}

