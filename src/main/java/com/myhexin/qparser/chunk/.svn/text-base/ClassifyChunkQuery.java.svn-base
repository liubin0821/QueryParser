package com.myhexin.qparser.chunk;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.AbstractMap.SimpleEntry;

import com.myhexin.qparser.Param;
import com.myhexin.qparser.Query;
import com.myhexin.qparser.chunk.crf.CRFChunker;

public class ClassifyChunkQuery {
    public static org.slf4j.Logger logger_ = org.slf4j.LoggerFactory.getLogger(ClassifyChunkQuery.class.getName());

    private CRFChunker crfChunker_;
    
    public static HashMap<String, String> PINYIN_TRANS_DICT = null ;
    public static ArrayList<String> CN_STOP_WORDS_LIST = null ; 
    
    public static final String FAILTAG = "[提取chunk失败]";
    public static final String FAIL = "[CHUNK-FAIL]";
    
    public static final String CHUNK_SPL = "_&_";//一种切分方法，不同chunk之间的分隔符
    public static final String CHUNKS_SPL = "_OR_";//一个问句，不同切分方法之间的分隔符
    
    public ClassifyChunkQuery(){
        crfChunker_ = new CRFChunker();
    }

	/**
     * 返回经过Chunk划分的原句，不附加pattern信息
     * @param rawQueryStr ：待切分句子
     * @param type ：领域类型
     * @param retMul ：是否返回多种切分结果 true：如果有多种切分可能，返回多种，否则，返回最可能的一种
     * @return
     */
    public String createNaturalLanguageChunk(
            String rawQueryStr, 
            Query.Type type,
            boolean retMul){
        List<Entry<String, Double>> rlts = createChunkKVRlt(rawQueryStr, type, retMul);
        String chunkRlt = createChunkStrRlt( rlts, retMul );
        return chunkRlt;
    }
    
    /**
     * 切分问句，返回Chunk切分结果和分数。<br>
     * @param rawQueryStr ：待切分问句
     * @param type ：问句类型
     * @param retMul ： 是否返回多个结果
     * @return：Key：问句，Val：对应scroe
     */
    public List<Entry<String, Double>> createChunkKVRlt(
            String rawQueryStr, 
            Query.Type type,
            boolean retMul){
        List<Entry<String, Double>> rlts = null;
        if( "crf".equals(Param.CHUNK_SWITCH) ){
            rlts = createCRFNaturalLanguageChunk(rawQueryStr); 
        }
        
        if( rlts == null || rlts.isEmpty() ){
            rlts = new ArrayList<Entry<String, Double>>();
            rlts.add(new SimpleEntry<String, Double>(rawQueryStr,1.0));
        }
        return rlts;
    }
    
    //将所有的切分结果合并成最终的字符串输出
    private String createChunkStrRlt( List<Entry<String, Double>> rlts, boolean retMul ){
        StringBuilder sb = new StringBuilder();
        Entry<String,Double> kv = null;
        for( int i=0; i<rlts.size(); i++ ){
            kv = rlts.get(i);
            sb.append(kv.getKey());
            if( !retMul ){
                break;
            }
            if( i+1<rlts.size() ){
                sb.append(CHUNKS_SPL);
            }
        }
        String rlt = sb.toString();
        return rlt;
    }
    
    //调用crfChunker来进行切分
    private List<Entry<String, Double>> createCRFNaturalLanguageChunk(String rawQueryStr){
        List<Entry<String, Double>> crfRlts = crfChunker_.run(rawQueryStr);
        return crfRlts;
    }
    
    public static void main(String [] args) {
    	String queryStr = "董事长易峥 pe>10";
        ClassifyChunkQuery chunkFacade_=new ClassifyChunkQuery();
        List<Entry<String, Double>> rlt = chunkFacade_.createChunkKVRlt(queryStr, Query.Type.STOCK, false);
        
        Entry<String, Double> rltEntry = rlt.get(0);
        String chunkRtn = rltEntry.getKey();
        Double chunkScore = rltEntry.getValue();
        String chunkLog = String.format("%s CHUNK=> %s\nCHUNK_SCORE=%.2f", queryStr, chunkRtn, chunkScore);
        //System.out.println(queryStr);
        //System.out.println(chunkRtn);
        //System.out.println(chunkScore);
        //System.out.println(chunkLog);
    }
}
