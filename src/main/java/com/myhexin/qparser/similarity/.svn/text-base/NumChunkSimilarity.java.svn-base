package com.myhexin.qparser.similarity;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeSet;

import com.myhexin.qparser.Query;
import com.myhexin.qparser.Query.Type;
import com.myhexin.qparser.similarity.CodeInfo.CodeInfoProp;
import com.myhexin.qparser.similarity.SimilarityChunkResult.Candidate;
import com.myhexin.qparser.util.Util;


/**
 * 1.如果是符号或数字就不做similarity转换
 * 2.替换掉中文字符,中文的问号，感叹号...
 * 3.用_&_把问句切开
 * 4.返回有村数字从npatterninfos.txt文件中找出来的codeInfo
 * 
 * 
 * @author 刘小峰 liuxiaofeng@myhexin.com
 * @Version 创建时间 2015-1-4
 *
 */
public class NumChunkSimilarity {
    private String CHUNK_SPL = SimilarityQueryWithOptions.SEG_IN_CHUNK;
    
    boolean DEBUG = false;
    //private String qText ;
    //private Type qType;
    
    public List<List<Candidate>> run(String qText,Type qType){
        //check if need simi, 如果是符号或者数字,就不做similarity转换
        if( !needSimi(qText) ){
            return null;
        }
        qText = replaceSymbol(qText);
        return runBody( qText, qType);
    }
    
    private String replaceSymbol(String qText){
    	//对>,<,=不替换
		String[] symbolList = { "、", "？", "！", "￥", "【", "】", "；", "‘", "”",
				"’", "“", "：", "《", "》", "，", "。", "（", "）", " ", "!", "\"",
				"#", "$", "%", "&", "'", "(", ")", "*", "+", ",", "-", ".",
				"/", ":", ";", "?", "@", "[", "]", "\\", "^", "_", "`", "{",
				"}", "|", "~" };
		for (String symbol : symbolList) {
			qText = qText.replace(symbol, "");
		}
		
		return qText;
    }
    
    /**
	 * 判断一个字符是否为中文符号
	 */
	private static boolean isChineseSymbolChar(char c) {
		char[] symbolList = { '、', '？', '！', '￥', '【', '】', '；', '‘', '”', '’',
				'“', '：', '《', '》', '，', '。', '（', '）' };
		boolean result = false;
		for (int i = 0; i < symbolList.length; i++) {
			if (symbolList[i] == (c)) {
				result = true;
			}
		}
		return result;
	}
    
    /**
     * 判断是否为数字或者符号的字符串
     */
	public static boolean isNumOrSymbolStr(String str) {
		for (int i = 0; i < str.length(); i++) {
			if (!(str.charAt(i) >= 32 && str.charAt(i) <= 64)
					&& !(str.charAt(i) >= 91 && str.charAt(i) <= 96)
					&& !(str.charAt(i) >= 123 && str.charAt(i) <= 126)
					&& !isChineseSymbolChar(str.charAt(i))) {
				return false;
			}
		}
		return true;
	}
    
	/*
	 * 检查问句中是否有数字或者符号，或者_&_符号
	 * 如果有, 就返回false
	 */
    private boolean needSimi(String queryStr){
        if( queryStr!=null && queryStr.contains(CHUNK_SPL) ){
            return false;
        }
        if( isNumOrSymbolStr(queryStr)){
            return true;
        }else{
            return false;
        }
    }
    
    
    
    private List<List<Candidate>> runBody( String qstr, Query.Type type ){
        List<List<Candidate>> rlt = new ArrayList<List<Candidate>>();
        
        //用_&_把问句切开
        String[] chunkStrs = qstr.split(CHUNK_SPL);
        for( int i=0; i<chunkStrs.length; i++ ){
            List<Candidate> chunkCandidates = runOneChunkBody( chunkStrs[i], type );
            if( chunkCandidates == null ){
                chunkCandidates = new ArrayList<Candidate>();
            }
            rlt.add(chunkCandidates);
        }
        
        return rlt;
        
    }
    
    /* 运行一个chunk
     * 
     * 
     * @param numStr
     * @param type
     * @return
     */
    private List<Candidate> runOneChunkBody( String numStr, Query.Type type ){
        //compute score and use early termanition 
    	Map<Query.Type, List<CodeInfo>> rltCodesInfos = createCandidates( numStr );
        
        //fill output candates list
        List<Candidate> rlt = fillCandidatesWithCodesInfos( type, rltCodesInfos );
        
        return rlt;
    }
    
