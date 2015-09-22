package com.myhexin.qparser.phrase.parsePlugins;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.myhexin.qparser.define.EnumDef.DescNodeType;
import com.myhexin.qparser.define.EnumDef.NodeType;
import com.myhexin.qparser.iterator.BoundaryInfos;
import com.myhexin.qparser.iterator.DescNode;
import com.myhexin.qparser.iterator.DescNodeNum;
import com.myhexin.qparser.iterator.SemanticRepresentationIteratorImpl;
import com.myhexin.qparser.iterator.SyntacticIteratorImpl;
import com.myhexin.qparser.iterator.SyntacticRepresentationIteratorImpl;
import com.myhexin.qparser.node.BoundaryNode;
import com.myhexin.qparser.node.BoundaryNode.SyntacticPatternExtParseInfo;
import com.myhexin.qparser.node.Environment;
import com.myhexin.qparser.node.FocusNode;
import com.myhexin.qparser.node.SemanticNode;
import com.myhexin.qparser.phrase.ParserAnnotation;
import com.myhexin.qparser.phrase.PhraseInfo;
import com.myhexin.qparser.phrase.SyntacticSemantic.Semantic.SemanticArgument;
import com.myhexin.qparser.phrase.SyntacticSemantic.Semantic.SemanticBindTo;
import com.myhexin.qparser.phrase.SyntacticSemantic.Semantic.SemanticBindToArgument.BindToType;
import com.myhexin.qparser.phrase.SyntacticSemantic.Semantic.SemanticBindToArgument.Source;
import com.myhexin.qparser.phrase.SyntacticSemantic.Semantic.SemanticPattern;
import com.myhexin.qparser.phrase.SyntacticSemantic.Syntactic.SyntacticPattern;

/**
 * 判断是否有句式和语义的elements完全相同的情况
 * 1、首先将每个ArrayList<SemanticNode> nodes中的元素提出来，构造字符串
 * 2、判断其在HashMap<String, ArrayList<SemanticNode>> qlistmap是否已存在，存在则删除，否则put
 */
public class PhraseParserPluginCheckIsSameElements extends PhraseParserPluginAbstract{
	
    public PhraseParserPluginCheckIsSameElements() {
        super("Check_Is_Same_Elements");
    }
    
	public ArrayList<ArrayList<SemanticNode>> process(ParserAnnotation annotation) {
		
		Environment ENV = annotation.getEnv();
		ArrayList<SemanticNode> nodes = annotation.getNodes();
		return checkIsSameElements(nodes, ENV);
    }
    
    public ArrayList<ArrayList<SemanticNode>> checkIsSameElements(ArrayList<SemanticNode> nodes, Environment ENV) {
    	if (nodes == null || nodes.size() == 0)
    		return null;
    	HashMap<String, ArrayList<SemanticNode>> qlistmap = (HashMap<String, ArrayList<SemanticNode>>) ENV.get("qlistmap", false);
    	if (qlistmap == null)
    		qlistmap = new HashMap<String, ArrayList<SemanticNode>>();
    	ArrayList<ArrayList<SemanticNode>> qlist = new ArrayList<ArrayList<SemanticNode>>();
    	String elementsStr = getElementsString(nodes);
    	if (qlistmap.get(elementsStr) == null) {
    		qlistmap.put(elementsStr, nodes);
    		qlist.add(nodes);
    	}
    	ENV.put("qlistmap", qlistmap, true);
        return qlist;
    }
    
    /**
     * 将ArrayList<SemanticNode> nodes中的元素提出来，构造字符串
     * 句式之间用_&_分隔
     */
    public String getElementsString(ArrayList<SemanticNode> nodes) {
    	String split = "_&_";
    	StringBuffer buffer = new StringBuffer();
    	if (nodes == null || nodes.size() == 0)
    		return null;
    	
    	ArrayList<ArrayList<SemanticNode>> tlist = new ArrayList<ArrayList<SemanticNode>>(1);
        Iterator iterator = new SyntacticIteratorImpl(nodes);
        if (!iterator.hasNext()) {
			tlist.add(nodes);
			return buffer.toString(); // 没有句式
		}
        
		if (nodes.get(0).type == NodeType.ENV) {
			Environment listEnv = (Environment) nodes.get(0);
			if (listEnv.containsKey("listDomain")) {
				for(Map.Entry entry : (Entry[])listEnv.get("listDomain",false))
	    		{
					buffer.append("-"+(String)entry.getKey());
	    		}
			}
		}
			
    	while (iterator.hasNext()) {
    		BoundaryInfos boundaryInfos = (BoundaryInfos) iterator.next();
    		String str = getSyntacticElementsString(nodes, boundaryInfos.bStart, boundaryInfos.bEnd);
            if (buffer.length() > 0) {
                buffer.append(split);
            }
            buffer.append(str);
    	}
    	
        return buffer.toString();
    }
    
