package com.myhexin.qparser.util.condition;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.myhexin.qparser.define.EnumDef.NodeType;
import com.myhexin.qparser.define.EnumDef.Unit;
import com.myhexin.qparser.except.BacktestCondException;
import com.myhexin.qparser.iterator.BoundaryInfos;
import com.myhexin.qparser.node.FocusNode;
import com.myhexin.qparser.node.NumNode;
import com.myhexin.qparser.node.SemanticNode;
import com.myhexin.qparser.phrase.SyntacticSemantic.Semantic.SemanticArgument.SemanArgType;
import com.myhexin.qparser.phrase.SyntacticSemantic.Semantic.SemanticBindTo;
import com.myhexin.qparser.phrase.SyntacticSemantic.Semantic.SemanticBindToArgument;
import com.myhexin.qparser.phrase.SyntacticSemantic.Semantic.SemanticBindToArgument.BindToType;
import com.myhexin.qparser.resource.model.SemanticIndexOpModel;
import com.myhexin.qparser.resource.model.SemanticOpModel;
import com.myhexin.qparser.util.Util;
import com.myhexin.qparser.util.condition.model.ConditionIndexModel;
import com.myhexin.qparser.util.condition.model.ConditionModel;
import com.myhexin.qparser.util.condition.model.ConditionOpModel;


/**
 * 比较运算的Condition
 * 
 * @author 刘小峰 liuxiaofeng@myhexin.com
 * @Version 创建时间 2015-5-8
 *
 */
public class ConditionBuilderComparator extends ConditionBuilderAbstract {
	
	//private final static Pattern eqNum = Pattern.compile("^([0-9]{1,5})(.*)(左右)$");
	//private final static Pattern eqNum = Pattern.compile("^([0-9]{1,10})(.*)$");
	private final static Pattern eqNum = Pattern.compile("^([0-9]{1,10}\\.?[0-9]{0,10})(.*)$");
	//private final static Pattern eqNumDigital = Pattern.compile("^([0-9]{1,5})(.*)$");
	private final static DecimalFormat decimalFormat = new DecimalFormat("#0.0000");
	
