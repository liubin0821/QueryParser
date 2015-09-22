/**
 * 
 */
package com.myhexin.qparser.util.condition;

import java.util.ArrayList;
import java.util.List;

import com.myhexin.qparser.date.DateInfoNode;
import com.myhexin.qparser.date.DateRange;
import com.myhexin.qparser.except.BacktestCondException;
import com.myhexin.qparser.iterator.BoundaryInfos;
import com.myhexin.qparser.node.DateNode;
import com.myhexin.qparser.node.FocusNode;
import com.myhexin.qparser.node.SemanticNode;
import com.myhexin.qparser.phrase.SyntacticSemantic.Semantic.SemanticBindTo;
import com.myhexin.qparser.phrase.SyntacticSemantic.Semantic.SemanticBindToArgument;
import com.myhexin.qparser.resource.model.SemanticOpModel;
import com.myhexin.qparser.util.condition.model.ConditionIndexModel;
import com.myhexin.qparser.util.condition.model.ConditionModel;
import com.myhexin.qparser.util.condition.model.ConditionOpModel;

/**
 * @author chenhao
 * 创新高类型的问句
 *
 */
public class ConditionBuilderRecordExtremum extends ConditionBuilderAbstract {
	@Override
	public List<ConditionModel> doBuild(ConditionParam param) throws BacktestCondException {
		BoundaryInfos boundaryInfos = param.getBoundaryInfos();
		SemanticBindTo semanticBindTo = param.getSbt();
		List<SemanticNode> nodes = param.getNodes();
		SemanticOpModel opModel = param.getOpModel();
		int semanticId = opModel == null ? 0 : opModel.getSemanticId();
		String domain = param.getDomain();
		List<ConditionModel> models = new ArrayList<ConditionModel>();
		ConditionOpModel parentModel = (ConditionOpModel) this.buildParentOpModel(opModel);
		int sonSize = 0;

		if (parentModel != null) {
			models.add(parentModel);
		}

		// 为空,返回
		if (boundaryInfos == null || nodes == null) {
			return models;
		}
		ConditionIndexModel indexCond = null;
		if (semanticBindTo != null) {
			// 把args转成Condition
			ArrayList<SemanticBindToArgument> args = semanticBindTo.getSemanticBindToArguments();
			for (SemanticBindToArgument arg : args) {
				SemanticNode indexNode = getElement(arg, nodes, semanticId, boundaryInfos.bStart);
				if (indexNode != null && indexNode.isFocusNode()) {
					FocusNode fNode = (FocusNode) indexNode;

					indexCond = toCondition(fNode, domain, param.getBacktestTime());
					if (indexCond != null) {
						models.add(indexCond);
						sonSize++;
					}
				} else if (indexNode != null && indexNode.isDateNode()) {
					// 创新高类型的问句，要将时间区间放在op内
					DateNode dateNode = (DateNode) indexNode;
					DateRange dateRange = dateNode.getDateinfo();
					String opProperty = "";
					if (dateRange != null) {
						DateInfoNode from = dateRange.getFrom();
						DateInfoNode to = dateRange.getTo();
						opProperty = "[" + DAY_RANGE_START + " " + from.toString("") + "," + DAY_RANGE_END + " "
								+ to.toString("") + "]";
					} else {
						opProperty = dateNode.getText();

					}
					parentModel.setOpProperty(opProperty);
				}
			}
		}
		parentModel.setUIText(ConditionUITextUtil.getUIText(indexCond, parentModel.getOpName(), null, null));
		parentModel.setSonSize(sonSize);
		return models;
	}
}
