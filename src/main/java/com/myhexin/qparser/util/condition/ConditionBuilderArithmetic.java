package com.myhexin.qparser.util.condition;

import java.util.ArrayList;
import java.util.List;

import com.myhexin.qparser.except.BacktestCondException;
import com.myhexin.qparser.iterator.BoundaryInfos;
import com.myhexin.qparser.node.FocusNode;
import com.myhexin.qparser.node.SemanticNode;
import com.myhexin.qparser.phrase.SyntacticSemantic.Semantic.SemanticArgument.SemanArgType;
import com.myhexin.qparser.phrase.SyntacticSemantic.Semantic.SemanticBindTo;
import com.myhexin.qparser.phrase.SyntacticSemantic.Semantic.SemanticBindToArgument;
import com.myhexin.qparser.resource.model.SemanticOpModel;
import com.myhexin.qparser.util.condition.model.ConditionIndexModel;
import com.myhexin.qparser.util.condition.model.ConditionModel;
import com.myhexin.qparser.util.condition.model.ConditionOpModel;


/**
 * 算术运算Condition
 * +,-,*,/
 * 
 * @author 刘小峰 liuxiaofeng@myhexin.com
 * @Version 创建时间 2015-5-8
 *
 */
public class ConditionBuilderArithmetic extends ConditionBuilderAbstract {

	@Override
	public List<ConditionModel> doBuild(ConditionParam param) throws BacktestCondException {
		BoundaryInfos boundaryInfos = param.getBoundaryInfos();
		SemanticBindTo semanticBindTo = param.getSbt();
		List<SemanticNode> nodes = param.getNodes();
		SemanticOpModel opModel = param.getOpModel();
		int semanticId = opModel == null ? 0 : opModel.getSemanticId();
		String opName = opModel == null ? "" : opModel.getOpName();
		String opProperty = opModel == null ? "" : opModel.getOpProperty();
		String domain = param.getDomain();

		SemanticNode indexNode;
		int sonSize = 0;
		ArrayList<SemanticBindToArgument> args = semanticBindTo.getSemanticBindToArguments();
		//把指标节点放到indexNodeList
		List<FocusNode> indexNodeList = new ArrayList<FocusNode>();
		boolean hasTechIndex  =false;
		for (SemanticBindToArgument arg : args) {
			if (arg.getType() == SemanArgType.INDEX) { //是指标,创建指标condition
				indexNode = getElement(arg, nodes, semanticId, boundaryInfos.bStart);
				if(indexNode.isFocusNode())
				{
					if(indexNode!=null) indexNodeList.add((FocusNode) indexNode);
					if(indexNode.getText()!=null && indexNode.getText().indexOf("均线")>=0) {
						hasTechIndex = true;
					}
				}
			}
		}//把指标节点放到indexNodeList 结束
		
		
		
		
		List<ConditionModel> conditionModels = new ArrayList<ConditionModel>();
		if(hasTechIndex && indexNodeList!=null && indexNodeList.size()==2) {
			/*
			 * 刘小峰
			 * 迁就回测数据端的逻辑,如果是技术指标的,按如下逻辑处理
			 * 问句: 5日均线大于10日均线的指数
			 * 原本是: 5日均线-10日均线>0
			 * 现在转成: 指数@5日均线大于10日均线
			 */
			if(indexNodeList!=null && indexNodeList.size()==2) {
				//第一个
				FocusNode fNode = indexNodeList.get(0);
				ConditionIndexModel indexCond = toCondition(fNode, domain, param.getBacktestTime());
				if(indexCond!=null)
				{
					modifyTechOpIndexAsIndex(indexCond, fNode, domain);
				}
				
				//第二个
				FocusNode fNode2 = indexNodeList.get(1);
				ConditionIndexModel indexCond2 = toCondition(fNode2, domain, param.getBacktestTime());
				if(indexCond2!=null)
				{
					modifyTechOpIndexAsIndex(indexCond2, fNode2, domain);
				}
				
				String op = opModel.getOpProperty();
				String opText = null;
				if(op.startsWith("(")) opText = "大于";
				else if(op.startsWith(">=")) opText = "大于等于";
				else if(op.startsWith("<")) opText = "小于";
				else if(op.startsWith("<=")) opText = "小于等于";
				else if(op.startsWith("=")) opText = "等于";
				else if(op.startsWith("!=")) opText = "不等于";
				
				if(indexCond!=null && indexCond2!=null)
				{
					indexCond.setIndexName(indexCond.getIndexName() + (opText!=null?opText:"") + indexCond2.getIndexName());
					for (String indexProperty : indexCond2.getIndexProperties()) {
						indexCond.addIndexProperty(indexProperty);
					}
					indexCond.setType("tech");
					if(indexCond!=null)
					{
						conditionModels.add(indexCond);
						indexCond.setUIText(ConditionUITextUtil.getUIText(indexCond, null, null, null));
					}
				}
			}
		}else{
			
			ConditionOpModel conditionOpModel = new ConditionOpModel();
			conditionOpModel.setOpName(opName);
			conditionOpModel.setOpProperty(opProperty);
			//先插入操作符
			conditionModels.add(conditionOpModel);
			if(indexNodeList!=null && indexNodeList.size()>0) {
				for(FocusNode node : indexNodeList) {
					ConditionIndexModel conditionIndexModel = toCondition(node, domain, param.getBacktestTime());
					if (conditionIndexModel != null) {
						conditionModels.add(conditionIndexModel);
						sonSize++;
					}
				}
			}
			conditionOpModel.setUIText(ConditionUITextUtil.getUITextOfArithmetic(conditionModels));
			conditionOpModel.setSonSize(sonSize);
		}
		return conditionModels;
	}

}