    /**
     * 将句式内的元素提出来，构造字符串
     * 
     */
    public String getSyntacticElementsString(List<SemanticNode> nodes, int bStart, int bEnd) {
    	
        BoundaryNode bNode = (BoundaryNode) nodes.get(bStart);
        BoundaryNode.SyntacticPatternExtParseInfo info = bNode.getSyntacticPatternExtParseInfo(false);
        String patternId = bNode.getSyntacticPatternId();
        
        if (BoundaryNode.getImplicitPattern(patternId) != null) { 
        	return getSyntacticElementsStringImplicit(null, null, info, nodes, bStart, bEnd);
        } else {
        	SyntacticPattern syntPtn = PhraseInfo.getSyntacticPattern(patternId);
        	return getSyntacticElementsStringNonImplicit(syntPtn, null, info, nodes, bStart, bEnd); 
        }
    }
    
    // 句式匹配上KEY_VALUE, STR_INSTANCE, FREE_VAR这三种情况
    private String getSyntacticElementsStringImplicit(SyntacticPattern syntPtn,
    		SemanticPattern pattern, SyntacticPatternExtParseInfo info,
			List<SemanticNode> nodes, int bStart, int bEnd) {
    	StringBuffer elements = new StringBuffer();
    	ArrayList<Integer> elelist;
    	for (int j = 1; (elelist = info.getElementNodePosList(j)) != null; j++) {
            for(int pos: elelist) {
                if(pos == -1)
                    continue;
                elements.append(nodes.get(bStart + pos).getText());
            }
    	}
        return elements.toString();
    }
    
    // 句式匹配上了，非KEY_VALUE, STR_INSTANCE, FREE_VAR
    private String getSyntacticElementsStringNonImplicit(SyntacticPattern syntPtn,
    		SemanticPattern pattern, SyntacticPatternExtParseInfo info,
			List<SemanticNode> nodes, int bStart, int bEnd) {
    	if(syntPtn.getSemanticBind().getId() != null)
    		return null;
    	StringBuffer elements = new StringBuffer();
        String representation = syntPtn.getSemanticBind().getChineseRepresentation();
        
        SyntacticRepresentationIteratorImpl it = new SyntacticRepresentationIteratorImpl(representation);
        while(it.hasNext()){
        	DescNode dn = it.next();
        	if (dn.getType() == DescNodeType.NUM) {
        		DescNodeNum dnn = (DescNodeNum) dn;
        		int arg = dnn.getNum();
                SemanticBindTo semanticBindTo = syntPtn.getSemanticBind().getSemanticBindTo(arg);
                String temp = getSyntacticElementsString(syntPtn, semanticBindTo, info, nodes, bStart, bEnd);
                elements.append(temp);
        	} else if (dn.getType() == DescNodeType.LOGIC) {
        		String logic = dn.getText();
        		if (dn.getText().equals("&"))
        			logic = "且";
                else if (dn.getText().equals("|"))
                	logic = "或";
                else if (dn.getText().equals("!"))
                	logic = "非";
        		elements.append(logic);
        	} else {
        		elements.append(dn.getText());
        	}
        }
		return elements.toString();
	}

	private String getSyntacticElementsString(SyntacticPattern syntPtn,
			SemanticBindTo semanticBindTo, SyntacticPatternExtParseInfo info,
			List<SemanticNode> nodes, int bStart, int bEnd) {
		if (semanticBindTo==null)
			return null;
		StringBuffer elements = new StringBuffer();
		String bindid = semanticBindTo.getBindToId()+"";
        SemanticPattern pattern = PhraseInfo.getSemanticPattern(bindid);
        String representation = pattern.getChineseRepresentation();
        
        SemanticRepresentationIteratorImpl it = new SemanticRepresentationIteratorImpl(representation);
        while(it.hasNext()){
        	DescNode dn = it.next();
        	if (dn.getType() == DescNodeType.NUM) {
        		DescNodeNum dnn = (DescNodeNum) dn;
        		int arg = dnn.getNum();
        		int i = semanticBindTo.getSemanticBindToArgument(arg).getElementId();
                if (semanticBindTo.getSemanticBindToArgument(arg).getSource() == Source.ELEMENT) {
    	            if (semanticBindTo.getSemanticBindToArgument(arg).getBindToType() == BindToType.SYNTACTIC_ELEMENT) {
    	            	SemanticArgument argument = pattern.getSemanticArgument(arg, false);
    		            String temp = getElementArgument(argument, info, i, bStart, nodes);
    		            elements.append(temp);
    	            } else {
    	            	SemanticBindTo semanticBindToSun = syntPtn.getSemanticBind().getSemanticBindTo(i);
    	                String temp = getSyntacticElementsString(syntPtn, semanticBindToSun, info, nodes, bStart, bEnd);
    	                elements.append(temp);
    	            }
                } else {
                	String temp = getFixedArgument(info, i); 
                	elements.append(temp);
                }
        	} else if (dn.getType() == DescNodeType.TEXT) {
        		elements.append(dn.getText());
        	} else {
        		elements.append(dn.getText());
        	}
        }
        
    	return elements.toString();
	}

