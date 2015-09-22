package com.myhexin.qparser.phrase;

import java.util.HashSet;
import java.util.List;

import com.myhexin.DB.mybatis.MybatisHelp;
import com.myhexin.qparser.ApplicationContext.ApplicationContextHelper;
import com.myhexin.qparser.ApplicationContext.BeanNameConsts;

public class StopWords {
	public static HashSet<String> stopWords = null;
	
	public static void init() {
		stopWords = new HashSet<String>();
	}
	
	public static void reload(HashSet<String> stopWordsTemp) {
		stopWords = stopWordsTemp;
	}
	
	// 加载词典
    public static void loadStopWords(List<String> lines) {
    	stopWords = loadDict(lines);
    }
    
    public static HashSet<String> loadDict(List<String> lines) {
    	HashSet<String> stopWords = new HashSet<String>();
        for (int iLine = 0; iLine < lines.size(); iLine++) {
            String word = lines.get(iLine).trim();
            stopWords.add(word);
        }
        return stopWords;
    }
    
    //加载-从数据库
    public static void loadStopWords(){
    	stopWords = loadDict();
    }
    
    public static HashSet<String> loadDict(){
    	HashSet<String> stopWords = new HashSet<String>();
    	MybatisHelp mybatisHelp = ApplicationContextHelper.getBean(BeanNameConsts.MYBATIS_HELP);
    	List<com.myhexin.DB.mybatis.mode.StopWords> stopWordsList = mybatisHelp.getStopWordsMapper().selectAll();
    	for(com.myhexin.DB.mybatis.mode.StopWords stopWordsMode:stopWordsList){
    		stopWords.add(stopWordsMode.getStopWords().trim());
    	}
    	return stopWords;
    }
}