package com.myhexin.qparser.phrase.parsePlugins;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.myhexin.qparser.iterator.BoundaryInfos;
import com.myhexin.qparser.iterator.SyntacticIteratorImpl;
import com.myhexin.qparser.node.BoundaryNode;
import com.myhexin.qparser.node.FocusNode;
import com.myhexin.qparser.node.SemanticNode;
import com.myhexin.qparser.phrase.ParserAnnotation;
import com.myhexin.qparser.phrase.SyntacticSemantic.Semantic.SemanticArgument.SemanArgType;
import com.myhexin.qparser.phrase.SyntacticSemantic.Semantic.SemanticBind;
import com.myhexin.qparser.phrase.SyntacticSemantic.Semantic.SemanticBindTo;
import com.myhexin.qparser.phrase.SyntacticSemantic.Semantic.SemanticBindToArgument;
import com.myhexin.qparser.phrase.SyntacticSemantic.Semantic.SemanticBindToArgument.BindToType;
import com.myhexin.qparser.phrase.SyntacticSemantic.Semantic.SemanticBindToArgument.Source;
import com.myhexin.qparser.phrase.SyntacticSemantic.Syntactic.SyntacticPattern;
import com.myhexin.qparser.util.condition.ConditionBuilderUtil;

/**
 * 连接句式与句式之间的关系 
 * 第一个需求:行业排名需求 
 * (1)证券行业涨跌幅排名前10
 * (2)证券行业排名前10 
 * (3)行业涨跌幅排名前10
 * (4)行业排名前10
 * 
 * 处理行业+排名2个句式之间的关系
 * 
 * 
 * 
 */
