package com.myhexin.qparser.similarity;

import java.util.ArrayList;
import java.util.Collections;

import com.myhexin.qparser.except.NotSupportedException;
import com.myhexin.qparser.except.UnexpectedException;

/**
 * 
 * 记录文本转换的信息, 根据转换信息转换文本
 * 
 * @author admin
 *
 */
public class TextTranser {
    /** 初始文本 */
    private String originStr;
    /** 转换好的文本 */
    private String rltStr;
    
    /** 原文本中需要转换的序列，记录每个转换的“起始”“截止”位置 */
    private ArrayList<IntIntPair> originChangeIdxes = null;
    /** 每个转换的对应文本，与{@link #originChangeIdxes} 位置对应 */
    private ArrayList<String> replaceStrs = null;
    /** 转换后，每个转换对应在结果序列{@link #rltStr}的“起始”和“截止”位置*/
    private ArrayList<IntIntPair> rltChangeIdxes = null;
    /** 每次转换的对应序列长度变换大小 */
    private int[] allChangeSizes = null;
    
    public TextTranser (String origin) {
        this.originStr = origin;
    }
    
    public void addTransInfo(int from, int to, String replaceStr) throws NotSupportedException{
        if(originChangeIdxes == null){
            originChangeIdxes = new ArrayList<IntIntPair>();
            replaceStrs = new ArrayList<String>();
        }
        
        if(from < 0 || from > originStr.length() || to < 0 || to > originStr.length() || 
           replaceStr == null){
           throw new NotSupportedException("Regex ChangeMsg Error [%d]-[%d],RepStr[%s]",
                   from, to, replaceStr); 
        }
        
        originChangeIdxes.add(new IntIntPair(from, to));
        replaceStrs.add(replaceStr);
    }
    
    public String getRltStr(){
        if(rltStr == null){
            rltStr = transOriginToRlt();
        }
        return rltStr;
    }

    /**
     * 
     * 根据记录的位置信息list<start, end> 和需转换的list<Str>
     * 转换为最终的文本
     * 
     * @return
     */
    private String transOriginToRlt() {
        String newText = originStr;
        rltChangeIdxes = new ArrayList<IntIntPair>(originChangeIdxes.size());
        allChangeSizes = new int[originChangeIdxes.size()];

        int changeSize = 0;
        for(int i =0 ; i<originChangeIdxes.size(); i++){
            IntIntPair changePair = originChangeIdxes.get(i);
            String tmpText = newText;
            
            int start = changePair.first + changeSize;
            int end = changePair.second + changeSize;
            String repStr = replaceStrs.get(i);
            
            assert(start >= 0 && start < newText.length() && end>=0 && end<=newText.length());
            String startStr = newText.substring(0, start);
            String endStr = newText.substring(end);
            
            newText = String.format("%s%s%s", startStr, repStr, endStr);

            changeSize += repStr.length() - (end - start);
            rltChangeIdxes.add(new IntIntPair(startStr.length(), startStr.length() + repStr.length()));
            allChangeSizes[i] = newText.length() - tmpText.length();
        }
        
        rltStr = newText;
        return rltStr;
    }
    
    /**
     * 找到被替换文本中idx位置在原文中的位置
     * @param repIdx 被替换文本位置（实际位置索引为repIdx-1）
     * @return
     * @throws UnexpectedException 
     */
    public int findRepStrIdxInOriginStr(int repIdx) throws Exception{
        if(repIdx == 0 ) { return repIdx; }
        
        if(rltChangeIdxes == null || allChangeSizes == null){
            if(replaceStrs == null || originChangeIdxes == null){
                return repIdx;
            }
            transOriginToRlt();
        }
        if(repIdx == rltStr.length()) { return originStr.length();}
        
        if(repIdx < 0 || repIdx > rltStr.length() + 1){
            throw new Exception(String.format("changeIdx[%d] outOfSize", repIdx));
        }
        
        int rltIdx = repIdx;
        for(int i = 0; i < rltChangeIdxes.size(); i++){
            IntIntPair chPair = rltChangeIdxes.get(i);
            if(repIdx < chPair.first) { 
                return rltIdx; 
            } else if(repIdx == chPair.first) {
                return originChangeIdxes.get(i).first;
            } else if(repIdx == chPair.second){
                return originChangeIdxes.get(i).second;
            } else if(repIdx > chPair.second){
                rltIdx = rltIdx - allChangeSizes[i];
            } else {
                throw new Exception(String.format("cant find idx[%d] in originText", repIdx));
            }
        }
        return rltIdx;        
    }    
    
    /**
     * 
     * 找到经过一系列变化的文本的idx位置在初始文本中的位置。
     * 
     * @param idx 需要查找的词语尾部节点（实际位置索引为idx-1）
     * @param transers 转换序列
     * @param isInvertOrder 转换序列之间，是否为正序
     * @return
     * @throws UnexpectedException 
     * @throws RoolbackException 
     */
    public static int findRepWordIdxInOriginByTransers(
            int idx, ArrayList<TextTranser> transers, boolean isPositOrder)
    throws Exception{
        int rlt = idx;
        ArrayList<TextTranser> newTransers = (ArrayList<TextTranser>) transers.clone();
        
        if(isPositOrder){
            Collections.reverse(newTransers);
        }
        for(int i = 0; i< newTransers.size(); i++){
            if(rlt<0) { break; }
            rlt = newTransers.get(i).findRepStrIdxInOriginStr(rlt);
        }
        return rlt;
    }
}
