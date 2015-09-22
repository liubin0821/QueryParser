/**
 * 
 */
package com.myhexin.qparser.date.parse;

import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.myhexin.qparser.date.ParserInterface;
import com.myhexin.qparser.node.ConsistPeriodNode;
import com.myhexin.qparser.node.SemanticNode;

/**
 * @author chenhao
 *
 */
public class ConsistPeriodCalcParser implements ParserInterface {
	private static Pattern numberPattern = Pattern.compile("(\\d+)");
	@Override
	public void parse(SemanticNode node, Calendar backtestTime) {
		if (node instanceof ConsistPeriodNode) {
			String text = node.getText();
			int periodNum = 0;
			Matcher matcher = numberPattern.matcher(text);
			if (matcher.find()) {
				periodNum = Integer.valueOf(matcher.group(1));
			}
			ConsistPeriodNode consistPeriodNode = (ConsistPeriodNode) node;
			consistPeriodNode.setPeriodNum(periodNum);
		}
	}

}
