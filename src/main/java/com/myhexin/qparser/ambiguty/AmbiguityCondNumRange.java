package com.myhexin.qparser.ambiguty;

import java.util.ArrayList;
import java.util.List;

import com.myhexin.DB.mybatis.mode.Aliases;
import com.myhexin.qparser.define.EnumDef.NodeType;
import com.myhexin.qparser.define.EnumDef.Unit;
import com.myhexin.qparser.node.BoundaryNode;
import com.myhexin.qparser.node.NumNode;
import com.myhexin.qparser.node.SemanticNode;
import com.myhexin.qparser.node.BoundaryNode.IMPLICIT_PATTERN;
import com.myhexin.qparser.number.NumRange;
import com.myhexin.qparser.onto.ClassNodeFacade;
import com.myhexin.qparser.onto.PropNodeFacade;
import com.myhexin.qparser.rule.IndexRuleLoader;

import org.w3c.dom.Node;

public class AmbiguityCondNumRange extends AmbiguityCondAbstract{

    /**
     * init
     */
	public AmbiguityCondNumRange()
	{
        super("numrange");
	}
	
    /**
     * 匹配一个条件。
     *
     * @param nodes query node list
     * @param start start
     * @param end end 
     * @param bStart boundary start
     * @param bEnd boundary end 
     * @param cNode 可能的class node
     * @return int 返回匹配结果
     *              －1 完全不合适，应该删除
     *              0 － 100 正确的可能性，100为最大
     */
    public double matchNode(ArrayList<SemanticNode> nodes, int start, int end, int bStart , int bEnd, ClassNodeFacade cNode, int cPos, String alias)
    {
    	//System.out.println("type:"+type_);
    	double ret = 50;
    	// 当解析为比较的语义时（即之比 或 的比值），值范围不能用于消歧
    	if (AmbiguityCondUnit.isCompareSemantics(nodes, bStart)
        		|| AmbiguityCondUnit.isKnowledgeOrLongTou(nodes, bStart)) {
    		return ret;
    	}
    	// 如果为FREE_VAR，不需要判断值范围
    	if (((BoundaryNode) nodes.get(bStart)).getSyntacticPatternId().equals(IMPLICIT_PATTERN.FREE_VAR.toString())){
        	return ret;
        }
    	
        List<PropNodeFacade> propList = cNode.getAllProps();
        if (propList == null)
        	return ret;
        //Unit unit = cNode.getValueUnit2();
        List<Unit> units = cNode.getValueUnits2();
        if (units == null) {
        	return 0;
        } else if (units.size() == 0) {
        	units.add(Unit.UNKNOWN);
        }
        
        // 以下关于属性是否匹配的判断逻辑是否存在问题？
        // 1、指标的属性中，带_下划线的是什么意思？带$、!、+、*的又是什么意思？
        // 2、绑定属性到指标中有if(pn.getText().charAt(0) == '_')continue;，而且对于是否同类型有更严格的判断，是否应该处理？
        // 3、type="DOUBLE"类型的在这里其实没有做匹配，是否存在问题？
        // 做法：
        // 1、如果设置了取值范围，且数值范围在取值范围之间，则单位相同加30分，单位未知加30分
        // 2、如果设置了取值范围，且数值范围不在取值范围之间，则加0分
        // 3、如果没有设置取值范围，则表示数值范围可能在取值范围之间，加10分
        boolean isThereNum = false;
        boolean isBetween = false;
        boolean isUnknown = false;
        boolean isUnitUnknown = false;
        for(PropNodeFacade pn: propList)
        {
        	if(pn.getText().charAt(0) != '_')
        		continue;
        	//double min = pn.getMin();
        	//double max = pn.getMax();
        	NumRange nr = IndexRuleLoader.indexNumRange.get(cNode.getText());
        	if (nr == null) {
        		isUnknown = true;
        	}
        	double min = nr==null ? Double.MIN_VALUE : nr.getDoubleFrom();
        	double max = nr==null ? Double.MAX_VALUE : nr.getDoubleTo();
        	for(int i=start; i < end || (end == nodes.size()-1 && i == end); i++)
	        {
	            if (nodes.get(i).type == NodeType.NUM) {
	            	NumNode nNode = (NumNode)nodes.get(i);
	            	boolean isUnitEqual = false;
	            	if (units.contains(nNode.getUnit()))	{
	            		isUnitEqual = true;
                    } else if ((units.contains(Unit.PERCENT) || units.contains(Unit.BEI)) 
                    		&& (nNode.getUnit() == Unit.PERCENT || nNode.getUnit() == Unit.BEI || nNode.getUnit() == Unit.UNKNOWN)) {
                    	isUnitEqual = true;
                    } else if(nNode.getUnit() != Unit.UNKNOWN && nNode.getUnit() != Unit.UNKNOWN) {
                    	isUnitEqual = false;
                    } else if (nNode.getUnit() == Unit.UNKNOWN) {
                    	isUnitEqual = true;
                    	isUnitUnknown = true;
                    }
	            	if (isUnitEqual == false) {
	            		continue;
	            	}
	            	isThereNum = true;
	            	NumNode sNode = (NumNode)nodes.get(i);
	            	double numMin = sNode.getFrom();
	            	double numMax = sNode.getTo();
		            if (min <= numMin && max >= numMax) {
		            	isBetween = true;
		            }
	            }
	        }
        }
        
        // 附加的分数：首先判断是否存在数值，然后判断是否在数值范围之间，再判断是否设置了取值范围，最后判断单位是否相同
        double scoreAdd = isThereNum ? (isBetween ? (isUnknown ? 10 : (isUnitUnknown ? 30 : 30)) : 0) : 0;
        //System.out.println("numrange score add:"+scoreAdd);
        ret = ret + scoreAdd;
        return ret <= 100 ? ret : 100;
    }

}
