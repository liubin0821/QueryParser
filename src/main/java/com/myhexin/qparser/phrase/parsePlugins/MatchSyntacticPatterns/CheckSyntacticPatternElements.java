package com.myhexin.qparser.phrase.parsePlugins.MatchSyntacticPatterns;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.myhexin.qparser.Query;
import com.myhexin.qparser.date.DateCompute;
import com.myhexin.qparser.date.DateRange;
import com.myhexin.qparser.define.EnumDef.DescNodeType;
import com.myhexin.qparser.define.EnumDef.NodeType;
import com.myhexin.qparser.define.EnumDef.Unit;
import com.myhexin.qparser.except.NotSupportedException;
import com.myhexin.qparser.iterator.BoundaryInfos;
import com.myhexin.qparser.iterator.DescNode;
import com.myhexin.qparser.iterator.DescNodeNum;
import com.myhexin.qparser.iterator.SemanticRepresentationIteratorImpl;
import com.myhexin.qparser.iterator.SyntacticIteratorImpl;
import com.myhexin.qparser.iterator.SyntacticRepresentationIteratorImpl;
import com.myhexin.qparser.node.BoundaryNode;
import com.myhexin.qparser.node.BoundaryNode.SyntacticPatternExtParseInfo;
import com.myhexin.qparser.node.DateNode;
import com.myhexin.qparser.node.Environment;
import com.myhexin.qparser.node.FocusNode;
import com.myhexin.qparser.node.NumNode;
import com.myhexin.qparser.node.SemanticNode;
import com.myhexin.qparser.node.StrNode;
import com.myhexin.qparser.node.UnknownNode;
import com.myhexin.qparser.number.NumParser;
import com.myhexin.qparser.number.NumRange;
import com.myhexin.qparser.onto.ClassNodeFacade;
import com.myhexin.qparser.phrase.PhraseInfo;
import com.myhexin.qparser.phrase.SyntacticSemantic.Semantic.SemanticArgument;
import com.myhexin.qparser.phrase.SyntacticSemantic.Semantic.SemanticArgument.SemanArgType;
import com.myhexin.qparser.phrase.SyntacticSemantic.Semantic.SemanticArgument.ValueType;
import com.myhexin.qparser.phrase.SyntacticSemantic.Semantic.SemanticBindTo;
import com.myhexin.qparser.phrase.SyntacticSemantic.Semantic.SemanticBindToArgument.BindToType;
import com.myhexin.qparser.phrase.SyntacticSemantic.Semantic.SemanticBindToArgument.Source;
import com.myhexin.qparser.phrase.SyntacticSemantic.Semantic.SemanticPattern;
import com.myhexin.qparser.phrase.SyntacticSemantic.Syntactic.SyntacticPattern;
import com.myhexin.qparser.phrase.parsePlugins.ParsePluginsUtil;

public class CheckSyntacticPatternElements {
	
	public static org.slf4j.Logger logger_ = org.slf4j.LoggerFactory.getLogger(CheckSyntacticPatternElements.class.getName());
	
	public static ArrayList<ArrayList<SemanticNode>> checkSyntacticPatternElements(ArrayList<SemanticNode> nodes, Environment ENV) {
		if (checkSyntacticPatternElementsIsComplete(nodes) == true)
			return checkSyntacticPatternElementsIsKeyValue(nodes, ENV);
		else
			return null;
	}
	
	private static boolean checkSyntacticPatternElementsIsComplete(ArrayList<SemanticNode> nodes) {
		if (nodes == null || nodes.size() == 0)
			return false;
        Iterator iterator = new SyntacticIteratorImpl(nodes);
        if (!iterator.hasNext()) {
			return true; // 没有句式
		}
        
    	while (iterator.hasNext()) {
    		BoundaryInfos boundaryInfos = (BoundaryInfos) iterator.next();
    		BoundaryNode bNode = (BoundaryNode) nodes.get(boundaryInfos.bStart);
            BoundaryNode.SyntacticPatternExtParseInfo info = bNode.getSyntacticPatternExtParseInfo(false);
            String patternId = bNode.getSyntacticPatternId();
            if (BoundaryNode.getImplicitPattern(patternId) != null) { 
    			return true;
            } else {
            	SyntacticPattern syntPtn = PhraseInfo.getSyntacticPattern(patternId);
            	// 暂时使用这种处理逻辑，后续需要调整
            	int a = info.presentArgumentCount + info.absentArgumentCount + info.presentKeywordCount + info.absentKeywordCount;
            	int b = syntPtn.getSyntacticElementMax()-1;
            	
            	if (a < b ) {
            		return false;
            	}
            	
            	a= info.elementNodePostList.size();
            	if(a!=b) {
            		return false;
            	}
            }
    	}
            	
		return true;
	}

