package com.myhexin.qparser.suggest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import com.myhexin.qparser.Param;
import com.myhexin.qparser.Query;
import com.myhexin.qparser.except.DataConfException;
import com.myhexin.qparser.except.UnexpectedException;
import com.myhexin.qparser.util.Util;

public class QuerySuggest {
    public static String ANS_SPL_TOKEN = "\t\t";
    //存放所有的推荐问句节点
    private static ArrayList<SentNode> sentNodesList = new ArrayList<SentNode>();
    //存放从指标标准名称映射到包含其推荐问句列表的映射表
    private static HashMap<String, ArrayList<SentNode>> index2SentListMap = new HashMap<String, ArrayList<SentNode>>();
    
    private static WebTempCreater tempCreater = new WebTempCreater();
    public static  ArrayList<ArrayList<String>> indexInCommonUse = new ArrayList<ArrayList<String>>();
    //以标准的指标名称为key，以知识上与其相关的指标标准名称的列表为value
    public static HashMap<String, ArrayList<String>> indexInCommonUseMap = new HashMap<String, ArrayList<String>>(); 
    private static String COMMON_SPL = "_SPL_";//一般常用的分隔符
    private static String ABB_SPL = "::";//用于分割知识上相关指标和其标准名称的分隔符
    
    
    private static org.slf4j.Logger logger_ = org.slf4j.LoggerFactory
            .getLogger(QuerySuggest.class.getName());
    
    public QuerySuggest(){
    }
    
    /**
     * 装载常用指标列表
     * @param list ：指标文件行列表
     */
    public static void loadIndexInCommonUse( ArrayList<String> list ) throws DataConfException{
        if( list == null || list.isEmpty() ){
            logger_.error( "常用指标列表为空" );
            throw new DataConfException(Param.IFIND_INDEX_IN_COMMON_USE, -1 ,
                    "The index in common use is empty." );
        }
        Iterator<String> iterator = list.iterator();
        String line = "";
        ArrayList<ArrayList<String>> newIndexInCommonUse = new ArrayList<ArrayList<String>>();
        HashMap<String, ArrayList<String>> newIndexInCommonUseMap = new HashMap<String, ArrayList<String>>();
        
        while( iterator.hasNext() ){
            line = iterator.next().trim();
            String[] temp = line.split(COMMON_SPL);
            if( (null == temp) || (temp.length < 2) ){
                logger_.error( String.format( "%s%s", "非合法的指数列表格式", line ));
                throw new DataConfException(Param.IFIND_INDEX_IN_COMMON_USE, -1 ,
                        String.format("%s%s", "Illegal index in common use file format:", line) );
            }
            ArrayList<String> indexGroupList = new ArrayList<String>();//放指标简称列表
            ArrayList<String> stdIndexGroupList = new ArrayList<String>();//放指标全称列表
            
            String[] indexGroupArray = temp[1].split("\\|");
            for( int i = 0; i < indexGroupArray.length; i++ ){
            	String[] abbAndStd = indexGroupArray[i].split(ABB_SPL);
            	if( abbAndStd.length != 2 ){
            		logger_.error( String.format( "%s%s", "非合法的指数列表格式", line ));
                    throw new DataConfException(Param.IFIND_INDEX_IN_COMMON_USE, -1 ,
                            String.format("%s%s", "Illegal index in common use file format:", line) );
            	}
            	
                indexGroupList.add(abbAndStd[0]);
                stdIndexGroupList.add(abbAndStd[1]);
            }
            newIndexInCommonUse.add(indexGroupList);
            for( String stdIndexName : stdIndexGroupList ){
            	newIndexInCommonUseMap.put(stdIndexName, stdIndexGroupList);
            }
        }
        synchronized( indexInCommonUse ){
            indexInCommonUse = newIndexInCommonUse;
        }
        synchronized( indexInCommonUseMap ){
        	indexInCommonUseMap = newIndexInCommonUseMap;
        }
    }
    