    private List<Candidate> fillCandidatesWithCodesInfos( Query.Type type, Map<Query.Type, List<CodeInfo>> codesInfos ){
        List<Candidate> rlt = new ArrayList<Candidate>();
        List<CodeInfo> domainCodesInfo = codesInfos.get(type);
        if( domainCodesInfo != null ){
            List<Candidate> nowDomainCandidates = fillCandidatesWithCodesInfo( type, domainCodesInfo );
            rlt.addAll(nowDomainCandidates);
        }
        
        Query.Type[] types = Query.Type.values();
        Query.Type nowType = null;
        for( int i=0; i<types.length; i++ ){
            nowType = types[i];
            if( nowType == type ){
                continue;
            }
            domainCodesInfo = codesInfos.get(nowType);
            if( domainCodesInfo == null ){
                continue;
            }
            List<Candidate> nowDomainCandidates = fillCandidatesWithCodesInfo( nowType, domainCodesInfo );
            rlt.addAll(nowDomainCandidates);
        }
        return rlt;
    }
    
    private List<Candidate> fillCandidatesWithCodesInfo( Query.Type queryType, List<CodeInfo> codeInfoList ){
        List<Candidate> rlt = new ArrayList<Candidate>();
        Collections.sort(codeInfoList);
        
        int outSizeLimit = 3;
        int outCount = 0;
        for(CodeInfo codeInfo : codeInfoList){
            String text = String.format("%s %s", codeInfo.getStrPropValue(CodeInfoProp.code), codeInfo.getStrPropValue(CodeInfoProp.shortname));
            Double score = codeInfo.getDoublePropValue(CodeInfoProp.score);
            Candidate candidate = new Candidate(text, score, queryType);
            rlt.add(candidate);
            ++outCount;
            if( outCount >= outSizeLimit ){
                break;
            }
        }
        return rlt;
    }
    
    private Map<Query.Type, List<CodeInfo>> createCandidates( String qstr){
        List<String>  codeStrArray = CodeInfo.createCodeArray(qstr);
        List<String> deZeroCodeStrArray = CodeInfo.createDeZeroCodeArray(qstr);
        System.out.println("codeStrArray=" + codeStrArray);
        System.out.println("deZeroCodeStrArray=" + deZeroCodeStrArray);
        
        Query.Type[] types = Query.Type.values();
        Query.Type nowType = null;
        CodesInfosResource codeInfoResource = CodesInfosResource.getInstance();
        
        
        
        Map<Query.Type, List<CodeInfo>> rlt = new HashMap<Query.Type, List<CodeInfo>>();
        for( int i=0; i<types.length; i++ ){
            nowType = types[i];
            List<CodeInfo> nowCodeInofList = codeInfoResource.getFiltedCodesInfos(nowType);
            if(nowCodeInofList==null || nowCodeInofList.size()==0) {
            	continue;
            }
            List<CodeInfo> rltList = createEarlyTerminationCodeInfos( codeStrArray, deZeroCodeStrArray, nowCodeInofList);
            rlt.put(nowType, rltList);
        }
        return rlt;
    }
    
    private List<CodeInfo> createEarlyTerminationCodeInfos( List<String> qStrArray, List<String> qDeZeroStrArray, List<CodeInfo> candidates ){
        int earlyTermSize = 10;
        int acceptCount = 0;
        List<CodeInfo> rlt = new ArrayList<CodeInfo>();
        
        //
        for( CodeInfo candidate : candidates ){
            //String str = candidate.getStrPropValue(CodeInfoProp.code);
            List<String> cStrArray = candidate.getListPropValue(CodeInfoProp.codeStrArray);
            List<String> cStrDeZeroArray = candidate.getListPropValue(CodeInfoProp.deZeroCodeStrArray);
            
            if( !isNeedComputeScore( qDeZeroStrArray, cStrDeZeroArray ) ){
                continue;
            }
            
            Double score = computeScore(qStrArray, cStrArray);
            
            if(DEBUG){
                String logStr = String.format("qarray[%s] carray[%s] score[%s]", 
                        Util.joinStr(qStrArray, ","), 
                        Util.joinStr(cStrArray, ","),
                        score
                        );
                System.out.println(logStr);
            }
            
            if( !isAcceptableScore(score) ){
                continue;
            }
            CodeInfo codeInfo = new CodeInfo();
            codeInfo.setStrPropValue(CodeInfoProp.code, candidate.getStrPropValue(CodeInfoProp.code));
            codeInfo.setStrPropValue(CodeInfoProp.shortname, candidate.getStrPropValue(CodeInfoProp.shortname));
            codeInfo.setDoublePropValue(CodeInfoProp.score, score);
            rlt.add(codeInfo);
            ++acceptCount;
            if( acceptCount >= earlyTermSize ){
                break;
            }
        }
        return rlt;
    }
    
