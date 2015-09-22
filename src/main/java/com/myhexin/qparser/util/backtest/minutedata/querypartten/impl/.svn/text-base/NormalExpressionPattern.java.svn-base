package com.myhexin.qparser.util.backtest.minutedata.querypartten.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.myhexin.qparser.define.EnumDef.NodeType;
import com.myhexin.qparser.node.SemanticNode;
import com.myhexin.qparser.util.backtest.minutedata.querypartten.MinuteDataQueryPattern;

public class NormalExpressionPattern implements MinuteDataQueryPattern {

    private Set<String> OP = new HashSet<String>();

    public NormalExpressionPattern() {
        OP.add(">");
        OP.add("<");
        OP.add(">=");
        OP.add("<=");
    }

    @Override
    public boolean isPatternMatch(List<SemanticNode> nodes) {
        for (int i = 0; i < nodes.size(); i++) {
            if (OP.contains(nodes.get(i).getPubText())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String matchPattern(List<SemanticNode> nodes) {
        boolean matched = isPatternMatch(nodes);

        if (matched) {
            for (int i = 0; i < nodes.size(); i++) {
                if (OP.contains(nodes.get(i).getPubText())) {
                    // find the op, try to find if there is number op after it.
                    SemanticNode numberNode = findNumberOp(nodes, i + 1);

                    StringBuilder sb = new StringBuilder();
                    sb.append(nodes.get(i - 1).getPubText()).append(",");
                    sb.append("-%").append(",");
                    sb.append(nodes.get(i + 1).getPubText()).append(",");

                    if (numberNode == null) {
                        sb.append(nodes.get(i).getPubText() + "0");
                    } else {
                    	System.out.println(nodes.get(i).getPubText());
                    	System.out.println(convertNumberNodeValue(numberNode.getPubText()));
                        sb.append(nodes.get(i).getPubText()).append(",").append(convertNumberNodeValue(numberNode.getPubText()));
                    }
                    return sb.toString();
                }
            }
        }
        return "";
    }

    private String convertNumberNodeValue(String numberNodeValue) {
        int pos = numberNodeValue.indexOf("%");
        if (pos > 0) {
            String value = numberNodeValue.substring(0, pos);
            double v = Integer.parseInt(value) / 100.0;
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