public class PhraseParserPluginConditionHangYeSort extends
		PhraseParserPluginAbstract {

	public static org.slf4j.Logger logger_ = org.slf4j.LoggerFactory.getLogger(PhraseParserPluginConditionHangYeSort.class.getSimpleName());

	public PhraseParserPluginConditionHangYeSort() {
		super("Connect_Syntactic");
	}

	public ArrayList<ArrayList<SemanticNode>> process(ParserAnnotation annotation) {
		ArrayList<SemanticNode> nodes = annotation.getNodes();
		connectSyntactic(nodes);
		
		ArrayList<ArrayList<SemanticNode>> qlist = new ArrayList<ArrayList<SemanticNode>>(1);
		qlist.add(nodes);
		return qlist;
	}

	
	
	/**
	 * 做句式和句式之间的绑定
	 * 比如:行业句式绑定到排名上去
	 * 
	 * 
	 * @param nodes
	 * @return
	 */
	private void connectSyntactic(ArrayList<SemanticNode> nodes) {
		// 遍历句式
		Iterator<BoundaryInfos> iterator = new SyntacticIteratorImpl(nodes);
		List<BoundaryInfos> infos = new ArrayList<BoundaryInfos>();
		while (iterator.hasNext()) {
			BoundaryInfos boundaryInfos = iterator.next();
			infos.add(boundaryInfos);
		}
		
		if(infos.size()>1) {
			for(int i=0;i<infos.size();i++) {
				BoundaryInfos parent_boundaryInfos = infos.get(i);
				//BoundaryNode boundaryNode = (BoundaryNode) nodes.get(parent_boundaryInfos.bStart);
				if(hasSyntacticPatternBindInfo(parent_boundaryInfos)==false) {
					continue;
				}
				//只检查相邻句式
				int l = i-1;
				if(l>=0) {
					BoundaryInfos child_boundaryInfos = infos.get(l);
					if(checkAndConnectSyntactic(nodes, parent_boundaryInfos, child_boundaryInfos)==false) {
						int r = i+1;
						if(r<infos.size()) {
							child_boundaryInfos = infos.get(r);
							checkAndConnectSyntactic(nodes, parent_boundaryInfos, child_boundaryInfos);
						}
					}
				}
			}
		}
	}

	
	
	private boolean hasSyntacticPatternBindInfo(BoundaryInfos boundaryInfos) {
		SyntacticPattern pNode = boundaryInfos.getSyntacticPattern();
		if(pNode==null) return false;
		String desc = pNode.getDescription();
		if(desc==null) return false;
		
		if(desc.contains("排名") 
				|| desc.contains("排名前") || desc.contains("排名后")
				|| desc.contains("最大") || desc.contains("最小")
				|| desc.contains("最低") || desc.contains("最高")
				|| desc.contains("前") || desc.contains("后")
				|| desc.contains("排行")) {
			
			return true;
		}else{
			return false;
		}
	}
	
	/**
	 * 绑定句式
	 * 
	 * 
	 * @param nodes
	 * @param pbNode
	 * @param cbNode
	 */
	private boolean checkAndConnectSyntactic(ArrayList<SemanticNode> nodes,BoundaryInfos parent_boundaryInfos, BoundaryInfos child_boundaryInfos) {
		BoundaryNode pbNode = (BoundaryNode)nodes.get(parent_boundaryInfos.bStart);
		if(hasSyntacticPatternBindInfo(parent_boundaryInfos)==false) {
			return false;
		}
		
	
		//再子句式和指标
			BoundaryNode cbNode = (BoundaryNode)nodes.get(child_boundaryInfos.bStart);
			String c_patternId = cbNode.getSyntacticPatternId();
			if(c_patternId==null) {
				return false;
			}
			
			
			if(c_patternId.equals("FREE_VAR")) {
				SemanticNode node = ConditionBuilderUtil.getNodeFromFreeVar(nodes, child_boundaryInfos);
				if(node!=null && node.isFocusNode()  ) {
					FocusNode fNode = (FocusNode) node;
					if(fNode.hasIndex() && fNode.getIndex()!=null && ("所属同花顺行业".equals(fNode.getIndex().getText()) || "所属申万行业".equals(fNode.getIndex().getText())) ) {
				    	pbNode.setBindBoundaryInfos(child_boundaryInfos);
				    	cbNode.setBindtoSyntactic(true);
				    	return true;
					}
				}
			}else if(c_patternId.equals("KEY_VALUE")) {
				BoundaryNode bNode = (BoundaryNode)nodes.get(child_boundaryInfos.bStart);
				SemanticNode node =  ConditionBuilderUtil.getNodeKeyValue(1, bNode, child_boundaryInfos, nodes);
				if(node!=null && node.isFocusNode()  ) {
			   		FocusNode fNode = (FocusNode) node;
			   		if(fNode.hasIndex() && fNode.getIndex()!=null && ("所属同花顺行业".equals(fNode.getIndex().getText()) || "所属申万行业".equals(fNode.getIndex().getText())) ) {
				    	pbNode.setBindBoundaryInfos(child_boundaryInfos);
				    	cbNode.setBindtoSyntactic(true);
				    	return true;
				    }
				}
			}else if(c_patternId.equals("STR_INSTANCE")) {
				SemanticNode node = ConditionBuilderUtil.getNodeFromFreeVar(nodes, child_boundaryInfos);
				if(node!=null && node.isFocusNode()  ) {
					FocusNode fNode = (FocusNode) node;
					if(fNode.hasIndex() && fNode.getIndex()!=null && ("所属同花顺行业".equals(fNode.getIndex().getText()) || "所属申万行业".equals(fNode.getIndex().getText())) ) {
				    	pbNode.setBindBoundaryInfos(child_boundaryInfos);
				    	cbNode.setBindtoSyntactic(true);
				    	return true;
					}
				}
			}else{
				//所属同花顺行业包含证券,近利润排名前10
				SyntacticPattern syntPattern = child_boundaryInfos.getSyntacticPattern();
				if(syntPattern!=null && syntPattern.getDescription()!=null && (syntPattern.getDescription().contains("不包含")==false &&  syntPattern.getDescription().contains("包含"))) {
					SemanticBind semanticBind = syntPattern.getSemanticBind();
					FocusNode fNode = null;
					if(semanticBind!=null && semanticBind.getSemanticBindTos()!=null){
						ArrayList<SemanticBindTo> bindToList = semanticBind.getSemanticBindTos();
						for (SemanticBindTo semanticBindTo : bindToList) {
							ArrayList<SemanticBindToArgument> args = semanticBindTo.getSemanticBindToArguments();
							for (SemanticBindToArgument arg : args) {
								if (arg.getType() == SemanArgType.INDEX || arg.getType() == SemanArgType.INDEXLIST) { //是指标,创建指标condition
									if(arg.getBindToType() == BindToType.SYNTACTIC_ELEMENT)
									{
										fNode =  (FocusNode) getElement(arg, nodes, child_boundaryInfos.bStart);
										if(fNode!=null) break;
									}
								}
							}
						}
					}
					
					
					if(fNode!=null && fNode.hasIndex() && fNode.getIndex()!=null && ("所属同花顺行业".equals(fNode.getIndex().getText()) || "所属申万行业".equals(fNode.getIndex().getText())) ) {
				    	pbNode.setBindBoundaryInfos(child_boundaryInfos);
				    	cbNode.setBindtoSyntactic(true);
				    	return true;
					}
				}
			}
			
		return false;
	}
	
	
	/*
	 * TODO 无耻的从ConditionBuilderAbstract拷贝了如下2个方法
	 * getElement
	 * getElementByPos
	 * 
	 * TODO 应该写一套Helper类, 能够方便的根据参数从句式语义中得到Node
	 * 
	 */
	protected SemanticNode getElement(SemanticBindToArgument arg, List<SemanticNode> nodes,int bStart) {
		SemanticNode node = null;
		int elemId = arg.getElementId();

		if(arg.getType() == SemanArgType.INDEX) {
			node = getElementByPos(arg, nodes, bStart, elemId);
		}
		
		return node;
	}
	
	protected SemanticNode getElementByPos(SemanticBindToArgument argument, List<SemanticNode> nodes, int bStart,
			int elementId) {
		BoundaryNode bNode = (BoundaryNode) nodes.get(bStart);
		SemanticNode node = null;
		if (argument.getSource() == Source.FIXED && bNode.extInfo != null && bNode.extInfo.fixedArgumentMap != null) {
			node = bNode.extInfo.fixedArgumentMap.get(elementId);
		} else if (bNode.extInfo != null) {
			int newIndexId = bNode.extInfo.getElementNodePos(elementId);

			if (newIndexId == -1 && bNode.extInfo != null && bNode.extInfo.absentDefalutIndexMap != null) {
				node = bNode.extInfo.absentDefalutIndexMap.get(elementId);
			} else {
				newIndexId = bStart + newIndexId;
				node = nodes.get(newIndexId);
			}
		}

		return node;
	}
}
