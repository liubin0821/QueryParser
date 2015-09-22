package com.myhexin.qparser.phrase.parsePlugins;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.myhexin.qparser.define.EnumDef.NodeType;
import com.myhexin.qparser.except.NotSupportedException;
import com.myhexin.qparser.except.UnexpectedException;
import com.myhexin.qparser.node.BoundaryNode;
import com.myhexin.qparser.node.DateNode;
import com.myhexin.qparser.node.FocusNode;
import com.myhexin.qparser.node.SemanticNode;
import com.myhexin.qparser.node.TechPeriodNode;
import com.myhexin.qparser.onto.ClassNodeFacade;
import com.myhexin.qparser.onto.PropNodeFacade;
import com.myhexin.qparser.phrase.ParserAnnotation;

public class PhraseParserPluginCompletionIndex extends
        PhraseParserPluginAbstract {
	@Autowired(required=true)
	private CompletionIndex1 completionIndex;
		
	public void setCompletionIndex(CompletionIndex1 completionIndex) {
    	this.completionIndex = completionIndex;
    }

	public PhraseParserPluginCompletionIndex() {
		super("Completion_Index");
	}

	public ArrayList<ArrayList<SemanticNode>> process(ParserAnnotation annotation) {
		return  completionIndex(annotation.getNodes());
	}

	/**
	 * 用于识别句中的周期节点
	 * 
	 * @param qlist
	 * @throws NotSupportedException
	 */
	private ArrayList<ArrayList<SemanticNode>> completionIndex(
	        ArrayList<SemanticNode> nodes) {
		ArrayList<ArrayList<SemanticNode>> qlist = new ArrayList<ArrayList<SemanticNode>>(1);
		try {
			completionIndex.completion(nodes);
		} catch (UnexpectedException e) {
		}
		qlist.add(nodes);
		return qlist;
	}
	
	//把绑定信息,打印出来
    public void logResult(StringBuilder sb, ArrayList<ArrayList<SemanticNode>> qlist) {
        for(int i=0 ; i<qlist.size(); i++) {
            sb.append(String.format("[match %d]:<BR>%s\n", i, _nodesHtmlAllBindings(qlist.get(i)) ));
        }
    }

}

@Component
class CompletionIndex1 {

	private static final String[] excludeBindForSpecialWords = {"股本","总股本","股票"};
	
	private static final String[] excludeBindForSpecialSyntactic= {"162","164"};
	
	// 这个isindex只是简单的判断
	private boolean isIndex(SemanticNode sn) {
		boolean rtn = false;
		if ((sn != null) && (sn.getType() == NodeType.FOCUS)) {
			FocusNode fn = (FocusNode) sn;
			if (fn.getIndex() != null) {
				rtn = true;
			}
		}
		return rtn;
	}
	

	/**
	 * 返回focus中的index，如果为null则说明此focus为keyword，如果不为null则为index
	 * 
	 * @author zd<zhangdong@myhexin.com>
	 * @param sn
	 * @return
	 */
	private ClassNodeFacade getIndex(SemanticNode sn) {
		ClassNodeFacade rtn = null;
		if (sn.getType() == NodeType.FOCUS) {
			rtn = ((FocusNode) sn).getIndex();
		}
		return rtn;
	}

	/**
	 * 单个指标补全
	 * @param unboundDate 
	 *
	 */
	private void completionIndexProps(ClassNodeFacade index,
	        HashMap<String, SemanticNode> bindedProps,
	        HashMap<String, SemanticNode> bindedPropsBoundary, 
	        boolean unboundDate) {
		if (index == null)
			return;
		List<PropNodeFacade> propList = index.getAllProps();
		if (propList == null)
			return;
		SemanticNode sn = null;
		for (PropNodeFacade pn : propList) {
			String type = pn.getText();
			if(unboundDate && isDateType(type)) {
				continue;
			}else {
				if (pn.getValue() != null) {
					// 已经绑定
					sn = pn.getValue();
					SemanticNode snGot = bindedPropsBoundary.get(type);
					if (snGot != null && !sn.getText().equals(snGot.getText()))
						bindedProps.put(type, null);
					else
						bindedProps.put(type, sn);
					bindedPropsBoundary.put(type, sn);
				} else {
					// 未绑定
					sn = bindedPropsBoundary.get(type);
					sn = sn == null ? bindedProps.get(type) : sn;
					//在报告期已绑定的情况下，区间也需要绑定
					if(sn == null && "+区间".equals(type) && bindedProps.get("报告期") != null) {
						sn = bindedProps.get("报告期");
					}else if(sn == null && "+区间".equals(type) && bindedProps.get("交易日期") != null) {
						sn = bindedProps.get("交易日期");
					}
					String boundIndexPropName = StringUtils.EMPTY;
					if(sn != null) {
						SemanticNode boundIndexProp = sn.getBoundToIndexProp();
						if(boundIndexProp instanceof PropNodeFacade) {
							boundIndexPropName = ((PropNodeFacade)boundIndexProp).getText();
						}
					}
					boolean isSameProp = isSameProp(boundIndexPropName, pn);
					if (sn != null && !hasExistDateProp(propList) && isSameProp && !StringUtils.contains(index.getText(), "股票")) {
						pn.setValue(sn);
				        sn.setIsBoundToIndex(true);
				        sn.setBoundToIndexProp(index, pn);
					}
				}
			}
		}
	}
	
