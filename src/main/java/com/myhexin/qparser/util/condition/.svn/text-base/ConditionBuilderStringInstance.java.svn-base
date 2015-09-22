package com.myhexin.qparser.util.condition;

import java.util.ArrayList;
import java.util.List;

import com.myhexin.qparser.except.BacktestCondException;
import com.myhexin.qparser.iterator.BoundaryInfos;
import com.myhexin.qparser.node.BoundaryNode;
import com.myhexin.qparser.node.FocusNode;
import com.myhexin.qparser.node.FocusNode.FocusItem;
import com.myhexin.qparser.node.SemanticNode;
import com.myhexin.qparser.node.StrNode;
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
public class ConditionBuilderStringInstance extends ConditionBuilderAbstract {

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
		
		//为空,返回
		if(boundaryInfos==null || nodes==null) {
			return models;
		}
		
		BoundaryNode bNode = (BoundaryNode)nodes.get(boundaryInfos.bStart);
		
		
		if(bNode!=null && bNode.extInfo!=null) {
			SemanticNode node = null;
			int indexId = 1;
			int newIndexId = bNode.extInfo.getElementNodePos(indexId);
	    	if(newIndexId==-1 && bNode.extInfo!=null && bNode.extInfo.absentDefalutIndexMap!=null) {
	    		node = bNode.extInfo.absentDefalutIndexMap.get(indexId);
	    	}else{
	    		indexId =boundaryInfos.bStart+ newIndexId;
	    		if(indexId>=0 && indexId<nodes.size()) node = nodes.get(indexId);
	    	}
	    	
	    	StrNode strNode = getStrNode(boundaryInfos, nodes);
			
	    	if(node!=null && node.isFocusNode() ) {
	    		FocusNode fNode = (FocusNode) node;
	    		ConditionIndexModel indexCond =  toCondition(fNode, domain, param.getBacktestTime(), strNode, "包含");
	    		if(indexCond!=null) {
	    			models.add(indexCond);
	    			indexCond.setUIText(ConditionUITextUtil.getUITextOfStrInstance(indexCond, "包含", strNode.getText(), null));
	    		}
	    		
	    		
	    		/*ClassNodeFacade classNode = fNode.getIndex();
	    		if(classNode!=null) {
	    			ConditionIndexModel indexCond = new ConditionIndexModel(classNode.getId(),classNode.getText(),domain);
	    			
	    			String macroDomainName = this.getMacroDomainName(classNode, 0);
	    			if(macroDomainName!=null) {
	    				indexCond.setIndexName(classNode.getText(), macroDomainName);
	    				indexCond.setDomain(macroDomainName);
	    			}else
	    				indexCond.setIndexName(classNode.getText(), domain);
					List<PropNodeFacade> props = classNode.getAllProps();
					addPropsToIndexCond(indexCond, props, new Calendar[]{param.getBacktestTime()}, strNode, domain,classNode.getReportType());
					models.add(indexCond);
					
					indexCond.setUIText(ConditionUITextUtil.getUITextOfStrInstance(indexCond, null, strNode.getText(), null));
	    		}*/
	    	}
		}
		return models;
	}

	
	private StrNode getStrNode(BoundaryInfos boundaryInfos, List<SemanticNode> nodes) {
		int i=0;
		if(boundaryInfos!=null) {
			while( (i+boundaryInfos.bStart) < nodes.size()) {
				SemanticNode node = nodes.get(i+boundaryInfos.bStart);
				if(node.isStrNode()) {
					return (StrNode) node;
				}else if(node.isFocusNode()) {
					return getStrNode(node);
				}
				i++;
			}
		}else {
			for(i=0;i<nodes.size();i++) {
				SemanticNode node = nodes.get(i);
				if(node.isStrNode()) {
					return (StrNode) node;
				}else if(node.isFocusNode()) {
					return getStrNode(node);
				}
			}
		}
		return null;
	}
	
	private StrNode getStrNode(SemanticNode node) {
		if(node.isFocusNode()) {
			FocusNode fNode = (FocusNode) node;
			List<FocusItem> items = fNode.getFocusItemList();
			for(FocusItem f : items) {
				if(f.getStr()!=null) {
					return f.getStr();
				}
			}
		}
		
		return null;
	}
}
