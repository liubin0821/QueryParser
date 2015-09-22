/**
 * @author: 	    吴永行 
 * @dateTime:	  2014-5-13 下午1:41:19
 * @description:   	
 * 
 */
package com.myhexin.qparser.phrase.parsePostPlugins.output.imp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.myhexin.qparser.Query;
import com.myhexin.qparser.conf.IndexDefOpInfo;
import com.myhexin.qparser.conf.IndexDefOpInfo.DefOpInfo;
import com.myhexin.qparser.define.EnumDef.DescNodeType;
import com.myhexin.qparser.define.EnumDef.LogicType;
import com.myhexin.qparser.define.EnumDef.NodeType;
import com.myhexin.qparser.iterator.BoundaryInfos;
import com.myhexin.qparser.iterator.DescNode;
import com.myhexin.qparser.iterator.DescNodeNum;
import com.myhexin.qparser.iterator.SemanticRepresentationIteratorImpl;
import com.myhexin.qparser.iterator.SyntacticIteratorImpl;
import com.myhexin.qparser.iterator.SyntacticRepresentationIteratorImpl;
import com.myhexin.qparser.node.BoundaryNode;
import com.myhexin.qparser.node.FocusNode;
import com.myhexin.qparser.node.NumNode;
import com.myhexin.qparser.node.SemanticNode;
import com.myhexin.qparser.node.BoundaryNode.IMPLICIT_PATTERN;
import com.myhexin.qparser.node.BoundaryNode.SyntacticPatternExtParseInfo;
import com.myhexin.qparser.onto.ClassNodeFacade;
import com.myhexin.qparser.phrase.PhraseInfo;
import com.myhexin.qparser.phrase.PhraseUtil;
import com.myhexin.qparser.phrase.SyntacticSemantic.Semantic.SemanticArgument;
import com.myhexin.qparser.phrase.SyntacticSemantic.Semantic.SemanticBind;
import com.myhexin.qparser.phrase.SyntacticSemantic.Semantic.SemanticBindTo;
import com.myhexin.qparser.phrase.SyntacticSemantic.Semantic.SemanticBindToArgument;
import com.myhexin.qparser.phrase.SyntacticSemantic.Semantic.SemanticPattern;
import com.myhexin.qparser.phrase.SyntacticSemantic.Semantic.SemanticArgument.SemanArgType;
import com.myhexin.qparser.phrase.SyntacticSemantic.Semantic.SemanticBindToArgument.BindToType;
import com.myhexin.qparser.phrase.SyntacticSemantic.Semantic.SemanticBindToArgument.Source;
import com.myhexin.qparser.phrase.SyntacticSemantic.Syntactic.SyntacticPattern;
import com.myhexin.qparser.phrase.parsePostPlugins.output.Output;

public class ChangeToStandardStatement  extends Output {
	
	protected static org.slf4j.Logger logger_ = org.slf4j.LoggerFactory.getLogger(ChangeToStandardStatement.class.getName());

	/*
	 * 问句回写
	 * 1、没有匹配上句式，则直接返回原句，这里返回null；
	 * 2、匹配上句式，则只将boundary内部的nodes回写。
	 */
    public static List<String> changeToStandardStatement(List<SemanticNode> nodes, String split) {
    	if (nodes == null || nodes.size() == 0)
    		return null;
		
		List<StringBuffer> buffers = new ArrayList<StringBuffer>();
        int boundaryStart = 0;
        int boundaryEnd = nodes.size() - 1;
        Iterator iterator = new SyntacticIteratorImpl(nodes);
        if (!iterator.hasNext())
			return new ArrayList<String>();
        BoundaryNode lastBNode = null;
    	while (iterator.hasNext()) {
    		BoundaryInfos boundaryInfos = (BoundaryInfos) iterator.next();
    		BoundaryNode bNode = (BoundaryNode) nodes.get(boundaryInfos.bStart);
        	boundaryStart = boundaryInfos.bStart; // 句式左边界
        	boundaryEnd = boundaryInfos.bEnd; // 句式右边界
            List<String> strs = representFromBoundaryNode(nodes, boundaryStart, boundaryEnd);
            // 为空或者只有一个为空的字符串,不显示
            if(strs == null || strs.isEmpty() || (strs.size()==1 && strs.get(0).isEmpty()))
            	continue;
            // 若为第一次构建，则首先插入一个空的字符串到buffers中
            if (buffers.size() == 0 && strs.size() > 0)
            	buffers.add(new StringBuffer());
            // 依次将获得的List<String> strs追加到buffers中
            for (int j = 0; j < buffers.size(); j++) {
            	StringBuffer buffer = buffers.get(j);
                if (buffer.length() > 0) {
                	if (lastBNode.contextLogicType == LogicType.OR)
                		buffer.append("，");
                	else
                		buffer.append(split);
                }
                for (int k=0; k<strs.size(); k++) {
                	String str = strs.get(k);
                	if (k==0) 
                		buffer.append(str);	// 只返回的第一个
                	else
                		;
                }
            }
            
            lastBNode = bNode;
        }
        List<String> strs = new ArrayList<String>();
        for (StringBuffer buffer : buffers)
        	strs.add(buffer.toString());
        return strs;
    }