	/**
	 * 
	 * @param boundIndexPropName
	 * @param pn
	 * @return
	 */
    private boolean isSameProp(String boundIndexPropName,
			PropNodeFacade pn) {
    	if(StringUtils.isEmpty(boundIndexPropName)) {
    		return true;
    	}else if( ("n日".equals(boundIndexPropName) || "分析周期".equals(boundIndexPropName) ) && StringUtils.equals(pn.getText(), boundIndexPropName) ) {
    		return true;
    	}else if( !"n日".equals(boundIndexPropName) && !"分析周期".equals(boundIndexPropName) &&  !"n日".equals(pn.getText()) && !"分析周期".equals(pn.getText())) {
    		return true;
    	}else {
    		return false;
    	}
	}

	/**
	 * 看看是否已经绑定了其他的日期或者周期属性，存在则不需要重复绑定了。
	 * 
	 * @author huangmin
	 *
	 * @param propList
	 * @return
	 */
	private boolean hasExistDateProp(List<PropNodeFacade> propList) {
		for (PropNodeFacade pn : propList) {
			SemanticNode snode = pn.getValue();
			if(snode != null && (snode instanceof DateNode || snode instanceof TechPeriodNode) && StringUtils.isNotEmpty(snode.getText()) && !"n日".equals(pn.getText())) {
				return true;
			}
		}
		
		return false;
	}


	/**
	 * 双向遍历list，以防止漏绑。
	 * 
	 * 指标补全
	 * 需要实现优先Boundary内补全
	 */
	private void completionProps(ArrayList<SemanticNode> list) {
		SemanticNode sn = null;
		HashMap<String, SemanticNode> bindedProps = new HashMap<String, SemanticNode>();
//		boolean isFirst = true;
//		int firstEnd = 0;
		for (int i =0;i < list.size();i++) {
			sn = list.get(i);
//			if(";".equals(sn.getText()) &&  isFirst) {
//				isFirst = false;
//				firstEnd = i;
//			}
			bindProps(sn, list, i, bindedProps);
		}
		
//		boolean hasCom = false;
//		for (int i = list.size() - 1; i >= 0; i--) {
//			sn = list.get(i);
//			if(sn.getType() == NodeType.BOUNDARY && ((BoundaryNode)sn).isStart() && checkSem(list,i) ) {
//				hasCom = true;
//			}
//			if(isFirstBoundary(firstEnd, list)== i && hasCom) { //反向遍历到第一个边界node
//				break;
//			}
//
//			bindProps(sn, list, i, bindedProps);
//		}
	}
	
	/**
	 * 查找两个边界node之间是否还有分号
	 * 
	 * @author huangmin
	 *
	 * @param list
	 * @param nodePos
	 * @return
	 */
	private boolean checkSem(ArrayList<SemanticNode> list,  int nodePos) {
		if(nodePos > 0) {
			for(int i = nodePos - 1; i >= 0; i--) {
				SemanticNode sem = list.get(i);
				if(sem.getType() != NodeType.BOUNDARY && ";".equals(sem.getText())) {
					return true;
				}else if(sem.getType() == NodeType.BOUNDARY) {
					break;
				}
			}
		}
		
		return false;		
	}
	
	/**
	 * 碰到一些特殊的指标，比如 股本，股票 之类的，后面的句式也不应该补绑日期。
	 * 
	 * @param list
	 * @param nodePos
	 * @return
	 */
	private boolean checkSpecialWords(ArrayList<SemanticNode> list, int nodePos) {
		if(nodePos > 0) {
			for(int i = nodePos - 1; i >= 0; i--) {
				SemanticNode sem = list.get(i);
				if(sem.getType() == NodeType.BOUNDARY && ((BoundaryNode)sem).isStart()) {
					break;
				}else if(ArrayUtils.contains(excludeBindForSpecialWords, sem.getText())) {
					return true;
				}
			}
		}
		
		return false;		
	}
	
