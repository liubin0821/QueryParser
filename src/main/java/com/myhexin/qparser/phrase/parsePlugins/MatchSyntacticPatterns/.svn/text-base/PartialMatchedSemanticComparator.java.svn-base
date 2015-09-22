/**
 * 
 */
package com.myhexin.qparser.phrase.parsePlugins.MatchSyntacticPatterns;

import java.util.Comparator;


/**
 * 句式匹配评价： 匹配的参数越多越好：包括index和value 缺省的关键字越少越好：目前关键字均不可缺省 匹配的关键字越多越好 匹配的句式越少越好
 * 匹配的非implicit句式越多越好 缺省的参数?
 * 
 * @author chenhao
 *
 */
public class PartialMatchedSemanticComparator implements Comparator<PartialMatchedSemantic> {
	public int compare(PartialMatchedSemantic nodes1, PartialMatchedSemantic nodes2) {
		PartialMatchedSemanticEvalItems evalItems1 = nodes1.getEvalItems();
		PartialMatchedSemanticEvalItems evalItems2 = nodes2.getEvalItems();
		// 匹配的参数越多越好
		if (evalItems1.presentArgumentCount != evalItems2.presentArgumentCount)
			return evalItems2.presentArgumentCount - evalItems1.presentArgumentCount;

		// 缺省的关键字越少越好
		if (evalItems1.absentKeywordCount != evalItems2.absentKeywordCount)
			return evalItems1.absentKeywordCount - evalItems2.absentKeywordCount;

		// 匹配的关键字越多越好
		if (evalItems1.presentKeywordCount != evalItems2.presentKeywordCount)
			return evalItems2.presentKeywordCount + evalItems2.notBeingUsedKeyword - evalItems1.presentKeywordCount
					+ evalItems1.notBeingUsedKeyword;

		// 匹配的句式越少越好
		if (evalItems1.syntPatternCount != evalItems2.syntPatternCount)
			return evalItems1.syntPatternCount - evalItems2.syntPatternCount;

		// 匹配的非implicit句式越多越好
		if (evalItems1.noImplicitSyntPatternCount != evalItems2.noImplicitSyntPatternCount)
			return evalItems2.noImplicitSyntPatternCount - evalItems1.noImplicitSyntPatternCount;

		// 缺省的参数?
		if (evalItems1.absentArgumentCount != evalItems2.absentArgumentCount) {
			//return evalItems2.absentArgumentCount - evalItems1.absentArgumentCount; 越多越好？
			return evalItems1.absentArgumentCount - evalItems2.absentArgumentCount; //越少越好？
		}
		return 0;
	}

}