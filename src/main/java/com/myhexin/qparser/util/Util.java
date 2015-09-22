package com.myhexin.qparser.util;

import java.io.*;
import java.text.*;
import java.util.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.codec.digest.DigestUtils;
import org.w3c.dom.Document;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.myhexin.qparser.except.DataConfException;

public class Util {
    /**
    Escape characters for text appearing as XML data, between tags.
    
    <P>The following characters are replaced with corresponding character entities :
    <table border='1' cellpadding='3' cellspacing='0'>
    <tr><th> Character </th><th> Encoding </th></tr>
    <tr><td> < </td><td> &amp;lt; </td></tr>
    <tr><td> > </td><td> &amp;gt; </td></tr>
    <tr><td> & </td><td> &amp;amp; </td></tr>
    <tr><td> " </td><td> &amp;quot;</td></tr>
    <tr><td> ' </td><td> &amp;#039;</td></tr>
    </table>
    
    <P>Note that JSTL's {@code <c:out>} escapes the exact same set of 
    characters as this method. <span class='highlight'>That is, {@code <c:out>}
     is good for escaping to produce valid XML, but not for producing safe 
     HTML.</span>
   */
    public static String escapeXML(String aText){
        final StringBuilder result = new StringBuilder();
        final StringCharacterIterator iterator = new StringCharacterIterator(aText);
        char character = iterator.current();
        while (character != CharacterIterator.DONE) {
            if (character == '<') {
                result.append("&lt;");
            } else if (character == '>') {
                result.append("&gt;");
            } else if (character == '\"') {
                result.append("&quot;");
            } else if (character == '\'') {
                result.append("&#039;");
            } else if (character == '&') {
                result.append("&amp;");
            } else {
                result.append(character);
            }
            character = iterator.next();
        }
        return result.toString();
    }
    
    public static String escapeURL(String str) {
        if(str == null) return null;
        try {
            return java.net.URLEncoder.encode(str, "utf-8");
        } catch (UnsupportedEncodingException e) {
            logger_.error("when escapeURL: " + e.getMessage());
            return null;
        }
    }
    
    public static String escapeHtml(String str) {
        final StringBuilder result = new StringBuilder();
        final StringCharacterIterator iterator = new StringCharacterIterator(
                str);
        char character = iterator.current();
        while (character != CharacterIterator.DONE) {
            if (character == '<') {
                result.append("&lt;");
            } else if (character == '>') {
                result.append("&gt;");
            } else if (character == '&') {
                result.append("&amp;");
            } else if (character == '\"') {
                result.append("&quot;");
            } else if (character == '\t') {
                result.append(String.format("&#%03d;", 9));
            } else if (character == '!') {
                result.append(String.format("&#%03d;", 33));
            } else if (character == '#') {
                result.append(String.format("&#%03d;", 35));
            } else if (character == '$') {
                result.append(String.format("&#%03d;", 36));
            } else if (character == '%') {
                result.append(String.format("&#%03d;", 37));
            } else if (character == '\'') {
                result.append(String.format("&#%03d;", 39));
            } else if (character == '(') {
                result.append(String.format("&#%03d;", 40));
            } else if (character == ')') {
                result.append(String.format("&#%03d;", 41));
            } else if (character == '*') {
                result.append(String.format("&#%03d;", 42));
            } else if (character == '+') {
                result.append(String.format("&#%03d;", 43));
            } else if (character == ',') {
                result.append(String.format("&#%03d;", 44));
            } else if (character == '-') {
                result.append(String.format("&#%03d;", 45));
            } else if (character == '.') {
                result.append(String.format("&#%03d;", 46));
            } else if (character == '/') {
                result.append(String.format("&#%03d;", 47));
            } else if (character == ':') {
                result.append(String.format("&#%03d;", 58));
            } else if (character == ';') {
                result.append(String.format("&#%03d;", 59));
            } else if (character == '=') {
                result.append(String.format("&#%03d;", 61));
            } else if (character == '?') {
                result.append(String.format("&#%03d;", 63));
            } else if (character == '@') {
                result.append(String.format("&#%03d;", 64));
            } else if (character == '[') {
                result.append(String.format("&#%03d;", 91));
            } else if (character == '\\') {
                result.append(String.format("&#%03d;", 92));
            } else if (character == ']') {
                result.append(String.format("&#%03d;", 93));
            } else if (character == '^') {
                result.append(String.format("&#%03d;", 94));
            } else if (character == '_') {
                result.append(String.format("&#%03d;", 95));
            } else if (character == '`') {
                result.append(String.format("&#%03d;", 96));
            } else if (character == '{') {
                result.append(String.format("&#%03d;", 123));
            } else if (character == '|') {
                result.append(String.format("&#%03d;", 124));
            } else if (character == '}') {
                result.append(String.format("&#%03d;", 125));
            } else if (character == '~') {
                result.append(String.format("&#%03d;", 126));
            } else {
                result.append(character);
            }
            character = iterator.next();
        }
        return result.toString();
    }
    
