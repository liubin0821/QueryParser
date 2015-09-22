package com.myhexin.qparser.util.condition;

import java.util.ArrayList;
import java.util.List;

import com.myhexin.qparser.except.BacktestCondException;
import com.myhexin.qparser.iterator.BoundaryInfos;
import com.myhexin.qparser.node.FocusNode;
import com.myhexin.qparser.node.SemanticNode;
import com.myhexin.qparser.node.StrNode;
import com.myhexin.qparser.phrase.SyntacticSemantic.Semantic.SemanticArgument.SemanArgType;
import com.myhexin.qparser.phrase.SyntacticSemantic.Semantic.SemanticBindTo;
import com.myhexin.qparser.phrase.SyntacticSemantic.Semantic.SemanticBindToArgument;
import com.myhexin.qparser.phrase.SyntacticSemantic.Semantic.SemanticBindToArgument.BindToType;
import com.myhexin.qparser.resource.model.SemanticOpModel;
import com.myhexin.qparser.util.condition.model.ConditionIndexModel;
import com.myhexin.qparser.util.condition.model.ConditionModel;


/**
 * 包含语义
 * 
 * @author 刘小峰 liuxiaofeng@myhexin.com
 * @Version 创建时间 2015-5-8
 *
 */
public class ConditionBuilderContain extends ConditionBuilderAbstract {
	@Override
	public List<ConditionModel> doBuild(ConditionParam param) throws BacktestCondException {
		BoundaryInfos boundaryInfos = param.getBoundaryInfos();
		SemanticBindTo semanticBindTo = param.getSbt();
		List<SemanticNode> nodes = param.getNodes();
		SemanticOpModel opModel = param.getOpModel();
		int semanticId = opModel == null ? 0 : opModel.getSemanticId();
		//String opName = opModel == null ? "" : opModel.getOpName();
		String domain = param.getDomain();
		
		
		FocusNode indexNode = null;
		SemanticNode constantNode = null;
		ConditionIndexModel indexCondition = null;
		
		ArrayList<SemanticBindToArgument> args = semanticBindTo.getSemanticBindToArguments();
		for (SemanticBindToArgument arg : args) {
			if (arg.getType() == SemanArgType.INDEX || arg.getType() == SemanArgType.INDEXLIST) { //是指标,创建指标condition
				if(arg.getBindToType() == BindToType.SYNTACTIC_ELEMENT)
					indexNode =  (FocusNode) getElement(arg, nodes, semanticId, boundaryInfos.bStart);
			} else if (arg.getType() == SemanArgType.CONSTANT || arg.getType() == SemanArgType.CONSTANTLIST
					|| arg.getType() == SemanArgType.ANY) {
				constantNode = getElement(arg, nodes,semanticId, boundaryInfos.bStart);
			}
		}
		

		
		if (indexNode != null && indexNode.isFocusNode() && constantNode!=null) {
			indexCondition = toCondition((FocusNode) indexNode, domain, param.getBacktestTime(), constantNode, opModel.getOpName());
			indexCondition.setUIText(ConditionUITextUtil.getUITextOfContain(indexCondition, opModel.getOpName(),constantNode.getText(),indexNode.getIndex()));
		}else if(indexNode != null && indexNode.isFocusNode()){
			indexCondition = toCondition((FocusNode) indexNode, domain, param.getBacktestTime());
			if(indexCondition!=null && constantNode!=null && constantNode.isStrNode()) {
				StrNode strNode = (StrNode) constantNode;
				indexCondition.addProp(opModel.getOpName(), strNode.getText(), true);
				indexCondition.setUIText(ConditionUITextUtil.getUITextOfContain(indexCondition, opModel.getOpName(), strNode.getText(), indexNode.getIndex()));
			}
		}
		
		//有比较符的默认只会有一个指标
		if (indexCondition != null) {
			List<ConditionModel> conditionModels = new ArrayList<ConditionModel>();
			conditionModels.add(indexCondition);
			return conditionModels;
		}else{
			return null;
		}
	}
}