	private static String getElementArgument(SemanticArgument argument, BoundaryNode.SyntacticPatternExtParseInfo info, int elementId, int bStart, List<SemanticNode> nodes) {
        ArrayList<Integer> list;
        if (elementId != -1 && (list = info.getElementNodePosList(elementId)) != null) {
            // SyntacticPattern中element的seq是从1开始顺序加入的，见SyntacticPattern.getSyntacticElement
            if (list.size() == 1 && list.get(0) == -1) {
                return getDefaultArgument(argument, info, elementId, nodes);
            } else {
                Collections.sort(list);
                return getPresentArgument(argument, nodes, bStart, list);
            }
        }
        return "";
	}
	
	private static String getFixedArgument(BoundaryNode.SyntacticPatternExtParseInfo info, int fixedArgumentId) {
		SemanticNode fixedNode = info.fixedArgumentMap.get(fixedArgumentId);
		String temp = "";
		if (fixedNode != null) {
        	if (fixedNode.type == NodeType.FOCUS && ((FocusNode)fixedNode).hasIndex()) { 
        		temp = fixedNode.getText()+((FocusNode)fixedNode).getFocusItemList();
        	} else {
        		temp = fixedNode.getText();
        	}
        }
		return temp;
	}
	/*
     * 获得句式匹配上的argument的默认信息
     * INDEX\INDEXLIST:默认的指标
     * CONSTANT:默认的文本
     * ANY:默认的指标或默认的文本
     * 
     */
    private static String getDefaultArgument(SemanticArgument argument, BoundaryNode.SyntacticPatternExtParseInfo info,
            int elemPos, List<SemanticNode> sNodes) {
        String text = "";
        switch (argument.getType()) {
            case INDEX:
            case INDEXLIST:
                SemanticNode defaultNode = info.absentDefalutIndexMap.get(elemPos);
                if (defaultNode != null) {
                	if (defaultNode.type == NodeType.FOCUS && ((FocusNode)defaultNode).hasIndex()) { 
                    	text = defaultNode.getText()+((FocusNode)defaultNode).getFocusItemList();
                	} else {
                    	text = defaultNode.getText();
                	}
                }
                break;
            case CONSTANT:
            case CONSTANTLIST:
                if (argument.getDefaultValue() != null) {
                    text = argument.getDefaultValue();
                }
                break;
            case ANY:
                if (argument.getDefaultIndex() != null) {
                    text = argument.getDefaultIndex();
                }
                else if (argument.getDefaultValue() != null) {
                    text = argument.getDefaultValue();
                }
                break;
            default:
                break;
        }
        return text;
    }

    /*
     * 获得句式匹配上的argument的相关信息：text和相应的属性修饰
     * INDEX:获得指标的text和focusList
     * INDEXLIST\ANY:获得list中各个node的信息
     * CONSTANT:text
     */
    private static String getPresentArgument(SemanticArgument argument, List<SemanticNode> sNodes, int start,
            List<Integer> poses) {
        if (argument == null) {
            return "";
        }
        StringBuffer sb = new StringBuffer();
        switch (argument.getType()) {
            case INDEX:
                SemanticNode sNode = getNodeByPosInfo(poses.get(0), start, sNodes);
                if (sNode.type == NodeType.FOCUS && ((FocusNode)sNode).hasIndex()) 
                	sb.append(sNode.getText()+((FocusNode)sNode).getFocusItemList());
                else
                	sb.append(sNode.getText());
                break;
            case INDEXLIST:
            case ANY:
                List<SemanticNode> nodes = getNodesByPosInfo(poses, start, sNodes);
                for (int i = 0; i < nodes.size(); i++) {
                	SemanticNode sn = nodes.get(i);
                    if (sn.type == NodeType.FOCUS && ((FocusNode)sn).hasIndex()) 
                    	sb.append(sn.getText()+((FocusNode)sn).getFocusItemList());
                    else
                    	sb.append(sn.getText());
                }
                break;
            case CONSTANT:
                SemanticNode node = getNodeByPosInfo(poses.get(0), start, sNodes);
                sb.append(node.getText());
                break;
            case CONSTANTLIST:
            	List<SemanticNode> constantList = getNodesByPosInfo(poses, start, sNodes);
                for (int i = 0; i < constantList.size(); i++) {
                	SemanticNode sn = constantList.get(i);
                    sb.append(sn.getText());
                }
                break;
            default:
                break;
        }
        return sb.toString();
    }
    
    private static List<SemanticNode> getNodesByPosInfo(List<Integer> poses, int start,
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
    
    private static SemanticNode getNodeByPosInfo(int pos, int start, List<SemanticNode> sNodes) {
        return sNodes.get(start + pos);
    }
}