    public static int parseInt(String str, int defaultVal) {
        try {
            return Integer.parseInt(str);
        } catch (NumberFormatException nfe) {
            logger_.error("UE: parse int exception {}", nfe.getMessage());
            return defaultVal;
        }
    }
    
    public static ArrayList<String> readTxtFile(String fileName) throws DataConfException{
        return readTxtFile(fileName, false);
    }
    
    public static ArrayList<String> readTxtFile(String fileName,
            boolean exitOnErr) throws DataConfException{
        ArrayList<String> rtn = new ArrayList<String>();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(
                    new FileInputStream(fileName), "utf-8"));
            String line = null;
            boolean isFirstLine = true;
            while((line = reader.readLine()) != null) {
                if(isFirstLine && line.length() > 0 && line.charAt(0) == BOM) {
                    line = line.substring(1);
                    isFirstLine = false;
                }
                rtn.add(line);
            }
        } catch (Exception e) {
            String errMsg = String.format("Exception while reading file: %s",
                    fileName, e.getMessage());
            if(exitOnErr) {
                logger_.error(errMsg);
                e.printStackTrace();
                System.exit(-1);
            } else {
                throw new DataConfException("", errMsg, e);
            }
            return null;
        } finally {
            if(reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    String errMsg = String.format("Exception while closing file [%s]: %s",
                            fileName, e.getMessage());
                    if(exitOnErr) {
                        logger_.error(errMsg);
                        System.exit(-1);
                    } else {
                        throw new DataConfException("", errMsg, e);
                    }
                }
            }
        }
        return rtn;
    }
    
    public static Document readXMLFile(String fileName) throws DataConfException {
        return readXMLFile(fileName, false);
    }
    
    public static Document readXMLFile(String fileName,
            boolean exitOnErr) throws DataConfException {
        InputStream is = null;
        Document doc = null;
        try {
            DocumentBuilderFactory domfac = DocumentBuilderFactory.newInstance();
            DocumentBuilder dombuilder;
            dombuilder = domfac.newDocumentBuilder();
            is = new FileInputStream(fileName);
            doc = dombuilder.parse(is);
        } catch (Exception e) {
        	if(e instanceof FileNotFoundException) {
        		if(fileName.startsWith("stock/")) {
        			fileName = "./data/"+fileName;
        			readXMLFile(fileName,exitOnErr);
        		}else {
                    String errMsg = String.format("Exception while reading file: %s",
                            fileName, e.getMessage());
                    if(exitOnErr) {
                        logger_.error(errMsg);
                        e.printStackTrace();
                        System.exit(-1);
                    } else {
                        throw new DataConfException("", errMsg, e);
                    }
                    return null;
        		}
        	}
        } finally {
            if(is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    String errMsg = String.format("Exception while closing file [%s]: %s",
                            fileName, e.getMessage());
                    if(exitOnErr) {
                        logger_.error(errMsg);
                        e.printStackTrace();
                        System.exit(-1);
                    } else {
                        throw new DataConfException("", errMsg, e);
                    }
                }
            }
        }
        return doc;
    }
    
    public static String joinStr(List<String> strs, String delimeter) {
        StringBuilder sb = new StringBuilder();
        for(String str : strs) {
            if(sb.length() > 0) {
                sb.append(delimeter);
            }
            sb.append(str);
        }
        return sb.toString();
    }
    
    /**
     * 若给定double实际是整数，则返回该整数的字符串，否则保留3位小数点
     * @param d
     * @return
     */
    public static String formatDouble(double d){
        if(Math.floor(d) == d) {
            return String.format("%.0f", d);
        } else {
            return String.format("%.3f", d);
        }
    }
    
    public static boolean StringIn(String str, String[] strList){
        for(String strMem : strList){
            if(strMem.equals(str)){
                return true;
            }
        }
        return false;
    }
    
    //ygf add “expma”、“amv”，例句：“12日expma大于50日expma”
    public static boolean StringInTechLine(String string){
        return StringIn(string, new String[]{"均线","vol","expma","amv", "线", "多头排列", "均线多头排列"});
    }

    public static String linkStringArrayList( ArrayList<String> list ){
        if( list == null ){
            return null;
        }
        StringBuilder sb = new StringBuilder();
        for( String str : list ){
            sb.append(str);
        }
        return sb.toString();
    }
    
    public static String linkStringArray( String[] strArray ){
        if( strArray == null ){
            return null;
        }
        StringBuilder sb = new StringBuilder();
        for( int i = 0; i < strArray.length; i++ ){
            sb.append(strArray[i]);
        }
        return sb.toString();
    }
    
    /**
     * 用来将异常获得的信息以字符串的 形式组合起来返回
     * @param list ： 记录异常信息list
     * @return 构建成功返回字符串，否则返回null
     */
    public static String getExceptionTraceStr( StackTraceElement[] list ){
        if( list == null ){
            return null;
        }
        StringBuilder sb = new StringBuilder();
        for( int i = 0; i < list.length; i++ ){
            sb.append(i);sb.append(":");sb.append(list[i].toString());sb.append("\n");
        }
        return sb.toString();
    }
    
    public String joinStrList(List<String> strList, String delimeter) {
        StringBuilder sb = new StringBuilder();
        for(String str : strList) {
            if(sb.length() > 0) sb.append(delimeter);
            sb.append(str);
        }
        return sb.toString();
    }
    
    public static boolean WordIn(String word, String[] words){
        for(String s : words){
            if(s.equals(word)) return true;
        }
        return false;
    }
    

    public static String encoderByMd5(String source) {
        return DigestUtils.md5Hex(source);
    }    
    
    public static boolean isEqualStr(String s1, String s2)
    {
    	if (s1 == null && s2 == null)
    	{
    		return true;
    	}
    	if (s1 != null && s2 != null && s1.equals(s2))
    	{
    		return true;
    	}
    	return false;
    }
    
    public static boolean containsStr(List<String> strs, String s)
	{
		for (int i = 0; i < strs.size(); i ++)
		{
			if (strs.get(i).equals(s))
			{
				return true;
			}
		}
		return false;
	}
    
    private static final char BOM = '\uFEFF';
    private static org.slf4j.Logger logger_ = org.slf4j.LoggerFactory.getLogger(Util.class.getName());
    
    public static String linkStringArrayListBySpl( List<String> list, String spl ){
        if( list == null || spl == null ){
            return null;
        }
        StringBuilder sb = new StringBuilder();
        int size = list.size();
        for( int i=0; i<size; i++ ){
            sb.append(list.get(i));
            if( i+1 <size ){
                sb.append(spl);
            }
        }
        return sb.toString();
    }
    
    /**
     * 获取jvm当中所有活着的线程id字符串
     * @return
     */
    public static Set<String> getAllAliveThreadIDStrs(){
        ThreadGroup group = Thread.currentThread().getThreadGroup();  
        ThreadGroup topGroup = group;  
        // 遍历线程组树，获取根线程组  
        while (group != null) {  
            topGroup = group;  
            group = group.getParent();  
        }  
        // 激活的线程数加倍  
        int estimatedSize = topGroup.activeCount() * 2;  
        Thread[] slackList = new Thread[estimatedSize];  
        // 获取根线程组的所有线程  
        int actualSize = topGroup.enumerate(slackList);  
        // copy into a list that is the exact size  
        Thread[] list = new Thread[actualSize];  
        System.arraycopy(slackList, 0, list, 0, actualSize);  
        Set<String> idStrs = new HashSet<String>();
        for( Thread thread : list ){
            idStrs.add(Long.toString(thread.getId()));
        }
        return idStrs;
    }
    
    /**
     * 获取当前线程的string ID
     * @return
     */
    public static String getNowThreadKey(){
        Thread nowThread = Thread.currentThread();
        long nowThreadID = nowThread.getId();
        return Long.toString(nowThreadID);
    }
    /**
     * 返回一个随机的0~ceil，不包含ceil的正整数
     * 
     * @param ceil
     * @return 成功返回正整数，失败返回-1
     */
    public static int getRdmInt(int ceil) {
        if (ceil <= 0) {
            return -1;
        }
        return random.nextInt(ceil);
    }
    public static Random random = new Random(System.currentTimeMillis());
    
    /**
     * 是否为空字符串，null 或者 length =0
     * @param str
     * @return
     */
    public static boolean isEmptyStr(String str) {
        return str == null || str.length() == 0;
    }
    
    /**
     * 返回对象的Json字符串
     * @param o 需要转换的对象
     * @return o为null时,返回空
     */
    public static String getObjJsonStr(Object o){
        return o == null ? null : gson.toJson(o);
    }
    public static Gson gson = new Gson();

    /**
     * 用Json反推回Map<String, Object>
     * @param param
     * @return
     */
	public static Map<String, Object> getMapFromJson(String param) {
		if(param==null||param.isEmpty())return null;
		Gson gson = new GsonBuilder().enableComplexMapKeySerialization()
				.create();
		Map<String, Object> paramMap = gson.fromJson(param,
				new TypeToken<Map<String, Object>>() {
				}.getType());
		return paramMap;
	}
	
	private final static DecimalFormat df = new DecimalFormat("0.#######");
	public static String numericFormat(double value) {
		try{
			return df.format(value);
		}catch(Exception e){
			return String.valueOf(value);
		}
	}
	
	public static String numericFormat(float value) {
		try{
			return df.format(value);
		}catch(Exception e){
			return String.valueOf(value);
		}
	}
}
