package com.myhexin.qparser.tokenize;

import java.util.List;
import java.util.Map.Entry;

import com.myhexin.qparser.Query;
import com.myhexin.qparser.ApplicationContext.ApplicationContextHelper;
import com.myhexin.qparser.chunk.ClassifyChunkQuery;
import com.myhexin.qparser.phrase.PhraseParser;

public class ChunkQueryTest {
	
	public static void main(String[] args) {
		String queryStr = "董事长易峥 pe>10";
        ClassifyChunkQuery chunkFacade_=new ClassifyChunkQuery();
        List<Entry<String, Double>> rlt = chunkFacade_.createChunkKVRlt(queryStr, Query.Type.STOCK, false);
        // 遍历chunk划分结果
        for (int i = 0; i < rlt.size(); i++) {
        	Entry<String, Double> rltEntry = rlt.get(i);
        	System.out.print("chunk rlt " + i + ": ");
        	System.out.print(rltEntry.getKey() + ": ");
        	System.out.println(rltEntry.getValue());
        }
        // 取解析中应用的分数最高的chunk划分结果
        Entry<String, Double> rltEntry = rlt.get(0);
        String chunkRtn = rltEntry.getKey();
        Double chunkScore = rltEntry.getValue();
        String chunkLog = String.format("%s CHUNK=> %s\nCHUNK_SCORE=%.2f", queryStr, chunkRtn, chunkScore);
        System.out.println(queryStr);
        System.out.println(chunkRtn);
        System.out.println(chunkScore);
        System.out.println(chunkLog);
	}
	
	public static PhraseParser parser;// = new PhraseParser("./conf/qparser.conf", "./data");
	static {
		// 加载xml文件，初始spring容器
		ApplicationContextHelper.loadApplicationContext();
		
		// phraseParser配置于qphrase.xml文件
		parser = (PhraseParser) ApplicationContextHelper.getBean("phraseParser");
	}
}
