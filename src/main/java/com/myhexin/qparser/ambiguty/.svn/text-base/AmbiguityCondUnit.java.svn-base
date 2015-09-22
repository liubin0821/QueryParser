package com.myhexin.qparser.ambiguty;

import java.util.ArrayList;
import java.util.List;

import com.myhexin.qparser.define.EnumDef.NodeType;
import com.myhexin.qparser.define.EnumDef.PropType;
import com.myhexin.qparser.define.EnumDef.Unit;
import com.myhexin.qparser.node.BoundaryNode;
import com.myhexin.qparser.node.BoundaryNode.IMPLICIT_PATTERN;
import com.myhexin.qparser.node.DateNode;
import com.myhexin.qparser.node.FocusNode;
import com.myhexin.qparser.node.NumNode;
import com.myhexin.qparser.node.SemanticNode;
import com.myhexin.qparser.node.StrNode;
import com.myhexin.qparser.onto.ClassNodeFacade;
import com.myhexin.qparser.onto.PropNodeFacade;
import com.myhexin.qparser.phrase.PhraseInfo;
import com.myhexin.qparser.phrase.SyntacticSemantic.Semantic.SemanticBindTo;
import com.myhexin.qparser.phrase.SyntacticSemantic.Semantic.SemanticPattern;
import com.myhexin.qparser.phrase.SyntacticSemantic.Syntactic.SyntacticPattern;

class AmbiguityCondUnit extends AmbiguityCondAbstract{

    /**
     * init
     */
	public AmbiguityCondUnit()
	{
        super("unit");
	}
	
	private final static String[] COMPARE_WORDS = new String[]{"之比","的比值"};
	private final static String[] SORT_WORDS = new String[]{"排名","最大","最小","最多","最少"};
	private final static String[] LONGTOU_WORDS = new String[]{"知识类","龙头股","龙头"};
	
