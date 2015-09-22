package com.myhexin.qparser.util.condition;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.myhexin.qparser.except.BacktestCondException;
import com.myhexin.qparser.iterator.BoundaryInfos;
import com.myhexin.qparser.node.BoundaryNode;
import com.myhexin.qparser.node.FocusNode;
import com.myhexin.qparser.node.SemanticNode;
import com.myhexin.qparser.onto.ClassNodeFacade;
import com.myhexin.qparser.onto.PropNodeFacade;
import com.myhexin.qparser.util.condition.model.ConditionIndexModel;
import com.myhexin.qparser.util.condition.model.ConditionModel;


/**
 * STR_INSTANCE
 * FREE_VAR
 * KEY_VALUE
 * 
 * @author 刘小峰 liuxiaofeng@myhexin.com
 * @Version 创建时间 2015-5-8
 *
 */
public class ConditionBuilderKeyValue extends ConditionBuilderAbstract {

	
	/*private SemanticNode getNode(int indexId, BoundaryNode bNode, BoundaryInfos boundaryInfos, List<SemanticNode> nodes) {
		SemanticNode node = null;
		int newIndexId = bNode.extInfo.getElementNodePos(indexId);
    	if(newIndexId==-1 && bNode.extInfo!=null && bNode.extInfo.absentDefalutIndexMap!=null) {
    		node = bNode.extInfo.absentDefalutIndexMap.get(indexId);
    	}else{
    		indexId =boundaryInfos.bStart+ newIndexId;
    		if(indexId>=0 && indexId<nodes.size()) node = nodes.get(indexId);
    	}
    	return node;
	}*/
	
	@Override
	public List<ConditionModel> doBuild(ConditionParam param) throws BacktestCondException {
		BoundaryInfos boundaryInfos = param.getBoundaryInfos();
		//SemanticBindTo semanticBindTo = param.getSbt();
		List<SemanticNode> nodes = param.getNodes();
		//SemanticOpModel opModel = param.getOpModel();
		//int semanticId = opModel == null ? 0 : opModel.getSemanticId();
		//String opName = opModel == null ? "" : opModel.getOpName();
		String domain = param.getDomain();
		List<ConditionModel> models = new ArrayList<ConditionModel>();
		
		//其他指标
		
		//为空,返回
		if(boundaryInfos==null || nodes==null) {
			return models;
		}
		
		BoundaryNode bNode = (BoundaryNode)nodes.get(boundaryInfos.bStart);
		
		
		if(bNode!=null && bNode.extInfo!=null) {
			SemanticNode keyNode = ConditionBuilderUtil.getNodeKeyValue(1, bNode, boundaryInfos, nodes);
	    	SemanticNode valueNode = ConditionBuilderUtil.getNodeKeyValue(2, bNode, boundaryInfos, nodes);
	    	
			
	    	if(keyNode!=null && keyNode.isFocusNode() ) {
	    		FocusNode fNode = (FocusNode) keyNode;
	    		ClassNodeFacade classNode = fNode.getIndex();
	    		if(classNode!=null) {
	    			
	    			String macroDomainName = this.getMacroDomainName(classNode, 0);
	    			
	    			ConditionIndexModel indexCond = new ConditionIndexModel(classNode.getId(),classNode.getText(),domain);
	    			if(macroDomainName!=null) {
	    				indexCond.setIndexName(classNode.getText(), macroDomainName);
	    				indexCond.setDomain(macroDomainName);
	    			}else
	    				indexCond.setIndexName(classNode.getText(), domain);
					List<PropNodeFacade> props = classNode.getAllProps();
					if(valueNode!=null) {
						this.addPropsToIndexCond(indexCond, props, new Calendar[]{param.getBacktestTime()}, valueNode, domain, classNode.getReportType(), "是");	
						indexCond.setUIText(ConditionUITextUtil.getUIText(indexCond, "是", valueNode.getText(), null));
					}else{
						this.addPropsToIndexCond(indexCond, props, new Calendar[]{param.getBacktestTime()}, null, null,classNode.getReportType(), "是");
						indexCond.setUIText(ConditionUITextUtil.getUIText(indexCond, "是", null, null));
					}
					
					
					/*if(valueNode!=null) {
						indexCond.addProp(ConditionBuilderAbstract.CONTAIN, valueNode.getText(), true);
					}*/
					models.add(indexCond);
	    		}
	    	}
		}
		return models;
	}

}
