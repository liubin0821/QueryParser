package com.myhexin.qparser.util.condition;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.myhexin.qparser.except.BacktestCondException;
import com.myhexin.qparser.iterator.BoundaryInfos;
import com.myhexin.qparser.node.BoundaryNode;
import com.myhexin.qparser.node.FocusNode;
import com.myhexin.qparser.node.NumNode;
import com.myhexin.qparser.node.SemanticNode;
import com.myhexin.qparser.phrase.PhraseInfo;
import com.myhexin.qparser.phrase.SyntacticSemantic.Semantic.SemanticArgument.SemanArgType;
import com.myhexin.qparser.phrase.SyntacticSemantic.Semantic.SemanticBind;
import com.myhexin.qparser.phrase.SyntacticSemantic.Semantic.SemanticBindTo;
import com.myhexin.qparser.phrase.SyntacticSemantic.Semantic.SemanticBindToArgument;
import com.myhexin.qparser.phrase.SyntacticSemantic.Syntactic.SyntacticPattern;
import com.myhexin.qparser.phrase.util.Consts;
import com.myhexin.qparser.resource.model.SemanticCondInfo;
import com.myhexin.qparser.resource.model.SemanticIndexOpModel;
import com.myhexin.qparser.resource.model.SemanticOpModel;
import com.myhexin.qparser.util.condition.model.ConditionIndexModel;
import com.myhexin.qparser.util.condition.model.ConditionModel;
import com.myhexin.qparser.util.condition.model.ConditionOpModel;


/**
 * 排序的Condition
 * 
 * 
 * @author 刘小峰 liuxiaofeng@myhexin.com
 * @Version 创建时间 2015-5-8
 *
 */
public class ConditionBuilderSort extends ConditionBuilderAbstract {

