package com.myhexin.qparser.util.condition;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.myhexin.qparser.except.BacktestCondException;
import com.myhexin.qparser.iterator.BoundaryInfos;
import com.myhexin.qparser.node.FocusNode;
import com.myhexin.qparser.node.NumNode;
import com.myhexin.qparser.node.SemanticNode;
import com.myhexin.qparser.number.NumRange;
import com.myhexin.qparser.onto.ClassNodeFacade;
import com.myhexin.qparser.onto.PropNodeFacade;
import com.myhexin.qparser.phrase.SyntacticSemantic.Semantic.SemanticArgument.SemanArgType;
import com.myhexin.qparser.phrase.SyntacticSemantic.Semantic.SemanticBindTo;
import com.myhexin.qparser.phrase.SyntacticSemantic.Semantic.SemanticBindToArgument;
import com.myhexin.qparser.phrase.util.Consts;
import com.myhexin.qparser.resource.model.SemanticIndexOpModel;
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
public class ConditionBuilderZhubi extends ConditionBuilderAbstract {

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
	
		
		//把args转成Condition
		ArrayList<SemanticBindToArgument> args = semanticBindTo.getSemanticBindToArguments();
		ConditionIndexModel indexCond = null;
		String opProperty = null;
		String propIntNumTimes = null; //"_整型数值(次)"
		for(SemanticBindToArgument arg :args) {
			if (arg.getType() == SemanArgType.INDEX) { //是指标,创建指标condition
				
				SemanticNode indexNode = getElement(arg, nodes, semanticId, boundaryInfos.bStart);
				if(indexNode!=null && indexNode.isFocusNode()) {
					FocusNode fNode = (FocusNode) indexNode;
					if (fNode.isBoundToIndex()) {
						continue;
					}
					
					ClassNodeFacade index = fNode.getIndex();
					if(index!=null){
						indexCond = new ConditionIndexModel(index.getId(),index.getText(),domain);
						//boolean ismarket = this.isUnderAbsMarketEnv(index, 0);
						indexCond.setIndexName(index.getText(), domain);
						for(PropNodeFacade prop : index.getAllProps()){
							if(prop.isDateProp() && prop.getValue()!=null) {
								this.addDateTimeProp(indexCond, prop, new Calendar[]{param.getBacktestTime()}, false, index.getReportType());
								continue;
								
							}
							
							String text = prop.getText();
							String value = null;
							if(text.equals("_整型数值(次)")){
								propIntNumTimes = text;
							}else if(text.equals("_整型数值(股)")){
								value = getNumValue("股","long", prop.getValue());
								if(value == null){
									String numStr = getNumValue("手","long",prop.getValue());
									if(numStr != null)
										value = String.valueOf(Long.parseLong(numStr)*100);
								}
							}else if(text.equals("_浮点型数值(元)")){
								value = getNumValue("元","double",prop.getValue());
							}else if(text.equals("_整型数值(档)")){
								value = getNumValue("档", "long",prop.getValue());
							}else{
								if(prop.getValue()!=null)
									value = prop.getValue().getText();
							}
							indexCond.addProp(text, value);
						}
						models.add(indexCond);
					}
					
				}
				
			}else if (arg.getType() == SemanArgType.CONSTANT) {
				SemanticNode constantNode = getElement(arg, nodes, semanticId, boundaryInfos.bStart);
				if(constantNode!=null){
					if(constantNode.isNumNode()) {
						NumNode numNode = (NumNode) constantNode;
						opProperty = String.valueOf(numNode.getNuminfo().getLongFrom());
					}else{
						opProperty = constantNode.getText();
					}
				}
					
			}
			
		}
		
		//设置opProperty
		if (indexCond != null && opProperty != null && opModel != null) {
			List<SemanticIndexOpModel> indexOpModels = opModel.getOpList();
			if (indexOpModels!=null && indexOpModels.size() > 0) {
				//TODO 这段
				SemanticIndexOpModel semanticIndexOpModel = indexOpModels.get(0);
				//增长>2,这个2从常量节点里取2
				indexCond.addIndexProperty(semanticIndexOpModel.getIndexOp2()+ opProperty);
			
				if(propIntNumTimes!=null) {
					indexCond.addProp(propIntNumTimes, semanticIndexOpModel.getIndexOp()+opProperty);
				}
			}
			
			
		}
		
		if(indexCond!=null) {
			indexCond.setUIText(ConditionUITextUtil.getUITextOfZhubi(indexCond));
		}
		return models;
	}
	
	/**逐笔：根据单位取绑定数值信息*/
	private String getNumValue(String unit,String type,SemanticNode node){
		if(node!=null && node.isNumNode()){
			NumNode numNode = (NumNode) node;
			NumRange numRange = numNode.getNuminfo();
			if(numRange != null ){
				if((numRange.getUnit()==null && unit.equals("元")) || numRange.getUnit().equals(unit)){
					String[] compList = Consts.COMP_ARR;
					String text = numNode.getText();
					String op = null;
					for(String comp:compList){
						if(text.startsWith(comp)){
							if(comp.equals("大于等于")){		
								op = ">=";
							}else if(comp.equals("大于")||comp.equals("超过")||comp.equals("高于")||comp.equals("多于")){	
								op = ">";
							}else if(comp.equals("小于等于")){	
								op = "<=";
							}else if(comp.equals("小于")||comp.equals("低于")||comp.equals("少于")){	
								op = "<";
							}else if(comp.equals("等于")){	
								op = "=";
							}else{	
								op = comp;	
							}
							break;
						}
					}
					if(type.equals("long"))
						return (op==null?"":op)+String.valueOf(numRange.getLongFrom());
					else
						return (op==null?"":op)+String.valueOf(numRange.getDoubleFrom());
				}
			}
		}
		return null;
	}
}
