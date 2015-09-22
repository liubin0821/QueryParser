package com.myhexin.qparser.phrase.parsePlugins;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;

import com.myhexin.qparser.ambiguty.AmbiguityProcessor;
import com.myhexin.qparser.define.EnumDef.NodeType;
import com.myhexin.qparser.node.BoundaryNode;
import com.myhexin.qparser.node.FocusNode;
import com.myhexin.qparser.node.FocusNode.FocusItem;
import com.myhexin.qparser.node.SemanticNode;
import com.myhexin.qparser.phrase.ParserAnnotation;

public class PhraseParserPluginDealWithAmbiguity extends PhraseParserPluginAbstract{

	@Autowired(required=true)
	private AmbiguityProcessor ambiguityProcessor = null;
	
	
    public void setAmbiguityProcessor(AmbiguityProcessor ambiguityProcessor) {
    	this.ambiguityProcessor = ambiguityProcessor;
    }

	public PhraseParserPluginDealWithAmbiguity() {
        super("Deal_with_Ambiguity");
    }
    
	public ArrayList<ArrayList<SemanticNode>> process(ParserAnnotation annotation) {
		ArrayList<SemanticNode> nodes = annotation.getNodes();
		return dealWithAmbiguity(nodes);
    }
    
    private ArrayList<ArrayList<SemanticNode>> dealWithAmbiguity(ArrayList<SemanticNode> nodes) {
        ArrayList<ArrayList<SemanticNode>> rlist = new ArrayList<ArrayList<SemanticNode>>(1);
        
        ambiguityProcessor.processAll(nodes, getLogsb_(nodes));
        
        if (!checkIsHasIndexDelAll(nodes))
        	rlist.add(nodes);
        return rlist;
    }
    
    public boolean checkIsHasIndexDelAll(ArrayList<SemanticNode> nodes)
    {
    	if (nodes == null)
			return true;
        ArrayList<Integer> boundaryList = getSyntacitcPatternList(nodes);
        if(boundaryList.size() <= 2)
            return false; // 没有句式
        int start = 0;
        int end = 0;
        int bStart = 0;
        int bEnd = 0;
        SemanticNode sNode = null;
        BoundaryNode bNode = null;
        boolean isThereAllDel = false;
        for(int i=1; i<boundaryList.size()-1; i++)
        {
            int pos = (int)boundaryList.get(i);
            sNode = nodes.get(pos);
            if(sNode!= null && sNode.type == NodeType.BOUNDARY)
            {
                bNode = (BoundaryNode) sNode;
                if(bNode.isStart())
                {
                    start = (int)boundaryList.get(i-1);
                    end = (int)boundaryList.get(i+2);
                    bStart = pos;
                    bEnd = (int)boundaryList.get(i+1);
                    boolean temp = checkIsHasIndexDelSyntactic(nodes, bNode, start, end, bStart, bEnd);
                    if (temp) {
                    	isThereAllDel = true;
                    	break;
                    }
                }
            }
        }
        
        return isThereAllDel;
    }
    
    public boolean checkIsHasIndexDelSyntactic(ArrayList<SemanticNode> nodes, BoundaryNode bNode, int start , int end, int bStart, int bEnd)
    {
        // 当FocusItem都被删除时，表示句式匹配不正确
    	boolean isThereAllDel = false;
    	BoundaryNode.SyntacticPatternExtParseInfo info = bNode.getSyntacticPatternExtParseInfo(false);
        if(info == null)
            return false;
		ArrayList<Integer> elelist;
		for (int j = 1; (elelist = info.getElementNodePosList(j)) != null; j++) {
			for (int pos : elelist) {
				int i = 0;
				SemanticNode sNode = null;
				
				if (pos != -1) {			// 显式指标
					i = bStart + pos;
					sNode = nodes.get(i);
				} else {					// 默认指标
					i = bStart;
					sNode = info.absentDefalutIndexMap.get(j);
				}
				if (sNode == null || sNode.type != NodeType.FOCUS)
					continue;
				FocusNode fNode = (FocusNode) sNode;
				if (!fNode.hasIndex())
					continue;
				boolean isHasFocusItem = false;
				boolean isHasFocusItemDel = false;
				for (FocusItem fi : fNode.getFocusItemList()) {
					if (fi.getType() != FocusNode.Type.INDEX)
						continue;
					// System.out.println(fi.canDelete_);
					if (fi.isCanDelete() == false) {
						isHasFocusItem = true;
					} else {
						isHasFocusItemDel = true;
					}
				}
				if (isHasFocusItemDel && isHasFocusItem == false) {
					isThereAllDel = true;
					break;
				}
			}
		}
    		
        return isThereAllDel;
    }
    
    /**
     * 取boundaryNode的位置信息，并在头上增加0位置，最后增加最后一个节点位置
     *
     * 结果 [0, bstartpos1, bendpos1, bstartpos2, bendpos2 ... bendposn, nodes.size()]
     * @param nodes
     * @return ArrayList<Integer> 位置信息数组
     */
    private ArrayList<Integer> getSyntacitcPatternList(ArrayList<SemanticNode> nodes)
    {
        int len = nodes.size();
        ArrayList<Integer> ret = new ArrayList<Integer>();
        ret.add(0);
        for(int i=0; i<len; i++)
        {
            SemanticNode node = nodes.get(i);
            if(node.type == NodeType.BOUNDARY)
            {
                ret.add(i);
            }
        }
        ret.add(len);
        return ret;
    }
}