    /**
     * 针对每一个句式进行回写
     */
    private static List<String> representFromBoundaryNode(List<SemanticNode> nodes, int start, int end) {
        BoundaryNode bNode = (BoundaryNode) nodes.get(start);
        String patternId = bNode.getSyntacticPatternId();
        
        if (BoundaryNode.getImplicitPattern(patternId) != null) { 
        	// YYY1：句式匹配上KEY_VALUE, STR_INSTANCE, FREE_VAR这三种情况
        	return representFromBoundaryNodeImplicit(nodes, start, end);
        } else {
        	// YYY2：匹配上了句式
        	return representFromBoundaryNodeNoneImplicit(nodes, start, end);
        }
    }
    
    private static List<String> representFromBoundaryNodeImplicit(List<SemanticNode> nodes, int start, int end) {
    	ArrayList<String> represents = new ArrayList<String>();
        StringBuffer newstr = new StringBuffer();
        BoundaryNode bNode = (BoundaryNode) nodes.get(start);
        BoundaryNode.SyntacticPatternExtParseInfo info = bNode.getSyntacticPatternExtParseInfo(false);
        String patternId = bNode.getSyntacticPatternId();
    	if (patternId.equals(IMPLICIT_PATTERN.KEY_VALUE.toString())) {
        	ArrayList<Integer> elelist;
        	boolean isKey = true;
			for (int j = 1; (elelist = info.getElementNodePosList(j)) != null; j++) {
				for (int pos : elelist) {
					if (pos == -1)
						continue;
					if (isKey == true && nodes.get(start + pos).type == NodeType.FOCUS) {
						FocusNode fn = (FocusNode) nodes.get(start + pos);
						if (fn.hasIndex()) {
							ClassNodeFacade cn = fn.getIndex();
							if (cn.isChangeToOld() == true)
								nodes.get(0).isExecutive = true;
						}
					} else if (isKey == false) {
						if (PersonIndexShowContain.matcher(newstr.toString()).matches()) // 特定的指标
							newstr.append("包含");
						else if (getPropsText(nodes.get(start + pos)).equals("是")
								|| getPropsText(nodes.get(start + pos)).equals("否"))
							newstr.append("为");
						else
							newstr.append("是");
					}
					newstr.append(getPropsText(nodes.get(start + pos)));
					isKey = false;
				}
			}
			represents.add(newstr.toString());
		} else if (patternId.equals(IMPLICIT_PATTERN.FREE_VAR.toString())) {
    		ArrayList<Integer> elelist;
			for (int j = 1; (elelist = info.getElementNodePosList(j)) != null; j++) {
				for (int pos : elelist) {
					if (pos == -1)
						continue;
					SemanticNode sNode = nodes.get(start + pos);
					FocusNode fNode = sNode.type == NodeType.FOCUS ? (FocusNode)sNode : null;
					ClassNodeFacade cn = fNode != null ? fNode.getIndex() : null;
					if (cn==null || (fNode.isBoundToIndex() && !cn.isBoundValueToThis())) continue;//有值绑定到它,不能不显示
					if (cn.isChangeToOld())
						nodes.get(0).isExecutive = true;
					newstr.append(getPropsText(nodes.get(start + pos)));
					DefOpInfo doi = cn != null ? IndexDefOpInfo.getDefOpInfoByIndexName(cn.getText(), Query.Type.ALL) : null;
					if (cn != null &&  doi!= null) {
						if (doi.getDefOp(null)!=null) {
							String defOp = doi.getDefOp(null).getText();
							newstr.append(defOp);
						}
					}
				}
			}
			represents.add(newstr.toString());
    	} else if (patternId.equals(IMPLICIT_PATTERN.STR_INSTANCE.toString())) {
			ArrayList<Integer> elelist;
			boolean isKey = true;
			boolean isBoundToIndex = false;
			boolean isContainProps = false;
			boolean isChangeToOld = false;
			String str_val = "";
			for (int j = 1; (elelist = info.getElementNodePosList(j)) != null; j++) {
				for (int pos : elelist) {											
					String tempText = null;
					if (pos == -1) {
						SemanticNode defaultNode = info.absentDefalutIndexMap.get(j);
						FocusNode fn = (FocusNode) defaultNode;
						isContainProps = isContainProps(fn);
						if (fn.hasIndex()) {
							ClassNodeFacade cn = fn.getIndex();
							if (cn.isChangeToOld() == true) {
								isChangeToOld = true;
								nodes.get(0).isExecutive = true;
							}
						}
						tempText = getPropsText(defaultNode);
					} else {
						if(nodes.get(start + pos).isBoundToIndex()) {
							isBoundToIndex = true;
							continue;
						}
						tempText = getPropsText(nodes.get(start + pos));
						str_val = tempText;
					}
					
					if (isKey == false) {
						if(PersonIndexShowContain.matcher(newstr.toString()).matches()) //特定的指标
							newstr.append("包含");
						else if (getPropsText(nodes.get(start + pos)).equals("是")
									|| getPropsText(nodes.get(start + pos)).equals("否"))
							newstr.append("为");
						else
							newstr.append("是");
					}
					newstr.append(tempText);
					isKey = false;
				}
			}
/*			if (isContainProps == false && isChangeToOld == false) {
				newstr = new StringBuffer(str_val);
			}*/
			// 没有被绑定才显示
			if(!isBoundToIndex)
				represents.add(newstr.toString());
		}
        //System.out.println("YYY1 "+ newstr.toString());
    	return represents;
    }
    
