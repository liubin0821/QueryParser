package com.myhexin.server;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import com.myhexin.qparser.Query;
import com.myhexin.qparser.ApplicationContext.ApplicationContextHelper;
import com.myhexin.qparser.node.SemanticNode;
import com.myhexin.qparser.phrase.parsePlugins.PhraseParserPluginAbstract;
import com.myhexin.qparser.tokenize.NoParserMatch;

public class ChunkParser {
	private static ParserPlugins parserPlugins;
	
    private static final Pattern PEOPLE_REG = Pattern.compile(".*(总经理|董事|独董|财务总监|董秘|监事|高管).*");
	
    static{
		// parserPluginsChunk配置于qparser_plugins_chunk.xml文件
		parserPlugins = (ParserPlugins) ApplicationContextHelper.getBean("parserPluginsChunk");
	}
    
	public static String parserQuery(String query) {
		String res = "";
        String[] chunks = query.trim().split("_&_");
        for(String chunk: chunks) {
            if(!isPeopleChunk(chunk)) {
                if(res.length() != 0)
                    res += "_&_";
                res += chunk;
                continue;
            }
            String chunkA = getChunk(chunk);
            if (NoParserMatch.noParserWellTrieTree.mpMatchingOne(chunkA))
            	chunkA = chunk;
            if(res.length() != 0)
                res += "_&_";
            res += chunkA;
        }
        return res;
	}
	
	private static String getChunk(String chunk) {
        Query queryInst = new Query(chunk);
        Parser.parser.parse(queryInst, parserPlugins.pre_plugins_, parserPlugins.plugins_, parserPlugins.post_plugins_);
        List<ArrayList<SemanticNode>> qlist = queryInst.getParseResult().qlist;
        if(qlist == null || qlist.size() != 1)
            return chunk;
        
        String standardStatement = PhraseParserPluginAbstract.getFromListEnv(qlist.get(0), "standardStatement",String.class, false);
        if(qlist.get(0).get(0).isExecutive && !standardStatement.contains("_&_")) 
        	return standardStatement;
        return chunk;
    }

    private static boolean isPeopleChunk(String chunk) {
        if(chunk.length() < 30) {
            if(PEOPLE_REG.matcher(chunk).matches()) {
                return true;
            }
        }
        return false;
    }
    
    public static void main(String[] args) {
    	System.out.println(getChunk("董事长属马"));
    	System.out.println(getChunk("董事长性别男"));
    	System.out.println(getChunk("董事长未婚"));
    	System.out.println(getChunk("董事长清华大学"));
    	System.out.println(getChunk("董事长2013年底任职"));
    	System.out.println(getChunk("董事长姓王"));
    	System.out.println(getChunk("董事长本科毕业"));
    }
}