    public static void loadSuggestSentence( ArrayList<String> list ) throws DataConfException{
        if( null == list ){
            logger_.error("推荐问句列表为空");
            throw new DataConfException(Param.SUGGEST_TEMP_FILE, -1 ,
                    "The suggest sentence is empty.");
        }
        ArrayList<SentNode> newSentNodesList  = new ArrayList<SentNode>();
        HashMap<String, ArrayList<SentNode>> newIndex2SentListMap = new HashMap<String, ArrayList<SentNode>>();
        
        Iterator<String> iterator = list.iterator();
        String now = null;
        while( iterator.hasNext() ){
            now = iterator.next();
            String[] tempArray = now.split(WebTempCreater.TEMP_SPL_TOKEN);
            if( tempArray.length != 5 ){
                logger_.error(String.format("%s%s", "推荐问句列表格式错误:", now));
                throw new DataConfException(Param.SUGGEST_TEMP_FILE, -1 ,
                        String.format("The suggest sentence has illegal format: %s.", now));
            }
            SentNode sentNode = new SentNode(tempArray);
           
            //加载所有推荐问句列表
            newSentNodesList.add(sentNode);
            //加载推荐问句中指标到包含其的问句列表的Map
            for( String indexName : sentNode.indexList_ ){
            	if( newIndex2SentListMap.containsKey(indexName) ){
            		newIndex2SentListMap.get(indexName).add(sentNode);
            	}else{
            		ArrayList<SentNode> tlist = new ArrayList<SentNode>();
            		tlist.add(sentNode);
            		newIndex2SentListMap.put(indexName, tlist);
            	}
            }
        }
        synchronized(sentNodesList){
            sentNodesList = newSentNodesList;
        }
        synchronized(index2SentListMap){
        	index2SentListMap = newIndex2SentListMap;
        }
    }
    
    /**
     * 装载拼音汉字映射文件
     * @param list
     * @throws DataConfException 
     */
    public static void loadu2pHashMap( ArrayList<String> list ) throws DataConfException{
        tempCreater.loadu2pHashMap(list);
    }
    
    /**
     * 为Ifind客户端提供问句推荐
     * @param query Query类型，QueryParser构建的Query
     * @param xmlRlt 要替换的传递给Ifind的xml格式指令
     * @return
     * @throws UnexpectedException 
     */
    public String makeSuggestForIfindClient( Query query, String xmlRlt ) {
        if( (null == query)||(null == xmlRlt) ){ return xmlRlt; }
        StringBuilder replaceSB = new StringBuilder();
        replaceSB.append("\t<replaces>\n");
        replaceSB.append("\t</replaces>\n</result>");
        String repPostXMLStr = xmlRlt.replace("</result>", replaceSB.toString());
        
        //调用makeSuggestForWeb来进行相关问句的推荐
        ArrayList<String> suggestSentList = makeSuggestForWeb( query );
        StringBuilder suggestSB = new StringBuilder();
        suggestSB.append("\t<suggests>\n");
        if( suggestSentList != null ){
            for( int i = 0; i<suggestSentList.size(); i++ ){
                suggestSB.append("\t\t<suggest>");
                suggestSB.append(Util.escapeXML(suggestSentList.get(i)));
                suggestSB.append("</suggest>\n");
            }
        }
        suggestSB.append("\t</suggests>\n</result>");
        String finalXMLStr = repPostXMLStr.replace("</result>", suggestSB.toString());
        return finalXMLStr;
    }
    
    /**
     * 相关问句推荐，接受Query类型问句
     * 以后无论推荐问句包装成什么形式,都应该以这个函数为入口
     * @param query
     * @return 当没有结果的时候，返回空list
     * @throws UnexpectedException 
     */
    public ArrayList<String> makeSuggestForWeb( Query query ) {
        WebQuerySuggester suggester = new WebQuerySuggester(sentNodesList,  index2SentListMap, tempCreater);
        ArrayList<String> list = null;
        list = suggester.makeSuggest(query);
        //去除特殊符号
        list =  specialCharacterFilter(list);
        return list; 
    }
    
    /**
     * 对于传入的问句字符串列表进行过滤，过滤掉引号之类的特殊字符串
     * @param list
     * @return
     */
    private ArrayList<String> specialCharacterFilter( ArrayList<String> list ){
        ArrayList<String> rlt = new ArrayList<String>();
        for( String str : list ){
            str = str.replace("\"", "");
            rlt.add(str);
        }
        return rlt;
    }
}
