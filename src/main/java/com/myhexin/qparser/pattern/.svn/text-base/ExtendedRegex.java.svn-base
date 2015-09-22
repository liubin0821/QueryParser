package com.myhexin.qparser.pattern;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.myhexin.qparser.Param;
import com.myhexin.qparser.ParseLog;
import com.myhexin.qparser.Query;
import com.myhexin.qparser.util.Pair;

public class ExtendedRegex {
    private static ArrayList<Pair<Pattern, String>> stockRegexInfo_ =
        new ArrayList<Pair<Pattern, String>>();
    private static ArrayList<Pair<Pattern, String>> fundRegexInfo_ =
        new ArrayList<Pair<Pattern, String>>();

    public static void setRegexInfo(ArrayList<Pair<Pattern, String>> info,
            Query.Type qtype) {
        if(qtype == Query.Type.STOCK) {
            stockRegexInfo_ = info;
        } else {
            fundRegexInfo_ = info;
        }
    }
    
    /**
     * 进行正则转换纠正。
     * 注意：这类转换，一般是十分通用的情况才用正则转换，否则正则太多会导致解析过慢。
     * @param query
     * @param qtype
     * @return
     */
    public static String transText(Query query, Query.Type qtype) {
        ArrayList<Pair<Pattern, String>> regexInfo = getPtn2Rpl(qtype);
        String newText = query.text;
        for (Pair<Pattern, String> ps : regexInfo) {
            Pattern pat = ps.first;
            String tmpText = newText;
            Matcher mid = pat.matcher(tmpText);
            boolean matched = false;
            
            //LING：改为循环替换正则
            int changeSize = 0;
            while(mid.find()) {
                String replaceStr = ps.second;
                matched = true;
                int start = mid.start();
                int end = mid.end();
                String orignalText = tmpText.substring(start, end);
                for (int count = 1, total = mid.groupCount(); count <= total; count++) {
                	String tagStr = String.format("group%d", count);
                    if (replaceStr.indexOf(tagStr) == -1
                            || mid.group(count) == null) {
                        continue;
                    }
                    String countStr = mid.group(count);
                    replaceStr = replaceStr.replace(tagStr, countStr);
                }
                
                if (replaceStr != null) {
                	//log regex
                	query.getLog().logTransWord(orignalText, replaceStr);
                    start = start + changeSize;
                    end = end + changeSize;
                    String startStr = start>=0 && start<newText.length() ? newText.substring(0,start):"";
                    String endStr = end>=0 && end<newText.length() ? newText.substring(end):"";
                    //形成新的问句
                    newText = startStr+replaceStr+endStr;
                    changeSize += (replaceStr.length() - (end - start));
                }                       
            }
            if(matched){
                if(Param.DEBUG){
                    query.getLog().logMsg(ParseLog.LOG_REGEX,
                            "\nREGEX:%s\nRESULT:%s", pat.toString(), newText);
                }
                //break;
            }
        }
        return newText;
    }
    
    private static ArrayList<Pair<Pattern, String>> getPtn2Rpl(Query.Type qtype){
        if(qtype == Query.Type.STOCK) {
            return stockRegexInfo_;
        } else {
            return fundRegexInfo_;
        }        
    }
    
}
