package com.myhexin.qparser.util.condition;

import java.util.ArrayList;
import java.util.List;

import com.myhexin.qparser.except.BacktestCondException;
import com.myhexin.qparser.iterator.BoundaryInfos;
import com.myhexin.qparser.node.FocusNode;
import com.myhexin.qparser.node.SemanticNode;
import com.myhexin.qparser.phrase.SyntacticSemantic.Semantic.SemanticBindTo;
import com.myhexin.qparser.phrase.SyntacticSemantic.Semantic.SemanticBindToArgument;
import com.myhexin.qparser.resource.model.SemanticOpModel;
import com.myhexin.qparser.util.condition.model.ConditionIndexModel;
import com.myhexin.qparser.util.condition.model.ConditionModel;
import com.myhexin.qparser.util.condition.model.ConditionOpModel;


/**
 * 指标condition输出
 * 
 * @author 刘小峰 liuxiaofeng@myhexin.com
 * @Version 创建时间 2015-5-8
 *
 */
public class ConditionBuilderIndex extends ConditionBuilderAbstract {

	@Override
	public List<ConditionModel> doBuild(ConditionParam param) throws BacktestCondException {
		BoundaryInfos boundaryInfos = param.getBoundaryInfos();
		SemanticBindTo semanticBindTo = param.getSbt();
		List<SemanticNode> nodes = param.getNodes();
		SemanticOpModel opModel = param.getOpModel();
		int semanticId = opModel == null ? 0 : opModel.getSemanticId();
		//String opName = opModel == null ? "" : opModel.getOpName();
		String domain = param.getDomain();
		List<ConditionModel> models = new ArrayList<ConditionModel>();
		ConditionOpModel parentModel = this.buildParentOpModel(opModel);
		if(parentModel!=null) {
			models.add(parentModel);
		}
		
		//其他指标

		
		//为空,返回
		if(boundaryInfos==null || nodes==null) {
			return models;
		}
		
		int sonSize = 0;
		if (semanticBindTo != null) {
		//把args转成Condition
			ArrayList<SemanticBindToArgument> args = semanticBindTo.getSemanticBindToArguments();
			for (SemanticBindToArgument arg : args) {
				SemanticNode indexNode = getElement(arg, nodes, semanticId, boundaryInfos.bStart);
				if (indexNode != null && indexNode.isFocusNode()) {
					FocusNode fNode = (FocusNode) indexNode;

					ConditionIndexModel indexCond = toCondition(fNode, domain, param.getBacktestTime());
					indexCond.setUIText(ConditionUITextUtil.getUIText(indexCond, null, null, null));
					if (indexCond != null)
						models.add(indexCond);
					sonSize++;
				}
			}
		}
		if (parentModel != null) {
			parentModel.setSonSize(sonSize);
		}

		return models;
	}

}
