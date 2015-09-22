/**
 * 
 */
package com.myhexin.qparser.util.condition;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import com.myhexin.qparser.ParseResult;
import com.myhexin.qparser.define.EnumDef.LogicType;
import com.myhexin.qparser.except.BacktestCondException;
import com.myhexin.qparser.iterator.BoundaryInfos;
import com.myhexin.qparser.iterator.SyntacticIteratorImpl;
import com.myhexin.qparser.iterator.SyntacticTimeSeqIteratorImpl;
import com.myhexin.qparser.node.BoundaryNode;
import com.myhexin.qparser.node.SemanticNode;
import com.myhexin.qparser.phrase.PhraseInfo;
import com.myhexin.qparser.phrase.SyntacticSemantic.Semantic.SemanticBind;
import com.myhexin.qparser.phrase.SyntacticSemantic.Semantic.SemanticBindTo;
import com.myhexin.qparser.phrase.SyntacticSemantic.Syntactic.SyntacticPattern;
import com.myhexin.qparser.phrase.util.Consts;
import com.myhexin.qparser.resource.ResourceInst;
import com.myhexin.qparser.resource.model.DomainInfo;
import com.myhexin.qparser.resource.model.SemanticCondInfo;
import com.myhexin.qparser.resource.model.SemanticOpModel;
import com.myhexin.qparser.util.condition.model.BackTestCondAnnotation;
import com.myhexin.qparser.util.condition.model.ConditionModel;
import com.myhexin.qparser.util.condition.model.ConditionOpModel;

/**
 * @author chenhao
 *
 */
public class ConditionBuilder {
	private static Logger logger_ = Logger.getLogger(ConditionBuilder.class);
	
	public static List<BackTestCondAnnotation> buildCondition(ParseResult pr, String query, Calendar backtestTime) throws BacktestCondException {
		List<BackTestCondAnnotation> jsonList = new ArrayList<BackTestCondAnnotation>();
		if (pr.qlist != null && pr.qlist.size() > 0) {
			int i = 0;
			for (ArrayList<SemanticNode> nodes : pr.qlist) {
				String domainStr = ConditionBuilderUtil.getDomainStr(nodes);
				
				//如果领域不在includeDomainNames List中,不要这个解析结果
				if (domainStr != null && domainStr.length() > 0 && DomainInfo.getInstance().contains(domainStr) == false) {
					//logger_.warn(domainStr + ": domain not in the include list");
					continue;
				}
				
				List<ConditionModel> condList = buildCondition(nodes, domainStr, backtestTime);
				ConditionBuilderUtil.mergeStkcodeCondition(condList);
				

				//最外层加一个andOp
				ConditionBuilderUtil.addAndOp(condList);
				
				//信息领域特殊buildcondition特殊处理
				/*if (domainStr != null && domainStr.equals(Consts.CONST_absXinxiDomain)) {
					condList = buildEventDomainCondition(nodes, domainStr, backtestTime);
				} else {
					condList = buildCondition(nodes, domainStr, backtestTime);
				}*/
				if (condList == null)
					continue;

				String jsonCond = ConditionBuilderUtil.createJson(condList);
				String condHtml = ConditionBuilderUtil.createConditionHtml(condList);
				
				//其他字段
				BackTestCondAnnotation btcAnnotation = new BackTestCondAnnotation();
				String standardOutput = ConditionBuilderUtil.getStandardOutput(nodes);
				btcAnnotation.setOutputs(standardOutput);
				btcAnnotation.setDomainStr(domainStr);
				btcAnnotation.setQuery(query);
				btcAnnotation.setBacktestTimeRange(backtestTime);
				btcAnnotation.setConditionHtml(condHtml);

				if (pr.standardQueriesScore != null && pr.standardQueriesScore.size() > 0
						&& i < pr.standardQueriesScore.size()) {
					btcAnnotation.setScore(pr.standardQueriesScore.get(i));
				}

				if (pr.standardQueries != null && pr.standardQueries.size() > 0 && i < pr.standardQueries.size()) {
					btcAnnotation.setStandardQuery(pr.standardQueries.get(i));
				}

				//走新接口,还是老接口设置和判断
				Boolean isAllUseOldApi = ResourceInst.getInstance().isAllUseOldApi();
				Boolean isAllUseNewapi = ResourceInst.getInstance().isAllUseNewapi();
				if (isAllUseOldApi == true) {
					btcAnnotation.setQueryType(null);
				} else if (isAllUseNewapi
						|| (ConditionBuilderUtil.isZhishuQuery(pr.qlist, i, btcAnnotation.getDomainStr()))) {
					btcAnnotation.setQueryType(Consts.ZHISHU_PinYin_STR);
				}  else if (isAllUseNewapi
						|| ConditionBuilderUtil.isNewQuery(condList) ) { //行业排名也走新接口
					btcAnnotation.setQueryType(Consts.ZHISHU_PinYin_STR);
				} else if (isAllUseNewapi
						|| (ConditionBuilderUtil.isEventQuery(pr.qlist, i, btcAnnotation.getDomainStr()))) {
					btcAnnotation.setQueryType(Consts.XINXI_STR);//TODO
				} else {
					btcAnnotation.setQueryType(null);
				}

				btcAnnotation.setResultCondJson(jsonCond);

				//计算公式输出
				/*BackTestExprTreeNode treeNode = BackTestExprTreeBuilder.buildExprTree(bkq.getBtConds());
				btcAnnotation.setCalcExprTree(treeNode);
				if(treeNode!=null) {
					btcAnnotation.setCalcExprTreeStr(treeNode.toExpr());
				}*/
				jsonList.add(btcAnnotation);
				i++;
			}
		}
		return jsonList;
	}

