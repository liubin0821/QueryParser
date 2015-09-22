package com.myhexin.qparser.util.backtest.minutedata.querypartten.impl;

import java.util.List;

import com.myhexin.qparser.define.EnumDef.NodeType;
import com.myhexin.qparser.node.FocusNode;
import com.myhexin.qparser.node.SemanticNode;
import com.myhexin.qparser.util.backtest.minutedata.querypartten.MinuteDataQueryPattern;

public class MaxMinExpressionPattern implements MinuteDataQueryPattern {

    private static final String MIN_KEY = "最低";
    private static final String MAX_KEY = "最高";

    public MaxMinExpressionPattern() {
        // empty.
    }

    @Override
    public boolean isPatternMatch(List<SemanticNode> nodes) {
        for (int i = 0; i < nodes.size(); i++) {
            String text = nodes.get(i).getPubText();
            if (MIN_KEY.equals(text) || MAX_KEY.equals(text)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String matchPattern(List<SemanticNode> nodes) {
        if (isPatternMatch(nodes)) {

            for (int i = 0; i < nodes.size(); i++) {
                String text = nodes.get(i).getPubText();

                if (MIN_KEY.equals(text) || MAX_KEY.equals(text)) {
                    // 往前找 时间区间 和 指标节点
                    SemanticNode timeNode = findTimeLength(nodes, i);

                    String indexName = findIndexName(nodes, i);

                    if (timeNode != null && indexName != null && indexName.length() > 0) {
                        StringBuilder sb = new StringBuilder();
                        sb.append(formatMinMaxWord(text)).append(",");
                        sb.append(indexName).append(",");
                        sb.append(getMinuteLength(timeNode.getPubText()));
                        return sb.toString();
                    }
                }
            }
        }
        return "";
    }
    
    /** 修改接口输出的关键词*/
    private String formatMinMaxWord(String word){
    	if(MAX_KEY.equals(word))
    		return "MAX";
    	else if(MIN_KEY.equals(word))
    		return "MIN";
    	else
    		return word;
    }
    
    private String getMinuteLength(String text) {
        int index = text.indexOf("分钟");
        if (index > 0) {
            return text.substring(0, index);
        }
        return text;
    }

    private SemanticNode findTimeLength(List<SemanticNode> nodes, int index) {
        for (int i = index - 1; i >= 0; i--) {
            if (nodes.get(i).getType() == NodeType.NUM || nodes.get(i).getType() == NodeType.DATE) {
                return nodes.get(i);
            }
        }
        return null;
    }

    private String findIndexName(List<SemanticNode> nodes, int index) {
        int currentIndex = index - 1;
        // 首先往前找到 时间节点。
        while (currentIndex >= 0 && nodes.get(currentIndex).getType() != NodeType.DATE) {
            currentIndex--;
        }

        while (currentIndex >= 0) {
            if (nodes.get(currentIndex).getType() == NodeType.FOCUS) {
                SemanticNode node = nodes.get(currentIndex);
                if (node.isFocusNode()) {
                    FocusNode focusNode = (FocusNode) node;

                    // 指标 包含在 FocusNode 里面
                    if (focusNode.getIndex() != null) {
                        return focusNode.getIndex().getText();
                    }
                }
            }
            currentIndex--;
        }
        return "";
    }

}
