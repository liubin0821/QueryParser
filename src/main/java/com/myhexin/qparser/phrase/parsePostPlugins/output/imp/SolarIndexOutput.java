package com.myhexin.qparser.phrase.parsePostPlugins.output.imp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.myhexin.qparser.Query;
import com.myhexin.qparser.conf.IndexDefOpInfo;
import com.myhexin.qparser.conf.IndexDefOpInfo.DefOpInfo;
import com.myhexin.qparser.define.EnumDef.NodeType;
import com.myhexin.qparser.node.BoundaryNode;
import com.myhexin.qparser.node.BoundaryNode.IMPLICIT_PATTERN;
import com.myhexin.qparser.node.BoundaryNode.SyntacticPatternExtParseInfo;
import com.myhexin.qparser.node.FocusNode;
import com.myhexin.qparser.node.NumNode;
import com.myhexin.qparser.node.SemanticNode;
import com.myhexin.qparser.onto.ClassNodeFacade;
import com.myhexin.qparser.onto.PropNodeFacade;
import com.myhexin.qparser.phrase.PhraseInfo;
import com.myhexin.qparser.phrase.PhraseUtil;
import com.myhexin.qparser.phrase.SyntacticSemantic.Semantic.SemanticArgument;
import com.myhexin.qparser.phrase.SyntacticSemantic.Semantic.SemanticArgument.SemanArgType;
import com.myhexin.qparser.phrase.SyntacticSemantic.Semantic.SemanticBind;
import com.myhexin.qparser.phrase.SyntacticSemantic.Semantic.SemanticBindTo;
import com.myhexin.qparser.phrase.SyntacticSemantic.Semantic.SemanticBindToArgument;
import com.myhexin.qparser.phrase.SyntacticSemantic.Semantic.SemanticBindToArgument.BindToType;
import com.myhexin.qparser.phrase.SyntacticSemantic.Semantic.SemanticBindToArgument.Source;
import com.myhexin.qparser.phrase.SyntacticSemantic.Semantic.SemanticPattern;
import com.myhexin.qparser.phrase.SyntacticSemantic.Syntactic.SyntacticPattern;
import com.myhexin.qparser.phrase.parsePostPlugins.output.Output;

public class SolarIndexOutput extends Output {
	
	protected static org.slf4j.Logger logger_ = org.slf4j.LoggerFactory.getLogger(SolarIndexOutput.class.getName());

