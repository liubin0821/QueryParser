package com.myhexin.qparser.iterator;

import com.myhexin.qparser.node.BoundaryNode;
import com.myhexin.qparser.node.BoundaryNode.IMPLICIT_PATTERN;

public class IteratorTestUtil {
	
	
	
    /**
     *   
     * @return
     */
  	public BoundaryNode createEndBoundaryNode() {	    
    	BoundaryNode boundary = new BoundaryNode();
        boundary.setType(BoundaryNode.END, IMPLICIT_PATTERN.FREE_VAR.toString());
		return boundary;
    }
    /**
     * 
     * @return
     */
    public BoundaryNode createStartBoundaryNode() {  	
    	BoundaryNode boundary = new BoundaryNode();
        boundary.setType(BoundaryNode.START, IMPLICIT_PATTERN.FREE_VAR.toString());
        BoundaryNode.SyntacticPatternExtParseInfo extInfo = boundary.getSyntacticPatternExtParseInfo(true);
        boundary.extInfo.addElementNodePos(1, 1);
        boundary.extInfo.presentArgumentCount++;
		return boundary;
    }
	
}
