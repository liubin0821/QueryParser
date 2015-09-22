package com.myhexin.qparser.phrase.parsePlugins;

import java.util.ArrayList;

import com.myhexin.qparser.node.FocusNode;
import com.myhexin.qparser.node.NodeUtil;
import com.myhexin.qparser.node.SemanticNode;
import com.myhexin.qparser.phrase.ParserAnnotation;
import com.myhexin.qparser.resource.model.MultilineTechopInfo;
import com.myhexin.qparser.resource.model.TechIndexParentChildInfo;


/**
 * 对于多线指标,如果出现多个多线指标,并且只有一个形态的,对每个多线指标,补全它的形态
 * 1. macd, kdj金叉 => macd金叉,kdj金叉
 * 2. macd低位金叉顶背离 =>macd低位金叉,macd顶背离
 * 
 * 
 * @author 刘小峰 liuxiaofeng@myhexin.com
 * @Version 创建时间 2015-5-6
 *
 */
public class PhraseParserPluginMultilineTechopAppend extends PhraseParserPluginAbstract{
	public static org.slf4j.Logger logger_ = org.slf4j.LoggerFactory.getLogger(PhraseParserPluginMultilineTechopAppend.class.getName());

	
	public PhraseParserPluginMultilineTechopAppend() {
        super("Multiline_Techop_Append");
    }
	
	
	@Override
	public ArrayList<ArrayList<SemanticNode>> process(ParserAnnotation annotation) {
		ArrayList<SemanticNode> nodes = annotation.getNodes();
		processAddOp(nodes);
		processAddMultiLineIndex(nodes);
		ArrayList<ArrayList<SemanticNode>> tlist = new ArrayList<ArrayList<SemanticNode>>(1);
		tlist.add(nodes);
		return tlist;
	}
	
	/**
	 * 逻辑很简单
	 * 如果有多个多线指标,并且指标的右边有一个技术形态的, 
	 * 那么就在这个多线指标右边加一个这个技术形态的节点
	 * macd, kdj金叉 => macd金叉, kdj金叉
	 * 
	 */
	public void processAddOp(ArrayList<SemanticNode> nodes) {
		for(int i=0;i<nodes.size();i++) {
			SemanticNode node = nodes.get(i);
			
			//是focusNode 并且是多线指标
			if(node.isFocusNode() && MultilineTechopInfo.getInstance().isMultilineIndex((FocusNode)node) ) {
				
				//检查下一个节点是不是技术形态
				if(i+1 < nodes.size()) {
					SemanticNode next_node = nodes.get(i+1);
					if(next_node.isFocusNode() && MultilineTechopInfo.getInstance().isTechOpName((FocusNode)next_node) ) {
						//如果是的, 跳过这两个节点
						i++;
						continue;
					}
					
					int multilineTechIndexCount = 0;
					
					//如果下一个节点不是技术形态,那么往后面找,看能不能找到匹配的技术形态
					for(int j=i+1; j<nodes.size();j++) {
						next_node = nodes.get(j);
						if(next_node.isFocusNode()) {
							FocusNode fNode = (FocusNode) next_node;
							//又找到一个多线技术指标
							if(MultilineTechopInfo.getInstance().isMultilineIndex(fNode) ) {
								multilineTechIndexCount ++;
								continue;
							}
							
							//是技术形态, 并且中间隔了一个多线指标
							if(fNode.keywordCount>0 && multilineTechIndexCount>0
									&& MultilineTechopInfo.getInstance().isTechOpName(fNode) 
									&& MultilineTechopInfo.getInstance().isIndexOpMatch((FocusNode)node, fNode) ) {
								//都匹配上了,那么添加一个节点
								SemanticNode newNode = NodeUtil.copyNode(fNode);
								nodes.add(i+1,newNode);
								i++;
								break;
							}
							
							//其他跳出条件
							//1.有其他关键字
							if(fNode.keywordCount>0) {
								break;
							}
							
							//如果有子线
							if(TechIndexParentChildInfo.getInstance().isParentChildMatch((FocusNode) node, fNode)) {
								break;
							}
						}
					}
				}
			}
		}
	}
	
	
	/**
	 * macd低位金叉顶背离 =>macd低位金叉,macd顶背离
	 * 
	 */
	public void processAddMultiLineIndex(ArrayList<SemanticNode> nodes) {
		for(int i=0;i<nodes.size();i++) {
			SemanticNode node = nodes.get(i);
			
			if(node.isFocusNode() && MultilineTechopInfo.getInstance().isTechOpName((FocusNode)node) ) {
				if(i>0) {
					int prevOpCount = 0;
					FocusNode techIndexNode = null;
					for(int j=i-1;j>=0;j--) {
						SemanticNode prev_node = nodes.get(j);
						if(j<i-1 && prevOpCount>0 && prev_node.isFocusNode() && MultilineTechopInfo.getInstance().isMultilineIndex((FocusNode)prev_node) ) {
							techIndexNode = (FocusNode)prev_node;
						}else if(prev_node.isFocusNode() && MultilineTechopInfo.getInstance().isTechOpName((FocusNode)prev_node) ) {
							prevOpCount++;
						}else{
							//其他情况都跳出
							break;
						}
					}
					
					if(techIndexNode!=null && prevOpCount>0) {
						SemanticNode newNode = NodeUtil.copyNode(techIndexNode);
						nodes.add(i,newNode);
						i++;
					}
				}
				
			}
		}	
	}
	
}