	public static ArrayList<ArrayList<SemanticNode>> checkSyntacticPatternElementsIsKeyValue(ArrayList<SemanticNode> nodes, Environment ENV) {
		ArrayList<ArrayList<SemanticNode>> rlist = new ArrayList<ArrayList<SemanticNode>>();
		if (nodes == null || nodes.size() == 0)
			return null;
        Iterator iterator = new SyntacticIteratorImpl(nodes);
        if (!iterator.hasNext()) {
        	rlist.add(nodes);
			return rlist; // 没有句式
		}
        
    	while (iterator.hasNext()) {
    		BoundaryInfos boundaryInfos = (BoundaryInfos) iterator.next();
    		BoundaryNode bNode = (BoundaryNode) nodes.get(boundaryInfos.bStart);
            BoundaryNode.SyntacticPatternExtParseInfo info = bNode.getSyntacticPatternExtParseInfo(false);
            String patternId = bNode.getSyntacticPatternId();
            if (BoundaryNode.getImplicitPattern(patternId) != null) { 
            	rlist.add(nodes);
    			return rlist;
            } else {
            	SyntacticPattern syntPtn = PhraseInfo.getSyntacticPattern(patternId);
            	String representation = syntPtn.getSemanticBind().getChineseRepresentation();
                SyntacticRepresentationIteratorImpl it = new SyntacticRepresentationIteratorImpl(representation);
                while(it.hasNext()){
                	DescNode dn = it.next();
                	if (dn.getType() == DescNodeType.NUM) {
                		DescNodeNum dnn = (DescNodeNum) dn;
                		int arg = dnn.getNum();
                        SemanticBindTo semanticBindTo = syntPtn.getSemanticBind().getSemanticBindTo(arg);
                        boolean tempRight = checkSemanticBindTo(syntPtn, semanticBindTo, info, nodes, boundaryInfos.bStart, boundaryInfos.bEnd, ENV);
                        if (tempRight == false) {
                        	return null;
                        }
                	}
                }
            }
    	}
    	rlist.add(nodes);
		return rlist;
	}

	private static boolean checkSemanticBindTo(SyntacticPattern syntPtn,
			SemanticBindTo semanticBindTo, SyntacticPatternExtParseInfo info,
			List<SemanticNode> nodes, int bStart, int bEnd, Environment ENV) {
		if (semanticBindTo == null)
			return true;
		info = info.copy();
        int min = info.getElementNodePosMin();
        if (min >= 0) {
        	info.offsetElementNodePos(min - 1);
        }
		String bindid = semanticBindTo.getBindToId()+"";
        SemanticPattern pattern = PhraseInfo.getSemanticPattern(bindid);
        if(pattern==null) { //liuxiaofeng 这里该返回true 还是 false
        	//logger_.info("[ERROR]pattern is NULL:" +bindid );
        	return false;
        }
        String representation = pattern.getChineseRepresentation();
        if (!pattern.isKeyValue())
        	return true;
        
        SemanticRepresentationIteratorImpl it = new SemanticRepresentationIteratorImpl(representation);
        ArrayList<SemanticNode> keys = new ArrayList<SemanticNode>();
        ArrayList<SemanticNode> values = new ArrayList<SemanticNode>();
        boolean isKeyValueRight = true;
        while(it.hasNext()){
        	DescNode dn = it.next();
        	if (dn.getType() == DescNodeType.NUM) {
        		DescNodeNum dnn = (DescNodeNum) dn;
        		int arg = dnn.getNum();
        		int i = semanticBindTo.getSemanticBindToArgument(arg).getElementId();
        		ArrayList<SemanticNode> temp = null;
                if (semanticBindTo.getSemanticBindToArgument(arg).getSource() == Source.ELEMENT) {
    	            if (semanticBindTo.getSemanticBindToArgument(arg).getBindToType() == BindToType.SYNTACTIC_ELEMENT) {
    	            	SemanticArgument argument = pattern.getSemanticArgument(arg, false);
    		            temp = getElementArgument(argument, info, i, bStart, nodes, (Query.Type) ENV.get("qType",false));
    	            } else {
    	            	SemanticBindTo semanticBindToSun = syntPtn.getSemanticBind().getSemanticBindTo(i);
    	            	boolean tempRight = checkSemanticBindTo(syntPtn, semanticBindToSun, info, nodes, bStart, bEnd, ENV);
    	            	if (tempRight == false) {
    	            		return false;
    	            	}
    	            }
                } else {
                	temp = getFixedArgument(info, i);
                }
                if (temp != null && temp.size() > 0) {
	                for (SemanticNode node : temp) {
	                	if (node.type == NodeType.FOCUS && ((FocusNode)node).hasIndex()) {
	                		for (SemanticNode value : values) {
	                			boolean tempRight = matchValueNodeToIndexNode(node, value, true);
	                			if (tempRight == false) {
	        	            		return false;
	        	            	}
	                		}
	                		keys.add(node);
	                	} else if (node.type == NodeType.NUM || node.type == NodeType.DATE) {
	                		for (SemanticNode key : keys) {
	                			boolean tempRight = matchValueNodeToIndexNode(key, node, true);
	                			if (tempRight == false) {
	        	            		return false;
	        	            	}
	                		}
	                		values.add(node);
	                	}
	                }
                }
        	}
        }
        return isKeyValueRight;
	}