	private static List<ConditionModel> buildCondition(ArrayList<SemanticNode> nodes, String domainStr, Calendar backtestTime)
			throws BacktestCondException {
		List<ConditionModel> result = new ArrayList<ConditionModel>();
		//通过第一个boundarynode来判断是否是分时策略问句
		BoundaryNode firstBoundaryNode = null;
		for (SemanticNode node : nodes) {
			if (node.isBoundaryStartNode()) {
				firstBoundaryNode = (BoundaryNode) node;
				break;
			}
		}
		if (firstBoundaryNode != null && firstBoundaryNode.contextLogicType == LogicType.TIMESEQUENCE) {
			SyntacticTimeSeqIteratorImpl iterator = new SyntacticTimeSeqIteratorImpl(nodes);
			result = timeSeqRecursiveBuild(iterator, nodes, domainStr, backtestTime);
		} else {
			SyntacticIteratorImpl iterator = new SyntacticIteratorImpl(nodes);
			if (backtestTime == null) {
				backtestTime = Calendar.getInstance();
			}
			result = recursiveBuild(iterator, nodes, domainStr, backtestTime);
		}
		return result;
	}

	private static List<ConditionModel> timeSeqRecursiveBuild(SyntacticTimeSeqIteratorImpl iterator,
			List<SemanticNode> nodes, String domainStr, Calendar backtestTime) throws BacktestCondException {
		List<ConditionModel> rtnCondList = new ArrayList<ConditionModel>();
		BoundaryInfos boundaryInfos = iterator.next();
		SyntacticIteratorImpl syntacticIteratorImpl = iterator.nextSyntacticIterator();
		BoundaryNode boundaryNode = (BoundaryNode) nodes.get(boundaryInfos.bStart);

		ConditionOpModel conditionOpModel = new ConditionOpModel();
		if (boundaryNode.contextLogicType != null) {
			conditionOpModel.setOpName(boundaryNode.contextLogicType.toString().toLowerCase());
			//从timesequence逻辑去call and和or的recursiveBuild方法,要把原来的timesequence节点变成and节点
			boundaryNode.contextLogicType = LogicType.AND;
		}

		List<ConditionModel> condList = new ArrayList<ConditionModel>();
		condList = recursiveBuild(syntacticIteratorImpl, nodes, domainStr, backtestTime);
		if (!iterator.hasNext()) {
			return condList;
		}

		rtnCondList.add(conditionOpModel);
		rtnCondList.addAll(condList);

		//之后所有构建出的condition
		List<ConditionModel> futureCondition = timeSeqRecursiveBuild(iterator, nodes, domainStr, backtestTime);
		rtnCondList.addAll(futureCondition);
		conditionOpModel.setSonSize(futureCondition.size() + condList.size());
		return rtnCondList;
	}