	@Override
	public List<ConditionModel> doBuild(ConditionParam param) throws BacktestCondException {
		BoundaryInfos boundaryInfos = param.getBoundaryInfos();
		SemanticBindTo semanticBindTo = param.getSbt();
		List<SemanticNode> nodes = param.getNodes();
		SemanticOpModel opModel = param.getOpModel();
		int semanticId = opModel == null ? 0 : opModel.getSemanticId();
		String domain = param.getDomain();
		String opProperty = "";
		FocusNode indexNode = null;
		SemanticNode constantNode = null;
		List<ConditionIndexModel> indexConds = null;
		//ConditionIndexModel indexCondition = null;
		
		/*
		 * prevOpModel表示嵌套语义
		 * 比如今天股价-昨天股价>0
		 * 一个是a-b的语义,一个是c>0的语义
		 */
		ConditionOpModel prevOpModel = null; //嵌套语义中上一个语义的Op
		List<ConditionModel> prevConds = param.getPrevConds();

		
		/*
		 * 通过语义配置,看语义配置中有多少指标,Constant
		 * 然后从nodes中,拿到指标节点， NumberNode
		 * indexNode
		 * constantNode
		 * opProperty
		 * 
		 */
		ArrayList<SemanticBindToArgument> args = semanticBindTo.getSemanticBindToArguments();
		for (SemanticBindToArgument arg : args) {
			if (arg.getType() == SemanArgType.INDEX || arg.getType() == SemanArgType.INDEXLIST) { //是指标,创建指标condition
				if(prevConds!=null ) {
					for(ConditionModel cond : prevConds) {
						if(cond.getConditionType() == ConditionModel.TYPE_OP) {
							prevOpModel = (ConditionOpModel) cond;
							break;
						}
					}
				}else if(arg.getBindToType() == BindToType.SYNTACTIC_ELEMENT || arg.getBindToType() == BindToType.SEMANTIC) {
					indexNode =  (FocusNode) getElement(arg, nodes, semanticId, boundaryInfos.bStart);
				}/*else if (arg.getBindToType() == BindToType.SEMANTIC && prevConds!=null) { //嵌套语义的情况
					for(ConditionModel cond : prevConds) {
						if(cond.getConditionType() == ConditionModel.TYPE_OP) {
							prevOpModel = (ConditionOpModel) cond;
							break;
						}
					}
				}*/
				//indexNode = (FocusNode) getElement(arg, nodes, semanticId, boundaryInfos.bStart);
			} else if (arg.getType() == SemanArgType.CONSTANT || arg.getType() == SemanArgType.CONSTANTLIST
					|| arg.getType() == SemanArgType.ANY) {
				constantNode = getElement(arg, nodes,semanticId, boundaryInfos.bStart);
				opProperty = constantNode.getText();
			}
		}
		

		/*
		 *把指标变成ConditionIndexModel 
		 * 
		 */
		List<ConditionModel> conditionModels = new ArrayList<ConditionModel>();
		if (indexNode != null && indexNode.isFocusNode()) {
			//indexCondition = toCondition((FocusNode) indexNode, domain, param.getBacktestTime());
			indexConds = this.toConditionList((FocusNode) indexNode, domain, param.getBacktestTime(), "COMPARE");
		}
		
		/*
		 * 如果parser_cond_ops表定义了这个语义
		 * 取到opProperty
		 */
		if (opModel != null) {
			List<SemanticIndexOpModel> indexOpModels = opModel.getOpList();
			if (indexOpModels != null && indexOpModels.size() > 0) {
				//TODO 这段
				SemanticIndexOpModel semanticIndexOpModel = indexOpModels.get(0);
				String opValue = semanticIndexOpModel.getIndexOpValue();
				//增长>2,这个2从常量节点里取2,如果数据库配好了值,就从数据库里拿
				if (opValue != null && opValue != "") {
					opProperty = opValue;
				}

				//百分比的指标,且常量节点没有单位的时候,操作符后面的属性要转
				//涨跌幅大于3 -> 大于0.03
				if (indexNode!=null && indexNode.getPropUnit() == Unit.PERCENT) {
					if (constantNode != null && constantNode.getType() == NodeType.NUM) {
						NumNode numNode = (NumNode) constantNode;
						if (numNode.getUnit() != Unit.PERCENT) {
							try {
								double numProperty = Double.valueOf(opProperty);
								numProperty /= 100;
								opProperty = Util.numericFormat(numProperty);
							} catch (Exception e) {
								//convert 失败的不抛错,目前发现%单位没有被去除的情况只有0%
								opProperty = "0";
							}
						}
					}
				}

				if (indexConds!=null && indexConds != null) {
					for(ConditionIndexModel indexCondition : indexConds){
						if(semanticIndexOpModel.getIndexOp2()!=null && semanticIndexOpModel.getIndexOp2().equals("=")) {
							String new_op = changeEq2Gtlt(opProperty);
							if(new_op!=null)
								indexCondition.addIndexProperty(new_op);
							else{
								//prevOpModel.setOpProperty(semanticIndexOpModel.getIndexOp2() + opProperty);
								indexCondition.addIndexProperty(semanticIndexOpModel.getIndexOp2() + opProperty);
							}
						}
						
						//>,<这种过滤条件已经在condition.properties中,所以传null
						//indexCondition.setUIText(ConditionUITextUtil.getUIText(indexCondition, semanticIndexOpModel.getIndexOp2(), opProperty, null));
						indexCondition.setUIText(ConditionUITextUtil.getUIText(indexCondition, null, null, null));
					}
				}else if(prevOpModel!=null) {
					if(semanticIndexOpModel.getIndexOp2()!=null && semanticIndexOpModel.getIndexOp2().equals("=")) {
						String new_op = changeEq2Gtlt(opProperty);
						if(new_op!=null)
							prevOpModel.setOpProperty(new_op);
						else{
							prevOpModel.setOpProperty(semanticIndexOpModel.getIndexOp2() + opProperty);
						}
					}
					
					
					//opProperty
					/*Matcher m = eqNum.matcher(opProperty);
					if(m.matches() && semanticIndexOpModel.getIndexOp2()!=null && semanticIndexOpModel.getIndexOp2().equals("=")) {
						String num = m.group(1);
						Double numVal = Double.parseDouble(num);
						double range = numVal*0.05;
						opProperty = String.format("(%s <%s", decimalFormat.format(numVal-range), decimalFormat.format(numVal + range));
						prevOpModel.setOpProperty(opProperty);
					}else{
						prevOpModel.setOpProperty(semanticIndexOpModel.getIndexOp2() + opProperty);
					}*/
					
					//prevOpModel.setOpProperty(semanticIndexOpModel.getIndexOp2() + opProperty);
					String uiText = prevOpModel.getUIText();
					if(uiText!=null) {
						uiText = uiText + semanticIndexOpModel.getIndexOp2() + opProperty;
						prevOpModel.setUIText(uiText);
					}
				}
			}
		}
		
		//有比较符的默认只会有一个指标
		if (indexConds != null) {
			conditionModels.addAll(indexConds);
		}

		return conditionModels;
	}
	
	private String changeEq2Gtlt(String opProperty) {
		Matcher m = eqNum.matcher(opProperty);
		if(m.matches() ) {
			String num = m.group(1);
			Double numVal = Double.parseDouble(num);
			double range = numVal*0.1;
			String new_opProperty = String.format("(%s <%s", decimalFormat.format(numVal-range), decimalFormat.format(numVal + range));
			return new_opProperty;
		}else {
			return null;
		}
	}
}
