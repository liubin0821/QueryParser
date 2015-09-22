package com.myhexin.qparser.phrase.parsePlugins;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

import com.myhexin.qparser.date.DateUtil;
import com.myhexin.qparser.define.EnumDef.NodeType;
import com.myhexin.qparser.iterator.BoundaryInfos;
import com.myhexin.qparser.iterator.IndexIteratorImpl;
import com.myhexin.qparser.iterator.SyntacticIteratorImpl;
import com.myhexin.qparser.node.BoundaryNode;
import com.myhexin.qparser.node.DateNode;
import com.myhexin.qparser.node.FocusNode;
import com.myhexin.qparser.node.FocusNode.FocusItem;
import com.myhexin.qparser.node.SemanticNode;
import com.myhexin.qparser.onto.PropNodeFacade;
import com.myhexin.qparser.phrase.ParserAnnotation;
import com.myhexin.qparser.phrase.StopWords;
import com.myhexin.qparser.resource.model.RefCodeInfo;

public class PhraseParserPluginCalculateScore extends
		PhraseParserPluginAbstract {
	public PhraseParserPluginCalculateScore() {
		super("Calculate_Score");
	}

	public ArrayList<ArrayList<SemanticNode>> process(ParserAnnotation annotation) {	
		//Environment ENV = annotation.get(ParserKeys.EnvironmentKey.class);
		ArrayList<SemanticNode> nodes = annotation.getNodes();
		//this.ENV = ENV;
		ArrayList<ArrayList<SemanticNode>> rlist = new ArrayList<ArrayList<SemanticNode>>(1);
		int score = calculateScore(nodes);
		SemanticNode node = nodes.get(0);
		node.setScore(score);
		if (score > 0)
			rlist.add(nodes);
		return rlist;
	}

	private static HashSet<String> BAD_PARSER_INDEXS = new HashSet<String>();
	private static HashSet<String> STRU_AUX_WORD = new HashSet<String>();
	private static HashSet<String> IGNORED_WORD = new HashSet<String>();

	static {
		// 当前系统解析不是很好的indexs
		String[] badParserIndexs = {};
		for (String word : badParserIndexs) {
			BAD_PARSER_INDEXS.add(word);
		}

		// 结构助词
		String[] struAuxWords = { "的" };
		for (String word : struAuxWords) {
			STRU_AUX_WORD.add(word);
		}

		// 可忽略词
		String[] ignoredWords = {};
		for (String word : ignoredWords) {
			IGNORED_WORD.add(word);
		}
	}

	// 判断是否为当前系统解析不是很好的indexs
	private boolean isBadParserIndex(String index) {
		if (index == null || index.trim().length() == 0)
			return false;
		if (BAD_PARSER_INDEXS.contains(index))
			return true;
		return false;
	}

	// 判断节点中是否包含当前系统解析不是很好的indexs
	private boolean isThereBadParserIndex(ArrayList<SemanticNode> nodes) {
		for (int i = 0; i < nodes.size(); ++i) {
			SemanticNode sNode = nodes.get(i);
			if (sNode == null)
				continue;
			if (isBadParserIndex(sNode.getText()))
				return true;
			if (sNode.getType() != NodeType.FOCUS)
				continue;
			FocusNode fNode = (FocusNode) sNode;
			if (!fNode.hasIndex())
				continue;
			if (isBadParserIndex(fNode.getIndex().getText()))
				return true;
		}
		return false;
	}

	// 判断该节点的indexs是否都已经删除
	private boolean isAllFocusItemsDel(SemanticNode sNode) {
		if (sNode.getType() != NodeType.FOCUS)
			return false;
		FocusNode fNode = (FocusNode) sNode;
		if (!fNode.hasIndex())
			return false;
		boolean isHasFocusItem = false;
		boolean isHasFocusItemDel = false;
		for (FocusItem fi : fNode.getFocusItemList()) {
			if (fi.getType() != FocusNode.Type.INDEX)
				continue;
			// System.out.println(fi.canDelete_);
			if (fi.isCanDelete() == false) {
				isHasFocusItem = true;
			} else {
				isHasFocusItemDel = true;
			}
		}
		if (isHasFocusItemDel && isHasFocusItem == false) {
			return true;
		}
		return false;
	}

	// 判断存在indexs是否都已经删除的节点
	private boolean isThereAllFocusItemsDel(ArrayList<SemanticNode> nodes) {
		IndexIteratorImpl iterator = new IndexIteratorImpl(nodes);
		while (iterator.hasNext()) {
			FocusNode fNode = iterator.next();
			if (isAllFocusItemsDel(fNode))
				return true;
		}
		return false;
	}

	// 判断句式中是否存在缺少指标的情况
	// 当句式中存在两个以上时间节点，且其中一个未被绑定
	private boolean isLackIndexBySyntacitc(ArrayList<SemanticNode> nodes,
			int start, int end, int bStart, int bEnd) {
		int dateNum = 0;
		int dateNumNoBound = 0;
		int dateNumNoBoundToday = 0;
		for (int i = start; i <= bEnd; i++) {
			// 提取时间节点
			if (nodes.get(i).getType() == NodeType.DATE
					&& !nodes.get(i).isBoundToSynt()) {
				dateNum++;
				DateNode dNode = (DateNode) nodes.get(i);
				if (dNode.isCombined() == false
						&& dNode.isBoundToIndex() == false) {
					dateNumNoBound++;
					if (dNode.getDateinfo().getFrom()
							.equals(dNode.getDateinfo().getTo())
							&& dNode.getDateinfo().getFrom()
									.equals(DateUtil.getNow())
							&& isTradeTimeNow()) {
						dateNumNoBoundToday++;
					}
				}
			}
		}
		if (dateNum >= 2 && dateNumNoBound > 0
				&& dateNumNoBound != dateNumNoBoundToday)
			return true;
		return false;
	}

	// 判断句式中是否存在缺少指标的情况
	private boolean isLackIndex(ArrayList<SemanticNode> nodes) {
		if (nodes == null || nodes.size() == 0)
			return false;

		ArrayList<ArrayList<SemanticNode>> tlist = new ArrayList<ArrayList<SemanticNode>>();
		Iterator iterator = new SyntacticIteratorImpl(nodes);
		while (iterator.hasNext()) {
			BoundaryInfos boundaryInfos = (BoundaryInfos) iterator.next();
			if (isLackIndexBySyntacitc(nodes, boundaryInfos.start,
					boundaryInfos.end, boundaryInfos.bStart, boundaryInfos.bEnd))
				return true;
		}

		return false;
	}

	/*
	 * 判断文本是否为结构助词 目前只有"的"
	 */
	private static boolean isStruAuxWord(String text) {
		if (text == null || text.trim().length() == 0)
			return false;
		if (STRU_AUX_WORD.contains(text))
			return true;
		return false;
	}

	// 判断是否为结构助词
	// 找到的前面的SemanticNode,和的后面的FocusNode,看是不是SemanticNode被绑定到了FocusNode.index.propList[?].value
	private static boolean isStruAuxWord(ArrayList<SemanticNode> nodes, int pos) {
		SemanticNode sn = nodes.get(pos);

		// 看是不是“的”
		if (isStruAuxWord(sn.getText())) {
			SemanticNode lastSN = null; // “的”前面的绑定到Index的Node
			SemanticNode nextSN = null; // “的”后面的有Index的FocusNode
			for (int i = pos - 1; i >= 0; i--) {
				if (nodes.get(i).getType() == NodeType.BOUNDARY)
					continue;
				else if (nodes.get(i).isBoundToIndex())
					lastSN = nodes.get(i);
				else
					break;
			}
			for (int i = pos + 1; i < nodes.size(); i++) {
				if (nodes.get(i).getType() == NodeType.BOUNDARY)
					continue;
				else if (nodes.get(i).getType() == NodeType.FOCUS
						&& ((FocusNode) nodes.get(i)).hasIndex())
					nextSN = nodes.get(i);
				else
					break;
			}
			// 查找结束

			if (lastSN != null && nextSN != null) {
				FocusNode fn = (FocusNode) nextSN;
				List<PropNodeFacade> pNodes = fn.getIndex().getAllProps();
				for (int i = 0; i < pNodes.size(); i++) {
					SemanticNode propValue = pNodes.get(i).getValue();
					SemanticNode valueNode = lastSN;
					if (propValue != null
							&& propValue.getType() == NodeType.CLASS
							&& lastSN.getType() == NodeType.FOCUS
							&& ((FocusNode) lastSN).hasIndex())
						valueNode = ((FocusNode) lastSN).getIndex();
					else if (propValue != null
							&& propValue.getType() == NodeType.STR_VAL
							&& lastSN.getType() == NodeType.FOCUS
							&& ((FocusNode) lastSN).hasString())
						valueNode = ((FocusNode) lastSN).getString();

					if (propValue != null && propValue.equals(valueNode))
						return true;
				}
			}
		}
		return false;
	}

	// 看Pattern.matches(), 先编译好pattern效率高
	private final static Pattern sepPattern = Pattern
			.compile("(\\s|,|;|\\.|。)");

	// 判断是否为分隔符
	private static boolean isSepWord(String text) {
		if (text == null)
			return false;
		if (sepPattern.matcher(text).matches())
			return true;
		return false;
	}

	// 判断是否为可忽略的词
	private static boolean isIgnoredWord(String text) {
		if (text == null || text.trim().length() == 0)
			return false;
		if (StopWords.stopWords.contains(text))
			return true;
		return false;
	}

	private Integer calculateScore(ArrayList<SemanticNode> nodes) {
		// TODO
		// 各种评判标准的分数和权值
		
		/**
		 * score1: 句式内+句式外被绑定的   = 节点总数
		   score2: 被绑定的参数个数+被绑定的关键字个数+句式内外绑定的节点数  = 句式内外节点数，非unknown节点
		   score3: 被绑定的参数个数+句式内外绑定的节点数  = 参数(非关键字：指标、字符串、时间、数值)
		   score4: 被绑定的关键字个数  = 关键字
		   score5: 句式内分隔符的个数   =  句式总数
		   score6: 句式内被使用节点数量   =  句式内节点总数
		 */
		double score1 = 100.0;
		double scoreWeight1 = 0.2;
		double score2 = 100.0;
		double scoreWeight2 = 0.2;
		double score3 = 100.0;
		double scoreWeight3 = 0.2;
		double score4 = 100.0;
		double scoreWeight4 = 0.2;
		
		//liuxiaofeng 2015/4/28 取消句式内分隔符降分逻辑
		//double score5 = 0;
		//double scoreWeight5 = -0.1;
		
		double score6 = 0;
		double scoreWeight6 = 0.2;
        double syntNum = 0;

        // 如果问句中包含均线，直接返回0
        if (isThereBadParserIndex(nodes))
        	return 0;
        
        // 当FocusItem都被删除时，表示句式匹配不正确
		if (isThereAllFocusItemsDel(nodes))
			return 0;
		
		// 当句式中存在两个以上时间节点，且其中一个未被绑定
		//if (isLackIndex(nodes))
		//	return 0;
		
		int nodesTotal = 0; 		// 句式内外节点数
		int nodesInnerBoundary = 0; // 句式内节点数
		int nodesInnerBoundaryInUsing = 0; // 句式内被使用的节点数
		int nodesSum = 0;			// 句式内外节点数，非unknown节点
		int nodesArg = 0; 			// 参数(非关键字：指标、字符串、时间、数值)
		int nodesKeyword = 0; 		// 关键字
		int nodesBound = 0;			// 句式内外绑定的节点数
		int nodesOuterBound = 0;	// 句式外绑定的节点数
		int presentNodesArg = 0;  	// 被绑定的参数个数
		int presentNodesKeyword = 0;// 被绑定的关键字个数
		//int absentNodesArg = 0; 	// 缺省的参数个数
		//int absentNodesKeyword = 0; // 缺省的关键字个数
		//int sepInnerBoundary = 0; 	// 句式内分隔符的个数
		boolean isInnerBoundary = false;
		
		for (int i = 1; i < nodes.size(); ++i) {
			SemanticNode sn = nodes.get(i);
			
			//不是BoundaryNode, 不是分隔符, 不是被合并
			//节点总数++,
			//如果在句式内, 句式内节点数++
			//不是Unknown, 非unknown节点数++
			if (sn.getType() != NodeType.BOUNDARY && (sn.getType() == NodeType.FOCUS || !isSepWord(sn.getText() )) && !sn.isCombined()) {
				nodesTotal++;
				if (isInnerBoundary)
					nodesInnerBoundary++;
				if (sn.getType() != NodeType.UNKNOWN)
					nodesSum++;
			}
			
			
			if (sn.getType() == NodeType.BOUNDARY) {
				BoundaryNode bn = (BoundaryNode) sn;
				if (bn.isStart()) {
                    syntNum++; // 绑定上的句式的个数
                    BoundaryNode.SyntacticPatternExtParseInfo info = bn.getSyntacticPatternExtParseInfo(true);
                    presentNodesArg += info.presentArgumentCount;
                    presentNodesKeyword += info.presentKeywordCount;
                    nodesArg += info.presentArgumentCount;
                    nodesKeyword += info.presentKeywordCount;
                    //absentNodesArg += info.absentArgumentCount;
                    //absentNodesKeyword += info.absentKeywordCount;
                    isInnerBoundary = true;
				} else if (bn.isEnd()) {
					isInnerBoundary = false;
				}
			} else if (sn.isCombined()) {
				
			} else if (sn.isBoundToSynt()) {
				if (isInnerBoundary) //句式内部,  绑定到句式上的节点
					nodesInnerBoundaryInUsing++;
			} else if (isStruAuxWord(nodes, i)) { //是不是  SemanticNode的FocusNode结构,
				presentNodesKeyword++;
				nodesKeyword++;
				//nodesInnerBoundary++; //不能多算一次
				if(isInnerBoundary) {
					nodesInnerBoundaryInUsing++;
				}else {
					nodesOuterBound ++;
				}
			} else if (!sn.isBoundToSynt() && !sn.isBoundToIndex()  && 
					(sn.getType() == NodeType.FOCUS || !isSepWord(sn.getText())) 
					&& isIgnoredWord(sn.getText())) {
				//没有被绑定到句式
				//没有被绑定到指标
				//是FocusNode, 或者不是分隔符
				//是忽略词
				nodesTotal--;
				if (isInnerBoundary)
					nodesInnerBoundary--;
				if (sn.getType() != NodeType.UNKNOWN)
					nodesSum--;
			}
			else if (sn.isBoundToIndex() ) {
				nodesBound++; // 被绑定
				nodesArg++;
				if (!isInnerBoundary)
					nodesOuterBound++;
				if (isInnerBoundary && !sn.isBoundToSynt()) //句式内部绑定到指标上的节点
					nodesInnerBoundaryInUsing++;
			} else if (sn.getType() == NodeType.FOCUS && ((FocusNode) sn).hasIndex()
					|| sn.getType() == NodeType.FOCUS && ((FocusNode) sn).hasString()
					|| sn.getType() == NodeType.STR_VAL
					|| sn.getType() == NodeType.NUM
					|| sn.getType() == NodeType.DATE) {
				nodesArg++;
			} else if (sn.getType() == NodeType.FOCUS && ((FocusNode) sn).hasKeyword()) {
				nodesKeyword++;
			} 
			
			/*else if (isInnerBoundary && isSepWord(sn.getText() )) {
				sepInnerBoundary++;
			}*/ 
		}
		
		if (nodesTotal != 0) {
			score1 = 100.0 * (nodesInnerBoundary + nodesOuterBound) / nodesTotal;//句式内+句式外被绑定的   = 节点总数
		}
		if (nodesSum != 0) {
			score2 = 100.0 * (presentNodesArg + nodesBound + presentNodesKeyword) / nodesSum; //被绑定的参数个数+被绑定的关键字个数+句式内外绑定的节点数  = 句式内外节点数，非unknown节点
		}
		if (nodesArg != 0) {
			score3 = 100.0 * (presentNodesArg + nodesBound) / nodesArg;////被绑定的参数个数+句式内外绑定的节点数  = 参数(非关键字：指标、字符串、时间、数值)
		}
		if (nodesKeyword != 0) {
			score4 = 100.0 * presentNodesKeyword / nodesKeyword; //被绑定的关键字个数  = 关键字
		}
		/*if (syntNum != 0) {
			score5 = 100.0 * sepInnerBoundary / syntNum;  //句式内分隔符的个数   =  句式总数
		}*/
		if (nodesInnerBoundary != 0) {
			score6 = 100.0 * nodesInnerBoundaryInUsing / nodesInnerBoundary;
		}
		
		StringBuilder logsb_ = getLogsb_(nodes);
		if(logsb_!= null){
			logsb_.append("## 0ms ## Calculate_Score 计算过程\n");
			logsb_.append("节点使用比例：").append(score1).append("% = ");
			logsb_.append("(").append(nodesInnerBoundary).append(" + ").append(nodesOuterBound).append(")").append(" / ").append(nodesTotal).append("\n");

			logsb_.append("被使用节点占非unknow节点比例：").append(score2).append("% = ");
			logsb_.append("(").append(presentNodesArg).append(" + ").append(nodesBound).append(" + ").append(presentNodesKeyword).append(")").append(" / ").append(nodesSum).append("\n");
			
			logsb_.append("非关键字节点被使用比例: ").append(score3).append("% = ");
			logsb_.append("(").append(presentNodesArg).append(" + ").append(nodesBound).append(")").append(" / ").append(nodesArg).append("\n");
			
			logsb_.append("关键字被使用比例:").append(score4).append("% = ");
			logsb_.append(presentNodesKeyword).append(" / ").append(nodesKeyword).append("\n");
			
			/*logsb_.append("句式内的分隔符和句式数量的比值(扣分项)").append(score5).append("% = ");
			logsb_.append(sepInnerBoundary).append(" / ").append(syntNum).append("\n");*/
			
			logsb_.append("句式内被使用节点占句式内节点总数比例:").append(score6).append("% = ");
			logsb_.append(nodesInnerBoundaryInUsing).append(" / ").append(nodesInnerBoundary).append("\n");
			
		}
		
		//+ score5 * scoreWeight5 
		int score = (int) (score1 * scoreWeight1 + score2 * scoreWeight2 + score3 * scoreWeight3 + score4 * scoreWeight4 + score6*scoreWeight6);
		if(logsb_!= null){
			logsb_.append("最后得分: " + score+"%\n");
		}
		//System.out.println(score);
		//放开可匹配句式的个数，从3个改为10个
		//刘小峰 2015/2/27 改成25个,wudan手机部门需求
        if(syntNum > RefCodeInfo.getInstance().getMaxSyntactNum()) 
            score = 0;
		return score;
	}

	private boolean isTradeTimeNow() {
		Date now = new Date();
		Calendar cal1 = Calendar.getInstance();
		Calendar cal2 = Calendar.getInstance();
		Calendar cal3 = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		SimpleDateFormat sdfdate = new SimpleDateFormat("yyyyMMdd");
		String time1 = sdfdate.format(now) + "091500";
		String time2 = sdfdate.format(now) + "160000";
		cal1.setTime(now);

		try {
			cal2.setTime(sdf.parse(time1));
			cal3.setTime(sdf.parse(time2));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (cal2.before(cal1) && cal3.after(cal1)) { // 在时间段内
			return true;
		}
		return false;
	}
}