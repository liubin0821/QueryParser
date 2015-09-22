/**
 * @author: 	    吴永行 
 * @dateTime:	  2014-2-26 下午3:04:33
 * @description:   	
 * 
 */
package com.myhexin.qparser.phrase.parsePlugins.bind.stateMachine;

import java.util.ArrayList;
import java.util.HashMap;

import com.myhexin.qparser.define.EnumDef.Direction;
import com.myhexin.qparser.define.EnumDef.NodeType;
import com.myhexin.qparser.node.SemanticNode;

public class BindNode {

	public int leftIndex = -1; //当前节点的左边一个index 在indexList中的位置
	public int leftBoundary = -1;//当前节点的左边一个boundary 在boundaryList中的位置
	public SemanticNode sn = null;

	BindInfo bindInfo = new BindInfo();
	
	public BindNode( SemanticNode sn,int leftIndex, int leftBoundary) {
	    this.leftIndex = leftIndex;
	    this.leftBoundary = leftBoundary;
	    this.sn = sn;
    }
	

	public final int getRightBoundaryPos() {
	    return this.leftBoundary + this.bindInfo.spandBoundary + 1;
    }


	public final int getLeftBoundaryPos() {
	    return this.leftBoundary - this.bindInfo.spandBoundary ;
    }

	
}

class BindInfo {
	public int spandIndex = 0; //跨越指标数  初始时他旁边的第一个指标
	public int spandBoundary = 0;//跨越boundary数  初始不跨过  boundary
	public Direction direction = Direction.LEFT;//方向
	public State state = null;

}

