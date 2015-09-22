package com.myhexin.qparser.node;

import java.util.ArrayList;
import java.util.List;

import com.myhexin.qparser.define.EnumDef.NodeType;
import com.myhexin.qparser.define.MiscDef;
import com.myhexin.qparser.onto.ClassNodeFacade;
import com.myhexin.qparser.onto.OntoFacadeUtil;
import com.myhexin.qparser.onto.PropNodeFacade;

/**
 * 提供与{@link SemanticNode}相关的方便方法的辅助类
 */
public class NodeUtil {

	private NodeUtil(){}
    /**
     * 判断给定节点是否是股票简称或股票代码
     * @param sn 待判断的节点
     * @return 是否是股票简称或股票代码
     */
    public static boolean isStockNode(SemanticNode sn) {
        if(sn == null || sn.type != NodeType.STR_VAL) return false;
        StrNode strNode = (StrNode)sn;
        if(strNode.text.length() < 3 || strNode.text.length() > 6
                || strNode.text.charAt(0) == '+' || strNode.text.charAt(0) == '-') 
            return false;
        if(strNode.subType.size() == 0) return false;
        
        return strNode.subType.contains(MiscDef.STK_NAME_PROP) || 
        		strNode.subType.contains(MiscDef.STK_CODE_PROP);
        
        /*for(SemanticNode asn : strNode.ofWhat) {
            if(asn.text.equals(MiscDef.STK_NAME_PROP) ||
                    asn.text.equals(MiscDef.STK_CODE_PROP)) {
                return true;
            }
        }
        return false;*/
    }
    
    /**
     * 判断给定节点是否是“所属概念”。该节点必须StrNode，且其第一个ofWhat是
     * {@link MiscDef#STK_CONCEPT_PROP}
     * @param sn 待判断节点
     * @return 是否是“所属概念”
     */
    public static boolean isConceptNode(SemanticNode sn) {
        if(sn == null || sn.type != NodeType.STR_VAL) return false;
        StrNode strNode = (StrNode)sn;
        if(strNode.subType.size() == 0) return false;
        //return MiscDef.STK_CONCEPT_PROP.equals(strNode.subType.get(0).text);
        return strNode.subType.contains(MiscDef.STK_CONCEPT_PROP);
    }
    
    public static boolean isBoolStrNode(StrNode sn) {
        return MiscDef.BOOL_YES_VAL.equals(sn.text) ||
                MiscDef.BOOL_NO_VAL.equals(sn.text);
    }
    
   /* public static DateNode copyDateNode(DateNode node) {
    	return node.copy();
    }
    
    public static NumNode copyNumNode(NumNode node) {
    	return node.copy();
    }
    
    public static FocusNode copyFocusNode(FocusNode node) {
    	return node.copy();
    }*/
    public static org.slf4j.Logger logger_ = org.slf4j.LoggerFactory.getLogger(NodeUtil.class.getName());
    public static SemanticNode copyNode(SemanticNode node) {
    	NodeType type = node.getType();
    	switch(type) {
    		case DATE: return ((DateNode)node).copy(); 
    		case NUM: return ((NumNode)node).copy(); 
    		case FOCUS: return ((FocusNode)node).copy();
    		case BOUNDARY: return ((BoundaryNode)node).copy();
    		case CLASS: return OntoFacadeUtil.copyWithValues((ClassNodeFacade)node);
    		case PROP: return OntoFacadeUtil.copy((PropNodeFacade)node);
    		case ENV: return ((Environment)node).copy();
    		case STR_VAL: return ((StrNode)node).copy();
    		default: {
    			//logger_.warn("UNCOPY TYPE:" + type);
    			//System.out.println("UNCOPY TYPE:" + type);
    			return node;
    		}
    	}
    }

	public static ArrayList<SemanticNode> copyNodes(List<SemanticNode> nodes) {
		ArrayList<SemanticNode> newNodes = new ArrayList<SemanticNode>();
		for (SemanticNode node : nodes) {
			newNodes.add(copyNode(node));
		}
		return newNodes;
	}

}
