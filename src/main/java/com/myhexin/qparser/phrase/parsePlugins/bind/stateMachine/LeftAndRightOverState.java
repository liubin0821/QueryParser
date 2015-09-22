/**
 * @author: 	    吴永行 
 * @dateTime:	  2014-2-27 下午3:25:14
 * @description:   	
 * 
 */
package com.myhexin.qparser.phrase.parsePlugins.bind.stateMachine;

import com.myhexin.qparser.define.EnumDef.Direction;

public class LeftAndRightOverState implements State {

	private OneTripBindPropToIndex bindMachine = null;
	public static final org.slf4j.Logger logger_ = org.slf4j.LoggerFactory
	        .getLogger(LeftAndRightOverState.class.getName());

	@Override
	public int getNextIndexPos(BindNode bn) {

		bn.bindInfo.spandBoundary++;//跨过的边界节点+1
		bn.bindInfo.spandIndex = 0;
		bn.bindInfo.direction = Direction.RIGHT;
		int leftBoundaryListPos = bn.getLeftBoundaryPos();
		int rightBoundaryListPos = bn.getRightBoundaryPos();
		if (!bindMachine.isBoundaryInList(leftBoundaryListPos)
		        && !bindMachine.isBoundaryInList(rightBoundaryListPos)) {

			bindMachine.changeToLeftAndRightNotOverState();//恢复初始状态
			return -1;//没有位置上合适的index了
		}
		//左边已经没有了
		if (!bindMachine.isBoundaryInList(leftBoundaryListPos)) {
			return getRightPosReasonable(bn);
		}
		//右边已经没有了	
		else if (!bindMachine.isBoundaryInList(rightBoundaryListPos)) {
			return getLeftReasonablePos(bn);
		} else //两边都还有    先右边后左边	
		{
			return getBothReasonablePos(bn);
		}
	}

	//内联的函数	
	private final int getRightPosReasonable(BindNode bn) {
		//状态机切换
		bindMachine.changeToLeftOverRightNotOverState();
		return bindMachine.getNextBindIndexPos(bn);
	}

	//内联的函数	
	private final int getLeftReasonablePos(BindNode bn) {
		//状态机切换
		bindMachine.changeToLeftNotOverRightOverState();
		return bindMachine.getNextBindIndexPos(bn);
	}

	//内联的函数	
	private final int getBothReasonablePos(BindNode bn) {
		//状态机切换
		bindMachine.changeToLeftAndRightNotOverState();
		return bindMachine.getNextBindIndexPos(bn);
	}

	public LeftAndRightOverState(OneTripBindPropToIndex bindMachine) {
		this.bindMachine = bindMachine;
	}

}
