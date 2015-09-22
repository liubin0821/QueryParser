package com.myhexin.qparser.util.backtest.minutedata.querypartten.impl;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.myhexin.qparser.date.DatePatterns;
import com.myhexin.qparser.define.EnumDef.NodeType;
import com.myhexin.qparser.node.FocusNode;
import com.myhexin.qparser.node.SemanticNode;
import com.myhexin.qparser.util.backtest.minutedata.querypartten.MinuteDataQueryPattern;

public class NumStopExpressionPattern implements MinuteDataQueryPattern {

    private static final String UP_STOP_KEY = "涨停";
    private static final String DIRECT_UP_STOP_KEY = "一字涨停";
    private static final String DOWN_STOP_KEY = "跌停";
    private static final String DIRECT_DOWN_STOP_KEY = "一字跌停";

    private static final Pattern NUM_PATTERN = Pattern
            .compile("第(\\d)");
    public NumStopExpressionPattern() {
        // empty.
    }

    @Override
    public boolean isPatternMatch(List<SemanticNode> nodes) {
        for (int i = 0; i < nodes.size(); i++) {
        	
            String text = nodes.get(i).getPubText();
            if (UP_STOP_KEY.equals(text) || DIRECT_UP_STOP_KEY.equals(text) || 
            		DOWN_STOP_KEY.equals(text) || DIRECT_DOWN_STOP_KEY.equals(text)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String matchPattern(List<SemanticNode> nodes) {
        if (isPatternMatch(nodes)) {
        	String indexName = "", opName = "OPEN";
        	int num = 0;
            for (int i = 0; i < nodes.size(); i++) {
            	SemanticNode nodeTemp = nodes.get(i);
            	
                String text = nodes.get(i).getPubText();

                if(NodeType.SORT.equals(nodeTemp.type)){
                	Matcher match  = NUM_PATTERN.matcher(text);                   
                    if(match.find() && match.group(1)!=null){
                    	num = Integer.parseInt(match.group(1));
                    }
                }
                
                if(text.contains(DIRECT_DOWN_STOP_KEY))
                	indexName = DIRECT_DOWN_STOP_KEY;
                else if(text.contains(DIRECT_UP_STOP_KEY))
                	indexName = DIRECT_UP_STOP_KEY;
                else if(text.contains(UP_STOP_KEY))
                	indexName = UP_STOP_KEY;
                else if(text.contains(DOWN_STOP_KEY))
                	indexName = DOWN_STOP_KEY;
                
            }
            
            
            if(!indexName.equals("") && !opName.equals("") && num != 0){
	            StringBuilder sb = new StringBuilder();
	            sb.append(indexName).append(",");
	            sb.append("OPEN").append(",");
	            sb.append(num);
	            return sb.toString();
            }
        }
        return "";
    }
    
}