	private static ArrayList<SemanticNode> getElementArgument(SemanticArgument argument, BoundaryNode.SyntacticPatternExtParseInfo info, int elementId, int bStart, List<SemanticNode> nodes, Query.Type qType) {
        ArrayList<Integer> list;
        if (elementId != -1 && (list = info.getElementNodePosList(elementId)) != null) {
            // SyntacticPattern中element的seq是从1开始顺序加入的，见SyntacticPattern.getSyntacticElement
            if (list.size() == 1 && list.get(0) == -1) {
                return getDefaultArgument(argument, info, elementId, nodes, qType);
            } else {
                Collections.sort(list);
                return getPresentArgument(argument, nodes, bStart, list);
            }
        }
        return null;
	}
	
	private static ArrayList<SemanticNode> getFixedArgument(BoundaryNode.SyntacticPatternExtParseInfo info, int fixedArgumentId) {
		ArrayList<SemanticNode> fixedNodes = new ArrayList<SemanticNode>();
		SemanticNode fixedNode = info.fixedArgumentMap.get(fixedArgumentId);
		if (fixedNode == null)
			return null;
		fixedNodes.add(fixedNode);
		return fixedNodes;
	}
	/*
     * 获得句式匹配上的argument的默认信息
     * INDEX\INDEXLIST:默认的指标
     * CONSTANT:默认的文本
     * ANY:默认的指标或默认的文本
     * 
     */
    private static ArrayList<SemanticNode> getDefaultArgument(SemanticArgument argument, BoundaryNode.SyntacticPatternExtParseInfo info,
            int elemPos, List<SemanticNode> sNodes, Query.Type qType) {
    	ArrayList<SemanticNode> defaultNodes = new ArrayList<SemanticNode>();
        switch (argument.getType()) {
            case INDEX:
            case INDEXLIST:
                SemanticNode defaultNode = info.absentDefalutIndexMap.get(elemPos);
                if (defaultNode != null) {
                	defaultNodes.add(defaultNode);
                }
                break;
            case CONSTANT:
            case CONSTANTLIST:
                if (argument.getDefaultValue() != null) {
                    SemanticNode defaultValue = getNodeForDefaultArgument(argument, qType);
                    defaultNodes.add(defaultValue);
                }
                break;
            case ANY:
                if (argument.getDefaultIndex() != null) {
                	SemanticNode defaultIndex = getNodeForDefaultArgument(argument, qType);
                    defaultNodes.add(defaultIndex);
                }
                else if (argument.getDefaultValue() != null) {
                	SemanticNode defaultValue = getNodeForDefaultArgument(argument, qType);
                    defaultNodes.add(defaultValue);
                }
                break;
            default:
                break;
        }
        return defaultNodes;
    }

