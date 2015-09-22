package com.myhexin.qparser.util.condition;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.myhexin.qparser.except.BacktestCondException;
import com.myhexin.qparser.iterator.BoundaryInfos;
import com.myhexin.qparser.node.FocusNode;
import com.myhexin.qparser.node.NumNode;
import com.myhexin.qparser.node.SemanticNode;
import com.myhexin.qparser.phrase.SyntacticSemantic.Semantic.SemanticArgument.SemanArgType;
import com.myhexin.qparser.phrase.SyntacticSemantic.Semantic.SemanticBindTo;
import com.myhexin.qparser.phrase.SyntacticSemantic.Semantic.SemanticBindToArgument;
import com.myhexin.qparser.resource.model.SemanticOpModel;
import com.myhexin.qparser.util.condition.model.ConditionIndexModel;
import com.myhexin.qparser.util.condition.model.ConditionModel;

/**
 * 技术指标Condition
 * 
 * @author 刘小峰 liuxiaofeng@myhexin.com
 * @Version 创建时间 2015-5-8
 * 
 */
public class ConditionBuilderTechOpAsIndex extends ConditionBuilderAbstract {

	@Override
	public List<ConditionModel> doBuild(ConditionParam param) throws BacktestCondException {
		BoundaryInfos boundaryInfos = param.getBoundaryInfos();
		SemanticBindTo semanticBindTo = param.getSbt();
		List<SemanticNode> nodes = param.getNodes();
		SemanticOpModel opModel = param.getOpModel();
		int semanticId = opModel == null ? 0 : opModel.getSemanticId();
		//String opName = opModel == null ? "" : opModel.getOpName();
		String domain = param.getDomain();
		List<ConditionModel> models = new LinkedList<ConditionModel>();
		
		//把形态都输出成指标
		/*ConditionModel parentModel = this.buildParentOpModel(opModel);
		if (parentModel != null) {
			models.add(parentModel);
		}*/

		//把args转成Condition
		ArrayList<SemanticBindToArgument> args = semanticBindTo.getSemanticBindToArguments();
		int argIndexCount = 0;
		SemanticNode numNode = null;
		for(SemanticBindToArgument arg :args) {
			if(arg.getType() == SemanArgType.INDEX) {
				argIndexCount ++;
				SemanticNode indexNode = getElement(arg, nodes, semanticId, boundaryInfos.bStart);
				if(indexNode!=null && indexNode.isFocusNode()) {
					FocusNode fNode = (FocusNode) indexNode;
					/*if (fNode.isBoundToIndex()) {
						continue;
					}*/
					
					ConditionIndexModel indexCond = toCondition(fNode, domain, param.getBacktestTime());
					indexCond.setType("tech");
					if(indexCond!=null)
					{
						modifyTechOpIndexAsIndex(indexCond, fNode, domain);
						models.add(indexCond);
					}
				}
			}if(arg.getType() == SemanArgType.CONSTANT) {
				numNode = getElement(arg, nodes, semanticId, boundaryInfos.bStart);
			}
			
		}
		
		//处理macd金叉, kdj金叉这种情况
		/* TODO 这么处理的原因是
		 * 数据端 不支持实时计算,他们把技术指标的数据保存起来,格式如下
		 * 300033 macd金叉   1
		 * 300034 macd金叉   0
		 */
		if(argIndexCount==1 && models.size()==1) {
			ConditionModel model = models.get(0);
			if(model.getConditionType() == ConditionModel.TYPE_INDEX) {
				ConditionIndexModel indexCond = (ConditionIndexModel) model;
				indexCond.setIndexName(indexCond.getIndexName() + opModel.getOpName(), domain);
				indexCond.addIndexProperty(TECH_OP_CONTAIN);
				indexCond.setUIText(ConditionUITextUtil.getUIText(indexCond, null, null, null));
				if(numNode!=null && numNode.isNumNode()) {
					NumNode _theNode = (NumNode) numNode;
					if(_theNode.getNuminfo()!=null){
						indexCond.addIndexProperty(opModel.getOpName() + " " + _theNode.getNuminfo().getDoubleFrom());
					}
				}
			}
		}else if(argIndexCount==2 && models.size()==2) {
			//5日均线金叉10日均线 的情况
			//实时计算的结果是: {金叉} {5日均线} {10日均线}
			//临时方案: 5日均线金叉10日均线 
			ConditionModel model1 = models.get(0);
			ConditionModel model2 = models.get(1);
			if(model1.getConditionType() == ConditionModel.TYPE_INDEX && model2.getConditionType() == ConditionModel.TYPE_INDEX) {
				ConditionIndexModel indexCond1 = (ConditionIndexModel) model1;
				ConditionIndexModel indexCond2 = (ConditionIndexModel) model2;
				indexCond1.setIndexName(indexCond1.getIndexName() + opModel.getOpName() + indexCond2.getIndexName(),domain);
				indexCond1.addIndexProperty(TECH_OP_CONTAIN);
				//op,opProp已经被合并到indexCondition中,所以这里传入NULL就可以了
				//indexCond1.setUIText(ConditionUITextUtil.getUIText(indexCond1, opModel.getOpName(), indexCond2.getIndexName(), null));
				indexCond1.setUIText(ConditionUITextUtil.getUIText(indexCond1, null, null, null));
				List<String> props = indexCond1.getIndexProperties();
				if(props!=null) {
					for(Iterator<String> it=props.iterator();it.hasNext();) {
						String ps = it.next();
						if(ps.indexOf(N_DAY)>=0) {
							it.remove();
							break;
						}
					}
				}
				models.clear();
				models.add(indexCond1);
			}
		}else{
			ConditionModel parentModel = this.buildParentOpModel(opModel);
			if (parentModel != null) {
				models.add(0,parentModel);
			}
			//getUITextOfTechIndex
			parentModel.setUIText(ConditionUITextUtil.getUITextOfTechIndex(models));
			
		}

		return models;
	}

	public final static String TECH_OP_CONTAIN = "包含1";
}