	/**
	 * 碰到一些特殊的句式，比如句式id为162的，本身或者后面的句式也不应该补绑日期。
	 * 
	 * @param list
	 * @param nodePos
	 * @return
	 */
	private boolean checkSpecialSyntactic(ArrayList<SemanticNode> list, int nodePos) {
		if(nodePos > 0) {
			SemanticNode curSem = list.get(nodePos);
			BoundaryNode bNode =(BoundaryNode)curSem;
			if(ArrayUtils.contains(excludeBindForSpecialSyntactic, bNode.getSyntacticPatternId())) {
				return true;
			} else {	
				int bStartCount = 0;
				for(int i = nodePos - 1; i >= 0; i--) {
					SemanticNode sem = list.get(i);
					if(sem.getType() == NodeType.BOUNDARY && ((BoundaryNode)sem).isStart()) {
						bStartCount++;
						if(ArrayUtils.contains(excludeBindForSpecialSyntactic, ((BoundaryNode)sem).getSyntacticPatternId()) && bStartCount == 1) {
							return true;
						}
					}
				}
			}
		}
		
		return false;		
	}
	
	/**
	 * 
	 * @author huangmin
	 *
	 * @param sn
	 * @param list
	 * @param nodePos
	 * @param bindedProps
	 * @return
	 */
	private void bindProps(SemanticNode sn, ArrayList<SemanticNode> list, int nodePos,
			HashMap<String, SemanticNode> bindedProps) {
		boolean unboundDate = false;
		if (sn.getType() != NodeType.BOUNDARY ) {
			return;
		}
		if (sn.getType() == NodeType.BOUNDARY && ((BoundaryNode)sn).isStart() && checkSem(list,nodePos)) {
			//分号分隔的句式之间时间完全独立
		    Iterator<Map.Entry<String, SemanticNode>> it = bindedProps.entrySet().iterator();
	        while(it.hasNext()) {
	            Map.Entry<String, SemanticNode> entry = it.next();
	            String type=entry.getKey();
	            if(isDateType(type)){
	                it.remove();
	            }  
	        }
		}
		if (sn.getType() == NodeType.BOUNDARY && ((BoundaryNode)sn).isStart() && checkSpecialWords(list,nodePos)) {
			unboundDate = true; //碰到一些特殊的指标，不再补绑时间
		}else if (sn.getType() == NodeType.BOUNDARY && ((BoundaryNode)sn).isStart() && checkSpecialSyntactic(list,nodePos)) {
			unboundDate = true; //碰到一些特殊的句式，忽略绑定时间
		}
		BoundaryNode bNode = (BoundaryNode) sn;
		if (bNode.isEnd()) {
			return;
		}
		
		BoundaryNode.SyntacticPatternExtParseInfo info = bNode.getSyntacticPatternExtParseInfo(false).copy();
		if (info == null) {
			return;
		}
		HashMap<String, SemanticNode> bindedPropsBoundary = new HashMap<String, SemanticNode>();
		ArrayList<Integer> elelist;
		for (int j = 1; (elelist = info.getElementNodePosList(j)) != null; j++) {
			for (int pos : elelist) {
				SemanticNode tsn = null;
				if (pos == -1) {
					tsn = info.absentDefalutIndexMap.get(j);
				} else {
					tsn = list.get(nodePos + pos);
				}
	
				if (!isIndex(tsn)) {
					continue;
				}
	
				ClassNodeFacade index = getIndex(tsn);
				completionIndexProps(index, bindedProps,
				        bindedPropsBoundary, unboundDate);
			}
		}
		// 对于语义固定值参数的处理
		for (int j = 1; j < info.fixedArgumentMap.size() + 1; j++) {
			SemanticNode fixedNode = info.fixedArgumentMap.get(j);
			if (!isIndex(fixedNode)) {
				continue;
			}
			ClassNodeFacade index = getIndex(fixedNode);
			completionIndexProps(index, bindedProps, bindedPropsBoundary, unboundDate);
		}
	}
	
	/**
	 * 
	 * @param type
	 * @return
	 */
	private boolean isDateType(String type) {
		switch(type) {
	        case "交易日期":
	        case "n日":
	        case "+区间":
	        case "报告期":
	        case "分析周期":
	        case "持续周期":
	            return true;
	        default:
	           return false;
        }
	}

	/**
	 * prop的补全
	 * 
	 * @param list
	 * @throws UnexpectedException
	 */
	public void completion(ArrayList<SemanticNode> list)
	        throws UnexpectedException {

		completionProps(list);
	}

	
}