    /*
     * 获得句式匹配上的argument的相关信息：text和相应的属性修饰
     * INDEX:获得指标的text和focusList
     * INDEXLIST\ANY:获得list中各个node的信息
     * CONSTANT:text
     */
    private static ArrayList<SemanticNode> getPresentArgument(SemanticArgument argument, List<SemanticNode> sNodes, int start,
            List<Integer> poses) {
    	ArrayList<SemanticNode> presentNodes = new ArrayList<SemanticNode>();
        if (argument == null) {
            return null;
        }
        StringBuffer sb = new StringBuffer();
        switch (argument.getType()) {
            case INDEX:
                SemanticNode sNode = getNodeByPosInfo(poses.get(0), start, sNodes);
                presentNodes.add(sNode);
                break;
            case INDEXLIST:
            case ANY:
                List<SemanticNode> nodes = getNodesByPosInfo(poses, start, sNodes);
                presentNodes.addAll(nodes);
                break;
            case CONSTANT:
                SemanticNode node = getNodeByPosInfo(poses.get(0), start, sNodes);
                presentNodes.add(node);
                break;
            case CONSTANTLIST:
            	List<SemanticNode> constantList = getNodesByPosInfo(poses, start, sNodes);
                presentNodes.addAll(constantList);
                break;
            default:
                break;
        }
        return presentNodes;
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

    
    private static SemanticNode getNodeForDefaultArgument(SemanticArgument arg, Query.Type qType) {
		if (arg.getType() == SemanArgType.INDEX || arg.getType() == SemanArgType.INDEXLIST) {
			return getNode(arg.getDefaultIndex(), arg.getType(), arg.getFixedValueType(), qType);
		} else if (arg.getType() == SemanArgType.CONSTANT || arg.getType() == SemanArgType.CONSTANTLIST) {
			return getNode(arg.getDefaultValue(), arg.getType(), arg.getFixedValueType(), qType);
		} else if (arg.getType() == SemanArgType.ANY) {
			SemanticNode semanticNode = null;
			semanticNode = getNode(arg.getDefaultIndex(), arg.getType(), arg.getFixedValueType(), qType);
			if (semanticNode != null)
				return semanticNode;
			semanticNode = getNode(arg.getDefaultValue(), arg.getType(), arg.getFixedValueType(), qType);
			if (semanticNode != null)
				return semanticNode;
		}
		return null;
	}

	public static SemanticNode getNode(String str, SemanArgType type, ValueType valueType, Query.Type qType) {
		if (str == null || str.trim() == "")
			return null;
		if (type == SemanArgType.INDEX || type == SemanArgType.INDEXLIST) {
	        return ParsePluginsUtil.getIndexNodeFromStr(str, qType);
		} else if ((type == SemanArgType.CONSTANT || type == SemanArgType.CONSTANTLIST)
				&& valueType == ValueType.DATE) {
			return ParsePluginsUtil.getDateNodeFromStr(str);
		} else if ((type == SemanArgType.CONSTANT || type == SemanArgType.CONSTANTLIST)
				&& (valueType == ValueType.NUMBER || valueType == ValueType.PERCENTAGE)) {
			return ParsePluginsUtil.getNumNodeFromStr(str);
		} else if ((type == SemanArgType.CONSTANT || type == SemanArgType.CONSTANTLIST)
				&& valueType == ValueType.STRING) {
			return ParsePluginsUtil.getStrNodeFromStr(str);
		} else if ((type == SemanArgType.CONSTANT || type == SemanArgType.CONSTANTLIST)
				&& valueType == ValueType.UNDEFINED) {
			try {
				DateRange dateRange = DateCompute.getDateInfoFromStr(str, null);
				if (dateRange != null) {
					return ParsePluginsUtil.getDateNodeFromStr(str);
				}
				NumRange numRange = NumParser.getNumRangeFromStr(str);
				if (numRange != null) {
					return ParsePluginsUtil.getNumNodeFromStr(str);
				}
				return new UnknownNode(str);
			} catch (NotSupportedException e) {
				logger_.error(e.getMessage());
				return new UnknownNode(str);
			}
		}
		return new UnknownNode(str);
	}
	
	/**
     * 判断是否属于KEY_VALUE类型
     * @param nodes
     * @param indexNodePos KEY即指标的pos
     * @param valueNodePos VALUE即值的pos
     * @param isDelete
     * @return
     */
    private static boolean matchValueNodeToIndexNode(SemanticNode isn, SemanticNode vsn, boolean isDelete) {
        if(isn == null || vsn == null)
            return false;
        if((isn == null || isn.type != NodeType.FOCUS) || vsn == null)
            return false;
        FocusNode fn = (FocusNode)isn;
        if(!fn.hasIndex())
        	return false;
        
        boolean ret = false;
        ArrayList<FocusNode.FocusItem> items = fn.getFocusItemList();
        for (int k = 0; k < items.size();) {
            if (items.get(k).getType() == FocusNode.Type.INDEX) {
                ClassNodeFacade index = items.get(k).getIndex();
                if(index!=null){
                	if(index.isStrIndex()) {
                	StrNode strNode = getStrValInstance(vsn);
                	if (strNode != null) {
                 	   Set<String> propSubType = index.getPropOfValue() !=null  ? index.getPropOfValue().getSubType() : null;
                       if(strNode.hasSubType(propSubType,false)) {
                            ret = true;
                            if(!isDelete)
                                return ret;
                            k++;
                            continue;
                        }
                	}
                } else if(index.isNumIndex() || index.isLongIndex() || index.isDoubleIndex()) {
                    if(vsn.type == NodeType.NUM) {
                    	NumNode nNode = (NumNode)vsn;
                    	Unit unit1 = nNode.getUnit();
                    	List<Unit> unit2 = index.getValueUnits2();
                    	if (isUnitEquals(unit1, unit2)) {
	                        ret = true;
	                        if(!isDelete)
	                            return ret;
	                        k++;
	                        continue;
                    	}
                    }
                } else if(index.isDateIndex()) {
                	if(vsn.type == NodeType.DATE) {
                		DateNode dNode = (DateNode)vsn;
                		if (isDateTypeEquals(index, dNode)) {
	                        ret = true;
	                        if(!isDelete)
	                            return ret;
	                        k++;
	                        continue;
                    	}
                	} else if (vsn.type == NodeType.NUM) {
                		NumNode nNode = (NumNode)vsn;
                		if (isNumTypeEquals(index, nNode)) {
                			ret = true;
	                        if(!isDelete)
	                            return ret;
	                        k++;
	                        continue;
                		}
                	}
                }
            }
            }
            if(isDelete)
                items.remove(k);
            else
                k++;
        }
        return ret;
    }
    
    private static StrNode getStrValInstance(SemanticNode node) {
    	if (node == null || node.isCombined == true)
    		return null;
    	StrNode strNode = null;
        if (node.type == NodeType.STR_VAL) {
            strNode = (StrNode)node;
        } else if (node.type == NodeType.FOCUS) {
        	FocusNode focusNode = (FocusNode) node;
        	if (focusNode.hasString()) {
        		strNode = focusNode.getString();
        	}
        }
        return strNode;
    }
    
    // KEY_VALUE匹配时单位的验证
    // 1、单位相同-true 2、其中一个单位UNKNOWN-true 3、百分比或倍-true
    private static boolean isUnitEquals(Unit unit1, List<Unit> unit2) {
    	//System.out.println(unit1 + ":" + unit2);
    	if (unit2 == null)
    		return false;
    	else if (unit2.size() == 0)
    		return true;
    	boolean ret = false;
    	if (unit2.contains(unit1))	{
            ret = true;
        } else if (unit1 == Unit.UNKNOWN || unit2.contains(Unit.UNKNOWN)) {
        	ret = true;
        } else if ((unit2.contains(Unit.PERCENT) || unit2.contains(Unit.BEI)) 
        		&& (unit1 == Unit.PERCENT || unit1 == Unit.BEI)) {
        	ret = true;
        } else {
        	ret = false;
        }
    	return ret;
    }
    
    // KEY_VALUE匹配时时间类型的验证
    // 暂时没有比较严格的判断逻辑，保留接口
    private static boolean isDateTypeEquals(ClassNodeFacade index, DateNode dNode) {
    	return true;
		//return false;
	}
    
    // KEY_VALUE匹配时时间类型与Num节点的验证
    // index：时间类型指标，有单位
    // NumNode：整型数值，无单位
    private static boolean isNumTypeEquals(ClassNodeFacade index, NumNode nNode) {
    	if (index.isDateIndex() 
    			&& index.getValueUnits2() != null && index.getValueUnits2().size() > 0 
    			&& nNode.isLongNum() && nNode.getUnit() == Unit.UNKNOWN)
    		return true;
		return false;
	}
}
