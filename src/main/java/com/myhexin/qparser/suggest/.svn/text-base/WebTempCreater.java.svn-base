package com.myhexin.qparser.suggest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import com.myhexin.qparser.Param;
import com.myhexin.qparser.Query;
import com.myhexin.qparser.define.EnumDef.NodeType;
import com.myhexin.qparser.except.DataConfException;
import com.myhexin.qparser.fuzzy.FuzzyAnalysis;
import com.myhexin.qparser.node.SemanticNode;

public class WebTempCreater {
    static final String RAW_SPL_TOKEN = "\t\t";
    static final String TEMP_SPL_TOKEN = "_SPL_";
    static final String PINYIN_SPL_TOKEN = "_PSPL_";
    static final String PATTERN_SPL_TOKEN = "_PAT_";//用来分隔PATTERN中不同字串
    static final String CLASS_MARKER = "[[CM]]";
    static final ArrayList<NodeType> ACCEPT_NODE_TYPE_LIST = new ArrayList<NodeType>();
    
    static{
        ACCEPT_NODE_TYPE_LIST.add(NodeType.LOGIC);
        ACCEPT_NODE_TYPE_LIST.add(NodeType.DATE);
        ACCEPT_NODE_TYPE_LIST.add(NodeType.NUM);
        ACCEPT_NODE_TYPE_LIST.add(NodeType.OPERATOR);
        ACCEPT_NODE_TYPE_LIST.add(NodeType.SORT);
        ACCEPT_NODE_TYPE_LIST.add(NodeType.AVG);
        ACCEPT_NODE_TYPE_LIST.add(NodeType.INST);
        ACCEPT_NODE_TYPE_LIST.add(NodeType.PROP);
        ACCEPT_NODE_TYPE_LIST.add(NodeType.CLASS);
        ACCEPT_NODE_TYPE_LIST.add(NodeType.STR_VAL);
    }
    
    private static final String SKIP_LIST = "的|地|,|;| |，|　|.?|？|：|:| |在|是|与|跟|和 |有|哪些|有什么|上市公司|股份|企业|公司|已|有哪几种|那家|股票|什么|哪家|了|包括";

    private static org.slf4j.Logger logger_ = org.slf4j.LoggerFactory
            .getLogger(QuerySuggest.class.getName());

    private HashSet<String> stopListSet_;
    private HashMap<String, String> u2pHashMap;
    
    public WebTempCreater() {
        stopListSet_ = new HashSet<String>();
        String[] stopWords = SKIP_LIST.split("\\|");
        for (int i = 0; i < stopWords.length; i++) {
            stopListSet_.add(stopWords[i]);
        }
        u2pHashMap = new HashMap<String, String>();
    }

    public void loadu2pHashMap( ArrayList<String> list ) throws DataConfException{
        Iterator<String> iterator = list.iterator();
        String now = null;
        while( iterator.hasNext() ){
            now = iterator.next();
            if( now.isEmpty() ){
                continue;
            }
            String[] pair = now.split(" ");
            try{
                String keyStr = pair[0];
                String pinyinStr = pair[1].split(",")[0];
                u2pHashMap.put( Character.toString((char)Integer.parseInt(keyStr,16)), pinyinStr.substring(0, pinyinStr.length()-1));
            }catch( ArrayIndexOutOfBoundsException e ){
                //异常的拼音文件
                logger_.debug("%s", now);
                throw new DataConfException(Param.UNICOD_TO_PINYIN, -1 ,
                        "错误的拼音汉字映射文件:" + now + ":" + e.getMessage());
            }
        }
    }
    