    /**
     * 根据两个去除了zero的str list来判断是否有必要计算分值
     * @return
     */
    private boolean isNeedComputeScore(
        List<String> qDeZeroStrs,
        List<String> cDeZeroStrs){
        if( Math.abs(qDeZeroStrs.size()-cDeZeroStrs.size()) > 2 ){
            return false;
        }else{
            return true;
        }
    }
    
    private boolean isAcceptableScore( Double score ){
        if( score >= 0.8 ){
            return true;
        }else{
            return false;
        }
    }
    
    public static Double computeScore( List<String> q, List<String> c ){
        double desScore = NumChunkSimilarity.computeDestinyScore(q,c);//密度打分
        double maxCommonScore = NumChunkSimilarity.computeMaxCommonSeqScore(q,c);//最大公共子串打分
        return 0.8*desScore + 0.2*maxCommonScore;
    }
    
    public static Double computeMaxCommonSeqScore( List<String> ql, List<String> cl ){
        List<String> maxCommonStrs = getMaxCommonStrs(ql, cl);
        if( maxCommonStrs.isEmpty() ){
            return 0.0;
        }
        String firstCommonStr = maxCommonStrs.get(0);
        Double score = (double)(firstCommonStr.length()*2)/(ql.size()+cl.size());
        return score;
    }
    
    public static List<String> getMaxCommonStrs( List<String> ql, List<String> cl ){
        int len1, len2;  
        len1 = ql.size();  
        len2 = cl.size(); 
        int maxLen = len1 > len2 ? len1 : len2;  
        String[] str1 = (String[])ql.toArray(new String[len1]);
        String[] str2 = (String[]) cl.toArray(new String[len2]);
        int[] max = new int[maxLen];// 保存最长子串长度的数组  
        int[] maxIndex = new int[maxLen];// 保存最长子串长度最大索引的数组  
        int[] c = new int[maxLen];  
        int i, j;  
        for (i = 0; i < len2; i++) {  
            for (j = len1 - 1; j >= 0; j--) {
                //System.out.println( String.format("str2[%s]:%s str1[%s]:%s",i,str2[i],j,str1[j]));
                if (str2[i].equals(str1[j])) {  
                    if ((i == 0) || (j == 0))  
                        c[j] = 1;  
                    else  
                        c[j] = c[j - 1] + 1;//此时C[j-1]还是上次循环中的值，因为还没被重新赋值  
                } else {  
                    c[j] = 0;  
                }  
  
                // 如果是大于那暂时只有一个是最长的,而且要把后面的清0;  
                if (c[j] > max[0]) {  
                    max[0] = c[j];  
                    maxIndex[0] = j;  
  
                    for (int k = 1; k < maxLen; k++) {  
                        max[k] = 0;  
                        maxIndex[k] = 0;  
                    }  
                }  
                // 有多个是相同长度的子串  
                else if (c[j] == max[0]) {  
                    for (int k = 1; k < maxLen; k++) {  
                        if (max[k] == 0) {  
                            max[k] = c[j];  
                            maxIndex[k] = j;  
                            break; // 在后面加一个就要退出循环了  
                        }  
                    }  
                }  
            }  
        }  
        List<String> rlt = new ArrayList<String>();
        for( j=0; j<maxLen; j++ ){
            if(max[j]>0){
                StringBuilder sb = new StringBuilder();
                for( i=maxIndex[j]-max[j]+1; i<=maxIndex[j]; i++ ){
                    sb.append(str1[i]);
                }
                rlt.add(sb.toString());
            }
        }
        return rlt;
    }
    
    public static Double computeDestinyScore(
        List<String> q, 
        List<String> c){
        List<Entry<Integer, Integer>> qToCPosMap = createPosMap(q, c);
        if( qToCPosMap == null || qToCPosMap.isEmpty() ){
            return 0.0;
        }
        double weightScore = computeWeigthScore(q, c, qToCPosMap);
        double posScore = computePosScore(q, c, qToCPosMap);
        double score = (0.9 * weightScore) + (0.1 * posScore);
        StringBuilder sb = new StringBuilder();
        for( Entry<Integer,Integer> entry : qToCPosMap ){
            sb.append(String.format("[%s|%s] ",entry.getKey(),entry.getValue()));
        }
        //System.out.println(sb.toString());
        //System.out.println( String.format("weightSocre [%s] posScore [%s]",weightScore,posScore) );
        return score;
    }
    
