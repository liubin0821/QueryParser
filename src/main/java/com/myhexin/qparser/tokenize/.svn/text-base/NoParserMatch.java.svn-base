package com.myhexin.qparser.tokenize;

import java.util.HashSet;
import java.util.List;

import com.myhexin.DB.mybatis.MybatisHelp;
import com.myhexin.DB.mybatis.mode.NoParser;
import com.myhexin.DB.mybatis.mode.NoParserWell;
import com.myhexin.qparser.ApplicationContext.ApplicationContextHelper;
import com.myhexin.qparser.ApplicationContext.BeanNameConsts;
import com.myhexin.qparser.trietree.TrieTree;

/**
 * 单词前缀树类
 * 包含用单词前缀树解决多串匹配问题的算法
 * @author linpeikun
 *
 */
public class NoParserMatch {
	public static TrieTree noParserTrieTree;
	public static TrieTree noParserWellTrieTree;
	
	public static void init() {
		noParserTrieTree = new TrieTree();
		noParserWellTrieTree = new TrieTree();
	}
	
	public static void reload(TrieTree noParserTrieTreeTemp, TrieTree noParserWellTrieTreeTemp) {
		noParserTrieTree = noParserTrieTreeTemp;
		noParserWellTrieTree = noParserWellTrieTreeTemp;
	}
	
    // 加载词典
    public static void loadDict(List<String> lines) {
        noParserTrieTree = loadTrieTree(lines);
    }
    
    // 加载词典
    public static void loadDictNoParser(List<String> lines) {
        noParserWellTrieTree = loadTrieTree(lines);
    }
    
    public static TrieTree loadTrieTree(List<String> lines) {
    	HashSet<String> dict = new HashSet<String>();
        for (int iLine = 0; iLine < lines.size(); iLine++) {
            String word = lines.get(iLine).trim();
            dict.add(word);
        }
        return new TrieTree(dict); 
    }
    
    //加载no_parser
    public static void loadNoParser(){
    	noParserTrieTree = loadTrieTreeOfNoParser();
    }
    
    public static TrieTree loadTrieTreeOfNoParser(){

		MybatisHelp mybatisHelp = ApplicationContextHelper.getBean(BeanNameConsts.MYBATIS_HELP);
    	List<NoParser> noParserList = mybatisHelp.getNoParserMapper().selectAll();
    	HashSet<String> dict = new HashSet<String>();
    	for(NoParser noParser:noParserList){
    		dict.add(noParser.getNoParser().trim());
    	}
    	return new TrieTree(dict);
    }
    
    //加载no_parser_well
    public static void loadNoParserWell(){
    	noParserWellTrieTree = loadTrieTreeOfNoParserWell();
    }
    
    public static TrieTree loadTrieTreeOfNoParserWell(){
    	MybatisHelp mybatisHelp = ApplicationContextHelper.getBean(BeanNameConsts.MYBATIS_HELP);
    	List<NoParserWell> noParserWellList = mybatisHelp.getNoParserWellMapper().selectAll();
    	HashSet<String> dict = new HashSet<String>();
    	for(NoParserWell noParserWell:noParserWellList){
    		dict.add(noParserWell.getNoParserWell().trim());
    	}
    	return new TrieTree(dict);
    }
}



