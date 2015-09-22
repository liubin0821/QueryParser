package com.myhexin.qparser.phrase.parsePlugins;

import java.util.ArrayList;
import java.util.Map;

import com.myhexin.qparser.define.EnumDef.NodeType;
import com.myhexin.qparser.except.NotSupportedException;
import com.myhexin.qparser.node.BoundaryNode;
import com.myhexin.qparser.node.BoundaryNode.IMPLICIT_PATTERN;
import com.myhexin.qparser.node.Environment;
import com.myhexin.qparser.node.FocusNode;
import com.myhexin.qparser.node.SemanticNode;
import com.myhexin.qparser.phrase.ParserAnnotation;

public class PhraseParserPluginCompleteIndexOfIndexProp extends PhraseParserPluginAbstract {

	public PhraseParserPluginCompleteIndexOfIndexProp() {
		super("Complete_Index_Of_Index_Prop");
	}

	public ArrayList<ArrayList<SemanticNode>> process(ParserAnnotation annotation) {
		ArrayList<SemanticNode> nodes = annotation.getNodes();
		return completeIndexOfIndexProp(nodes);
	}

	/**
	 * 用于识别句中的周期节点
	 * 
	 * @param qlist
	 * @throws NotSupportedException
	 */
	@SuppressWarnings("unchecked")
    private ArrayList<ArrayList<SemanticNode>> completeIndexOfIndexProp(
	        ArrayList<SemanticNode> nodes) {
		ArrayList<ArrayList<SemanticNode>> qlist = new ArrayList<ArrayList<SemanticNode>>(1);
		qlist.add(nodes);
		
		if(nodes.get(0).type==NodeType.ENV){
			Environment listEnv = (Environment)nodes.get(0);
			
			Map<SemanticNode, FocusNode> completeMap ;
			completeMap=listEnv.get("completionIndexOfIndexProp",Map.class, true);;
			if(completeMap==null)
				return qlist;
			
			
			ArrayList<SemanticNode> addingNodes = new ArrayList<SemanticNode>();
			for (int i = 0; i < nodes.size(); i++) {
				SemanticNode sn = nodes.get(i);
				if (sn.type == NodeType.FOCUS) {
					FocusNode fn = (FocusNode) sn;
					if (!fn.isIndexNode())
						continue;
	
					//添加一个freeValue的句式
					if (completeMap.containsKey(fn) /*&& !fn.isBoundToIndex*/) {
						BoundaryNode boundary = new BoundaryNode();
						boundary.setType(BoundaryNode.START, IMPLICIT_PATTERN.FREE_VAR.toString());
						addingNodes.add(boundary);
						BoundaryNode.SyntacticPatternExtParseInfo extInfo = boundary.getSyntacticPatternExtParseInfo(true);
						extInfo.addElementNodePos(1, 1);
		                extInfo.presentArgumentCount++;
		                fn.isBoundToSynt = true;
		                FocusNode toAddNode = completeMap.get(fn);
						addingNodes.add(toAddNode);
						boundary = new BoundaryNode();
				        boundary.setType(BoundaryNode.END, IMPLICIT_PATTERN.FREE_VAR.toString());
				        addingNodes.add(boundary);
					}
				} else if (sn.type == NodeType.BOUNDARY) {
					BoundaryNode bn = (BoundaryNode) sn;
					if (bn.isEnd() && addingNodes.size()>0){
						nodes.addAll(i + 1, addingNodes);
						i+=addingNodes.size();
						addingNodes.clear();
						
					}
				}
			}
		}

		return qlist;
	}
	
	//把绑定信息,打印出来
    public void logResult(StringBuilder sb, ArrayList<ArrayList<SemanticNode>> qlist) {
        for(int i=0 ; i<qlist.size(); i++) {
            sb.append(String.format("[match %d]:<BR>%s\n", i, _nodesHtmlAllBindings(qlist.get(i)) ));
        }
    }

}
