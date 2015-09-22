package com.myhexin.qparser.util.backtest.minutedata.querypartten.impl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.myhexin.qparser.date.DatePatterns;
import com.myhexin.qparser.define.EnumDef.NodeType;
import com.myhexin.qparser.node.SemanticNode;
import com.myhexin.qparser.util.backtest.minutedata.querypartten.MinuteDataQueryPattern;

public class NormalTechExpressionPattern implements MinuteDataQueryPattern {

    private Set<String> TECH_OP = new HashSet<String>();
    public static Set<String> TECH_INDEX = new HashSet<String>();
    
    private static final Pattern TECH_DATE_NUM_PATTERN = Pattern
            .compile("(\\d*)(日|号)");
    private static Map<String , String> techIndexConvertMap = new HashMap<String , String>();
    
    static{
    	techIndexConvertMap.put("均线", "ma%NUM%");
    	
    	//暂时只添加均线用于测试
    	TECH_INDEX.add("均线");
    }
    
    public NormalTechExpressionPattern() {
    	TECH_OP.add(">");
    	TECH_OP.add("<");
    	TECH_OP.add(">=");
    	TECH_OP.add("<=");
        //暂时只添加上穿、金叉用于测试
    	TECH_OP.add("上穿");
    	TECH_OP.add("金叉");
    }

    @Override
    public boolean isPatternMatch(List<SemanticNode> nodes) {
    	boolean hasTechOp = false;
    	boolean hasTechIndex = false;
    	
        for (int i = 0; i < nodes.size(); i++) {
            if (TECH_OP.contains(nodes.get(i).getPubText())) {
                hasTechOp = true;
            }
            
            else if (TECH_INDEX.contains(nodes.get(i).getPubText())) {
            	hasTechIndex = true;
            }
        }
        return hasTechIndex & hasTechOp;
    }

    @Override
    public String matchPattern(List<SemanticNode> nodes) {
        boolean matched = isPatternMatch(nodes);
        StringBuilder sb = new StringBuilder();
        if (matched) {
            for (int i = 0; i < nodes.size(); i++) {
            	SemanticNode nowNode = nodes.get(i);
                if (TECH_OP.contains(nowNode.getPubText())) {
                	if(sb.length() != 0){
                		sb.append(",");
                	}
                	sb.append(nowNode.getPubText());
                }else if(TECH_INDEX.contains(nodes.get(i).getPubText())){
                	if(sb.length() != 0){
                		sb.append(",");
                	}
                	sb.append(convertTechIndex(nodes , i));
                }
            }
        }
        return sb.toString();
    }

    
    private String convertTechIndex(List<SemanticNode> nodes , int i){
    	if(i < 1)
    		return "";
    	
    	SemanticNode preNode = nodes.get(i - 1);
    	SemanticNode nowNode = nodes.get(i);
    	if(NodeType.DATE.equals(preNode.type)){
    		Matcher match = TECH_DATE_NUM_PATTERN.matcher(preNode.getPubText());
    		if (match.matches()) {
    			int num = Integer.parseInt(match.group(1));
    			String pattern = techIndexConvertMap.get(nowNode.getPubText());
    			return pattern.replaceAll("%NUM%", num+"");
            }
    	}
    	
    	return "";
    }
    
    private String convertNumberNodeValue(String numberNodeValue) {
        int pos = numberNodeValue.indexOf("%");
        if (pos > 0) {
            String value = numberNodeValue.substring(0, pos);
            float v = Float.parseFloat(value) / 100.0F;
            return String.valueOf(v);
        }
        return numberNodeValue;
    }

    private SemanticNode findNumberOp(List<SemanticNode> nodes, int index) {
        if (index >= nodes.size()) {
            return null;
        }

        for (int i = index; i < nodes.size(); i++) {
            if (nodes.get(i).getType() == NodeType.NUM) {
                return nodes.get(i);
            }
        }
        return null;
    }

}
