package com.myhexin.server.plugins;

import java.text.DecimalFormat;
import java.util.ArrayList;

import com.myhexin.qparser.define.EnumDef.NodeType;
import com.myhexin.qparser.node.BoundaryNode;
import com.myhexin.qparser.node.Environment;
import com.myhexin.qparser.node.FocusNode;
import com.myhexin.qparser.node.SemanticNode;
import com.myhexin.qparser.node.FocusNode.FocusItem;
import com.myhexin.qparser.phrase.PhraseInfo;
import com.myhexin.qparser.phrase.SyntacticSemantic.Syntactic.SyntacticPattern;
import com.myhexin.server.vo.impl.ParserParam;
import com.myhexin.server.vo.impl.ParserResult;

/**
 * @author 徐祥
 * @createDataTime 2014-5-9 下午5:24:27
 * @description 解析组合插件抽象类
 */
public abstract class ParserPluginsAbstract implements Plugins {
	
	@Override
	public Object work(Object obj) {
		return work((ParserParam) obj);
	}
	
	/**
	 * @descrption 解析组合插件处理抽象函数
	 */
	public abstract ParserResult work(ParserParam param);
	
    protected String getSentence(ArrayList<ArrayList<SemanticNode>> qlist, boolean bPrintFocusList) {
        if (qlist == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (ArrayList<SemanticNode> list : qlist) {
            sb.append(formatSentence(list, true, bPrintFocusList));
            sb.append("\n");
        }
        return sb.toString().trim();
    }
    
    protected String formatSentence(ArrayList<SemanticNode> list, boolean bPrintBoundary, boolean bPrintFocusList) {
    	DecimalFormat df = new DecimalFormat("0.00");
    	StringBuilder sb = new StringBuilder();
        for (SemanticNode sn : list) {
            if(sn.score != -1)
                sb.append("[score:"+sn.score+"]");
            if (sn.type == NodeType.BOUNDARY && bPrintBoundary) {
                BoundaryNode bn = (BoundaryNode) sn;
                if (bn.isStart()) {
                    sb.append("【");
                    String syntacticPatternId = bn.getSyntacticPatternId();
                    sb.append("<a href=\"http://172.20.201.164/test/?op=syntactic&id="+syntacticPatternId+"\" target=_blank>"+syntacticPatternId+"</a>");
                    if (BoundaryNode.getImplicitPattern(syntacticPatternId) == null) {
            		    SyntacticPattern syntPtn = PhraseInfo.getSyntacticPattern(syntacticPatternId);
            		    String semanticPatternId = syntPtn.getSemanticBind().getId();
            		    semanticPatternId = semanticPatternId!=null ? semanticPatternId : syntPtn.getSemanticBind().getSemanticBindToIds();
            		    String[] temps = semanticPatternId.split("&");
            		    sb.append(":");
            		    for (String temp : temps) {
            		    	sb.append("<a href=\"http://172.20.201.164/test/?op=semantic&id="+temp+"\" target=_blank>"+temp+"</a> ");
            		    }
                    }
                } else {
                    sb.append("】");
                }
            } else if (sn.type != NodeType.BOUNDARY) {
                sb.append("("+sn.getText());
                if (sn.isBoundToSynt)
                	sb.append("|b-synt");
                else if (sn.isBoundToIndex())
                	sb.append("|b-index");
                if (bPrintFocusList) {
                    if (sn.getCanDelIndexStrs() != null) {
                        sb.append("(D=");
                        for (String s : sn.getCanDelIndexStrs()) {
                            sb.append(s).append(",");
                        }
                        sb.append(")");
                    }
                    if (sn.type == NodeType.FOCUS && ((FocusNode) sn).hasIndex()) {
                        String types = String.format("[");
                        FocusNode node = (FocusNode) sn;
                        if(node.getIndex() != null) {
                            sb.append("-"+node.getIndex().getText());
                            sb.append(node.getIndex().getDomains());
                        }
                        for (int j = 0; j < node.focusList.size(); j++) {
                            FocusItem item = node.focusList.get(j);
                            if (item.getType() == FocusNode.Type.INDEX) {
                                types += "|" + item.getContent();
                                types += "" + item.getIndex().getDomains();
                                types += ":"+df.format(item.getScore());
                                if (item.isCanDelete()) {
                                    types += "(D)";
                                }
                            }
                        }
                        types += "]";
                        sb.append(types);
                    }
                }
                sb.append(")");
            }
        }
        return sb.toString().trim();
    }

}
