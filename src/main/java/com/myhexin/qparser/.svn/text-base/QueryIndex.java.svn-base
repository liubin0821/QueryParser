/**
 * @author: 	    吴永行 
 * @dateTime:	  2014-10-8 下午3:03:38
 * @description:   	
 * 
 */
package com.myhexin.qparser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.myhexin.qparser.define.EnumDef.NodeType;
import com.myhexin.qparser.node.BoundaryNode;
import com.myhexin.qparser.node.FocusNode;
import com.myhexin.qparser.node.SemanticNode;
import com.myhexin.qparser.node.BoundaryNode.SyntacticPatternExtParseInfo;
import com.myhexin.qparser.onto.ClassNodeFacade;
import com.myhexin.qparser.phrase.PhraseInfo;
import com.myhexin.qparser.phrase.SyntacticSemantic.Semantic.SemanticArgument;
import com.myhexin.qparser.phrase.SyntacticSemantic.Semantic.SemanticBindTo;
import com.myhexin.qparser.phrase.SyntacticSemantic.Semantic.SemanticPattern;
import com.myhexin.qparser.phrase.SyntacticSemantic.Semantic.SemanticBindToArgument.BindToType;
import com.myhexin.qparser.phrase.SyntacticSemantic.Semantic.SemanticBindToArgument.Source;
import com.myhexin.qparser.phrase.SyntacticSemantic.Syntactic.SyntacticPattern;

public class QueryIndex {

	public static QueryIndex instance = new QueryIndex();
	
    public  List<List<String>> getQueriesIndexs(ArrayList<ArrayList<SemanticNode>> qlist) {
    	List<List<String>> indexsList = new ArrayList<List<String>>();
        if (qlist == null || qlist.size() == 0) {
            return null;
        } else {
            for (int i = 0; i < qlist.size(); i++) {
            	List<String> indexs = getQueryIndexs(qlist.get(i));
            	if(indexs != null)
            		indexsList.add(indexs);
            }
        }
        return indexsList;
    }
    

    
    /**
     * get Query Indexs; 
     */
    public  List<String> getQueryIndexs(ArrayList<SemanticNode> nodes) {
    	List<String> indexs = new ArrayList<String>();
        if (nodes == null || nodes.size() == 0) {
            return null;
        } else {
        	int boundaryStart = 0;
            int boundaryEnd = nodes.size() - 1;
            int lastEnd = -1;
            boolean bMathch = false;
            boolean bEndMatch = false;
            for (int i = 0; i < nodes.size(); i++) {
                SemanticNode sNode = nodes.get(i);
                if (sNode.type == NodeType.BOUNDARY) {
                    BoundaryNode bNode = (BoundaryNode) sNode;
                    if (bNode.isStart()) {
                        bMathch = true;
                        boundaryStart = i;
                    } else if (bNode.isEnd()) {
                        bEndMatch = true;
                        boundaryEnd = i;
                    }
                }
                if (bMathch && bEndMatch) {
                	List<String> index = getIndexFromBoundaryNode(nodes, boundaryStart, boundaryEnd, lastEnd);
                	if (index != null)
                    	indexs.addAll(index);
                	lastEnd = boundaryEnd;
                    bMathch = false;
                    bEndMatch = false;
                }
            }
        }
        return indexs;
    }
   