	@Override
	public List<ConditionModel> doBuild(ConditionParam param) throws BacktestCondException {
		BoundaryInfos boundaryInfos = param.getBoundaryInfos();
		SemanticBindTo semanticBindTo = param.getSbt();
		List<SemanticNode> nodes = param.getNodes();
		SemanticOpModel opModel = param.getOpModel();
		Calendar backtestTime = param.getBacktestTime();
		int semanticId = opModel == null ? 0 : opModel.getSemanticId();
		String opName = opModel == null ? "" : opModel.getOpName();
		String domain = param.getDomain();
		String opProperty = "";
		SemanticNode indexNode;
		SemanticNode constantNode;

		List<ConditionModel> conditionModels = new ArrayList<ConditionModel>();
		List<ConditionIndexModel> indexConditionList = null;
		ConditionOpModel conditionOpModel = new ConditionOpModel();

		ArrayList<SemanticBindToArgument> args = semanticBindTo.getSemanticBindToArguments();
		for (SemanticBindToArgument arg : args) {
			if (arg.getType() == SemanArgType.INDEX) { //是指标,创建指标condition
				indexNode = getElement(arg, nodes, semanticId, boundaryInfos.bStart);
				indexConditionList = toConditionList((FocusNode) indexNode, domain, param.getBacktestTime(), "SORT");
			} else if (arg.getType() == SemanArgType.CONSTANT) {
				constantNode = getElement(arg, nodes, semanticId, boundaryInfos.bStart);
				if(constantNode!=null && constantNode.isNumNode()) {
					NumNode numNode = (NumNode)constantNode;
					if(numNode!=null && numNode.getNuminfo()!=null) {
						opProperty = String.valueOf(numNode.getNuminfo().getLongFrom());
						if(numNode.getNuminfo().getUnit()!=null && numNode.getNuminfo().getUnit().equals("%") ) {
							opProperty +="%";
						}
					}else if(constantNode!=null) {
						opProperty = constantNode.getText();
					}
				}else if(constantNode!=null) {
					opProperty = constantNode.getText();
				}
				
			}
		}

		if (opModel != null) {
			List<SemanticIndexOpModel> indexOpModels = opModel.getOpList();
			if (indexOpModels != null && indexOpModels.size() > 0) {
				SemanticIndexOpModel semanticIndexOpModel = indexOpModels.get(0);
				String opValue = semanticIndexOpModel.getIndexOpValue();
				//增长>2,这个2从常量节点里取2,如果数据库配好了值,就从数据库里拿
				if (opValue != null && opValue != "") {
					opProperty = opValue;
				}

				opProperty = semanticIndexOpModel.getIndexOp2() + opProperty;
			}
		}

		conditionOpModel.setOpName(opName);
		conditionOpModel.setOpProperty(opProperty);
		conditionOpModel.setSonSize(1);
		//插入sort操作符
		conditionModels.add(conditionOpModel);
		ConditionIndexModel indexCondition = null;
		if(indexConditionList!=null && indexConditionList.size()>0) {
			indexCondition = indexConditionList.get(0);
			if(indexConditionList.size()>1) {
				indexConditionList = indexConditionList.subList(1, indexConditionList.size());
			}
		}
		if (indexCondition != null) {
			conditionModels.add(indexCondition);
		}
		conditionOpModel.setUIText(ConditionUITextUtil.getUITextOfSort(indexCondition, opProperty));
		
		boolean addHangYeCond = addHangYeSortCondition(nodes, boundaryInfos,domain,backtestTime, conditionModels,conditionOpModel, indexCondition);
		if(addHangYeCond==false && checkIsHangyeCondition(param.getPrevConds())) {
			List<ConditionModel> prevConds = param.getPrevConds();
			addNestedCondtionAsHangYeCondition(conditionModels,conditionOpModel,prevConds);
			param.setPrevConds(null); //已经被用作行业sort,不用重复输出
			
			ConditionModel hangYeCond = null;
			if(prevConds!=null && prevConds.size()>0) {
				hangYeCond = prevConds.get(0);
			}
			
			conditionOpModel.setUIText(ConditionUITextUtil.getUITextOfHangYeSort(indexCondition, opProperty, hangYeCond));
		}
		
		
		/*if( ! addHangYeSortCondition(nodes, boundaryInfos,domain,backtestTime, conditionModels,conditionOpModel) ) {
			//这个加失败了, 才考虑"涨跌幅行业排名前10",行业被忽略问题
			//TODO 这个不能随便加,信号要严格
			//addMissingHangYeCondition(nodes,conditionModels,conditionOpModel, domain, backtestTime);
		}*/
		
		if(indexConditionList.size()>0) {
			conditionModels.addAll(indexConditionList);
			ConditionBuilderUtil.addAndOp(conditionModels, true);
		}
		
		return conditionModels;
	}
	
