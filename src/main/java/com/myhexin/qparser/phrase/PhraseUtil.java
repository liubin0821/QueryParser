/**
 * @author: 	    吴永行 
 * @dateTime:	  2013-10-29 下午6:51:18
 * @description:   	
 * 
 */
package com.myhexin.qparser.phrase;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.myhexin.qparser.define.EnumDef.NodeType;
import com.myhexin.qparser.node.BoundaryNode;
import com.myhexin.qparser.node.FocusNode;
import com.myhexin.qparser.node.FocusNode.FocusItem;
import com.myhexin.qparser.node.SemanticNode;
import com.myhexin.qparser.phrase.SyntacticSemantic.Syntactic.SyntacticPattern;

public class PhraseUtil {
	
	protected static void addCalcScoreFormula(StringBuilder log_sb) {
		if(log_sb!=null) {
		log_sb.append("## 0ms ## Calculate_Score计算公式\n");
    	log_sb.append("节点使用比例： (句式内 + 句式外被使用节点) 占 总节点比例\n");
        log_sb.append("被使用节点占非unknow节点比例：(被绑定的参数个数+被绑定的关键字个数+句式内外绑定的节点数)  / 句式内外节点数，非unknown节点 \n");
        log_sb.append("非关键字节点被使用比例: (句式内 + 句式外被使用节点) 占 总节点比例 \n");
        log_sb.append("关键字被使用比例: 被绑定的关键字个数  / 关键字 \n");
    	log_sb.append("句式内的分隔符和句式数量的比值(扣分项): 句式内分隔符的个数 / 句式总数 \n");
    	log_sb.append("句式内被使用节点占句式内节点总数比例: 句式内被使用节点 / 句式内节点总数 \n");
	
		}
	}
	/**
	 * 取boundaryNode的位置信息，并在头上增加0位置，最后增加最后一个节点位置
	 * 
	 * 结果 [0, bstartpos1, bendpos1, bstartpos2, bendpos2 ... bendposn,
	 * nodes.size()]
	 * 
	 * @param nodes
	 * @return ArrayList<Integer> 位置信息数组
	 */
	public static ArrayList<Integer> getSyntacitcPatternList(
			ArrayList<SemanticNode> nodes) {
		int len = nodes.size();
		ArrayList<Integer> ret = new ArrayList<Integer>();
		ret.add(0);
		for (int i = 0; i < len; i++) {
			SemanticNode node = nodes.get(i);
			if (node.type == NodeType.BOUNDARY) {
				ret.add(i);
			}
		}
		ret.add(len);
		return ret;
	}
	
	public static boolean isIndex(SemanticNode sn) {
		boolean rtn = false;
		if ((sn != null) && (sn.type == NodeType.FOCUS)) {
			FocusNode fn = (FocusNode) sn;
			if (fn.getIndex() != null) {
				rtn = true;
			}
		}
		return rtn;
	}
	
    public static final List<SemanticNode> getNodesByPosInfo(List<Integer> poses, int start,
            List<SemanticNode> sNodes) {
        List<SemanticNode> nodes = new ArrayList<SemanticNode>();
        for (int i = 0; i < poses.size(); i++) {
            SemanticNode sNode = getNodeByPosInfo(poses.get(i), start, sNodes);
            if (sNode != null) {
                nodes.add(sNode);
            }
        }
        return nodes;
    }
    
    public static final SemanticNode getNodeByPosInfo(int pos, int start, List<SemanticNode> sNodes) {
        return sNodes.get(start + pos);
    }
    
    protected static void debug(String pluginTitle, ParserAnnotation annotation) {
    	/*boolean debug = RefCodeInfo.getInstance().isdebug();
    	if(debug) {
    		//PhraseParserPrePluginWordSegment
    		if(pluginTitle.equals(PhraseParserPrePluginWordSegment.TITLE)) {
        		String segmentedText = annotation.getSegmentedText();
        		System.out.println(pluginTitle);
        		System.out.println("\t分词结果:" + segmentedText);
        	}
    	}*/
    }
    
    /*protected static void printBindingInfo(String pluginTitle, ArrayList<ArrayList<SemanticNode>> qlist) {
    	boolean debug = ResourceInst.getInstance().isdebug();
    	if(debug) {
	    	if(qlist!=null && qlist.size()>0 ) { //&& "#### Bind Num And Date To Index".equals(pluginTitle)
	        	String s = BindingInfoUtil.getBindingInfo(pluginTitle, qlist.get(0));
	        	System.out.println(s);
	        }
    	}
    }*/
    
    static SortByScoreComparator scoreComparator = new SortByScoreComparator();
    protected static void sortByScore(ArrayList<ArrayList<SemanticNode>> qlist) {
    	if(qlist!=null)
    		Collections.sort(qlist, scoreComparator);
    }
    
    public static String nodesHtml(ArrayList<SemanticNode> nodes) {
    	StringBuilder  buf = new StringBuilder();
    	for(SemanticNode node : nodes) {
    		buf.append(node.toString()).append("<BR>");
    	}
    	return buf.toString();
    }
    
    //按分数排序
    static class SortByScoreComparator implements Comparator<ArrayList<SemanticNode>> {
    	@Override
		public int compare(ArrayList<SemanticNode> o1, ArrayList<SemanticNode> o2) {
    		ArrayList<SemanticNode> rlist1 = o1;
    		ArrayList<SemanticNode> rlist2 = o2;
			return rlist2.get(0).score-rlist1.get(0).score;
		}
    }
    
    

    protected static String getSentence(ArrayList<ArrayList<SemanticNode>> qlist, boolean bPrintFocusList) {
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
    
    private static String formatSentence(ArrayList<SemanticNode> list, boolean bPrintBoundary, boolean bPrintFocusList) {
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
            } else if (sn.type != NodeType.BOUNDARY && sn.type != NodeType.ENV) {
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
                                types += ":"+df.format(item.getScore() );
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