    //生成粗略的问句模板，主要会用在自动完成
    public String createTempStr(String inputStr) {
        String midStr = inputStr;
        Iterator<String> iterator = stopListSet_.iterator();
        String nowStr = null;
        while( iterator.hasNext() ){
            nowStr = iterator.next();
            midStr = midStr.replace(nowStr, "");
        }
        
        // 首先过滤掉特殊符号和数字
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < midStr.length(); i++) {
            char nowChar = midStr.charAt(i);
            if( nowChar == '>' || nowChar == '<' || nowChar == '=' || nowChar == '*' ){
                sb.append(nowChar);
                continue;
            }
            if ((FuzzyAnalysis.isNumber(nowChar)
                    || FuzzyAnalysis.isOther(nowChar))) {
                continue;
            } else {
                sb.append(nowChar);
            }
        }
        String temp = sb.toString();
        // 全角转半角，大写转小写
        String res = FuzzyAnalysis.QtoB(temp).toLowerCase();
        return res;
    }

    //生成拼音模板，也主要用在问句自动完成
    public String createTempPinyin( String tempStr ){
        StringBuilder sb = new StringBuilder();
        for(  int i = 0; i < tempStr.length(); i++ ){
            String nowStr = Character.toString(tempStr.charAt(i));
            if( u2pHashMap.containsKey(nowStr) ){
                sb.append( u2pHashMap.get(nowStr));
            }else{
                sb.append(nowStr);
            }
            if( i < (tempStr.length() - 1) ){
                sb.append(PINYIN_SPL_TOKEN);
            }
        }
        return sb.toString();
    }
    
    /**
     * 根据query来构造一个语义模板
     * @param query
     * @return
     */
    public static String createTempPatternBuyQuery( Query query ){
        String rlt = "";
        StringBuilder sb = new StringBuilder();
        for( SemanticNode node : query.getNodes() ){
            if( ! ACCEPT_NODE_TYPE_LIST.contains( node.type )  ){continue;}
            if( NodeType.DATE == node.type ){
                sb.append( NodeType.DATE.name() );
            }else if( NodeType.NUM == node.type ){
                sb.append( NodeType.NUM.name() );
            }else if( NodeType.STR_VAL == node.type ){
                sb.append( NodeType.STR_VAL.name() );
            }else if( NodeType.CLASS == node.type ){
                sb.append(  CLASS_MARKER + node.getText() );
            }else{
                sb.append( node.getText() );
            }
            sb.append( PATTERN_SPL_TOKEN );
        }
        if( sb.length() > 0 ){
            sb.delete( sb.length()-PATTERN_SPL_TOKEN.length(), sb.length() );
        }
        rlt = sb.toString();
        return rlt;
    }
    
    /**
     * 通过一个QueryParser的实例来生成一个语义模板
     * @param str ：问句字符串
     * @param qp ：QueryParser的实例
     * @return
     */
    /*public String createTempPattern( String str, QueryParser qp ){
        Query query = new Query(str);
        qp.parse(query);
        String rlt = createTempPatternBuyQuery(query);
        return rlt;
    }*/
    
    /*public void createWebTempFile(String inputFileName, String outputFileName)
            throws DataConfException, IOException {
        try {
            QueryParser qp = QueryParser.getParser("./conf/qparser.conf");
            String ls = System.getProperty("line.separator");
            ArrayList<String> rawSentList = Util.readTxtFile(inputFileName);
            ArrayList<SentNode> resList = new ArrayList<SentNode>();
            String str = null;
            for (int i = 0; i < rawSentList.size(); i++) {
                str = rawSentList.get(i);
                str.trim();
                String[] strs = str.split(RAW_SPL_TOKEN);
                if (strs.length != 2) {
                    logger_.error(String.format("%s%s", "正确问句原始文件格式错误", str));
                    continue;
                }

                String sentStr = strs[0];
                String scoreStr = strs[1];

                SentNode sentNode = new SentNode();
                sentNode.sentence_ = sentStr;
                sentNode.score_ = Double.parseDouble(scoreStr);
                sentNode.temple_ = createTempStr(sentStr);
                sentNode.setTempleSplList_();
                sentNode.pinyinTemple_ = createTempPinyin(sentNode.temple_);
                sentNode.setPinyinSplList_();
                sentNode.patternTemple_ = createTempPattern(sentNode.sentence_, qp);
                sentNode.setPatternSplList();
                
                resList.add(sentNode);
            }

            FileWriter writer = new FileWriter(outputFileName);

            Iterator<SentNode> iterator = resList.iterator();
            SentNode now = null;
            StringBuilder sb = new StringBuilder();
            while (iterator.hasNext()) {
                now = iterator.next();
                System.out.println(now.sentence_);
                sb.append(now.temple_);
                sb.append(TEMP_SPL_TOKEN);
                sb.append(now.pinyinTemple_);
                sb.append(TEMP_SPL_TOKEN);
                sb.append(now.patternTemple_);
                sb.append(TEMP_SPL_TOKEN);
                sb.append(now.sentence_);
                sb.append(TEMP_SPL_TOKEN);
                sb.append(now.score_);
                sb.append(ls);
                writer.write(sb.toString());
                sb = new StringBuilder();
            }
            writer.close();
        } catch (DataConfException e) {
            throw new DataConfException("", -1, "Unknown file name %s",
                    inputFileName);
        }
    }*/

    /**
     * @param args
     * @throws DataConfException 
     */
    /*public static void main(String[] args) throws DataConfException {
        // TODO Auto-generated method stub
        String inputFileName = "./data/click_count_sent.txt";
        String outputFileName = "./data/web_tempsentence.txt";
        String u2pFileName = "./data/unicode2pinyin.txt";
        
        WebTempCreater creater = new WebTempCreater();
        creater.loadu2pHashMap(Util.readTxtFile(u2pFileName));
        try {
            creater.createWebTempFile(inputFileName, outputFileName);
            System.out.println("Finished!");
        } catch (DataConfException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }*/
}
