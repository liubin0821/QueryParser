package com.myhexin.qparser.phrase.parsePlugins;

import java.util.ArrayList;
import java.util.List;

import com.myhexin.qparser.define.EnumDef.NodeType;
import com.myhexin.qparser.node.Environment;
import com.myhexin.qparser.node.SemanticNode;
import com.myhexin.qparser.phrase.ParserAnnotation;
import com.myhexin.qparser.resource.model.UserDefineIndexInfo;


/**
 * 把指标转换成用于定于指标和值
 * 配置请看stock/stock_user_index.xml
 * 
 * 
 * @author 刘小峰 liuxiaofeng@myhexin.com
 * @Version 创建时间 2015-9-10
 *
 */
public class PhraseParserPluginUserIndex extends PhraseParserPluginAbstract {
	public static org.slf4j.Logger logger_ = org.slf4j.LoggerFactory.getLogger(PhraseParserPluginUserIndex.class.getName());


	public PhraseParserPluginUserIndex() {
		super("User_Index");
	}

	public ArrayList<ArrayList<SemanticNode>> process(ParserAnnotation annotation) {
		ArrayList<SemanticNode> nodes = annotation.getNodes();
		return processUserIndex(annotation.getEnv(),nodes);
	}

	private UserDefineIndexInfo instance = UserDefineIndexInfo.getInstance();
	private ArrayList<ArrayList<SemanticNode>> processUserIndex(Environment ENV,ArrayList<SemanticNode> nodes) {
		if (nodes == null || nodes.size() == 0)
			return null;
		
		//遍历指标
		ArrayList<SemanticNode> new_nodes = new ArrayList<SemanticNode>(nodes.size());
		for(SemanticNode node : nodes) {
			if(node.getType() == NodeType.ENV) {
				new_nodes.add(node);
				continue;
			}
			
			List<SemanticNode> sub_nodes = instance.getUserDefineIndex(node.getText(), null);
			if(sub_nodes!=null) {
				new_nodes.addAll(sub_nodes);
			}else{
				new_nodes.add(node);
			}
		}
		
		
		ArrayList<ArrayList<SemanticNode>> tlist = new ArrayList<ArrayList<SemanticNode>>(1);
		tlist.add(new_nodes);
		return tlist;
	}
}
