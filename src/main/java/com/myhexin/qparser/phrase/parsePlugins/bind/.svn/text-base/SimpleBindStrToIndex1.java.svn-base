/**
 * @author: 	    吴永行 
 * @dateTime:	  2014-5-12 下午8:21:39
 * @description:   	
 * 
 */
package com.myhexin.qparser.phrase.parsePlugins.bind;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.myhexin.qparser.define.EnumDef.Direction;
import com.myhexin.qparser.define.EnumDef.NodeType;
import com.myhexin.qparser.define.EnumDef.PropType;
import com.myhexin.qparser.except.UnexpectedException;
import com.myhexin.qparser.node.DateNode;
import com.myhexin.qparser.node.FocusNode;
import com.myhexin.qparser.node.SemanticNode;
import com.myhexin.qparser.node.StrNode;
import com.myhexin.qparser.onto.ClassNodeFacade;
import com.myhexin.qparser.onto.PropNodeFacade;

@Component
public class SimpleBindStrToIndex1 {
	

	/**
	 * 单一方向的绑定 整句绑定
	 * 
	 * @param list
	 * @param direction
	 * @throws UnexpectedException
	 */
    public void bindToSingleDerection(ArrayList<SemanticNode> list, Direction direction, BreakType breakType) throws UnexpectedException {
        for (int i = 0; i < list.size(); i++) {
            SemanticNode sn = list.get(i);
            if(sn.getType()==NodeType.STR_VAL){
            	StrNode str = (StrNode) sn;
            	bindSinglePattern(list, str, direction, i);
            }
        }
    }

    /**
     * @author: 	    吴永行 
     * @dateTime:	  2014-11-18 下午2:50:49
     * @description:   	
     * @param list
     * @param str
     * @param direction
     * @param i
     */
    private final void bindSinglePattern(ArrayList<SemanticNode> list, StrNode str, Direction direction,
            int i) {
    	FocusNode fn = getIndex(list,direction,i);
    	if(fn!=null)
    		bindNodeToIndexProps(str,fn.getIndex());
    }

	/**
     * @author: 	    吴永行 
     * @dateTime:	  2014-11-18 下午2:55:43
     * @description:   	
     * @param list
     * @param direction
     * @param i
     * @return
     */
    private final FocusNode getIndex(ArrayList<SemanticNode> list, Direction direction, int i) {
	    
	    if(direction==Direction.LEFT){
	    	while (--i>=0) {
				SemanticNode sn = list.get(i);
				if(sn.getType()==NodeType.FOCUS){
					FocusNode fn = (FocusNode) sn;
					if(fn.hasIndex())
						return fn;
				}
			}
	    }
	    else if (direction==Direction.RIGHT) {
	    	int len=list.size();
	    	while (++i<len) {
				SemanticNode sn = list.get(i);
				if(sn.getType()==NodeType.FOCUS){
					FocusNode fn = (FocusNode) sn;
					if(fn.hasIndex())
						return fn;
				}
			}
		}
	    return null;
    }

	public static void bindNodeToProp(SemanticNode sn, PropNodeFacade pn, SemanticNode pnParent) {
    	if (sn.getType() == NodeType.DATE && ((DateNode) sn).isCombined() )
    		return;
        pn.setValue(sn);
        sn.setIsBoundToIndex(true);
        sn.setBoundToIndexProp(pnParent, pn);
    }

	/**
     * @author: 	    吴永行 
     * @dateTime:	  2014-4-2 下午1:30:56
     * @description:   	
     * @param sn
     * @param index
     * @return
     * 
     */
    public boolean bindNodeToIndexProps(SemanticNode sn, ClassNodeFacade index) {
    	if (index == null || isNotBoundedIndex(sn) )
              return false;
    	
        switch (sn.getType()) {
		case STR_VAL:
			if(bindStrNodeToProps(sn, index.getClassifiedProps(PropType.STR), (StrNode) sn, index)){ 
				index.setIsBoundValueToThis(true);
				return true;
			}
			return false;
			
			
		//看代码这个case肯定跑不到啊!!! liuxiaofeng 2015/3/31
		//TODO 下次注释掉,免得迷惑人
		case FOCUS: 
         	if(((FocusNode)sn).hasString())
	        	if(bindStrNodeToProps(sn, index.getClassifiedProps(PropType.STR), ((FocusNode)sn).getString(), index)){ 
	        		index.setIsBoundValueToThis(true);
	        		return true;   
	        	}
        	return false;
		
		default:
			return false;
		}
    }
    
    private final boolean isNotBoundedIndex(SemanticNode sn) { 	
		switch (sn.getType()) {
		case FOCUS:
			return !((FocusNode) sn).hasIndex() && sn.isBoundToIndex();
		default:
			return sn.isBoundToIndex();
		}	    
    }

	/**
	 * @author: 	    吴永行 
	 * @dateTime:	  2013-12-31 上午11:25:53
	 * @description:   	
	 * 
	 */
	private static boolean bindStrNodeToProps(SemanticNode sn,
			List<PropNodeFacade> propList, StrNode stringNode, SemanticNode pnParent) {
		
		if(stringNode == null || propList == null) return false;
		if(stringNode.isBoundToIndex()) return false;//已经被绑定
		
		for (PropNodeFacade pn : propList) {
			if (pn.isValueProp())
				continue;
    		if (pn.getValue() !=null) 
				continue;
			
		    if (pn.isStrProp()) {
		    	
		    	for(String st : stringNode.subType)
		    		if(pn.getSubType().contains(st)){
		    			bindFocuStrNodeToPro(sn, pn, pnParent);
		                return true;
		    		}
		    }
		}
		return false;
	}

    private static final void bindFocuStrNodeToPro(SemanticNode sn, PropNodeFacade pn, SemanticNode pnParent) {
	    if (sn.getType()==NodeType.FOCUS) {
	    	sn.setIsBoundToIndex(true);
	    	sn.setBoundToIndexProp(pnParent, pn);
	    	
	    	bindNodeToProp(((FocusNode)sn).getString(), pn, pnParent);
	    	return ;
	    }
	    bindNodeToProp(sn, pn, pnParent);
    }
	
    /**
     * prop的绑定
     * 
     * @param list
     * @throws UnexpectedException
     */
    public void bind(ArrayList<SemanticNode> list) throws UnexpectedException {
        bindToSingleDerection(list, Direction.LEFT, BreakType.INDEX);
        bindToSingleDerection(list, Direction.RIGHT, BreakType.INDEX);
    }

    private enum BreakType {
		INDEX, BOUNDARY, SEPARATOR, NONE
    }
}