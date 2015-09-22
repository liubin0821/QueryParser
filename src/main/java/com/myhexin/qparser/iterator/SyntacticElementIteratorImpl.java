package com.myhexin.qparser.iterator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.myhexin.qparser.define.EnumDef.NodeType;
import com.myhexin.qparser.node.BoundaryNode;
import com.myhexin.qparser.node.SemanticNode;
import com.myhexin.qparser.phrase.PhraseInfo;
import com.myhexin.qparser.phrase.SyntacticSemantic.Syntactic.SyntacticPattern;

public class SyntacticElementIteratorImpl implements Iterator<List<SemanticNode>>{
	
	public static org.slf4j.Logger logger_ = org.slf4j.LoggerFactory.getLogger(SyntacticElementIteratorImpl.class.getName());
	private List<SemanticNode> semanticNodes;
	private int cursorPos = -1;
	private int boundNodePos = -1;//boundaryStart位置
	private ArrayList<ArrayList<Integer>> eleList ;
	private int size = 0;

	public SyntacticElementIteratorImpl(List<SemanticNode> semanticNodes,int i) {
		if(semanticNodes == null || semanticNodes.get(i).type != NodeType.BOUNDARY){
			logger_.error("格式错误,semanticNodes不为空, i对应BOUNDARY开始节点");
			return ;
		}
		
		BoundaryNode bn = (BoundaryNode) semanticNodes.get(i);
		boundNodePos = i;
		if (!bn.isStart()) {
			logger_.error("格式错误,semanticNodes不为空, i对应BOUNDARY开始节点");
			return ;
		}
		
		this.semanticNodes = semanticNodes;
		
		eleList = bn.extInfo.elementNodePostList;
		if (eleList == null) {
			logger_.error("句式元素为空");
			return ;
		}
		size = eleList.size();
		cursorPos = 0;

	}

	public boolean hasNext() {
		return cursorPos<size;
	}

	
	public List<SemanticNode> next() {
		List<SemanticNode> list = new ArrayList<SemanticNode>();
		for (Integer nodePos : eleList.get(cursorPos++)) {
			if(nodePos!=-1)
				list.add(semanticNodes.get(boundNodePos+nodePos));
			else{ //默认指标
				BoundaryNode bn = (BoundaryNode) semanticNodes.get(boundNodePos);
				list.add(bn.extInfo.absentDefalutIndexMap.get(cursorPos));
			}
				
		}
		return list;
	}
	
	




	@Override
	public void remove() {
		// TODO Auto-generated method stub

	}
}
