package com.myhexin.qparser.phrase.parsePlugins;

import java.util.ArrayList;

import com.myhexin.qparser.date.DateRange;
import com.myhexin.qparser.define.EnumDef.NodeType;
import com.myhexin.qparser.except.UnexpectedException;
import com.myhexin.qparser.node.DateNode;
import com.myhexin.qparser.node.Environment;
import com.myhexin.qparser.node.FocusNode;
import com.myhexin.qparser.node.NumNode;
import com.myhexin.qparser.node.SemanticNode;
import com.myhexin.qparser.number.NumRange;
import com.myhexin.qparser.phrase.ParserAnnotation;

public class PhraseParserPluginMatchIndexAndKeywords extends PhraseParserPluginAbstract{

	
	
    public PhraseParserPluginMatchIndexAndKeywords() {
        super("Match_Index_and_Keywords");
    }
    
	public ArrayList<ArrayList<SemanticNode>> process(ParserAnnotation annotation) {
		
		Environment ENV = annotation.getEnv();
		ArrayList<SemanticNode> nodes = annotation.getNodes();
		String ms_ltp = (String) ENV.get("ms_ltp", false); // 主搜索分词开关
		if (ms_ltp == null || !"1".equals(ms_ltp)) {
		  	return  matchIndexAndKeywords(nodes);
		} else {
			ArrayList<ArrayList<SemanticNode>> tlist = new ArrayList<ArrayList<SemanticNode>>(1);
			tlist.add(nodes);
			return tlist;
		}
    }

    private ArrayList<ArrayList<SemanticNode>> matchIndexAndKeywords(ArrayList<SemanticNode> nodes) {
        ArrayList<ArrayList<SemanticNode>> tlist = new ArrayList<ArrayList<SemanticNode>>(1);
        int size = nodes.size();
        for (int i = 0; i < size; i++) {			
            SemanticNode node = nodes.get(i);
            if (node.type == NodeType.ENV) {
            	continue;
            }else if (node.type == NodeType.NUM) {
                NumNode numNode = (NumNode) node;
                NumRange nr = numNode.getNuminfo();
                if (nr == null) {
                    continue; // 待处理，此时是否需要转化为UNKNOWN节点
                }
            } else if (node.type == NodeType.DATE) {
                DateNode dateNode = (DateNode) node;
                DateRange dr = dateNode.getDateinfo();
                if (dr == null) {
                    continue; // 待处理，此时是否需要转化为UNKNOWN节点
                }
            } else {
                try {
                    FocusNode focusNode = ParsePluginsUtil.getFocusNode(node.getPubText(), node);                 
                    if (focusNode != null) {
                        nodes.set(i, focusNode);
                    }
                } catch(UnexpectedException e) {
                    continue;
                }
            }
        }
        tlist.add(nodes);
        return tlist;
    }
}
