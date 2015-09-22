package com.myhexin.qparser.phrase.parsePlugins;

import java.util.ArrayList;

import com.myhexin.qparser.Query;
import com.myhexin.qparser.define.EnumDef.NodeType;
import com.myhexin.qparser.node.Environment;
import com.myhexin.qparser.node.FocusNode;
import com.myhexin.qparser.node.FocusNode.FocusItem;
import com.myhexin.qparser.node.FocusNode.Type;
import com.myhexin.qparser.node.SemanticNode;
import com.myhexin.qparser.node.StrNode;
import com.myhexin.qparser.onto.ClassNodeFacade;
import com.myhexin.qparser.phrase.ParserAnnotation;

/**
 * 保留某领域指标等信息
 * 根据问句指定的领域: ALL,STOCK,FUND,FUND,HGHY,SEARCH
 * 删除那些领域不是某一种领域的指标或别名
 * 
 * 
 */
public class PhraseParserPluginRemainSomeDomain extends PhraseParserPluginAbstract {
	public static org.slf4j.Logger logger_ = org.slf4j.LoggerFactory.getLogger(PhraseParserPluginRemainSomeDomain.class.getName());
	
	public PhraseParserPluginRemainSomeDomain() {
		super("Remove_Some_Domain");
	}

	public ArrayList<ArrayList<SemanticNode>> process(ParserAnnotation annotation) {
		
		Environment ENV = annotation.getEnv();
		ArrayList<SemanticNode> nodes = annotation.getNodes();
		return remainSomeDomain(nodes, ENV);
	}

	/**
	 * @author: 	    吴永行 
	 * @dateTime:	  2013-10-28 下午6:59:12
	 * @description:   	
	 * @param nodes
	 * @return
	 * 
	 */
	private ArrayList<ArrayList<SemanticNode>> remainSomeDomain(ArrayList<SemanticNode> nodes, Environment ENV) {
		if (nodes == null || nodes.size() == 0)
			return null;
		ArrayList<ArrayList<SemanticNode>> rlist = new ArrayList<ArrayList<SemanticNode>>(1);
		for (int i=0; i < nodes.size(); i++) {
			SemanticNode sn = nodes.get(i);
			if (sn.type == NodeType.FOCUS) {
				FocusNode fn = (FocusNode) sn;
				ArrayList<FocusNode.FocusItem> itemList = fn.getFocusItemList();
                int k = 0;
                int size = itemList.size();
                while (k < size) {
                    FocusNode.FocusItem fi = itemList.get(k);
					if ((fi.getType() == Type.INDEX && remainSomeDomain(fi.getIndex(), ENV) == false)
							|| (fi.getType() == Type.STRING && remainSomeDomain(fi.getStr() ) == false)) {
						fn.removeFocusItem(fi.getType(), fi.getContent(), fi);
						k--;
                        size--;
					}
					k++;
				}
				if (fn.hasIndex() && remainSomeDomain(fn.getIndex(), ENV) == false) {
					for (FocusItem fi : fn.focusList)
						if (fi.getType() == Type.INDEX)
							fn.setIndex(fi.getIndex());
				} else if (!fn.hasIndex()) {
					fn.setIndex(null);
				}
				if (fn.hasString() && remainSomeDomain(fn.getString())) {
					;
				} else if (!fn.hasString()) {

				}
			}
		}
		rlist.add(nodes);
		return rlist;
	}

	private boolean remainSomeDomain(ClassNodeFacade index, Environment ENV) {
		return index!=null && index.remainDomain((Query.Type) ENV.get("qType",false));
	}
	
	private boolean remainSomeDomain(StrNode string) {
		return true;
	}
}
