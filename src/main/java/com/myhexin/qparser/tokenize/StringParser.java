package com.myhexin.qparser.tokenize;

import java.util.ArrayList;
import java.util.HashMap;

import org.springframework.stereotype.Component;

import com.myhexin.qparser.conf.SpecialWords;
import com.myhexin.qparser.define.EnumDef.LogicType;
import com.myhexin.qparser.define.EnumDef.NodeType;
import com.myhexin.qparser.define.EnumDef.SpecialWordType;
import com.myhexin.qparser.define.MiscDef;
import com.myhexin.qparser.node.Environment;
import com.myhexin.qparser.node.LogicNode;
import com.myhexin.qparser.node.NodeUtil;
import com.myhexin.qparser.node.SemanticNode;
import com.myhexin.qparser.node.StrNode;
import com.myhexin.qparser.node.UnknownNode;

/**
 * 对字符串进行识别、合并、ofwhat属性改造。主要根据trigger、字符串型指标进行未识别词到StrVal词语的转换，
 * 以及StrVal的属性改变，并对相邻的相同属性的字符串进行合并处理。
 * 
 */
@Component
public class StringParser {
	//private static org.slf4j.Logger logger_ = org.slf4j.LoggerFactory.getLogger(StringParser.class.getName());
	
	//所属概念,所属申万行业,经营范围,所属证监会行业,主营产品名称,所属同花顺行业
    //private Pattern industryConpcetPattern = Pattern.compile("所属概念|所属申万行业|经营范围|所属证监会行业|主营产品名称|所属同花顺行业") ;
    


    public ArrayList<SemanticNode> parse(Environment ENV,ArrayList<SemanticNode> nodes) {

        for (int i = 0; i < nodes.size(); i++) {
            if (nodes.get(i).type == NodeType.STR_VAL) {
                mergeStrNodes(ENV,nodes, i);
            }
        }
        
        return nodes;
    }
	
    
    /**
     * 将当前StrNod右边连续出现的、有重叠OfWhat的StrNode合成一个StrNode
     * @param pos 当前StrNode位置
     * @param nodes 所有语义节点
     */
    private void mergeStrNodes(Environment ENV,ArrayList<SemanticNode> nodes, int pos) {
        int curPos = pos;
        StrNode curStrNode = (StrNode) nodes.get(curPos);
        if(NodeUtil.isBoolStrNode(curStrNode)) {
            return;
        }
        
        StrNode lastStrNode = curStrNode;
        LogicType strLogic = null;
        while (++curPos < nodes.size()) {
            SemanticNode curNode = nodes.get(curPos);
            if (curNode.type == NodeType.STR_VAL) {
                StrNode sn = (StrNode)curNode;
                if(!StrNodeUtil.hasSameOfWhat(ENV,curStrNode, sn)) {
                    break;
                } else if(lastStrNode != null){ //无任何分割，直接合并
                    lastStrNode.setText( lastStrNode.getText() + sn.getText() );
                    lastStrNode.subType.retainAll(sn.subType);
                    HashMap<String, String> lastId = lastStrNode.getId();
                    for(String key : sn.getId().keySet()){
                    	if(lastId.containsKey(key))
                    		lastId.put(key, lastId.get(key)+"|"+sn.getId().get(key));
                    	else
                    		lastId.put(key, sn.getId().get(key));
                    }
                    nodes.remove(curPos);
                    curPos--;
                } else {
                    lastStrNode = sn;
                }
            } else if (curNode.type == NodeType.LOGIC) {
                lastStrNode = null;
                LogicNode ln = (LogicNode)curNode;
                // 现只支持一种逻辑：要么是”and“，要么是”or“
                //and可改成or，但反之不行。这是由于A、B或C，之前的、视作为and了
                if (strLogic != null && strLogic != ln.logicType) {
                    break;
                } else if(curPos + 1 < nodes.size() &&
                        nodes.get(curPos+1).type == NodeType.STR_VAL &&
                        StrNodeUtil.hasSameOfWhat(ENV,curStrNode, (StrNode)nodes.get(curPos+1))) {
                    //逻辑节点后还是同类StrNode
                    strLogic = ((LogicNode) curNode).logicType;
                    lastStrNode = (StrNode)nodes.get(curPos+1);
                    nodes.remove(curPos);
                } else {
                    break;
                }
            } else if (curNode.type == NodeType.UNKNOWN) {
                lastStrNode = null;
                UnknownNode un = (UnknownNode)curNode;
                boolean isNextNodeHasSameOfWhat = curPos + 1 < nodes.size() &&
                        nodes.get(curPos+1).type == NodeType.STR_VAL &&
                            StrNodeUtil.hasSameOfWhat(ENV,curStrNode, (StrNode)nodes.get(curPos+1)); 
                if(SpecialWords.hasWord(un.getText(), SpecialWordType.TRIGGER_SKIP) &&
                        isNextNodeHasSameOfWhat) {
                    strLogic = LogicType.OR;
                    lastStrNode = (StrNode)nodes.get(curPos+1);
                    nodes.remove(curPos);
                } else {
                    break;
                }
            } else {
                break;
            }
        }
        
        doMergeStrNodes(nodes, pos, curPos, strLogic);
    }

    private void doMergeStrNodes(ArrayList<SemanticNode> nodes, int from,
            int to, LogicType logic) {
        StrNode surviveNode = (StrNode)nodes.get(from);
        final String logicStr = logic == null ? "" : logic == LogicType.AND ?
                MiscDef.STR_AND : MiscDef.STR_OR;
        for (int n = to - from; --n > 0;) {
            StrNode curNode = (StrNode)nodes.get(from + 1);
            surviveNode.subType.addAll(curNode.subType);
            HashMap<String, String> surviveId = surviveNode.getId();
            for(String key : curNode.getId().keySet()){
            	if(surviveId.containsKey(key))
            		surviveId.put(key, surviveId.get(key)+"|"+curNode.getId().get(key));
            	else
            		surviveId.put(key, curNode.getId().get(key));
            }
            surviveNode.setText( surviveNode.getText() +  String.format("%s%s", logicStr, curNode.getText()) );
            nodes.remove(from + 1);
        }
    }
    
    
}