	private static Pattern BindToOtherBind = Pattern.compile("#(\\d+)");
	private static Pattern OnlyStrInstanceNotShow = Pattern.compile(".+姓名$");
	private static Pattern PChineseRepresentation = Pattern.compile("(?:#(\\d+)[&\\|!|且|与|或|非|,]?)+");
	/*
	 * 问句回写，目前存在一定问题，因为只处理了两种情况
	 * 1、没有匹配上句式，则将nodes一个个回写；
	 * 2、匹配上句式，则只将boundary内部的nodes回写
	 */
    public static List<String> changeToStandardStatement(List<SemanticNode> nodes, String split) {
    	List<StringBuffer> buffers = new ArrayList<StringBuffer>();
        int boundaryStart = 0;
        int boundaryEnd = nodes.size() - 1;
        int lastEnd = -1;
        boolean bMathch = false;
        boolean bEndMatch = false;
        int matchSub = 0;
        for (int i = 0; i < nodes.size(); i++) {
        	// 注：此处回写的信息是否存在问题？因为boundary之外的，没有被绑定的nodes并不回写
            SemanticNode sNode = nodes.get(i);
            if (sNode.type == NodeType.BOUNDARY) {
                matchSub++;
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
                List<String> strs = representFromBoundaryNode(nodes, boundaryStart, boundaryEnd, lastEnd);
                lastEnd = boundaryEnd;
                bMathch = false;
                bEndMatch = false;
                //为空或者只有一个为空的字符串,不显示
                if(strs.isEmpty() || (strs.size()==1 && strs.get(0).isEmpty())) continue;//如果没有结果往下继续
                
                if (buffers.size() == 0 && strs.size() > 0)
                	buffers.add(new StringBuffer());
                
                List<StringBuffer> temps = new ArrayList<StringBuffer>();
                for (int j = 0; j < buffers.size(); j++) {
                	StringBuffer buffer = buffers.get(j);
	                if (buffer.length() > 0) {
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
                buffers.addAll(temps);
            }
        }
        if (matchSub == 0) {
        	StringBuffer buffer = new StringBuffer();
            for (int i = 1; i < nodes.size(); i++) {
            	//buffer.append(nodes.get(i).getText());
            	// 新增：连续时间的处理1
            	if (nodes.get(i).type == NodeType.DATE)
            		datePresention(buffer, nodes.get(i));
            	
            	else {
            		buffer.append(nodes.get(i).getText());
            	}
            }
            buffers.add(buffer);
        }
        List<String> strs = new ArrayList<String>();
        for (StringBuffer buffer : buffers)
        	strs.add(buffer.toString());
        return strs;
    }

    /**
     * todo:
     */
    private static List<String> representFromBoundaryNode(List<SemanticNode> nodes, int start, int end, int lastEnd)
    {
    	ArrayList<String> represents = new ArrayList<String>();
        StringBuffer newstr = new StringBuffer();
        BoundaryNode bNode = (BoundaryNode) nodes.get(start);
        BoundaryNode.SyntacticPatternExtParseInfo info = bNode.getSyntacticPatternExtParseInfo(false);
        String patternId = bNode.getSyntacticPatternId();
        // YYY1：句式匹配上KEY_VALUE, STR_INSTANCE, FREE_VAR这三种情况
        StringBuffer propsOutput = new StringBuffer();
        if (BoundaryNode.getImplicitPattern(patternId) != null) {
        	if (patternId.equals(IMPLICIT_PATTERN.KEY_VALUE.toString())) {
	        	ArrayList<Integer> elelist;
	        	boolean isKey = true;
				for (int j = 1; (elelist = info.getElementNodePosList(j)) != null; j++) {
					for (int pos : elelist) {
						if (pos == -1)
							continue;
						
						if (isKey == false) {
							newstr.append("_");
						}
						newstr.append(getPropsText(nodes.get(start + pos),propsOutput));
						isKey = false;
					}
				}
				newstr.append(propsOutput.toString());
				represents.add(newstr.toString());
			}
        	else if (patternId.equals(IMPLICIT_PATTERN.FREE_VAR.toString())) {
        		ArrayList<Integer> elelist;
				for (int j = 1; (elelist = info.getElementNodePosList(j)) != null; j++) {
					for (int pos : elelist) {
						if (pos == -1)
							continue;
						SemanticNode sNode = nodes.get(start + pos);
						FocusNode fNode = sNode.type == NodeType.FOCUS ? (FocusNode)sNode : null;
						ClassNodeFacade cn = fNode != null ? fNode.getIndex() : null;
						//add by wyh 以绑定 不显示
						if(cn==null || (fNode.isBoundToIndex() && !cn.isBoundValueToThis())) {
							//newstr.append(showBoundedFreeVar(nodes.get(start + pos)));
							continue;
						}
						else 
							newstr.append(getPropsText(nodes.get(start + pos),propsOutput));
						DefOpInfo doi = cn != null ? IndexDefOpInfo.getDefOpInfoByIndexName(cn.getText(), Query.Type.ALL) : null;
						if (cn != null &&  doi!= null) {
							if (doi.getDefOp(null)!=null) {
								String defOp = doi.getDefOp(null).getText();
								newstr.append(defOp);
							}
						}
					}
				}
				newstr.append(propsOutput.toString());
				represents.add(newstr.toString());
        	}
			else if (patternId.equals(IMPLICIT_PATTERN.STR_INSTANCE.toString())) {
				ArrayList<Integer> elelist;
				boolean isKey = true;
				boolean isBoundToIndex = false;
				for (int j = 1; (elelist = info.getElementNodePosList(j)) != null; j++) {
					for (int pos : elelist) {											
						String tempText = null;
						if (pos == -1) {
							SemanticNode defaultNode = info.absentDefalutIndexMap.get(j);
							tempText = getPropsText(defaultNode,propsOutput);
						} else {
							//add by wyh 以绑定 不显示
							if(nodes.get(start + pos).isBoundToIndex()) {
								isBoundToIndex = true;
								continue;
							}
							tempText = getPropsText(nodes.get(start + pos),propsOutput);
						}
						
						if (isKey == false) {
							newstr.append("_");
						}
						newstr.append(tempText);
						isKey = false;
					}
				}
				newstr.append(propsOutput.toString());
				
				//没有被绑定才显示
				if(!isBoundToIndex)
				 represents.add(newstr.toString());
			}
        	return represents;
        }

        // YYY2：句式匹配上了，但是并没有匹配上语义：这种情况应该不会发生，所以
        SyntacticPattern syntPtn = PhraseInfo.getSyntacticPattern(patternId);
        SemanticBind sb = syntPtn.getSemanticBind();
        String ChineseRepresentation = sb.getChineseRepresentation();
        
		if (ChineseRepresentation != null && !ChineseRepresentation.equals("")) {
			//入口语义
	        Matcher bindToMatcher = BindToOtherBind.matcher(ChineseRepresentation);
	        //ChineseRepresentation格式错误
	        if (!PChineseRepresentation.matcher(ChineseRepresentation).matches()) {
	        	logger_.error("配置文件中ChineseRepresentation("+ChineseRepresentation+")格式错误,必须以#数字开头");
	        	return errorInXmlReturn(nodes,start,end,newstr,represents,propsOutput);
			}
	        int last=0;
	        while (bindToMatcher.find()) {
	        	String mainBindToSeq = bindToMatcher.group(1);
		        SemanticBindTo  mainBindTo = syntPtn.getSemanticBind().getSemanticBindTo(mainBindToSeq);
		        
		        //以后要删除
		        String logic = ChineseRepresentation.substring(last, bindToMatcher.start());
		        if (logic.equals("&"))
		        	logic = "且";
	            else if (logic.equals("|"))
	            	logic = "或";
	            else if (logic.equals("!"))
	            	logic = "非";
		        newstr.append(logic);
		        //删除结束
		        
		        //递归调用得到语义的回显
		        newstr.append(getRepresentBySemantic(syntPtn,mainBindTo,info,nodes,start,propsOutput));		        
		        last = bindToMatcher.end();
			} 
		}
		
		newstr.append(propsOutput.toString());
		
        represents.add(newstr.toString());
    	return represents;
        //return newstr.toString();
    }
    
    /**
	 * @author: 	    吴永行 
	 * @param info 
	 * @param start 
	 * @param nodes 
	 * @dateTime:	  2013-11-8 下午1:16:37
	 * @description:   	
	 * 
	 */
	private static String getRepresentBySemantic(SyntacticPattern syntPtn,
			SemanticBindTo bindTo, SyntacticPatternExtParseInfo info, List<SemanticNode> nodes, int start
			,StringBuffer propsOutput) {
		
		SemanticPattern semPtn = PhraseInfo.getSemanticPattern(bindTo.getBindToId()+"");
		assert(semPtn!=null);//没有语义配置文件错误

		StringBuffer result = new StringBuffer();
		
		int semanticPropsClassNodeId = bindTo.getSemanticPropsClassNodeId();
		if (semanticPropsClassNodeId != -1) {
			String temp = getSemanticProps(info, semanticPropsClassNodeId,propsOutput);
        	result.append(temp);
		}
		
		// YYY3：即匹配上了句式，又匹配上了语义，根据语义定义的方式回写
        String representation = semPtn.getChineseRepresentation();
        int last = 0;
        int idx = representation.indexOf("$");
        while (idx != -1 && idx < representation.length() - 1) {
            String before = representation.substring(last, idx).trim();
            last = idx;
            // 增加语义中包含负号的处理
            boolean isNegative = false;
            if (before.contains("-") && 
            		(before.equals(">-") || before.equals(">=-") || before.equals("=-") || before.equals("<-") || before.equals("<=-"))) {
            	before = before.replace("-", "");
            	isNegative = true;
            } else {
            	result.append(before);
            	before = "";
            }
            
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
            int arg = Integer.parseInt(txt);//得到语义中的$1 的标号
            
            //句式中bindto的SemanticArgument 必须和语义中的 Argument一一对应, arg必须在范围之内
            if(arg > semPtn.getSemanticArgumentCount()){
            	logger_.error("语义中的$num:" + arg +" 超出范围");
            	return "";
            }
            
            SemanticBindToArgument sad = bindTo.getSemanticBindToArgument(arg);
            //System.out.println();
            //递归调用用 先计算 #1 对应的语义
            if (sad.getSource() == Source.ELEMENT) {
				if (sad.getBindToType() == BindToType.SEMANTIC) {
					result.append(getRepresentBySemantic(syntPtn, syntPtn.getSemanticBind().getSemanticBindTo(sad.getElementId()), info, nodes, start,propsOutput));
				} else {
	
					SemanticArgument argument = semPtn.getSemanticArgument(arg, false);
					ArrayList<Integer> list;
					boolean matched = false;
	
					int i = sad.getElementId();
					list = info.getElementNodePosList(i);
					if (list != null) {
						if (list.size() == 1 && list.get(0) == -1) {
							String temp = getDefaultArgument(argument, info, i,nodes, isNegative, before,propsOutput);
							result.append(temp);
						} else {
							Collections.sort(list);
							String temp = getPresentArgument(argument, nodes, start, list, isNegative, before,propsOutput);
							result.append(temp);
						}
						matched = true;
					}
	
					// 增加的回写逻辑：当语义需要的字段无法从句式中的对应位置获取时，使用语义中的默认值
					// 这种回写只适合值的情况，指标类型必须匹配上
					if ((argument.getType() == SemanArgType.CONSTANT || argument.getType() == SemanArgType.CONSTANTLIST) && matched == false) {
						String temp = getDefaultArgument(argument, info, 0, nodes,isNegative, before,propsOutput);
						result.append(temp);
					} else if ((argument.getType() == SemanArgType.INDEX || argument.getType() == SemanArgType.INDEXLIST) && matched == false) {
						// 需要增加处理，这种情况下句式配置是存在问题的
					}
				}
            } else {
            	/*
            	if (sad.getType() == SemanticArgument.SemanArgType.INDEX 
            			|| sad.getType() == SemanticArgument.SemanArgType.INDEXLIST) {
            		String temp = sad.getIndex();
            		result.append(temp);
            	} else if (sad.getType() == SemanticArgument.SemanArgType.CONSTANT) {
            		String temp = sad.getValue();
            		result.append(temp);
            	}
            	*/
            	String temp = getFixedArgument(info, sad.getElementId(),propsOutput);
            	result.append(temp);
            }
			last += (txt.length() + 1);
			idx = representation.indexOf("$", idx + 1);
		}
        result.append(representation.substring(last));			
		return result.toString();
	}

	/**
	 * @author: 	    吴永行 
	 * @param represents 
	 * @dateTime:	  2013-11-8 上午10:57:04
	 * @description:  配置文件有错，返回结果 	
	 * 
	 */
	private static ArrayList<String> errorInXmlReturn(List<SemanticNode> nodes,
			int start, int end, 
			StringBuffer newstr, ArrayList<String> represents,StringBuffer propsOutput) {
		for (int i = start + 1; i < end; i++) {
			newstr.append(getPropsText(nodes.get(i),propsOutput));
		}
		represents.add(newstr.toString());
		return represents;
	}
	
	/*
	 * 获得语义属性
	 */
	private static String getSemanticProps(SyntacticPatternExtParseInfo info,
			int semanticPropsClassNodeId,StringBuffer propsOutput) {
		SemanticNode semanticProps = info.semanticPropsMap.get(semanticPropsClassNodeId);
		String text = "";
		if (semanticProps != null) {
        	if (semanticProps.type == NodeType.FOCUS && ((FocusNode)semanticProps).hasIndex()) { 
        		text = getPropsText(semanticProps,propsOutput);
        	} else {
        		text = semanticProps.getText();
        	}
        }
		return text;
	}
	
	/*
	 * 获得设定为固定值的参数
	 */
	private static String getFixedArgument(BoundaryNode.SyntacticPatternExtParseInfo info, int fixedArgumentId
			,StringBuffer propsOutput) {
		SemanticNode fixedNode = info.fixedArgumentMap.get(fixedArgumentId);
		String text = "";
		if (fixedNode != null) {
        	if (fixedNode.type == NodeType.FOCUS && ((FocusNode)fixedNode).hasIndex()) { 
        		text = getPropsText(fixedNode,propsOutput);
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
            int elemPos, List<SemanticNode> sNodes, boolean isNegative, String before
            ,StringBuffer propsOutput) {
        String text = "";
        switch (argument.getType()) {
            case INDEX:
            case INDEXLIST:
                SemanticNode defaultNode = info.absentDefalutIndexMap.get(elemPos);
                if (defaultNode != null) {
                    text = getPropsText(defaultNode,propsOutput);
                }
                break;
            case CONSTANT:
            case CONSTANTLIST:
                if (argument.getDefaultValue() != null) {
                	String defVal = argument.getDefaultValue();
                	// 增加语义中包含负号的处理
                	if (isNegative == true && !(defVal.equals("0") || defVal.equals("0%")))
                		text = before + "-" + argument.getDefaultValue();
                	else
                		text = before + argument.getDefaultValue();
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
            List<Integer> poses, boolean isNegative, String before,StringBuffer propsOutput) {
        if (argument == null) {
            return "";
        }
        StringBuffer sb = new StringBuffer();
        switch (argument.getType()) {
            case INDEX:
                SemanticNode sNode = PhraseUtil.getNodeByPosInfo(poses.get(0), start, sNodes);
                sb.append(getPropsText(sNode,propsOutput));
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
                		sb.append(before+"-"+node.getText());
                		break;
                	} else if (nNode.getFrom() == nNode.getTo() && nNode.getFrom() < 0) {
                		if (before.contains("<")) {
                			before = before.replace("<", ">");
                			sb.append(before+node.getText());
                		} else if (before.contains(">")) {
                			before = before.replace(">", "<");
                			sb.append(before+node.getText());
                		} else
                			sb.append(before+node.getText());
                		break;
                	}
                }
                sb.append(before+node.getText());
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

    private static final String showBoundedFreeVar (SemanticNode sNode){
		if (sNode.type == NodeType.FOCUS) {
			FocusNode fNode = (FocusNode) sNode;
			if (fNode.hasIndex()) {
				ClassNodeFacade cNode = fNode.getIndex();
				return cNode.getText() + "{@b}";
			}
		}
		return "";
    }
    
    protected static String getPropsText(SemanticNode sNode, StringBuffer propsOutput){
   	 if (sNode == null ) {
            return "";
        }
        // 处理连续回写的逻辑
        StringBuffer sb = new StringBuffer();
        if (sNode.type != NodeType.FOCUS) {
        	if (sNode.type == NodeType.DATE)
        		datePresention(sb, sNode);
        	else if(!sNode.isBoundToIndex()){
        		sb.append(sNode.getText());
        	}
            return sb.toString();
        }
        //FOCUSNode
        else{
       	 FocusNode fNode = (FocusNode) sNode;
            ClassNodeFacade cNode = fNode.getIndex();
            //不是指标
            if (cNode == null) {
                sb.append(sNode.getText());
                return sb.toString();
            }
            //是指标
            List<PropNodeFacade> pNodes = cNode.getAllProps();
            sb.append(cNode.getText()/*+"{"*/);//指标
            for (int i = 0; i < pNodes.size(); i++) {
                SemanticNode propValue = pNodes.get(i).getValue();
                if (propValue == null || propValue.getText() == null ||propValue.getText().length()==0) {
                    continue;
                }
                propsOutput.append(" " + pNodes.get(i).getText()+"_");
               switch (propValue.getType()) {
				case DATE:
                	datePresention(propsOutput, propValue);
					break;
				case STR_VAL:
					propsOutput.append(propValue.getText());
					break;
				default:
					propsOutput.append(propValue.getText());
					break;
				}
            }   
            //sb.append("}");
            return sb.toString();
        }
   }
    
    private static String getPropsTextOld(SemanticNode sNode) {
        if (sNode == null) {
            return "";
        }
        // 处理连续回写的逻辑
        StringBuffer sb = new StringBuffer();
        if (sNode.type != NodeType.FOCUS) {
        	//sb.append(sNode.getText());
        	// 新增：连续时间的处理2
        	if (sNode.type == NodeType.DATE)
        		datePresention(sb, sNode);
        	//待删除, 确认无误后就删除了
        	/*if (sNode.type == NodeType.DATE && ((DateNode) sNode).getFrequencyInfo() != null) {
        		DateNode dn = (DateNode) sNode;
        		NumRange nr = dn.getFrequencyInfo().getLength();
        		Unit unit = dn.getFrequencyInfo().getUnit();
        		String text = "";
        		if (nr.getLongFrom() == nr.getLongTo()) {
        			text = dn.text + "有" + nr.getLongFrom() + EnumConvert.getStrFromUnit(unit);
        		}
        		else if (nr.getLongFrom() != nr.getLongTo()) {
        			text = dn.text + "有" + nr.getLongFrom() + "-" +nr.getLongTo() + EnumConvert.getStrFromUnit(unit);
        		}
        		sb.append(text);
        	} else if (sNode.type == NodeType.DATE && ((DateNode) sNode).isSequence && !sNode.text.contains("连续")) {
        		if (((DateNode) sNode).getDateinfo().getLength() > 0)
        			sb.append("连续" + sNode.text);
        		else 
        			sb.append(sNode.text + "连续");
        	}*/
        	else {
        		sb.append(sNode.getText());
        	}
            return sb.toString();
        }
        FocusNode fNode = (FocusNode) sNode;
        ClassNodeFacade cNode = fNode.getIndex();
        if (cNode == null) {
            sb.append(sNode.getText());
            return sb.toString();
        }
        List<PropNodeFacade> pNodes = cNode.getAllProps();
        boolean isSequenceAfterIndex = false;
        for (int i = 0; i < pNodes.size(); i++) {
            SemanticNode propValue = pNodes.get(i).getValue();
            if (propValue == null || propValue.getText() == null || propValue.type == NodeType.STR_VAL) {
                continue;
            }
            if(propValue.getText().length()>0 && propValue.type==NodeType.DATE)
            {
            	//原来指标处理逻辑
				if (!cNode.getFieldsAll().contains("人物")) {
					isSequenceAfterIndex = datePresention(sb, propValue);
					sb.append("的");
					continue;
				}
				//新类型的指标处理逻辑
            	sb.append(cNode.getText()+"的");
            	sb.append(pNodes.get(i).getText()+"是");
            	datePresention(sb, propValue);
            	sb.append(",");
            }
        }
        sb.append(cNode.getText());
        sb.append(isSequenceAfterIndex ? "连续" : "");
        /*
         * 指标别名，歧义指标等。
         * String otherAlias = getAliasIndexStr(fNode);
         * sb.append(otherAlias);
         */
        // compare prop
        if (cNode.getCompareProp() != null) {
            sb.append("的").append(cNode.getCompareProp().getText());
        }
		boolean isFirstProp = true; 
        for (int i = 0; i < pNodes.size(); i++) {
            SemanticNode propValue = pNodes.get(i).getValue();
            if (propValue != null && propValue.getText() != null && propValue.type == NodeType.STR_VAL) {
            	//原来指标处理逻辑
            	if (!cNode.getFieldsAll().contains("人物")) {
					sb.append(propValue.getText());
					continue;
				}
            	//新类型的指标处理逻辑
            	if(isFirstProp) {
            		isFirstProp = false;
            		sb.append(pNodes.get(i).getText()+"包含");
                    sb.append(propValue.getText());
            		
            	}else{
            		sb.append(",");
            		sb.append(pNodes.get(i).getText()+"包含");
                    sb.append(propValue.getText());
            	}   
            }

        }
        return sb.toString();
    }

}