    /**
     * todo:
     */
    protected  List<String> getIndexFromBoundaryNode(List<SemanticNode> nodes, int start, int end, int lastEnd)
    {
    	List<String> index = new ArrayList<String>();
        BoundaryNode bNode = (BoundaryNode) nodes.get(start);
        BoundaryNode.SyntacticPatternExtParseInfo info = bNode.getSyntacticPatternExtParseInfo(false);
        String patternId = bNode.getSyntacticPatternId();
        // YYY1：句式匹配上KEY_VALUE, STR_INSTANCE, FREE_VAR这三种情况
        if (BoundaryNode.getImplicitPattern(patternId) != null) {
        	ArrayList<Integer> elelist;
			for (int j = 1; (elelist = info.getElementNodePosList(j)) != null; j++) {
				SemanticNode sn = null;
				for (int pos : elelist) {
					if (pos == -1) {
						sn = info.absentDefalutIndexMap.get(j);
					} else {
						sn = nodes.get(start + pos);
					}
					
					if (sn != null && sn.type == NodeType.FOCUS && ((FocusNode) sn).getIndex() != null)
	            		index.add(getIndexShowText(((FocusNode) sn).getIndex()));
					
				}
			}
			//System.out.println("YYY1 "+ index);
			return index;
        }

        // YYY2-3：即匹配上了句式
        SyntacticPattern syntPtn = PhraseInfo.getSyntacticPattern(patternId);
        String bindid = syntPtn.getSemanticBind().getId();
        SemanticPattern pattern = PhraseInfo.getSemanticPattern(bindid);
        if (pattern == null) {
        	return getIndexFromBoundaryNodeNoneImplicit(syntPtn, null, info, nodes, start, end);
        } else {
        	return null;
        }
    }
  
    
    /**
     * @author: 	    吴永行 
     * @dateTime:	  2014-10-9 上午9:57:48
     * @description:   	
     * @param index
     * @return
     */
    protected String getIndexShowText(ClassNodeFacade index) {
	    if(index == null)
	    	return "";
	    return index.getText();
    }



