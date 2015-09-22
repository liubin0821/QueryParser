/**
 * @author: 	    吴永行 
 * @dateTime:	  2014-2-27 下午3:22:56
 * @description:   	
 * 
 */
package com.myhexin.qparser.phrase.parsePlugins.bind.stateMachine;

import com.myhexin.qparser.define.EnumDef.Direction;

public class LeftOverRightNotOverState implements State{
	
	private OneTripBindPropToIndex bindMachine = null;
	public static final org.slf4j.Logger logger_ = org.slf4j.LoggerFactory.getLogger(LeftOverRightNotOverState.class.getName());
	


	public LeftOverRightNotOverState(OneTripBindPropToIndex bindMachine) {
	    this.bindMachine = bindMachine;
    }

	@Override
	public int getNextIndexPos(BindNode bn) {

		//计算当前 index 是否符合规则
		if (bn.bindInfo.direction == Direction.RIGHT) {
			//下一个位置
			bn.bindInfo.spandIndex++;

			return getRightPosReasonable(bn);
		}else if (bn.bindInfo.direction == Direction.LEFT) {
			bn.bindInfo.direction = Direction.RIGHT;
			if(bn.bindInfo.spandBoundary > 0)
				bn.bindInfo.spandIndex = 1;
			else
				bn.bindInfo.spandIndex++;
			return getRightPosReasonable(bn);
		}
		logger_.error("状态机转换错误");
		return -2;//错误

	}
	
	//内联的函数	
	private final int getRightPosReasonable(BindNode bn) {
		int boundaryListPos = bn.getRightBoundaryPos();
	    int indexListPos = bindMachine.getRightIndexPos(boundaryListPos,bn);
	    if (bindMachine.isIndexInList(indexListPos) &&//没有超出数组范围
	    		bindMachine.isBoundaryInList(boundaryListPos) 
	            && bindMachine.indexs.get(indexListPos) < bindMachine.boundarys.get(boundaryListPos).boundaryPos)
	    	//右边时,  指标需要在boundary的左边
	    	return indexListPos;
	    
	    //状态机切换
	    bindMachine.changeToLeftAndRightOverState();
		return bindMachine.getNextBindIndexPos(bn); 
    }
}
