package com.myhexin.qparser.similarity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.myhexin.qparser.Query;
import com.myhexin.qparser.except.DataConfException;


/**
 * 表示配置中的一行数据,放到kvs,kvas,kvds三个map中
 * 
 * 000029  深深房A ssfa    A   1   G深深房(20060217),深深房A(20061009),深圳经济特区房地产(集团)股份有限公司(00000000)
 * 
 */
class CodeInfo implements Comparable<CodeInfo>{
    public enum CodeInfoProp{
        code,//代码
        shortname,//简称
        fullname,//全称
        abmarket,//沪深股市
        state,//在市状态
        codeStrArray,//未去除0的code的分词结果
        deZeroCodeStrArray,//去除了0之后的code的分词结果
        score,//本code的分值
    }
    
    
    private Map<CodeInfoProp, String> kvs = new HashMap<CodeInfoProp, String>();
    
    private Map<CodeInfoProp, List<String>> kvas = new HashMap<CodeInfoProp, List<String>>();
    
    private Map<CodeInfoProp, Double> kvds = new HashMap<CodeInfoProp, Double>();
    
    public int compareTo(CodeInfo arg0) {
        Double myScore = getDoublePropValue(CodeInfoProp.score);
        Double compScore = arg0.getDoublePropValue(CodeInfoProp.score);
        if( myScore == null || compScore == null ){
            return 0;
        }
        if( myScore > compScore ){
            return -1;
        }else if( myScore < compScore ){
            return 1;
        }else{
            return 0;
        }
    }
    
    public void setStrPropValue( CodeInfoProp propName, String value ){
        kvs.put(propName, value);
    }
    
    public void setDoublePropValue( CodeInfoProp propName, Double value ){
        kvds.put(propName, value);
    }
    
    public String getStrPropValue( CodeInfoProp propName ){
        return kvs.get(propName);
    }
    
    public Double getDoublePropValue( CodeInfoProp propName ){
        return kvds.get(propName);
    }
    
    public List<String> getListPropValue( CodeInfoProp propName ){
        return kvas.get(propName);
    }
    
    public boolean fill( String str, Query.Type type ){
        //String logStr = null;
        if( str == null ){
            //logStr = "input str is null";
            return false; //throw new DataConfException(logStr);
        }
        if( type == null ){
            //logStr = "input type is null";
            return false; //throw new DataConfException(logStr);
        }
        try{
            fillBody(str, type);
            return true;
        }catch( DataConfException e ){
            //throw e;
        	return false;
        }catch( Exception e ){
            //logStr = Util.getExceptionTraceStr(e);
        	return false; ////throw new DataConfException(logStr);
        }
    }
    
    /**
     * 不同领域的code info文件每行的格式不同，所以除了要待解析的字符串，还需要领域type
     * @param str ：待解析的字符串
     * @param type ：领域类型
     * @throws DataConfException 
     */
    private boolean fillBody( String str, Query.Type type ) throws DataConfException{
        if( type == Query.Type.STOCK  ){
            return fillWhenStock(str);
        }else if( type == Query.Type.FUND ){
        	return fillWhenFund(str);
        }else{
        	return fillWhenDefault(str);
        }
    }
    