    /**
     * 寻找从query到candidate之间字符的一一映射
     * @param q
     * @param c
     * @return
     */
    private static List<Entry<Integer, Integer>> createPosMap(
        List<String> q,
        List<String> c){
        List<Entry<Integer,Integer>> rlt = new ArrayList<Entry<Integer, Integer>>();
        TreeSet<Integer> metQIndexs = new TreeSet<Integer>();
        for( int cIndex=0; cIndex<c.size(); cIndex++ ){
            String cs = c.get(cIndex);
            for( int qIndex = 0; qIndex<q.size(); qIndex++ ){
                if( metQIndexs.contains(qIndex) ){
                    continue;
                }
                String qs = q.get(qIndex);
                if( !qs.equals(cs) ){
                    continue;
                }
                rlt.add(new SimpleEntry<Integer, Integer>(qIndex, cIndex));
                metQIndexs.add(qIndex);
                break;
            }
        }
        return rlt;
    }
    
    /**
     * 计算权重分值
     * @param q
     * @param c
     * @param qToCPosMap
     * @return
     */
    private static double computeWeigthScore(
        List<String> q,
        List<String> c,
        List<Entry<Integer, Integer>> qToCPosMap){
        int qMatchWeight = qToCPosMap.size();
        int cMatchWeight = qToCPosMap.size();
        int qAllWeight = q.size();
        int cAllWeight = c.size();
        double score = (double)(qMatchWeight + cMatchWeight)/(double)(qAllWeight+cAllWeight);
        return score;
    }
    
    /**
     * 计算逆序分值
     * @param q
     * @param c
     * @param qToCPosMap
     * @return
     */
    private static double computePosScore(
        List<String> q,
        List<String> c,
        List<Entry<Integer, Integer>> qToCPosMap){
        //最大逆序数
        int maxRevCount = qToCPosMap.size() - 1;
        if( maxRevCount == 0 ){
            return 1.0;
        }
        
        int realRevCount = 0;
        Entry<Integer, Integer> preEntry = null;
        for( Entry<Integer, Integer> entry : qToCPosMap ){
            if( preEntry != null ){
                if( entry.getKey() < preEntry.getKey() ){
                    realRevCount++;
                }
            }
            preEntry = entry;
        }
        if( realRevCount == 0 ){
            return 1.0;
        }
            
        double score = (double)(maxRevCount-realRevCount)/(double)(maxRevCount);
        return score;
    }
    
    private Double computEditDistanceScore(
        List<String> q,
        List<String> c){
        Double fullScore = 100.0;
        int editDistance = getEditDistance(q, c);
        return fullScore-editDistance;
    }
    
    private static int getEditDistance( List<String> q, List<String> c ){
        int d[][]; // matrix
        int n; // length of s
        int m; // length of t
        int i; // iterates through s
        int j; // iterates through t
        String s_i; // ith character of s
        String t_j; // jth character of t
        int cost; // cost

        // Step 1
        n = q.size();
        m = c.size();
        
        if (n == 0) {
            return m;
        }
        if (m == 0) {
            return n;
        }
        d = new int[n + 1][m + 1];

        // Step 2

        for (i = 0; i <= n; i++) {
            d[i][0] = i;
        }

        for (j = 0; j <= m; j++) {
            d[0][j] = j;
        }

        // Step 3

        for (i = 1; i <= n; i++) {
            s_i = q.get(i - 1);
            // Step 4
            for (j = 1; j <= m; j++) {
                t_j = c.get(j - 1);
                // Step 5
                if (s_i.equals(t_j)) {
                    cost = 0;
                } else {
                    cost = 1;
                }
                // Step 6
                d[i][j] = Minimum(d[i - 1][j] + 1, d[i][j - 1] + 1, d[i - 1][j - 1] + cost);
            }
        }
        // Step 7
        return d[n][m];
    }
    
    private static int Minimum(int a, int b, int c) {
        int mi;

        mi = a;
        if (b < mi) {
            mi = b;
        }
        if (c < mi) {
            mi = c;
        }
        return mi;
    }
    
    public static void main( String[] args ){
        String qStr = "02706";
        List<String> q = new ArrayList<String>();
        String[] qArray = qStr.split("");
        for( int i=0; i<qArray.length; i++ ){
            if( qArray[i].isEmpty() ){
                continue;
            }
            q.add(qArray[i]);
        }
        
        String cStr = "002706";
        List<String> c = new ArrayList<String>();
        String[] cArray = cStr.split("");
        for( int i=0; i<cArray.length; i++ ){
            if( cArray[i].isEmpty() ){
                continue;
            }
            c.add(cArray[i]);
        }
        System.out.println(computeDestinyScore(q,c));
    }
}