	private final static String HANG_YE_STR = "行业";
	
	
	//检查前面是不是有行业的Condition指标,比如所属同花顺行业，所属申万行业
	private boolean checkIsHangyeCondition(List<ConditionModel> prevConds) {
		if(prevConds==null || prevConds.size()==0) return false;
		if(prevConds.size()==1){
			ConditionModel cond = prevConds.get(0);
			if(cond.getConditionType() == ConditionModel.TYPE_INDEX) {
				String indexName =( (ConditionIndexModel) cond).getIndexName();
				if( Consts.STR_THS_HY.equals(indexName) || Consts.STR_SW_HY.equals(indexName) ) {
					return true;
				}
			}
		}
		
		return false;
	}
	
	
	//涨跌幅行业排名前10
	//这里的行业在句式中被忽略,这里代码硬处理一下
	/*private void addMissingHangYeCondition(List<SemanticNode> nodes, List<ConditionModel> conditionModels,ConditionOpModel conditionOpModel,String domain, Calendar backtestTime) {
		for(SemanticNode node : nodes) {
			if(node.isFocusNode()==false) continue;
			
			FocusNode fnode = (FocusNode) node;
			if(fnode.hasIndex() && fnode.getIndex()!=null) {
				ClassNodeFacade cNode = fnode.getIndex();
				String text = cNode.getText();
				if(text!=null && text.contains(HANG_YE_STR)) {
					ConditionIndexModel indexCond = toCondition(fnode, domain, backtestTime);
					if(indexCond!=null) {
						conditionModels.add(indexCond);
						conditionOpModel.setSonSize(conditionOpModel.getSonSize() + 1);
								
						//TODO 这里hardcode行业,因为现在只有行业排名这种需求
						conditionOpModel.setOpName(HANG_YE_STR + conditionOpModel.getOpName());
						break;
					}
				}
			}
		}
	}*/
	
	
	private void addNestedCondtionAsHangYeCondition(List<ConditionModel> conditionModels,ConditionOpModel conditionOpModel, List<ConditionModel> prevConds) {
		conditionModels.addAll(prevConds);
		conditionOpModel.setSonSize(conditionOpModel.getSonSize() + prevConds.size());
				
		//TODO 这里hardcode行业,因为现在只有行业排名这种需求
		if("sort".equals(conditionOpModel.getOpName() ) ) {
			conditionOpModel.setOpName(HANG_YE_STR + conditionOpModel.getOpName());
		}
	}
	
	
	//处理行业+排名的情况
	//把行业condition添加到这个sortOp下面, 行业的condition指：所属同花顺行业
	private boolean addHangYeSortCondition(List<SemanticNode> nodes, BoundaryInfos boundaryInfos, String domain, Calendar backtestTime, List<ConditionModel> conditionModels,ConditionOpModel conditionOpModel,ConditionIndexModel indexCondition) throws BacktestCondException {
		
		BoundaryNode bNode = (BoundaryNode) nodes.get(boundaryInfos.bStart);
		BoundaryInfos child_boundaryInfos = bNode.getBindBoundaryInfos();
		if(child_boundaryInfos!=null && child_boundaryInfos.bStart!=boundaryInfos.bStart) {
			BoundaryNode child_bNode = (BoundaryNode) nodes.get(child_boundaryInfos.bStart);
			String child_patternId = child_bNode.getSyntacticPatternId();
					
			ConditionBuilderAbstract builder = null;
			SemanticBindTo sbt1 = null;
					
			if (child_patternId.equals("FREE_VAR") || child_patternId.equals("STR_INSTANCE") || child_patternId.equals("KEY_VALUE")) {
				builder = ConditionBuilderFactory.getConditionBuilder(child_patternId, nodes, child_boundaryInfos);
			}else {
				SyntacticPattern syntPtn = PhraseInfo.getSyntacticPattern(child_patternId); //句式
				if(syntPtn!=null && syntPtn.getSemanticBind()!=null){
					SemanticBind semanticBind = syntPtn.getSemanticBind();
					ArrayList<SemanticBindTo> bindToList = semanticBind.getSemanticBindTos();
					if (bindToList != null) {
						for (SemanticBindTo sbt : bindToList) {
							sbt1 = sbt;
							int child_semanticId = sbt.getBindToId();
							SemanticOpModel child_opModel = SemanticCondInfo.getInstance().getSemanticOpInfo(child_semanticId);
							String child_opName = child_opModel == null ? "" : child_opModel.getOpClazzName();
							builder = ConditionBuilderFactory.getConditionBuilder(child_opName, nodes,child_boundaryInfos);
							//TODO 现在需求只处理了 "包含" 句式,所以可以break
							break;
						}
					}
				}
			}
			if (builder != null) {
				ConditionParam param1 = new ConditionParam(domain);
				param1.setNodes(nodes);
				param1.setBoundaryInfos(child_boundaryInfos);
				param1.setBacktestTime(backtestTime);
				param1.setSbt(sbt1);
				List<ConditionModel> currentConds = builder.buildCondition(param1);
				if(currentConds!=null && currentConds.size()>0) {
					conditionModels.addAll(currentConds);
					conditionOpModel.setSonSize(conditionOpModel.getSonSize() + currentConds.size());
					
					ConditionModel opModel = currentConds.get(0);
					
					//TODO 这里hardcode行业,因为现在只有行业排名这种需求
					if("sort".equals(conditionOpModel.getOpName() ) ) {
						conditionOpModel.setOpName(HANG_YE_STR + conditionOpModel.getOpName());
						conditionOpModel.setUIText(ConditionUITextUtil.getUITextOfHangYeSort(indexCondition, null, opModel));
					}
					return true;
				}
			}
		}
		return false;
	}
	
}