    /**
     * 000029  深深房A ssfa    A   1   G深深房(20060217),深深房A(20061009),深圳经济特区房地产(集团)股份有限公司(00000000)
     * @param str
     * @throws DataConfException
     */
    private boolean fillWhenStock( String str ) throws DataConfException{
        String[] array = str.split("\t");
        //String logStr = null;
        int clenLimit = 5;
        if( array.length < clenLimit ){
            //logStr = String.format( "%s colume is shorter than %s", str, clenLimit );
            return false;//throw new DataConfException(logStr);
        }
        kvs.put(CodeInfoProp.code, array[0]);
        String shortName = toLowerAndHalf(array[1]);
        kvs.put(CodeInfoProp.shortname, shortName);
        String abMarket = toLowerAndHalf(array[3]);
        kvs.put(CodeInfoProp.abmarket, abMarket);
        kvs.put(CodeInfoProp.state, array[4]);
        
        List<String> codeStrList = CodeInfo.createCodeArray(array[0]);
        kvas.put(CodeInfoProp.codeStrArray, codeStrList);
        List<String> deZeroStrList = CodeInfo.createDeZeroCodeArray(array[0]);
        kvas.put(CodeInfoProp.deZeroCodeStrArray, deZeroStrList);
        return true;
    }
    
    
    /**
     * 将大写全角的字符转成小写半角的相应字符。发生转换的字符列举如下：<br>
     * ！  ＂  ＃  ＄  ％  ＆  ＇  （  ）  ＊  ＋  ，  －  ．  ／  <br>
     *  ０  １  ２  ３  ４  ５  ６  ７  ８  ９  ：  ；  ＜  ＝  ＞  ？ <br>
     *  ＠  Ａ  Ｂ  Ｃ  Ｄ  Ｅ  Ｆ  Ｇ  Ｈ  Ｉ  Ｊ  Ｋ  Ｌ  Ｍ  Ｎ  Ｏ <br>
     *  Ｐ  Ｑ  Ｒ  Ｓ  Ｔ  Ｕ  Ｖ  Ｗ  Ｘ  Ｙ  Ｚ  ［  ＼  ］  ＾  ＿ <br>
     *  ｀  ａ  ｂ  ｃ  ｄ  ｅ  ｆ  ｇ  ｈ  ｉ  ｊ  ｋ  ｌ  ｍ  ｎ  ｏ <br>
     *  ｐ  ｑ  ｒ  ｓ  ｔ  ｕ  ｖ  ｗ  ｘ  ｙ  ｚ  ｛  ｜  ｝  ～ <br>
     */
    public static String toLowerAndHalf(String text) {
        StringBuilder sb = new StringBuilder(text.length());
        for(int i = 0; i < text.length(); i++) {
            char ch = text.charAt(i);
            if('！' <= ch && ch <= '～') { ch -= ('！'-'!'); }
            if('A' <= ch && ch <= 'Z') { ch += ('a' - 'A'); }
            else if('　' == ch) { ch = ' '; }
            sb.append(ch);
        }
        return sb.toString();
    }
    
    /**
     * 000001  华夏成长    hxczhh  2   华夏成长证券投资基金    000001.OF   华夏成长混合
     * @param str
     * @throws DataConfException
     */
    private boolean fillWhenFund( String str ) throws DataConfException{
        String[] array = str.split("\t");
        //String logStr = null;
        int clenLimit = 2;
        if( array.length < clenLimit ){
            //logStr = String.format( "%s colume is shorter than %s", str, clenLimit );
            return false; //throw new DataConfException(logStr);
        }
        
        kvs.put(CodeInfoProp.code, array[0]);
        String shortName = toLowerAndHalf(array[1]);
        kvs.put(CodeInfoProp.shortname, shortName);
        List<String> codeStrList = CodeInfo.createCodeArray(array[0]);
        kvas.put(CodeInfoProp.codeStrArray, codeStrList);
        List<String> deZeroStrList = CodeInfo.createDeZeroCodeArray(array[0]);
        kvas.put(CodeInfoProp.deZeroCodeStrArray, deZeroStrList);
        return true;
    }
    
    private boolean fillWhenDefault( String str ){
        return false;
    }
    
    /**
     * 根据传入的纯数字的字符串，字符的数组
     * @param numStr
     * @return
     */
    public static List<String> createCodeArray( String numStr ){
        String[] rawArray = numStr.split("");
        List<String> rlt = new ArrayList<String>();
        for( int i=0; i<rawArray.length; i++ ){
            if( rawArray[i] == null || rawArray[i].isEmpty() ){
                continue;
            }
//            if( "0".equals(rawArray[i]) ){
//                continue;
//            }
            rlt.add(rawArray[i]);
        }
        return rlt;
    }
    
    /**
     * 根据传入的纯数字的字符串，字符的数组
     * @param numStr
     * @return
     */
    public static List<String> createDeZeroCodeArray( String numStr ){
        String[] rawArray = numStr.split("");
        List<String> rlt = new ArrayList<String>();
        for( int i=0; i<rawArray.length; i++ ){
            if( rawArray[i] == null || rawArray[i].isEmpty() ){
                continue;
            }
            if( "0".equals(rawArray[i]) ){
                continue;
            }
            rlt.add(rawArray[i]);
        }
        return rlt;
    }
}
