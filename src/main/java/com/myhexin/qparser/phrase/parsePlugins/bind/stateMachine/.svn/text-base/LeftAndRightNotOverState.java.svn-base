/**
 * @author: 	    吴永行 
 * @dateTime:	  2014-2-27 下午3:20:26
 * @description:   	
 * 
 */
package com.myhexin.qparser.phrase.parsePlugins.bind.stateMachine;

import com.myhexin.qparser.define.EnumDef.Direction;

public class LeftAndRightNotOverState implements State {

	private OneTripBindPropToIndex bindMachine = null;
	public static final org.slf4j.Logger logger_ = org.slf4j.LoggerFactory.getLogger(LeftAndRightNotOverState.class.getName());
	
	public LeftAndRightNotOverState(OneTripBindPropToIndex bindMachine) {
	    this.bindMachine = bindMachine;
    }
	
	
	@Override
	public int getNextIndexPos(BindNode bn) {

		//计算当前 index 是否符合规则
		if (bn.bindInfo.direction == Direction.LEFT) {

			//转到下一个位置
			bn.bindInfo.spandIndex++;
			if (bn.bindInfo.spandBoundary == 0) {
				bn.bindInfo.direction = Direction.RIGHT;

				return getRightPosReasonable(bn);

			} else {
				return getLeftReasonablePos(bn);
			}

		} else if (bn.bindInfo.direction == Direction.RIGHT) {

			//转到下一个位置
			if (bn.bindInfo.spandBoundary == 0) {
				bn.bindInfo.direction = Direction.LEFT;

				return getLeftReasonablePos(bn);
			} else if (bn.bindInfo.spandBoundary > 0){
				bn.bindInfo.spandIndex++;
				return getRightPosReasonable(bn);
			}
		}
		logger_.error("状态机转换错误, 状态机中方向不能为BOTH ");
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
	    bindMachine.changeToLeftNotOverRightOverState();
		return bindMachine.getNextBindIndexPos(bn); 
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
	    bindMachine.changeToLeftOverRightNotOverState();
		return bindMachine.getNextBindIndexPos(bn);
    }	

}
