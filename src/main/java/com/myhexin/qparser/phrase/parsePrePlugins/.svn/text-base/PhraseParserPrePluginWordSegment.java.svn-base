package com.myhexin.qparser.phrase.parsePrePlugins;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.myhexin.qparser.Param;

import com.myhexin.qparser.define.EnumDef.UrlReqType;
import com.myhexin.qparser.node.Environment;
import com.myhexin.qparser.phrase.ParserAnnotation;
import com.myhexin.qparser.phrase.util.Consts;
import com.myhexin.qparser.util.RequestItem;
import com.myhexin.qparser.util.ResponseItem;
import com.myhexin.qparser.util.URLReader;

public class PhraseParserPrePluginWordSegment extends PhraseParserPrePluginAbstract {
	public static org.slf4j.Logger logger_ = org.slf4j.LoggerFactory.getLogger(PhraseParserPrePluginWordSegment.class.getName());
	public final static String TITLE = "Word_Segment";
	
    public PhraseParserPrePluginWordSegment() {
        super(TITLE);
    }
    
    @Override
    public void process(ParserAnnotation annotation) {
    	String query = annotation.getQueryText();
    	if (query == null || query.trim().length() == 0)
    		return;
    	
    	Environment ENV = annotation.getEnv();
    	String ms_ltp = (String) ENV.get("ms_ltp", false); // 主搜索分词开关
    	
    	String segLine = getLtpRlt(query, ms_ltp); // 获得分词结果
        // 如果queryList为空，返回原问句
    	if (segLine != null && segLine.length() > 0) {
    		annotation.setSegmentedText(segLine);
    	}else{
    		annotation.setQueryText( null);
    		annotation.setStopProcessFlag(true);
    	}
    }
    
    /**
     * 单种可能性的分词
     * 返回分词结果
     */
    private String getLtpRlt(String text, String ms_ltp) {
      RequestItem reqItem = new RequestItem(UrlReqType.TKN_LTP);
      reqItem.setQueryStr(text);
      
      String seggerUrl = Param.ALL_SEGGER_URL;
      if ("1".equals(ms_ltp)) {
        seggerUrl = Param.MAINSEARCH_SEGGER_URL;
      }
      //System.out.println("seggerUrl:" + seggerUrl);
      URLReader urlReader = new URLReader(seggerUrl);
      ResponseItem rspItem = null;
      for (int i = 0; i < Param.MAX_CONNECT_TIMES; i++) {
        try {
          rspItem = urlReader.run(reqItem);
          if( rspItem==null || !rspItem.getRspOK() ){
            logger_.info(seggerUrl + text);
            logger_.info("try " + (i+1));
            logger_.info(rspItem.getRspLogsStr(Consts.STR_NEW_LINE));
          } else {
            break;
          }
        } catch (Exception e) {
          logger_.info(seggerUrl + text);
          logger_.info("try " + (i+1));
        }
        
      }
      if( rspItem==null || !rspItem.getRspOK() ){
        logger_.error("try " + Param.MAX_CONNECT_TIMES);
        logger_.error(seggerUrl + text);
        logger_.error(rspItem.getRspLogsStr(Consts.STR_NEW_LINE));
        if ("1".equals(ms_ltp)) {
          return getLtpRlt(text, "0");
        }
      }
      String rspStr = rspItem.getRspRltsStr(Consts.STR_NEW_LINE);
      return rspStr;
    }
    
    /**
     * 多种可能性的分词
     * 返回分词结果
     */
    public static ArrayList<String> getLtpRlts(String text) {
        ArrayList<String> res = new ArrayList<String>();

        BufferedReader reader = null;
        HttpURLConnection conn = null;
        try {
            String seggerUrl = Param.MULTI_SEGGER_URL;
            seggerUrl += java.net.URLEncoder.encode(text, "utf-8");
            conn = (HttpURLConnection) new URL(seggerUrl).openConnection();
            reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));
            
            String line = null;
            line = reader.readLine();
            while((line = reader.readLine()) != null) {
                res.add(line);
            }
        } catch (IOException e) {
            //发生此异常时，标准做法是调用 conn.getErrorStream() 并将其中的内容全部读出
            //以保证此连接的底层持久连接可被复用。但在JDK 6中，系统自动会读取至多512K字节
            //（见系统属性http.KeepAlive.remainingData），而我们的分词服务在出错时肯定不会
            //发送这么多，因此没必要去手动读完所有的response中的数据，直接在finally中关闭即可。
        } finally {
            if(reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                }
            }
        }
        return res;
    }
    
    @Override
    public String getLogResult(ParserAnnotation annotation ) {
    	String queryText = annotation.getQueryText();
    	String querySegText = annotation.getSegmentedText();
    	if(queryText!=null)
    		return String.format("[%s] 分词结果 : %s\n", queryText, querySegText);
    	else
    		return null;
    }
}