    /**
     * 匹配一个条件。
     *
     * @param nodes query node list
     * @param start start
     * @param end end 
     * @param bStart boundary start
     * @param bEnd boundary end 
     * @param cNode 可能的class node
     * @return int 返回匹配结果
     *              －1 完全不合适，应该删除
     *              0 － 100 正确的可能性，100为最大
     */
	public double matchNode(ArrayList<SemanticNode> nodes, int start, int end, int bStart , int bEnd, ClassNodeFacade cNode, int cPos, String alias)
    {
		// 信号包括：
		// 1、是否存在相同单位
		// 2、是否存在单位为UNKNOWN
		// 3、是否存在单位为PERCENT或BEI
		// 4、是否存在单位为只
		// 5、是否存在不同单位
    	//System.out.println("type:"+type_);
        double ret = 50;
        // 当解析为比较的语义时（即之比 或 的比值），值范围不能用于消歧
        // 或者排名类时，值范围不能用于消歧
        if (isSemanticsOfWords(nodes, bStart, COMPARE_WORDS) || isSemanticsOfWords(nodes, bStart, SORT_WORDS) || isSemanticsOfWords(nodes, bStart, LONGTOU_WORDS)) {
    		return ret;
    	}
        // 如果为FREE_VAR，不需要判断单位
        if (((BoundaryNode) nodes.get(bStart)).getSyntacticPatternId().equals(IMPLICIT_PATTERN.FREE_VAR.toString()) || ((BoundaryNode) nodes.get(bStart)).getSyntacticPatternId().equals(IMPLICIT_PATTERN.STR_INSTANCE.toString())){
        	return ret;
        }
        
        //Unit unit = cNode.getValueUnit2();
        List<Unit> units = cNode.getValueUnits2();
        if (units == null) {
        	return 0;
        } else if (units.size() == 0) {
        	units.add(Unit.UNKNOWN);
        }
        //System.out.println(unit);
        // 改为根据句式中的单位
        SemanticNode sNode = nodes.get(bStart);
        if(sNode!= null && sNode.type == NodeType.BOUNDARY && ((BoundaryNode) sNode).isStart())
        {
        	BoundaryNode bNode = (BoundaryNode) sNode;
        	BoundaryNode.SyntacticPatternExtParseInfo info = bNode.getSyntacticPatternExtParseInfo(false);
            if(info == null)
                return ret;
            // key_value类型，必须百分百匹配上，num+单位即为num+单位，比例即为比例
            // 比例的情况：n%、n倍、小数
            boolean isKeyValue = false;
            if (bNode.getSyntacticPatternId().equals(IMPLICIT_PATTERN.KEY_VALUE.toString())){
	        	isKeyValue = true;
	        }
            
            ArrayList<Integer> elelist;
            boolean isThereEqual = false;
            boolean isThereUnknown = false;
            boolean isThereCompare = false;
            boolean isThereZhiOrGe = false;
            boolean isThereNotEqual = false;
            for (int j = 1; (elelist = info.getElementNodePosList(j)) != null; j++) {
                for(int pos: elelist) {
                    if(pos == -1)
                        continue;
                    SemanticNode sn = nodes.get(bStart + pos);
                    if(sn.type != NodeType.NUM && sn.type != NodeType.STR_VAL && sn.type != NodeType.FOCUS && sn.type != NodeType.DATE)
                        continue;
                    if(sn.type == NodeType.NUM) {
	                    NumNode nNode = (NumNode)sn;
	                    if (isKeyValue) {
	                    	if (units.contains(nNode.getUnit()))	{
		                        isThereEqual = true;
		                    } else if ((units.contains(Unit.PERCENT) || units.contains(Unit.BEI)) 
		                    		&& (nNode.getUnit() == Unit.PERCENT || nNode.getUnit() == Unit.BEI || nNode.getUnit() == Unit.UNKNOWN)
		                    		&& (nNode.getFrom() >= -1000 && nNode.getTo() <= 1000)) {
		                    	isThereEqual = true;
		                    	if (nNode.getUnit() == Unit.UNKNOWN)
			                    	isThereUnknown = true;
		                    } else if (nNode.getUnit() == Unit.UNKNOWN || units.contains(Unit.UNKNOWN)) {
		                    	isThereUnknown = true;
		                    } else {
		                    	return -1;
		                    }
	                    	break;
	                    }
	                    //System.out.println(nNode.getUnit());
	                    // 如果单位相同，标记存在相同单位并退出循环
	                    // 如果单位为UNKNOWN，标记存在UNKNOWN
	                    // 如果单位为PERCENT或BEI，而且与指标需要的单位不同
	                    // 如果单位不为UNKNOWN，且单位与指标需要的单位不同，标记存在不同单位
	                    if (units.contains(nNode.getUnit()))	{
	                        isThereEqual = true;
	                        break;
	                    } else if ((units.contains(Unit.PERCENT) || units.contains(Unit.BEI)) 
	                    		&& (nNode.getUnit() == Unit.PERCENT || nNode.getUnit() == Unit.BEI || nNode.getUnit() == Unit.UNKNOWN)
	                    		&& (nNode.getFrom() >= -1000 && nNode.getTo() <= 1000)) {
	                    	isThereEqual = true;
	                    	if (nNode.getUnit() == Unit.UNKNOWN)
		                    	isThereUnknown = true;
	                    	break;
	                    } else if (nNode.getUnit() == Unit.PERCENT || nNode.getUnit() == Unit.BEI) {
	                    	isThereCompare = true;
	                	} else if (nNode.getUnit() == Unit.ZHI || nNode.getUnit() == Unit.GE
	                			|| nNode.getUnit() == Unit.JIA || nNode.getUnit() == Unit.WEI) {
	                		isThereZhiOrGe = true;
	                	} else if (nNode.getUnit() == Unit.UNKNOWN || units.contains(Unit.UNKNOWN)) {
	                    	isThereUnknown = true;
	                    } else if (nNode.getUnit() != Unit.UNKNOWN && !units.contains(nNode.getUnit())) {
	                    	isThereNotEqual = true;
	                    }
                    } else if (sn.type == NodeType.STR_VAL || sn.type == NodeType.FOCUS) {
                    	boolean isPropEqual = false;
                    	StrNode strNode = null;
                    	if (sn.type == NodeType.FOCUS) {
                    		strNode = getStrValInstance(sn);
                    	} else {
                    		strNode = (StrNode) sn;
                    	}
                    	if (strNode == null)
                    		break;
						if (isKeyValue) {
							//for (SemanticNode ofwhat : strNode.ofWhat) {
							for (String st : strNode.subType) {
								//多个值属性
								for(PropNodeFacade pn : cNode.getClassifiedProps(PropType.STR))
									if (pn.isValueProp()  && pn.subTypeContain(st)) {
										isPropEqual = true;
										break;
									}
							}
							if (isPropEqual == false)
								return -1;
						}
                    } else if(sn.type == NodeType.DATE) {
	                    DateNode dNode = (DateNode)sn;
	                    if (isKeyValue) {
	                    	if (cNode.isDateIndex()) {
	                    		isThereEqual = true;
	                    	}
	                    }
                    }
                }
            }
            if (isThereEqual && !isThereUnknown)
            	ret = 90;
            else if (isThereEqual && isThereUnknown)
            	ret = 75;
            else if (isThereUnknown)
            	ret = 70;
            else if (isThereCompare || isThereZhiOrGe)
            	ret = 50;
            else if (isThereNotEqual)
            	ret = -1;
        }
        /*
        for(int i=bStart; i< bEnd; i++)
        {
            SemanticNode sNode = nodes.get(i);
            if(sNode.type != NodeType.NUM)
            {
                continue;
            }
            NumNode nNode = (NumNode)sNode;
            System.out.println(nNode.getUnit());
            if(nNode.getUnit() == unit)
            {
                ret = 90;
                break;
            }
        }
        */
        //System.out.println("unit score:"+ret);
        return ret;
    }

	
	// 判断是否为比较的语义时（即之比 或 的比值或 /）
	public static boolean isCompareSemantics(ArrayList<SemanticNode> nodes, int bStart) {
		BoundaryNode bNode = (BoundaryNode) nodes.get(bStart);
		String patternId = bNode.getSyntacticPatternId();
        if (BoundaryNode.getImplicitPattern(patternId) != null){
        	return false;
        }
        SyntacticPattern syntPtn = PhraseInfo.getSyntacticPattern(patternId);
        if (PhraseInfo.getSemanticPattern(syntPtn.getSemanticBind().getId()) != null) {
        	String bindid = syntPtn.getSemanticBind().getId();
            SemanticPattern pattern = PhraseInfo.getSemanticPattern(bindid);
        	String representation = pattern.getChineseRepresentation();
        	if (representation.contains("之比") || representation.contains("的比值")||representation.contains("/")) {
        		return true;
        	}
        } else {
        	for (SemanticBindTo semanticBindTo : syntPtn.getSemanticBind().getSemanticBindTos()) {
        		String bindid = semanticBindTo.getBindToId() + "";
                SemanticPattern pattern = PhraseInfo.getSemanticPattern(bindid);
                String representation = pattern.getChineseRepresentation();
            	if (representation.contains("之比") || representation.contains("的比值")||representation.contains("/")) {
            		return true;
            	}
        	}
        }
		return false;
	}
	
	
	// 判断是否为比较的语义时（即之比 或 的比值或 /）
	public static boolean isSemanticsOfWords(ArrayList<SemanticNode> nodes, int bStart, String[] words) {
		if(words==null) return false;
		
		BoundaryNode bNode = (BoundaryNode) nodes.get(bStart);
		String patternId = bNode.getSyntacticPatternId();
        if (BoundaryNode.getImplicitPattern(patternId) != null){
        	return false;
        }
        SyntacticPattern syntPtn = PhraseInfo.getSyntacticPattern(patternId);
        if (PhraseInfo.getSemanticPattern(syntPtn.getSemanticBind().getId()) != null) {
        	String bindid = syntPtn.getSemanticBind().getId();
            SemanticPattern pattern = PhraseInfo.getSemanticPattern(bindid);
        	String representation = pattern.getChineseRepresentation();
        	for(String s: words) {
        		if(representation.contains(s)) {
        			return true;
        		}
        	}
        } else {
        	for (SemanticBindTo semanticBindTo : syntPtn.getSemanticBind().getSemanticBindTos()) {
        		String bindid = semanticBindTo.getBindToId() + "";
                SemanticPattern pattern = PhraseInfo.getSemanticPattern(bindid);
                String representation = pattern.getChineseRepresentation();
                for(String s: words) {
            		if(representation.contains(s)) {
            			return true;
            		}
            	}
        	}
        }
		return false;
	}
	
	
	
	
	// 判断是否为龙头句式，龙头句式不作index 和 str_val的校验
	public static boolean isKnowledgeOrLongTou(ArrayList<SemanticNode> nodes, int bStart) {
		BoundaryNode bNode = (BoundaryNode) nodes.get(bStart);
		String patternId = bNode.getSyntacticPatternId();
        if (BoundaryNode.getImplicitPattern(patternId) != null){
        	return false;
        }
        SyntacticPattern syntPtn = PhraseInfo.getSyntacticPattern(patternId);
        if (PhraseInfo.getSemanticPattern(syntPtn.getSemanticBind().getId()) != null) {
        	String bindid = syntPtn.getSemanticBind().getId();
            SemanticPattern pattern = PhraseInfo.getSemanticPattern(bindid);
        	String representation = pattern.getChineseRepresentation();
        	if (representation.contains("知识类") || representation.contains("龙头股") || representation.contains("龙头")) {
        		return true;
        	}
        } else {
        	for (SemanticBindTo semanticBindTo : syntPtn.getSemanticBind().getSemanticBindTos()) {
        		String bindid = semanticBindTo.getBindToId() + "";
                SemanticPattern pattern = PhraseInfo.getSemanticPattern(bindid);
                String representation = pattern.getChineseRepresentation();
                if (representation.contains("知识类") || representation.contains("龙头股") || representation.contains("龙头")) {
            		return true;
            	}
        	}
        }
		return false;
	} 
	
	private StrNode getStrValInstance(SemanticNode node) {
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
}
