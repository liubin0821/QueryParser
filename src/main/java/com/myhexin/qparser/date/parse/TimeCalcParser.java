/**
 * 
 */
package com.myhexin.qparser.date.parse;

import java.util.Calendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.myhexin.qparser.date.ParserInterface;
import com.myhexin.qparser.except.NotSupportedException;
import com.myhexin.qparser.node.SemanticNode;
import com.myhexin.qparser.node.TimeNode;
import com.myhexin.qparser.time.info.TimeRange;
import com.myhexin.qparser.time.parse.TimeStrParser;

/**
 * @author chenhao
 *
 */
public class TimeCalcParser implements ParserInterface {
	private static Logger _logger = LoggerFactory.getLogger(TimeCalcParser.class);

	public void parse(SemanticNode node, Calendar backtestTime) {
		try {
			if (node.isTimeNode()) {
				TimeNode timeNode = (TimeNode) node;
				TimeRange range = TimeStrParser.parse(timeNode.getText());
				timeNode.setTimeRange(range);
				node.setSkipOldDateParser(true);
			}

		} catch (NotSupportedException e) {
			_logger.error(e.getMessage());
			e.printStackTrace();
		}

	}

}