    private static List<String> representFromBoundaryNodeNoneImplicit(List<SemanticNode> nodes, int start, int end) {
    	ArrayList<String> represents = new ArrayList<String>();
        StringBuffer newstr = new StringBuffer();
        BoundaryNode bNode = (BoundaryNode) nodes.get(start);
        BoundaryNode.SyntacticPatternExtParseInfo info = bNode.getSyntacticPatternExtParseInfo(false);
        String patternId = bNode.getSyntacticPatternId();
        SyntacticPattern syntPtn = PhraseInfo.getSyntacticPattern(patternId);
        SemanticBind sb = syntPtn.getSemanticBind();
        String chineseRepresentation = sb.getChineseRepresentation();
        
        SyntacticRepresentationIteratorImpl it = new SyntacticRepresentationIteratorImpl(chineseRepresentation);
        while(it.hasNext()){
        	DescNode dn = it.next();
        	if (dn.getType() == DescNodeType.NUM) {
        		DescNodeNum dnn = (DescNodeNum) dn;
        		int arg = dnn.getNum();
                SemanticBindTo mainBindTo = syntPtn.getSemanticBind().getSemanticBindTo(arg);
		        //递归调用得到语义的回显
		        newstr.append(getRepresentBySemantic(syntPtn,mainBindTo,info,nodes,start));
        	} else if (dn.getType() == DescNodeType.LOGIC) {
        		String logic = dn.getText();
        		if (dn.getText().equals("&"))
        			logic = "且";
                else if (dn.getText().equals("|"))
                	logic = "或";
                else if (dn.getText().equals("!"))
                	logic = "非";
        		newstr.append(logic);
        	} else {
        		newstr.append(dn.getText());
        	}
        }
        represents.add(newstr.toString());
    	return represents;
    }
    
