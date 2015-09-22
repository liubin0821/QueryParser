package com.myhexin.qparser.phrase.parsePlugins;

import java.util.ArrayList;
import java.util.List;

import com.myhexin.qparser.define.EnumDef.NodeType;
import com.myhexin.qparser.node.BoundaryNode;
import com.myhexin.qparser.node.Environment;
import com.myhexin.qparser.node.FocusNode;
import com.myhexin.qparser.node.SemanticNode;
import com.myhexin.qparser.phrase.ParserAnnotation;
import com.myhexin.qparser.phrase.PhraseInfo;
import com.myhexin.qparser.phrase.SyntacticSemantic.Semantic.SemanticBindTo;
import com.myhexin.qparser.phrase.SyntacticSemantic.Semantic.SemanticPattern;
import com.myhexin.qparser.phrase.SyntacticSemantic.Syntactic.SyntacticPattern;

public class PhraseParserPluginDealWithIncrease extends
		PhraseParserPluginAbstract {
	public PhraseParserPluginDealWithIncrease() {
		super("Deal_With_Increase_and_Decrease");
	}

	public ArrayList<ArrayList<SemanticNode>> process(ParserAnnotation annotation) {
		
		Environment ENV = annotation.getEnv();
		ArrayList<SemanticNode> nodes = annotation.getNodes();
		//this.ENV = ENV;
		ArrayList<ArrayList<SemanticNode>> rlist = new ArrayList<ArrayList<SemanticNode>>(1);
		int score = isIncreaseRight(nodes);
		if (score > 0)
			rlist.add(nodes);
		
		return rlist;
	}

	private int isIncreaseRight(ArrayList<SemanticNode> nodes) {
		if (nodes == null)
			return 0;
		ArrayList<Integer> boundaryList = getSyntacitcPatternList(nodes);
		if (boundaryList.size() <= 2)
			return 100; // 没有句式

		int start = 0;
		int end = 0;
		int bStart = 0;
		int bEnd = 0;
		SemanticNode sNode = null;
		BoundaryNode bNode = null;

		for (int i = 1; i < boundaryList.size() - 1; i++) {
			int pos = (int) boundaryList.get(i);
			sNode = nodes.get(pos);
			if (sNode != null && sNode.type == NodeType.BOUNDARY) {
				bNode = (BoundaryNode) sNode;
				if (bNode.isStart()) {
					start = (int) boundaryList.get(i - 1);
					end = (int) boundaryList.get(i + 2);
					bStart = pos;
					bEnd = (int) boundaryList.get(i + 1);
					if (!isIncreaseRightBySyntacitc(nodes, bNode, start, end,
							bStart, bEnd))
						return 0;
				}
			}
		}
		return 100;
	}

	// 判断增长、同比增长等问题是否是对的
	private boolean isIncreaseRightBySyntacitc(ArrayList<SemanticNode> nodes,
			BoundaryNode bNode, int start, int end, int bStart, int bEnd) {
		int increaseNumPre = 0;
		int increaseNumNow = 0;
		// 计算原问句中“增长”的数量
		for (int i = start; i <= bEnd; i++) {
			SemanticNode sNode = nodes.get(i);
			if (sNode.getText().contains("增长") || sNode.getText().contains("减少")
					|| sNode.getText().contains("上涨") || sNode.getText().contains("下跌")
					|| sNode.getText().contains("预增") || sNode.getText().contains("预亏")) {
				increaseNumPre++;
			} else if (increaseKeywords.contains(sNode.getText()) || decreaseKeywords.contains(sNode.getText())) {
				increaseNumPre++;
			}
		}
		
		// 计算解析结果中“增长”的数量
		BoundaryNode.SyntacticPatternExtParseInfo info = bNode.getSyntacticPatternExtParseInfo(false);
		if (info == null)
			return true;
		String patternId = bNode.getSyntacticPatternId();
		if (BoundaryNode.getImplicitPattern(patternId) == null) {
			SyntacticPattern syntPtn = PhraseInfo.getSyntacticPattern(patternId);
			String bindid = syntPtn.getSemanticBind().getId();
			if (bindid == null) { // 新语义
				for (SemanticBindTo semanticBindTo : syntPtn.getSemanticBind().getSemanticBindTos()) {
					//System.out.println(semanticBindTo.getBindToId()+"");
					SemanticPattern pattern = PhraseInfo.getSemanticPattern(semanticBindTo.getBindToId()+"");
					if (pattern != null) {
						String representation = pattern.getChineseRepresentation();
						if (representation.contains("增长") || representation.contains("减少")
								|| representation.contains("上涨") || representation.contains("下跌")
								|| representation.contains("预增") || representation.contains("预亏")) {
							increaseNumNow++;
							break;
						}
					}
				}
				//System.out.println(increaseNumNow);
			}
		}
		ArrayList<Integer> elelist;
		for (int j = 1; (elelist = info.getElementNodePosList(j)) != null; j++) {
			for (int pos : elelist) {
				int i = 0;
				SemanticNode sNode = null;

				if (pos != -1) { // 显式指标
					i = bStart + pos;
					sNode = nodes.get(i);
				} else { // 默认指标
					i = bStart;
					sNode = info.absentDefalutIndexMap.get(j);
				}
				if (sNode == null || sNode.type != NodeType.FOCUS)
					continue;
				FocusNode fNode = (FocusNode) sNode;
				if (!fNode.hasIndex())
					continue;
				if (fNode.getIndex().getText().contains("增长")) {
					increaseNumNow++;
				}
			}
		}
		
		if (increaseNumNow >= 2 && increaseNumPre < increaseNumNow)
			return false;
		return true;
	}

	/**
	 * 取boundaryNode的位置信息，并在头上增加0位置，最后增加最后一个节点位置
	 * 
	 * 结果 [0, bstartpos1, bendpos1, bstartpos2, bendpos2 ... bendposn,
	 * nodes.size()]
	 * 
	 * @param nodes
	 * @return ArrayList<Integer> 位置信息数组
	 */
	private ArrayList<Integer> getSyntacitcPatternList(
			ArrayList<SemanticNode> nodes) {
		int len = nodes.size();
		ArrayList<Integer> ret = new ArrayList<Integer>();
		ret.add(0);
		for (int i = 0; i < len; i++) {
			SemanticNode node = nodes.get(i);
			if (node.type == NodeType.BOUNDARY) {
				ret.add(i);
			}
		}
		ret.add(len);
		return ret;
	}
	
	private static List<String> increaseKeywords = new ArrayList<String>();
	private static List<String> decreaseKeywords = new ArrayList<String>();
	static {
		if (PhraseInfo.keywordGroupMap_.getKeywordsByDesc("增长") != null)
			increaseKeywords.addAll(PhraseInfo.keywordGroupMap_.getKeywordsByDesc("增长"));
		if (PhraseInfo.keywordGroupMap_.getKeywordsByDesc("上涨") != null)
			increaseKeywords.addAll(PhraseInfo.keywordGroupMap_.getKeywordsByDesc("上涨"));
		if (PhraseInfo.keywordGroupMap_.getKeywordsByDesc("预增") != null)
			increaseKeywords.addAll(PhraseInfo.keywordGroupMap_.getKeywordsByDesc("预增"));
		if (PhraseInfo.keywordGroupMap_.getKeywordsByDesc("减少") != null)
			decreaseKeywords.addAll(PhraseInfo.keywordGroupMap_.getKeywordsByDesc("减少"));
		if (PhraseInfo.keywordGroupMap_.getKeywordsByDesc("下跌") != null)
			decreaseKeywords.addAll(PhraseInfo.keywordGroupMap_.getKeywordsByDesc("下跌"));
		if (PhraseInfo.keywordGroupMap_.getKeywordsByDesc("预亏") != null)
			decreaseKeywords.addAll(PhraseInfo.keywordGroupMap_.getKeywordsByDesc("预亏"));
	}
}
