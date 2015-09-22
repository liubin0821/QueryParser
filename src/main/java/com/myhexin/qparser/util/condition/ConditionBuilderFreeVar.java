package com.myhexin.qparser.util.condition;

import java.util.ArrayList;
import java.util.List;

import com.myhexin.qparser.except.BacktestCondException;
import com.myhexin.qparser.iterator.BoundaryInfos;
import com.myhexin.qparser.node.BoundaryNode;
import com.myhexin.qparser.node.FocusNode;
import com.myhexin.qparser.node.SemanticNode;
import com.myhexin.qparser.onto.ClassNodeFacade;
import com.myhexin.qparser.util.condition.model.ConditionIndexModel;
import com.myhexin.qparser.util.condition.model.ConditionModel;


/**
 * FREE_VAR
 * 
 * @author 刘小峰 liuxiaofeng@myhexin.com
 * @Version 创建时间 2015-5-8
 *
 */
public class ConditionBuilderFreeVar extends ConditionBuilderAbstract {

	@Override
	public List<ConditionModel> doBuild(ConditionParam param) throws BacktestCondException {
		BoundaryInfos boundaryInfos = param.getBoundaryInfos();
		//SemanticBindTo semanticBindTo = param.getSbt();
		List<SemanticNode> nodes = param.getNodes();
		//SemanticOpModel opModel = param.getOpModel();
		//int semanticId = opModel == null ? 0 : opModel.getSemanticId();
		//String opName = opModel == null ? "" : opModel.getOpName();
		String domain = param.getDomain();
		List<ConditionModel> models = new ArrayList<ConditionModel>(1);
		
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
	    	if(node!=null && node.isFocusNode() ) {
	    		FocusNode fNode = (FocusNode) node;
	    		
	    		if(isBindtoIndexAndIsPersonDomain(fNode) ) {
	    			return models;
	    		}
	    		
				List<ConditionIndexModel> indexConds = this.toConditionList(fNode, domain, param.getBacktestTime(), "FREE_VAR");
	    		if(indexConds!=null) {
					modifyTechOpIndexAsIndex(indexConds, fNode, domain);
	    		}
	    		models.addAll(indexConds);
	    		for(ConditionIndexModel cond : indexConds) {
	    			cond.setUIText(ConditionUITextUtil.getUIText(cond, null, null, fNode.getIndex()));
	    		}
	    	}
		}
		return models;
	}

	/**
	 * 是不是被绑定到其他指标
	 * 并且
	 * 是abs_人属性
	 * 
	 * @param fNode
	 * @return
	 */
	private boolean isBindtoIndexAndIsPersonDomain(FocusNode fNode) {
		if(fNode.isBoundToIndex() || (fNode.hasIndex() && fNode.getIndex()!=null && fNode.getIndex().isBoundToIndex())) {
			ClassNodeFacade cNode = fNode.getIndex();
			if(cNode.getCategorysAll()!=null && cNode.getCategorysAll().contains("abs_人") ) {
				return true;
			}
		}
		
		return false;
	}
	
}