    /**
	 * @author: 	    吴永行 
	 * @param info 
	 * @param start 
	 * @param nodes 
	 * @dateTime:	  2013-11-8 下午1:16:37
	 * @description:  YYY3：即匹配上了句式，又匹配上了语义，根据语义定义的方式回写
	 */
	private static String getRepresentBySemantic(SyntacticPattern syntPtn,
			SemanticBindTo bindTo, SyntacticPatternExtParseInfo info, List<SemanticNode> nodes, int start) {
		SemanticPattern semPtn = PhraseInfo.getSemanticPattern(bindTo.getBindToId()+"");
		boolean needCalculate = false;//处理需要计算的语义,如"每10股转5股","每股转增股本{}=(5股/10股)"=>"每股转增股本{}=0.5"
		if(Integer.parseInt(semPtn.getId()) == 167)	needCalculate = true;
		
		StringBuffer result = new StringBuffer();
		int semanticPropsClassNodeId = bindTo.getSemanticPropsClassNodeId();
		if (semanticPropsClassNodeId != -1) {
			String temp = getSemanticProps(info, semanticPropsClassNodeId);
        	result.append(temp);
		}
		
        String representation = semPtn.getChineseRepresentation();
		SemanticRepresentationIteratorImpl it = new SemanticRepresentationIteratorImpl(representation);
        String pre = "";
        String prePre = "";
        boolean isNegative = false;
		while(it.hasNext()){
        	DescNode dn = it.next();
        	if (dn.getType() == DescNodeType.NUM) {
        		DescNodeNum dnn = (DescNodeNum) dn;
        		int arg = dnn.getNum();
        		SemanticBindToArgument sad = bindTo.getSemanticBindToArgument(arg);
                if (sad.getSource() == Source.ELEMENT) {
    				if (sad.getBindToType() == BindToType.SEMANTIC) { // 递归调用用 先计算 #1 对应的语义
    					String semanticResult = getRepresentBySemantic(syntPtn, syntPtn.getSemanticBind().getSemanticBindTo(sad.getElementId()), info, nodes, start);
    					result.append(semanticResult);
    				} else {
    					SemanticArgument argument = semPtn.getSemanticArgument(arg, false);
    					ArrayList<Integer> list;
    					boolean matched = false;
    	
    					int i = sad.getElementId();
    					list = info.getElementNodePosList(i);
    					if (list != null) {
    						if (list.size() == 1 && list.get(0) == -1) {
    							String temp = getDefaultArgument(argument, info, i, nodes, isNegative);
    							result.append(temp);
    						} else {
    							Collections.sort(list);
    							// 若为比较符号带负号，则涉及转义
    							if (pre.equals("-") 
    									&& (prePre.equals(">") || prePre.equals(">=") || prePre.equals("<") || prePre.equals("<=")))
    								result = new StringBuffer(result.substring(0, result.length()-prePre.length()));
    							String temp = getPresentArgument(argument, nodes, start, list, isNegative, prePre);
    							result.append(temp);
    						}
    						matched = true;
    					}
    	
    					// 增加的回写逻辑：当语义需要的字段无法从句式中的对应位置获取时，使用语义中的默认值
    					// 这种回写只适合值的情况，指标类型必须匹配上
    					if ((argument.getType() == SemanArgType.CONSTANT || argument.getType() == SemanArgType.CONSTANTLIST)
    							&& matched == false) {
    						String temp = getDefaultArgument(argument, info, 0, nodes, isNegative);
    						result.append(temp);
    					} else if ((argument.getType() == SemanArgType.INDEX || argument.getType() == SemanArgType.INDEXLIST)
    							&& matched == false) {
    						// 需要增加处理，这种情况下句式配置是存在问题的
    					}
    				}
                } else {
                	String temp = getFixedArgument(info, sad.getElementId());
                	result.append(temp);
                }
                isNegative = false;
        	} else if (dn.getType() == DescNodeType.TEXT) {
        		if (dn.getText().contains("-") && 
                		(pre.equals(">") || pre.equals(">=") || pre.equals("=") || pre.equals("<") || pre.equals("<="))) {
        			isNegative = true;
                } else {
                	result.append(dn.getText());
                	isNegative = false;
                }
        	} else {
        		result.append(dn.getText());
        		isNegative = false;
        	}
        	prePre = pre;
        	pre = dn.getText();
        }
		if(needCalculate){//语义中需要计算的部分
        	String resultNew = calculateFromResult(result.toString());
        	if(resultNew != null){
        		result.delete(0, result.length());
        		result.append(resultNew);
        	}
        }
		return result.toString();
	}
	
	/*
	 * 获得语义属性
	 */
	private static String getSemanticProps(SyntacticPatternExtParseInfo info,
			int semanticPropsClassNodeId) {
		SemanticNode semanticProps = info.semanticPropsMap.get(semanticPropsClassNodeId);
		String text = "";
		if (semanticProps != null) {
        	if (semanticProps.type == NodeType.FOCUS && ((FocusNode)semanticProps).hasIndex()) { 
        		text = getPropsText(semanticProps);
        	} else {
        		text = semanticProps.getText();
        	}
        }
		return text;
	}
	
	/*
	 * 获得设定为固定值的参数
	 */
	private static String getFixedArgument(BoundaryNode.SyntacticPatternExtParseInfo info, int fixedArgumentId) {
		SemanticNode fixedNode = info.fixedArgumentMap.get(fixedArgumentId);
		String text = "";
		if (fixedNode != null) {
        	if (fixedNode.type == NodeType.FOCUS && ((FocusNode)fixedNode).hasIndex()) { 
        		text = getPropsText(fixedNode);
        	} else {
        		text = fixedNode.getText();
        	}
        }
		return text;
	}

