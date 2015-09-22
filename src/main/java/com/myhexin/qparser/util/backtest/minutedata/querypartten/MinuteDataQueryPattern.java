package com.myhexin.qparser.util.backtest.minutedata.querypartten;

import java.util.List;

import com.myhexin.qparser.node.SemanticNode;

public interface MinuteDataQueryPattern {

    public boolean isPatternMatch(List<SemanticNode> nodes);

    public String matchPattern(List<SemanticNode> nodes);
}
