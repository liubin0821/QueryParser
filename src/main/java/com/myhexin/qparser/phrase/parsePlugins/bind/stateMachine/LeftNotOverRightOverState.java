/**
 * @author: 	    吴永行 
 * @dateTime:	  2014-2-27 下午3:24:27
 * @description:   	
 * 
 */
package com.myhexin.qparser.phrase.parsePlugins.bind.stateMachine;

import com.myhexin.qparser.define.EnumDef.Direction;

public class LeftNotOverRightOverState implements State{
	public static final org.slf4j.Logger logger_ = org.slf4j.LoggerFactory.getLogger(LeftNotOverRightOverState.class.getName());
	private OneTripBindPropToIndex bindMachine = null;
	
	@Override
    public int getNextIndexPos(BindNode bn) {
		//计算当前 index 是否符合规则
		if (bn.bindInfo.direction == Direction.LEFT) {
			//转到下一个位置
			bn.bindInfo.spandIndex++;			
			return getLeftReasonablePos(bn);
			
		}else if (bn.bindInfo.direction == Direction.RIGHT) {
			bn.bindInfo.direction = Direction.LEFT;
			if(bn.bindInfo.spandBoundary > 0)
				bn.bindInfo.spandIndex = 1;
			return getLeftReasonablePos(bn);
		}
		logger_.error("状态机转换错误, 方向不为LEFT 但是转换到LeftNotOverRightOverState");
		return -2;//错误
    }
	
	//内联的函数	
	private final int getLeftReasonablePos(BindNode bn) {
		int boundaryListPos = bn.getLeftBoundaryPos();
	    int indexListPos = bindMachine.getLeftIndexPos(boundaryListPos,bn);
	    if (bindMachine.isIndexInList(indexListPos) &&
	    		bindMachine.isBoundaryInList(boundaryListPos) 
	            && bindMachine.indexs.get(indexListPos) > bindMachine.boundarys.get(boundaryListPos).boundaryPos) //左边时,  指标需要在boundary的右边
	    	return indexListPos;
	    
	  //状态机切换
	    bindMachine.changeToLeftAndRightOverState();
		return bindMachine.getNextBindIndexPos(bn);
    }
	

	

	public LeftNotOverRightOverState(OneTripBindPropToIndex bindMachine) {
	    this.bindMachine = bindMachine;
    }

}