	private static List<ConditionModel> recursiveBuild(Iterator<BoundaryInfos> iterator, List<SemanticNode> nodes,
			String domainStr, Calendar backtestTime) throws BacktestCondException {
		List<ConditionModel> rtnCondList = new ArrayList<ConditionModel>();
		BoundaryInfos boundaryInfos = iterator.next();
		//lxf 有NULLPointerException
		if(boundaryInfos==null) {
			return rtnCondList;
		}
		BoundaryNode bNode = (BoundaryNode)nodes.get(boundaryInfos.bStart);
		if(bNode.isBindtoSyntactic())  {
			//如果这个句式已经绑定到其他句式,那么留到其他句式去处理
			if (iterator.hasNext()) {
				return recursiveBuild(iterator, nodes, domainStr, backtestTime);
			} else {
				return rtnCondList;
			}
		}
		
		
		String patternId = boundaryInfos.syntacticPatternId;
		//当前构建出的condition
		List<ConditionModel> condList = new ArrayList<ConditionModel>();
		BoundaryNode boundaryNode = (BoundaryNode) nodes.get(boundaryInfos.bStart);

		//build condition 过程
		if (patternId == null) {
			patternId = "STR_INSTANCE";
		}
		if (patternId.equals("FREE_VAR") || patternId.equals("STR_INSTANCE") || patternId.equals("KEY_VALUE")) {
			ConditionBuilderAbstract builder = null;
			//如果是信息领域
			if(domainStr!=null && domainStr.equals(Consts.CONST_absXinxiDomain)){
				//又是STR_INSTANCE,并且判断后发现可以skip
				if(patternId.equals("STR_INSTANCE") && ConditionBuilderUtil.isNewsDomainAndCanSkip(domainStr, bNode, boundaryInfos, nodes)) {
					if (iterator.hasNext()) {
						return recursiveBuild(iterator, nodes, domainStr, backtestTime);
					} else {
						return rtnCondList;
					}
				}else {
					//其他情况,用FREE_VAR Builder处理
					builder = ConditionBuilderFactory.getConditionBuilder(Consts.CONST_absXinxiDomain, nodes, boundaryInfos);
				}
			}else{
				//拿到ConditionBuilder instance,构建ConditionModel
				builder = ConditionBuilderFactory.getConditionBuilder(patternId, nodes, boundaryInfos);
			}
			
			if (builder != null) {
				ConditionParam param = new ConditionParam(domainStr);
				param.setNodes(nodes);
				param.setBoundaryInfos(boundaryInfos);
				param.setBacktestTime(backtestTime);
				List<ConditionModel> currentConds = builder.buildCondition(param);
				if(currentConds!=null) {
					condList.addAll(currentConds);
				}
			}
		} else {
			SyntacticPattern syntPtn = PhraseInfo.getSyntacticPattern(patternId); //句式
			if (syntPtn == null) {
				String msg = patternId + "　对应的 SyntacticPattern == NULL";
				logger_.info(msg);
				throw new BacktestCondException(msg);
			}

			//绑定的语义
			SemanticBind semanticBind = syntPtn.getSemanticBind();
			ArrayList<SemanticBindTo> bindToList = semanticBind.getSemanticBindTos();
			
			if (bindToList != null) {
				List<ConditionModel> prevConds=null; //处理嵌套语义,这里存的是上一个语义的OpModel
				for (SemanticBindTo sbt : bindToList) {
					int semanticId = sbt.getBindToId();
					SemanticOpModel opModel = SemanticCondInfo.getInstance().getSemanticOpInfo(semanticId);
					ConditionBuilderAbstract builder = null;

					//opModel按道理不应该会为null,
					String opName = opModel == null ? "" : opModel.getOpClazzName();
					builder = ConditionBuilderFactory.getConditionBuilder(opName, nodes, boundaryInfos);

					ConditionParam param = new ConditionParam(domainStr);
					param.setOpModel(opModel);
					param.setNodes(nodes);
					param.setSbt(sbt);
					param.setBoundaryInfos(boundaryInfos);
					param.setPrevConds(prevConds);
					param.setBacktestTime(backtestTime);
					List<ConditionModel> currentConds = builder.buildCondition(param);
					if (currentConds != null) {
						//嵌套语义,加个and
						if(param.getPrevConds()!=null) {
							condList.addAll(currentConds);
							ConditionOpModel conditionOpModel = new ConditionOpModel();
							if (boundaryNode.contextLogicType != null) {
								conditionOpModel.setOpName(boundaryNode.contextLogicType.toString().toLowerCase());
							}
							conditionOpModel.setSonSize(condList.size());
							condList.add(0,conditionOpModel);
						}else {
							//说明previous condition被后面的condition用掉了,比如行业sort
							condList.clear();
							condList.addAll(currentConds);
						}
						prevConds = currentConds;
					}
				}
			} else {
				logger_.info("SemanticBindTo List for SyntacticPattern[" + patternId + "] IS NULL");
			}
		}

		//拼接过程
		if (!iterator.hasNext()) {
			//后面没有了就不用添加and,or操作符了
			return condList;
		}

		ConditionOpModel conditionOpModel = new ConditionOpModel();
		if (boundaryNode.contextLogicType != null) {
			conditionOpModel.setOpName(boundaryNode.contextLogicType.toString().toLowerCase());
		}
		rtnCondList.add(conditionOpModel);
		rtnCondList.addAll(condList);
		//之后所有构建出的condition
		List<ConditionModel> futureCondition = recursiveBuild(iterator, nodes, domainStr, backtestTime);
		rtnCondList.addAll(futureCondition);
		conditionOpModel.setSonSize(futureCondition.size() + condList.size());

		return rtnCondList;

	}

