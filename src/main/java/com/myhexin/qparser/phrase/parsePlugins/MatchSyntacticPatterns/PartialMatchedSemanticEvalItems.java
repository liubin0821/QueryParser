/**
 * 
 */
package com.myhexin.qparser.phrase.parsePlugins.MatchSyntacticPatterns;

import java.util.ArrayList;

import com.myhexin.qparser.define.EnumDef.NodeType;
import com.myhexin.qparser.node.BoundaryNode;
import com.myhexin.qparser.node.BoundaryNode.IMPLICIT_PATTERN;
import com.myhexin.qparser.node.FocusNode;
import com.myhexin.qparser.node.SemanticNode;

/**
 * @author chenhao
 * 句式匹配过程中剪枝的依据,和打分插件逻辑比较相似
 */
public class PartialMatchedSemanticEvalItems {
	public int syntPatternCount = 0;
	public int noImplicitSyntPatternCount = 0;
	public int presentArgumentCount = 0;
	public int presentKeywordCount = 0;
	public int absentArgumentCount = 0;
	public int absentKeywordCount = 0;
	public int notBeingUsedKeyword = 0;

	public PartialMatchedSemanticEvalItems() {

	}

	public PartialMatchedSemanticEvalItems(int syntPatternCount, int noImplicitSyntPatternCount, int presentArgumentCount,
			int presentKeywordCount, int absentArgumentCount, int absentKeywordCount, int notBeingUsedKeyword) {
		this.syntPatternCount = syntPatternCount;
		this.noImplicitSyntPatternCount = noImplicitSyntPatternCount;
		this.presentArgumentCount = presentArgumentCount;
		this.presentKeywordCount = presentKeywordCount;
		this.absentArgumentCount = absentArgumentCount;
		this.absentKeywordCount = absentKeywordCount;
		this.notBeingUsedKeyword = notBeingUsedKeyword;
	}

	public static PartialMatchedSemanticEvalItems getArgumentCount(ArrayList<SemanticNode> list) {
		int syntPatternCount = 0;
		int noImplicitSyntPatternCount = 0;
		int presentArgumentCount = 0;
		int presentKeywordCount = 0;
		int absentArgumentCount = 0;
		int absentKeywordCount = 0;
		int notBeingUsedKeyword = 0;

		int boundaryPos = 0;
		int j = 0;
		for (SemanticNode node : list) {
			if (node.type == NodeType.BOUNDARY) {
				BoundaryNode boundary = (BoundaryNode) node;
				if (boundary.isStart()) {
					boundaryPos = j;
					syntPatternCount += 1;
					String syntacticPatternId = boundary.getSyntacticPatternId();
					BoundaryNode.SyntacticPatternExtParseInfo extInfo = boundary.getSyntacticPatternExtParseInfo(true);
					IMPLICIT_PATTERN implicit = BoundaryNode.getImplicitPattern(syntacticPatternId);
					if (implicit == null) {
						presentArgumentCount += extInfo.presentArgumentCount;
						presentKeywordCount += extInfo.presentKeywordCount;
						absentArgumentCount += extInfo.absentArgumentCount;
						absentKeywordCount += extInfo.absentKeywordCount;
						noImplicitSyntPatternCount += 1;
					} else if (implicit == IMPLICIT_PATTERN.KEY_VALUE) {
						presentArgumentCount += extInfo.presentArgumentCount;
						// 既可作为keyword又可作为str_val的不作为评价的标准
						int pos = extInfo.getElementNodePosList(2).get(0);
						if (pos != -1 && boundaryPos + pos < list.size()) {
							SemanticNode sn = list.get(boundaryPos + pos);
							if (sn != null && sn.type == NodeType.FOCUS) {
								presentArgumentCount -= 1;
								presentKeywordCount += 1;
							}
						}
					} else if (implicit == IMPLICIT_PATTERN.FREE_VAR) {
						presentArgumentCount += extInfo.presentArgumentCount;
					} else if (implicit == IMPLICIT_PATTERN.STR_INSTANCE) {
						presentArgumentCount += extInfo.presentArgumentCount;

						// 既可作为keyword又可作为str_val的不作为评价的标准
						int pos = extInfo.getElementNodePosList(1).get(0);
						if (pos != -1 && boundaryPos + pos < list.size()) {
							SemanticNode sn = list.get(boundaryPos + pos);
							if (sn != null && sn.type == NodeType.FOCUS) {
								presentArgumentCount -= 1;
								presentKeywordCount += 1;
							}
						}
					}
				}
			} else if (node.type == NodeType.FOCUS) {
				FocusNode fn = (FocusNode) node;
				if (fn.hasKeyword() && fn.isCombined)
					notBeingUsedKeyword++;
			}
			j++;
		}

		PartialMatchedSemanticEvalItems evalItems = new PartialMatchedSemanticEvalItems(syntPatternCount, noImplicitSyntPatternCount, presentArgumentCount,
				presentKeywordCount, absentArgumentCount, absentKeywordCount, notBeingUsedKeyword);
		return evalItems;
	}
}