	// YYY3：匹配上了句式，语义
    private  List<String> getIndexFromBoundaryNodeNoneImplicit(SyntacticPattern syntPtn, SemanticPattern pattern, BoundaryNode.SyntacticPatternExtParseInfo info, List<SemanticNode> nodes, int start, int end)
    {
    	List<String> index = new ArrayList<String>();
    	if(syntPtn.getSemanticBind().getId() != null)
    		return null;
        String representation = syntPtn.getSemanticBind().getChineseRepresentation();
        int idx = representation.indexOf("#");
        while (idx != -1 && idx < representation.length() - 1) {
            String txt = "";
            for (int i = idx + 1; i < representation.length(); i++) {
                char c = representation.charAt(i);
                if (Character.isDigit(c)) {
                    txt += c;
                } else {
                    break;
                }
            }
            if (txt.length() == 0) {
                idx = representation.indexOf("#", idx + 1);
                continue;
            }
            int arg = Integer.parseInt(txt);
            SemanticBindTo semanticBindTo = syntPtn.getSemanticBind().getSemanticBindTo(arg);
            List<String> temp = getIndexFromBoundaryNode(syntPtn, semanticBindTo, info, nodes, start, end);
            index.addAll(temp);
            idx = representation.indexOf("#", idx + 1);
        }
        //System.out.println("YYY3 "+ index);
        return index;
    }
    
    
    private  List<String> getIndexFromBoundaryNode(SyntacticPattern syntPtn,
			SemanticBindTo semanticBindTo, SyntacticPatternExtParseInfo info,
			List<SemanticNode> nodes, int bStart, int bEnd) {
		if (semanticBindTo==null)
			return null;
		List<String> index = new ArrayList<String>();
		String bindid = semanticBindTo.getBindToId()+"";
        SemanticPattern pattern = PhraseInfo.getSemanticPattern(bindid);
        String representation = pattern.getChineseRepresentation();
        int idx = representation.indexOf("$");
        while (idx != -1 && idx < representation.length() - 1) {
            String txt = "";
            for (int i = idx + 1; i < representation.length(); i++) {
                char c = representation.charAt(i);
                if (Character.isDigit(c)) {
                    txt += c;
                } else {
                    break;
                }
            }
            if (txt.length() == 0) {
                idx = representation.indexOf("$", idx + 1);
                continue;
            }
            int arg = Integer.parseInt(txt);
            int i = semanticBindTo.getSemanticBindToArgument(arg).getElementId();
            if (semanticBindTo.getSemanticBindToArgument(arg).getSource() == Source.ELEMENT) {
	            if (semanticBindTo.getSemanticBindToArgument(arg).getBindToType() == BindToType.SYNTACTIC_ELEMENT) {
		            SemanticArgument argument = pattern.getSemanticArgument(arg, false);
		            ArrayList<Integer> list;
		            if (i != -1 && (list = info.getElementNodePosList(i)) != null) {
		                // SyntacticPattern中element的seq是从1开始顺序加入的，见SyntacticPattern.getSyntacticElement
		            	if (list.size() == 1 && list.get(0) == -1) {
	                        String temp = getDefaultArgument(argument, info, i, nodes);
	                        //System.out.println("default:" + temp);
	                        if (temp != null && !temp.equals(""))
	                        	index.add(temp);
	                    } else {
	                        Collections.sort(list);
	                        String temp = getArgument(argument, nodes, bStart, list);
	                        //System.out.println("argument:" + temp);
	                        if (temp != null && !temp.equals(""))
	                        	index.add(temp);
	                    }
		            }
		            
	            } else {
	            	SemanticBindTo semanticBindToSun = syntPtn.getSemanticBind().getSemanticBindTo(i);
	            	List<String> temp = getIndexFromBoundaryNode(syntPtn, semanticBindToSun, info, nodes, bStart, bEnd);
	                index.addAll(temp);
	            }
            } else {
            	if (semanticBindTo.getSemanticBindToArgument(arg).getType() == SemanticArgument.SemanArgType.INDEX 
            			|| semanticBindTo.getSemanticBindToArgument(arg).getType() == SemanticArgument.SemanArgType.INDEXLIST) {
            		String temp = semanticBindTo.getSemanticBindToArgument(arg).getIndex();
            		index.add(temp);
            	} 
            }
            idx = representation.indexOf("$", idx + 1);
        }
    	return index;
	}
    
    
    /*
     * 获得句式匹配上的argument的默认信息
     * INDEX\INDEXLIST:回写默认的指标
     * CONSTANT:回写默认的文本
     * ANY:回写默认的文本
     * 
     */
    public  String getDefaultArgument(SemanticArgument argument, BoundaryNode.SyntacticPatternExtParseInfo info,
            int elemPos, List<SemanticNode> sNodes) {
        String text = "";
        switch (argument.getType()) {
            case INDEX:
            case INDEXLIST:
                SemanticNode defaultNode = info.absentDefalutIndexMap.get(elemPos);
                if (defaultNode != null && defaultNode.type == NodeType.FOCUS) {
                	FocusNode fn = (FocusNode) defaultNode;
                	if (fn.hasIndex())
                		text = getIndexShowText(fn.getIndex());
                }
                break;
            case CONSTANT:
            case CONSTANTLIST:
                break;
            case ANY:
                if (argument.getDefaultIndex() != null) {
                    text = argument.getDefaultIndex();
                }
                break;
            default:
                break;
        }
        if (text.length() == 0) {
            text = argument.getSpecificIndex();
        }
        return text;
    }
 
    
    /*
     * 获得句式匹配上的argument的相关信息：text和相应的属性修饰
     * INDEX:获得指标的text和相应的属性
     * INDEXLIST\ANY:获得list中各个node的信息
     * CONSTANT:直接将text回写
     */
    public  String getArgument(SemanticArgument argument, List<SemanticNode> sNodes, int start,
            List<Integer> poses) {
        if (argument == null) {
            return "";
        }
        StringBuffer sb = new StringBuffer();
        switch (argument.getType()) {
            case INDEX:
                SemanticNode sNode = getNodeByPosInfo(poses.get(0), start, sNodes);
                if (sNode.type == NodeType.FOCUS && ((FocusNode) sNode).getIndex() != null) 
                	sb.append(getIndexShowText(((FocusNode) sNode).getIndex()));
                break;
            case INDEXLIST:
            case ANY:
                List<SemanticNode> nodes = getNodesByPosInfo(poses, start, sNodes);
                for (int i = 0; i < nodes.size(); i++) { 
                    if (nodes.get(i).type == NodeType.FOCUS && ((FocusNode) nodes.get(i)).getIndex() != null)
                    	sb.append(getIndexShowText(((FocusNode) nodes.get(i)).getIndex()));
                }
                break;
            case CONSTANT:
            case CONSTANTLIST:
                break;
            default:
                break;
        }
        return sb.toString();
    }
    
    protected  List<SemanticNode> getNodesByPosInfo(List<Integer> poses, int start,
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
    
    protected  SemanticNode getNodeByPosInfo(int pos, int start, List<SemanticNode> sNodes) {
        return sNodes.get(start + pos);
    }
  
  
}