	/*private static List<ConditionModel> buildEventDomainCondition(ArrayList<SemanticNode> nodes, String domainStr, Calendar backtestTime)
			throws BacktestCondException {
		//String conditionJson =null;
		if (domainStr.equals(Consts.CONST_absXinxiDomain) == false) {
			return null;
		}

		List<ConditionModel> condList = new LinkedList<ConditionModel>();

		Iterator<BoundaryInfos> iterator1 = new SyntacticIteratorImpl(nodes);
		if (iterator1.hasNext() == false) {
			return null;
		}
		while (iterator1.hasNext()) {
			BoundaryInfos boundaryInfos = iterator1.next();
			BoundaryNode bNode = (BoundaryNode) nodes.get(boundaryInfos.bStart);
			String patternId = boundaryInfos.syntacticPatternId;
			//如果是事件领域, 跳过
			if (patternId != null && patternId.equals("STR_INSTANCE")
					&& ConditionBuilderUtil.isNewsDomainAndCanSkip(domainStr, bNode, boundaryInfos, nodes)) {
				continue;
			}

			ConditionBuilderAbstract builder = getConditionBuilder(Consts.CONST_absXinxiDomain, nodes);
			if (builder != null) {
				ConditionParam param = new ConditionParam(domainStr);
				param.setNodes(nodes);
				param.setBacktestTime(backtestTime);
				param.setBoundaryInfos(boundaryInfos);
				List<ConditionModel> conds = builder.buildCondition(param);
				if (conds != null) {
					condList.addAll(conds);
				}
			}
		}
		return condList;
	}*/

}