    /*
     * 获得句式匹配上的argument的默认信息
     * INDEX\INDEXLIST:回写默认的指标
     * CONSTANT:回写默认的文本
     * ANY:回写默认的文本
     * 
     */
    private static String getDefaultArgument(SemanticArgument argument, BoundaryNode.SyntacticPatternExtParseInfo info,
            int elemPos, List<SemanticNode> sNodes, boolean isNegative) {
        String text = "";
        switch (argument.getType()) {
            case INDEX:
            case INDEXLIST:
                SemanticNode defaultNode = info.absentDefalutIndexMap.get(elemPos);
                if (defaultNode != null) {
                    text = getPropsText(defaultNode);
                }
                break;
            case CONSTANT:
            case CONSTANTLIST:
                if (argument.getDefaultValue() != null) {
                	String defVal = argument.getDefaultValue();
                	// 增加语义中包含负号的处理
                	if (isNegative == true && !(defVal.equals("0") || defVal.equals("0%")))
                		text = "-" + argument.getDefaultValue();
                	else
                		text = argument.getDefaultValue();
                }
                break;
            case ANY:
                if (argument.getDefaultIndex() != null) {
                    // text = argument.getDefaultIndex();
                }
                if (argument.getDefaultValue() != null) {
                    text = argument.getDefaultValue();
                }
                break;
            default:
                break;
        }
        if (text.length() == 0) {
            text = argument.getSpecificIndex();
            text = text == null ? argument.getDefaultValue() : text;
            text = text == null ? "" : text;
        }
        return text;
    }

    /*
     * 获得句式匹配上的argument的相关信息：text和相应的属性修饰
     * INDEX:获得指标的text和相应的属性
     * INDEXLIST\ANY:获得list中各个node的信息
     * CONSTANT:直接将text回写
     */
    private static String getPresentArgument(SemanticArgument argument, List<SemanticNode> sNodes, int start,
            List<Integer> poses, boolean isNegative, String prePre) {
        if (argument == null) {
            return "";
        }
        StringBuffer sb = new StringBuffer();
        switch (argument.getType()) {
            case INDEX:
                SemanticNode sNode = PhraseUtil.getNodeByPosInfo(poses.get(0), start, sNodes);
                sb.append(getPropsText(sNode));
                break;
            case INDEXLIST:
            case ANY:
                List<SemanticNode> nodes = PhraseUtil.getNodesByPosInfo(poses, start, sNodes);
                for (int i = 0; i < nodes.size(); i++) {
                    sb.append(getPropsText(nodes.get(i)));
                    if (i < nodes.size()-1)
                    	sb.append("、");
                }
                break;
            case CONSTANT:
                SemanticNode node = PhraseUtil.getNodeByPosInfo(poses.get(0), start, sNodes);
                // 增加语义中包含负号的处理
                if (isNegative && node.type == NodeType.NUM) {
                	NumNode nNode = (NumNode) node;
                	if (nNode.getFrom() == nNode.getTo() && nNode.getFrom() > 0) {
                		sb.append(prePre+"-"+node.getText());
                		break;
                	} else if (nNode.getFrom() == nNode.getTo() && nNode.getFrom() < 0) {
                		if (prePre.equals(">") || prePre.equals(">=") || prePre.equals("<") || prePre.equals("<=")) {
                			prePre = prePre.contains(">") ? prePre.replace(">", "<") : prePre.replace("<", ">");
                			sb.append(prePre+node.getText());
                		} else {
                			sb.append(prePre+node.getText());
                		}
                		break;
                	} else {
                		sb.append(prePre+node.getText());
                		break;
                	}
                }
                sb.append(node.getText());
                break;
            case CONSTANTLIST:
            	List<SemanticNode> constantList = PhraseUtil.getNodesByPosInfo(poses, start, sNodes);
                for (int i = 0; i < constantList.size(); i++) {
                    sb.append(getPropsText(constantList.get(i)));
                    if (i < constantList.size()-1)
                    	sb.append("、");
                }
                break;
            default:
                break;
        }
        return sb.toString();
    }
    
    private static String calculateFromResult(String result){
    	Pattern pattern = Pattern.compile("（(\\d+).*([/])(\\d+).*）");//（$1/$2）
    	Matcher matcher = pattern.matcher(result);
    	if(matcher.find()){
    		double num1 = Double.parseDouble(matcher.group(1));
    		char op = matcher.group(2).charAt(0);
    		double num2 = Double.parseDouble(matcher.group(3));
    		switch (op) {
			case '/':
				if(num2 != 0)
					return String.valueOf(num1/num2);
				break;
			default:
				break;
			}
    	}
    	return null;
    }
    
}