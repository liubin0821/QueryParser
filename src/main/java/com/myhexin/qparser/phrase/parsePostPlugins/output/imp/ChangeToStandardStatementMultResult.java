/**
 * @author: 	    吴永行 
 * @dateTime:	  2014-5-13 下午1:42:09
 * @description:   	
 * 
 */
package com.myhexin.qparser.phrase.parsePostPlugins.output.imp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

import com.myhexin.qparser.Param;
import com.myhexin.qparser.Query;
import com.myhexin.qparser.Query.Type;
import com.myhexin.qparser.conf.IndexDefOpInfo;
import com.myhexin.qparser.conf.IndexDefOpInfo.DefOpInfo;
import com.myhexin.qparser.conf.IndexInteraction;
import com.myhexin.qparser.date.DatePatterns;
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
import com.myhexin.qparser.node.BoundaryNode.IMPLICIT_PATTERN;
import com.myhexin.qparser.node.BoundaryNode.SyntacticPatternExtParseInfo;
import com.myhexin.qparser.node.Environment;
import com.myhexin.qparser.node.FocusNode;
import com.myhexin.qparser.node.NumNode;
import com.myhexin.qparser.node.SemanticNode;
import com.myhexin.qparser.onto.ClassNodeFacade;
import com.myhexin.qparser.onto.PropNodeFacade;
import com.myhexin.qparser.phrase.IndexMultPossibility;
import com.myhexin.qparser.phrase.OutputResult;
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
import com.myhexin.qparser.phrase.parsePlugins.PhraseParserPluginBindIndexToIndex;
import com.myhexin.qparser.phrase.parsePlugins.bind.BindNumOrDateToIndex1;
import com.myhexin.qparser.phrase.parsePlugins.bind.BindStrToIndex;
import com.myhexin.qparser.phrase.parsePostPlugins.output.Output;
import com.myhexin.qparser.tech.TechMisc;

public class ChangeToStandardStatementMultResult extends Output  {
	protected static org.slf4j.Logger logger_ = org.slf4j.LoggerFactory.getLogger(ChangeToStandardStatementMultResult.class.getName());

	//private static Environment  ENV;
	/*
	 * 问句回写
	 * 1、没有匹配上句式，则直接返回原句，这里返回null；
	 * 2、匹配上句式，则只将boundary内部的nodes回写。
	 */
    public static OutputResult changeToStandardStatement(List<SemanticNode> nodes, String split, Environment ENV) {
    	if (nodes == null || nodes.size() == 0 || ENV == null)
    		return null;
    	//ENV = ENV_p;
    	OutputResult result = new OutputResult();
    	
        int boundaryStart = 0;
        int boundaryEnd = nodes.size() - 1;
        Iterator iterator = new SyntacticIteratorImpl(nodes);
        if (!iterator.hasNext())
			return result;
        HashMap<Integer,List<String>> allStr = new HashMap<Integer,List<String>>();
    	while (iterator.hasNext()) {
    		BoundaryInfos boundaryInfos = (BoundaryInfos) iterator.next();
        	boundaryStart = boundaryInfos.bStart; // 句式左边界
        	boundaryEnd = boundaryInfos.bEnd; // 句式右边界
            List<String> strs = representFromBoundaryNode(nodes, boundaryStart, boundaryEnd,ENV);
            
          //单个句式数量限制
    	    if(Param.MULTRESULT_ONE_SYNTACTIC_MAX_NUM > 0 
    	    		&& Param.MULTRESULT_ONE_SYNTACTIC_MAX_NUM < strs.size())
    	    	strs =  strs.subList(0, Param.MULTRESULT_ONE_SYNTACTIC_MAX_NUM);
            	
            // 为空或者只有一个为空的字符串,不显示
            if(strs == null || strs.isEmpty() || (strs.size()==1 && strs.get(0).isEmpty()))
            	continue;
            allStr.put(boundaryStart,strs);
    	}
    	//按照所以指标存在的可能性得到存在的多种可能性个数，与需要显示的对比，如果小于，则按照原来的方式处理，否则按照最新优化方案。
    	BoundaryNode lastBNode = null;    
    	Object[] keys = allStr.keySet().toArray();
    	Arrays.sort(keys);
    	
    	String prefix = null;
    	int count = 0;
    	for(Object key:keys) {
    		count++;
            List<String> temp = null; //new ArrayList<String>();
            if(allStr.size()==1){
            	temp = getRandMultResult(allStr.get(key),Param.MULTRESULT_ONE_INDEX_DISPLAY_MAX_NUM);
            }else{
            	temp = getRandMultResult(allStr.get(key),Param.MULTRESULT_MORE_INDEX_DISPLAY_MAX_NUM);
            }
            if(count == 1 && temp != null && temp.size() == 1 && StringUtils.isNotBlank(temp.get(0)) && temp.get(0).indexOf("的") > -1) {
            	String xfix = temp.get(0).substring(0, temp.get(0).indexOf("的"));
            	if(xfix.contains("连续") && (xfix.contains("日") || xfix.contains("天") || xfix.contains("月") || xfix.contains("年"))) {
            		prefix = xfix;
            	}
            }else if(count > 1 && temp != null && temp.size() == 1 && StringUtils.isNotBlank(temp.get(0)) && temp.get(0).indexOf("的") > -1) {
            	String tprefix = temp.get(0).substring(0, temp.get(0).indexOf("的"));
            	if(StringUtils.isNotBlank(prefix) && StringUtils.equals(prefix, tprefix)) {
            		String postFix = temp.get(0).substring(temp.get(0).indexOf("的") + 1);
            		temp.clear();
            		temp.add(postFix);
            	}
            }
            
            result.syntacticOutputs.add(temp);            
            if (result.syntacticOutputs.size() > 1) 
            	result.syntacticRelations.add(lastBNode.contextLogicType==LogicType.OR ? "或":split);
            lastBNode = ((BoundaryNode)nodes.get((int) key));
    	    }
    	formatSameOutPut(result);
       return result;
    }
    
    /**
     * 对于相同的后缀，合并前缀，输出易读的格式。
     * 
     * @author huangmin
     *
     * @param result
     */
    private static void formatSameOutPut(OutputResult result) {
    	if(result != null) {
    		List<List<String>> syntacticOutputs = result.syntacticOutputs;
    		if(syntacticOutputs.size() > 0) {
    			List<String> prefix = new ArrayList<String>();
    			for(int i = syntacticOutputs.size() - 1; i >= 0; i--) {
    				List<String> chunks = syntacticOutputs.get(i);
    				if(chunks != null && chunks.size() > 0) {
    					String chunk = chunks.get(0);
    					if(chunk.indexOf("的") > 0 && chunk.length() - 1 > chunk.indexOf("的")) {
    						String[] ndayChunk = chunk.split("的");
    						String nDay = ndayChunk[0];
    						String avgLineIndex = ndayChunk[1];
    						List<String> preChunks = null;
    						if(i > 0) {
    							preChunks = syntacticOutputs.get(i - 1);
    						}
							if(preChunks != null && preChunks.size() > 0) {
		    					String preChunk = preChunks.get(0);
		    					if(preChunk.indexOf("的") > 0 && preChunk.length() - 1 > preChunk.indexOf("的")) {
		    						String[] preNdayChunk = preChunk.split("的");
		    						String preNDay = preNdayChunk[0];
		    						String preVvgLineIndex = preNdayChunk[1];
		    						if(preVvgLineIndex.equals(avgLineIndex) && nDay.length() < 8 && preNDay.length() < 8) {
		    							Matcher dmatcher = DatePatterns.NDAY_YEAR.matcher(nDay);
		    							Matcher pdmatcher = DatePatterns.NDAY_YEAR.matcher(preNDay);
		    							if(dmatcher.find() && pdmatcher.find()) {
			        						if(!prefix.contains(nDay)) {
			        							prefix.add(nDay);
			        						}
			    							if(!prefix.contains(preNDay)) {
			    								prefix.add(preNDay);
			    							}
		    							}
		    							syntacticOutputs.remove(i);
		    						}else if(prefix.size() > 0) {
			    						appendPrefix(syntacticOutputs, prefix, i,
												avgLineIndex);
			    					}
		    					}
							}else if(prefix.size() > 0) {
	    						appendPrefix(syntacticOutputs, prefix, i,
										avgLineIndex);
	    					}
    					}
    				}
    			}
    		}
    	}
	}

    /**
     * 追加相同的前缀。
     * 
     * @author huangmin
     *
     * @param syntacticOutputs
     * @param prefix
     * @param i
     * @param avgLineIndex
     */
	private static void appendPrefix(List<List<String>> syntacticOutputs,
			List<String> prefix, int i, String avgLineIndex) {
		String prefixStr = StringUtils.EMPTY;
		for(int j = prefix.size() - 1; j >=0; j--) {
			prefixStr += prefix.get(j)+"、";
		}
		if(prefixStr.length()> 1 && prefixStr.lastIndexOf("、") == prefixStr.length() - 1) {
			prefixStr = prefixStr.substring(0, prefixStr.length() - 1);
		}
		String newChunk = prefixStr + "的" +avgLineIndex;
		List<String> newChunkList = new ArrayList<String>();
		newChunkList.add(newChunk);
		syntacticOutputs.set(i, newChunkList);
		prefix.clear();
	}

	/*
     * 如果list.size<=num, 返回list
     * 如果list.size>num, 删除list.size-num个item
     * 
     * 
     */
    private static List<String> getRandMultResult(List<String> list,int num){
    	List<String> temp = new ArrayList<String>(list.size());
    	temp.addAll(list);
    	if(list.size() > num){
    	   	for(int i=0;i<temp.size()-num;){
    		   	int randNum = new Random().nextInt(temp.size()-1)+1;
    		   	temp.remove(randNum);
    	   	}
    	}
    	return temp;
    }
    /**
     * 针对每一个句式进行回写
     */
    private static List<String> representFromBoundaryNode(List<SemanticNode> nodes, int start, int end,Environment ENV) {
        BoundaryNode bNode = (BoundaryNode) nodes.get(start);
        String patternId = bNode.getSyntacticPatternId();
        
        if (BoundaryNode.getImplicitPattern(patternId) != null) { 
        	// YYY1：句式匹配上KEY_VALUE, STR_INSTANCE, FREE_VAR这三种情况
        	return representFromBoundaryNodeImplicit(nodes, start, end,ENV);
        } else {
        	// YYY2：匹配上了句式
        	return representFromBoundaryNodeNoneImplicit(nodes, start, end,ENV);
        }
    }
    
    
    /*
     * FREE_VAR
     * KEY_VALUE
     * STR_INSTANCE
     * 
     */
    private static List<String> representFromBoundaryNodeImplicit(List<SemanticNode> nodes, int start, int end,Environment ENV) {
    	ArrayList<StringBuffer> represents = new ArrayList<StringBuffer>();
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
						String result = getPropsText(nodes.get(start + pos),ENV).get(0);
						if (PersonIndexShowContain.matcher(newstr.toString()).matches()) // 特定的指标
							newstr.append("包含");
						else if (result.equals("是")
								|| result.equals("否"))
							newstr.append("为");
						else
							newstr.append("是");
					}
					newstr.append(getPropsText(nodes.get(start + pos),ENV).get(0));
					isKey = false;
				}
			}
			represents.add(newstr);
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
					represents = getNewResult(represents, getPropsText(nodes.get(start + pos),ENV));
					//newstr.append(.get(0));
					DefOpInfo doi = cn != null ? IndexDefOpInfo.getDefOpInfoByIndexName(cn.getText(), Query.Type.ALL) : null;
					if (cn != null &&  doi!= null) {
						if (doi.getDefOp(null)!=null) {
							String defOp = doi.getDefOp(null).getText();
							represents = getNewResult(represents, defOp);
							//newstr.append(defOp);
						}
					}
				}
			}
			//represents.add(newstr.toString());
    	} else if (patternId.equals(IMPLICIT_PATTERN.STR_INSTANCE.toString())) {
			ArrayList<Integer> elelist;
			boolean isKey = true;
			boolean isBoundToIndex = false;
			boolean isContainProps = false;
			boolean isChangeToOld = false;
			ArrayList<String> str_val = null;
			for (int j = 1; (elelist = info.getElementNodePosList(j)) != null; j++) {
				for (int pos : elelist) {											
					ArrayList<String> tempText = null;
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
						tempText = getPropsText(defaultNode,ENV);
					} else {
						if(nodes.get(start + pos).isBoundToIndex()) {
							isBoundToIndex = true;
							continue;
						}
						tempText = getPropsText(nodes.get(start + pos),ENV);
						str_val = tempText;
					}
					
					if (isKey == false) {
						if(PersonIndexShowContain.matcher(newstr.toString()).matches()) //特定的指标
							newstr.append("包含");
						else if (getPropsText(nodes.get(start + pos),ENV).equals("是")
									|| getPropsText(nodes.get(start + pos),ENV).equals("否"))
							newstr.append("为");
						else
							newstr.append("是");
					}
					newstr.append(tempText.get(0));
					isKey = false;
				}
			}
			/*if (isContainProps == false && isChangeToOld == false && str_val!= null && str_val.size()>0) {
				newstr = new StringBuffer(str_val.get(0));
			}*/
			// 没有被绑定才显示
			if(!isBoundToIndex)
				represents.add(newstr);
		}
        //System.out.println("YYY1 "+ newstr.toString());
    	 ArrayList<String> newResult = new ArrayList<String>();
         for (StringBuffer sbResult : represents) {
         	newResult.add(sbResult.toString());
 		}
     	return newResult;
    }
    
    
    /*
     * 非FREE_VAR, KEY_VALUE,STR_INSTANCE
     * 
     */
    private static List<String> representFromBoundaryNodeNoneImplicit(List<SemanticNode> nodes, int start, int end,Environment ENV) {
    	ArrayList<StringBuffer> represents = new ArrayList<StringBuffer>();
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
                represents = getNewResult(represents, getRepresentBySemantic(syntPtn,mainBindTo,info,nodes,start,ENV));
        	} else if (dn.getType() == DescNodeType.LOGIC) {
        		String logic = dn.getText();
        		if (dn.getText().equals("&"))
        			logic = "且";
                else if (dn.getText().equals("|"))
                	logic = "或";
                else if (dn.getText().equals("!"))
                	logic = "非";
        		represents = getNewResult(represents,logic);
        	} else {
        		represents = getNewResult(represents,dn.getText());
        	}
        }
        ArrayList<String> newResult = new ArrayList<String>();
        for (StringBuffer sbResult : represents) {
        	newResult.add(sbResult.toString());
		}
    	return newResult;
    }
    
    /**
	 * @author: 	    吴永行 
	 * @param info 
	 * @param start 
	 * @param nodes 
	 * @dateTime:	  2013-11-8 下午1:16:37
	 * @description:  YYY3：即匹配上了句式，又匹配上了语义，根据语义定义的方式回写
	 */
	private static ArrayList<String> getRepresentBySemantic(SyntacticPattern syntPtn,
			SemanticBindTo bindTo, SyntacticPatternExtParseInfo info, List<SemanticNode> nodes, int start,Environment ENV) {
		SemanticPattern semPtn = PhraseInfo.getSemanticPattern(bindTo.getBindToId()+"");
		boolean needCalculate = false;//处理需要计算的语义,如"每10股转5股","每股转增股本{}=(5股/10股)"=>"每股转增股本{}=0.5"
		if(Integer.parseInt(semPtn.getId()) == 167)	needCalculate = true;
		
		ArrayList<StringBuffer> result = new ArrayList<StringBuffer>();
		int semanticPropsClassNodeId = bindTo.getSemanticPropsClassNodeId();
		if (semanticPropsClassNodeId != -1) {
			ArrayList<String> temp = getSemanticProps(info, semanticPropsClassNodeId,ENV);
			result = getNewResult(result,temp);
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
    					ArrayList<String> semanticResult = getRepresentBySemantic(syntPtn, syntPtn.getSemanticBind().getSemanticBindTo(sad.getElementId()), info, nodes, start,ENV);
    					result = getNewResult(result,semanticResult);
    				} else {
    					SemanticArgument argument = semPtn.getSemanticArgument(arg, false);
    					ArrayList<Integer> list;
    					boolean matched = false;
    	
    					int i = sad.getElementId();
    					list = info.getElementNodePosList(i);
    					if (list != null) {
    						if (list.size() == 1 && list.get(0) == -1) {
    							ArrayList<String> temp = getDefaultArgument(argument, info, i, nodes, isNegative,ENV);
    							result = getNewResult(result,temp);
    						} else {
    							Collections.sort(list);
    							// 若为比较符号带负号，则涉及转义
    							for (int k = 0; k<result.size(); k++) {
    								StringBuffer tempResult = result.get(k);
									if (pre.equals("-") 
    									&& (prePre.equals(">") || prePre.equals(">=") || prePre.equals("<") || prePre.equals("<="))){
										tempResult = new StringBuffer(tempResult.substring(0, tempResult.length()-prePre.length()));
										result.set(k, tempResult);
									}
    							}							   								
    							ArrayList<String> temp = getPresentArgument(argument, nodes, start, list, isNegative, prePre,ENV);
    							result = getNewResult(result,temp);
    						}
    						matched = true;
    					}
    	
    					// 增加的回写逻辑：当语义需要的字段无法从句式中的对应位置获取时，使用语义中的默认值
    					// 这种回写只适合值的情况，指标类型必须匹配上
    					if ((argument.getType() == SemanArgType.CONSTANT || argument.getType() == SemanArgType.CONSTANTLIST)
    							&& matched == false) {
    						ArrayList<String> temp = getDefaultArgument(argument, info, 0, nodes, isNegative,ENV);
    						result = getNewResult(result,temp);
    					} else if ((argument.getType() == SemanArgType.INDEX || argument.getType() == SemanArgType.INDEXLIST)
    							&& matched == false) {
    						// 需要增加处理，这种情况下句式配置是存在问题的
    					}
    				}
                } else {
                	ArrayList<String> temp = getFixedArgument(info, sad.getElementId(),ENV);
                	result = getNewResult(result,temp);
                }
                isNegative = false;
        	} else if (dn.getType() == DescNodeType.TEXT) {
        		if (dn.getText().contains("-") && 
                		(pre.equals(">") || pre.equals(">=") || pre.equals("=") || pre.equals("<") || pre.equals("<="))) {
        			isNegative = true;
                } else {
                	getNewResult(result, dn.getText());
                	isNegative = false;
                }
        	} else {
        		getNewResult(result, dn.getText());
        		isNegative = false;
        	}
        	prePre = pre;
        	pre = dn.getText();
        }
			
		ArrayList<String> returnResult = new ArrayList<String>();
		for (StringBuffer sb : result) {
			returnResult.add(sb.toString());
		}
		if(needCalculate){//语义中需要计算的部分
			for(int i=0;i<returnResult.size();i++){
	        	String resultNew = calculateFromResult(returnResult.get(i));
	        	if(resultNew != null)
	        		returnResult.set(i, resultNew);
			}
        }
		return returnResult;
	}
	
	/**
     * @author: 	    吴永行 
     * @dateTime:	  2014-3-31 下午1:50:06
     * @description:  笛卡尔积  产生多个可能结果 	
     * 
     */
    private static ArrayList<StringBuffer> getNewResult(ArrayList<StringBuffer> result, ArrayList<String> strList) {
    	if (strList.size() == 0) {
			return result;
		} else if (strList.size() == 1) {
    		return getNewResult(result, strList.get(0));
		} else {
    		ArrayList<StringBuffer> newResult = new ArrayList<StringBuffer>();
			for (String str : strList) {
				for (StringBuffer sb : result) {
					StringBuffer newSb = new StringBuffer();
					newSb.append(sb.toString()+str);
					newResult.add(newSb);
				}
				if(result.size()==0){
					StringBuffer newSb = new StringBuffer();
					newSb.append(str);
					newResult.add(newSb);
				}
					
			}
			return newResult;
		}
    }

	/**
     * @author: 	    吴永行 
     * @dateTime:	  2014-3-31 下午2:41:32
     * @description:   	
     * @param result
     * @param strList
     * @return
     * 
     */
    public static ArrayList<StringBuffer> getNewResult(
            ArrayList<StringBuffer> result, String str) {
    	if (result.size()==0) {
    		StringBuffer sb = new StringBuffer();
    		sb.append(str);
    		result.add(sb);
    		return result;
		}
	    for (StringBuffer sb : result) {
	    	sb.append(str);
	    }
	    return result;
    }

	/*
	 * 获得语义属性
	 */
	private static ArrayList<String> getSemanticProps(SyntacticPatternExtParseInfo info,
			int semanticPropsClassNodeId,Environment ENV) {
		SemanticNode semanticProps = info.semanticPropsMap.get(semanticPropsClassNodeId);
		ArrayList<String> result = new ArrayList<String>();
		if (semanticProps != null) {
        	if (semanticProps.type == NodeType.FOCUS && ((FocusNode)semanticProps).hasIndex()) { 
        		result = getPropsText(semanticProps,ENV);
        	} else {
        		result.add(semanticProps.getText());
        	}
        }
		return result;
	}
	
	/*
	 * 获得设定为固定值的参数
	 */
	private static ArrayList<String> getFixedArgument(BoundaryNode.SyntacticPatternExtParseInfo info, int fixedArgumentId,Environment ENV) {
		SemanticNode fixedNode = info.fixedArgumentMap.get(fixedArgumentId);
		ArrayList<String> result = new ArrayList<String>();
		if (fixedNode != null) {
        	if (fixedNode.type == NodeType.FOCUS && ((FocusNode)fixedNode).hasIndex()) { 
        		result = getPropsText(fixedNode,ENV);
        	} else {
        		result.add(fixedNode.getText());
        	}
        }
		return result;
	}

    /*
     * 获得句式匹配上的argument的默认信息
     * INDEX\INDEXLIST:回写默认的指标
     * CONSTANT:回写默认的文本
     * ANY:回写默认的文本
     * 
     */
    private static ArrayList<String> getDefaultArgument(SemanticArgument argument, BoundaryNode.SyntacticPatternExtParseInfo info,
            int elemPos, List<SemanticNode> sNodes, boolean isNegative,Environment ENV) {
    	ArrayList<String> result = new ArrayList<String>();
        switch (argument.getType()) {
            case INDEX:
            case INDEXLIST:
                SemanticNode defaultNode = info.absentDefalutIndexMap.get(elemPos);
                if (defaultNode != null) {
                	result = getPropsText(defaultNode,ENV);
                }
                break;
            case CONSTANT:
            case CONSTANTLIST:
                if (argument.getDefaultValue() != null) {
                	String defVal = argument.getDefaultValue();
                	// 增加语义中包含负号的处理
                	if (isNegative == true && !(defVal.equals("0") || defVal.equals("0%")))
                		result.add( "-" + argument.getDefaultValue());
                	else
                		result.add(argument.getDefaultValue());
                }
                break;
            case ANY:
                if (argument.getDefaultIndex() != null) {
                    // text = argument.getDefaultIndex();
                }
                if (argument.getDefaultValue() != null) {
                	result.add(argument.getDefaultValue());
                }
                break;
            default:
            	String text = "";
                text = argument.getSpecificIndex();
                text = text == null ? argument.getDefaultValue() : text;
                text = text == null ? "" : text;
                result.add(text);
                break;
        }
        return result;
    }

    /*
     * 获得句式匹配上的argument的相关信息：text和相应的属性修饰
     * INDEX:获得指标的text和相应的属性
     * INDEXLIST\ANY:获得list中各个node的信息
     * CONSTANT:直接将text回写
     */
    private static ArrayList<String> getPresentArgument(SemanticArgument argument, List<SemanticNode> sNodes, int start,
            List<Integer> poses, boolean isNegative, String prePre,Environment ENV) {
    	ArrayList<String> result = new ArrayList<String>();
        if (argument == null) {
        	result.add("");
            return result;
        }
        StringBuffer sb = new StringBuffer();
        switch (argument.getType()) {
            case INDEX:
                SemanticNode sNode = PhraseUtil.getNodeByPosInfo(poses.get(0), start, sNodes);
                result = getPropsText(sNode,ENV);
                break;
            case INDEXLIST:
            case ANY:
                List<SemanticNode> nodes = PhraseUtil.getNodesByPosInfo(poses, start, sNodes);
                for (int i = 0; i < nodes.size(); i++) {
                	List<String> indexs = getPropsText(nodes.get(i),ENV);
                    sb.append(indexs.size()>0 ? indexs.get(0):"");
                    if (i < nodes.size()-1)
                    	sb.append("、");
                }
                //added by huangmin for 删除顿号之间重复的修饰符，比如: 连续3天的融资余额、连续3天的融券余额 变成 连续3天的融资余额、融券余额
                String resultStr = removeDuplicatePrefix(sb.toString(), "、");
                //added end                
                result.add(resultStr);
                break;
            case CONSTANT:
                SemanticNode node = PhraseUtil.getNodeByPosInfo(poses.get(0), start, sNodes);
                // 增加语义中包含负号的处理
                if (isNegative && node.type == NodeType.NUM) {
                	NumNode nNode = (NumNode) node;
                	if (nNode.getFrom() == nNode.getTo() && nNode.getFrom() > 0) {
                		sb.append(prePre+"-"+node.getText());
                		result.add(sb.toString());
                		break;
                	} else if (nNode.getFrom() == nNode.getTo() && nNode.getFrom() < 0) {
                		if (prePre.equals(">") || prePre.equals(">=") || prePre.equals("<") || prePre.equals("<=")) {
                			prePre = prePre.contains(">") ? prePre.replace(">", "<") : prePre.replace("<", ">");
                			sb.append(prePre+node.getText());
                		} else {
                			sb.append(prePre+node.getText());
                		}
                		result.add(sb.toString());
                		break;
                	} else {
                		sb.append(prePre+node.getText());
                		result.add(sb.toString());
                		break;
                	}
                }
                sb.append(node.getText());
                result.add(sb.toString());
                break;
            case CONSTANTLIST:
            	List<SemanticNode> constantList = PhraseUtil.getNodesByPosInfo(poses, start, sNodes);
                for (int i = 0; i < constantList.size(); i++) {
                    sb.append(getPropsText(constantList.get(i),ENV));
                    if (i < constantList.size()-1)
                    	sb.append("、");
                }
                result.add(sb.toString());
                break;
            default:
                break;
        }
        return result;
    }
  
    //必须保证 result 至少有一个元素
    private static ArrayList<String> getPropsText(SemanticNode sNode,Environment ENV) {
    	ArrayList<String> result = new ArrayList<String>();
		if (sNode == null) {
			return result;
		}
		// 处理连续回写的逻辑
		StringBuffer sb = new StringBuffer();
		if (sNode.type != NodeType.FOCUS) {
			if (sNode.type == NodeType.DATE)
				datePresention(sb, sNode);
			else
				sb.append(sNode.getText());
			result.add(sb.toString());
			return result;
		} else {
			FocusNode fNode = (FocusNode) sNode;
			ClassNodeFacade cNode = fNode.getIndex();
			// 不是指标
			if (cNode == null) {
				sb.append(sNode.getText());
				result.add(sb.toString());
				return result;
			}
			// 是指标
			
			getIndexPropMultText(result, sb, fNode, cNode,ENV);						
			return result;
		}
	}

	/**
     * @author: 	    吴永行 
     * @dateTime:	  2014-4-18 下午1:49:16
     * @description:  多种可能性 	
     */
    private static void getIndexPropMultText(List<String> result,
            StringBuffer sb, FocusNode fNode, ClassNodeFacade cNode,Environment ENV) {
    	LinkedHashMap<ClassNodeFacade, Double> indexList = new LinkedHashMap<ClassNodeFacade, Double>();
	    double currentScore = -1;
	    ClassNodeFacade defaultCN = null;
	    boolean isDefaultOK = false;
	    Type qType = ENV.get("qType",false) != null ? ((Type) ENV.get("qType",false)) : null;
	    IndexMultPossibility impb = new IndexMultPossibility();
	    impb.originalText = fNode.getText();
	    HashSet<String> temp = new HashSet<String>();
	    
	    //默认指标符合qType限制
		if(isIndexTypeOK(cNode, qType)){
			isDefaultOK = true;
			getIndexPropsText(sb, cNode);
			result.add(sb.toString());	    	
	    	impb.possibleIndex.add(cNode.getText());
	    	temp.add(cNode.getText());    	
		}
				
		
	    ArrayList<FocusNode.FocusItem> itemList = fNode.getFocusItemList();
	    int j = 0, size = itemList.size();
	    while (j < size) {
	    	FocusNode.FocusItem item = itemList.get(j++);
	    	if (item.getType() == FocusNode.Type.INDEX) {
	    		ClassNodeFacade cn = item.getIndex();
	    		if (cn == null )
	    			continue;
	    		
	    		//默认指标可行
	    		if(isDefaultOK && cn.getText().equals(cNode.getText())){
	    			currentScore = item.getScore() ;
	    			continue;
	    		}	    		
	    		
	    		if(!isIndexTypeOK(cn, qType))
	    			continue;	
	    		
	    		//默认指标不可行
	    		if(!isDefaultOK && item.getScore() > currentScore
	    				&& canAsPossibleResult(item.getIndex(),cNode)){
	    			currentScore = item.getScore();
	    			defaultCN = item.getIndex();
	    			//continue;
	    		}	    		
	    		//注意focusNode的index和item对应的index他们是两个不同实例,  在setIndex的时候clone过了
	    		if(!temp.contains(cn.getText())){
	    			temp.add(cn.getText());
	    			indexList.put(cn, item.getScore() );
	    		}
	    	}
	    }	    
	    temp=null;
	    
	    if (!isDefaultOK) {
	    	if(defaultCN != null){ //添加可以替换的第一个指标
	    		getIndexPropsText(sb, defaultCN);
	    		impb.possibleIndex.add(defaultCN.getText());
	    	}
	    	else {//为空时添加默认指标
	    		getIndexPropsText(sb, cNode);
	    		impb.possibleIndex.add(cNode.getText());
	    	}
	    	result.add(sb.toString());
	    }
	    Map.Entry[] indexArray = getSortedHashtableByValue(indexList);
	    
	    ArrayList<String> indexInteractions = new ArrayList<String>();
	    for (String originalText : impb.originalText.split("\\|")) {
	    	if(IndexInteraction.indexInteractions_.get(originalText) != null)
	    		indexInteractions.addAll(IndexInteraction.indexInteractions_.get(originalText));
		}
	    for ( Map.Entry entry : indexArray) {
	    	ClassNodeFacade cn = (ClassNodeFacade) entry.getKey();
	    	//得到所有可能输出
	    	if(Param.ALL_MULTRESULT){
	    		if (canAsPossibleResult(cn,cNode)){
	    			sb = new StringBuffer();
		    		getIndexPropsText(sb, cn);	
		    		if(indexInteractions!=null && indexInteractions.contains(cn.getText()))
		    			result.add(sb.toString());
		    		impb.possibleIndex.add(cn.getText());	
	    		}
	    		continue;
	    	}
	    	
	    	//原始方案
	    	if(canAsPossibleResult(cn,cNode)){
		    	if(indexList.get(cn) >= currentScore){
		    		sb = new StringBuffer();
		    		getIndexPropsText(sb, cn);
		    		if(indexInteractions!=null && indexInteractions.contains(cNode.getText()))
		    			result.add(sb.toString());	    		
		    	}
	    		impb.possibleIndex.add(cn.getText());
	    	}
	}
	    //多种可能型读取indexInteractions并去交集。 2014.05.15  like
	    if(indexInteractions != null && !indexInteractions.isEmpty())
		    impb.possibleIndex.retainAll(indexInteractions);


	    Object obj = ENV.get("IndexMultPossibilitys",false);
	    if (obj!= null) {
			List<IndexMultPossibility> impbs = (List<IndexMultPossibility>) obj;
			impbs.add(impb);
		}
	    
	    //单个指标数量限制
	    if(Param.MULTRESULT_ONE_INDEX_MAX_NUM > 0 
	    		&& Param.MULTRESULT_ONE_INDEX_MAX_NUM < result.size())
	    	result = result.subList(0, Param.MULTRESULT_ONE_INDEX_MAX_NUM);
	    
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })  
    public static Map.Entry[] getSortedHashtableByValue(Map h) {  	  
        Set set = h.entrySet();  	  
        Map.Entry[] entries = (Map.Entry[]) set.toArray(new Map.Entry[set.size()]);  	  
        Arrays.sort(entries, ENTRY_DOUBLE_VALUE_COMPARATOR); 
        return entries;  
    }  

    @SuppressWarnings("rawtypes")
    static class EntryDoubleValueComparator implements Comparator<Map.Entry> {
    	public int compare(Map.Entry arg0, Map.Entry arg1) {  
        	Double key1 = (Double) ((Map.Entry) arg0).getValue();  
        	Double key2 = (Double) ((Map.Entry) arg1).getValue();  
            return key2.compareTo(key1);  
        }  	
    }
    @SuppressWarnings("rawtypes")
	private static Comparator ENTRY_DOUBLE_VALUE_COMPARATOR = new EntryDoubleValueComparator();
    
	/**
     * @author: 	    吴永行 
     * @dateTime:	  2014-4-22 下午4:49:01
     * @description:   	
     * @param cNode
     * @param qType
     * 
     */
    private final static boolean isIndexTypeOK(ClassNodeFacade cNode, Type qType) {
	    if (qType != null ) {
	    	if (qType == Type.ALL) 
	    		return true;
	    	
	    	if(cNode==null || cNode.getDomains()==null || cNode.getFieldsAll()==null) {
	    		return false;
	    	}
	    	
			if(!cNode.getDomains().contains(qType) && !cNode.getFieldsAll().contains(qType.name().toLowerCase()))
				return false;
		}
	    
	    return true;
    }
    
    public static final boolean canAsPossibleResult(ClassNodeFacade cn, ClassNodeFacade useingCn){
    	//没有值绑定给他
    	if(useingCn == null)
    		return false;
    	
    	if (!useingCn.isBoundValueToThis()) {
			return  true;
		}    	
    	    	
		for (PropNodeFacade pn : useingCn.getAllProps()) {
			if (pn.getValue() != null) {
				SemanticNode sn = pn.getValue();
				switch (sn.getType()) {
				case STR_VAL:
				case TECH_PERIOD:
					if (!BindStrToIndex.bindNodeToIndexProps(sn, cn))
						return false;
					break;
				case DATE:
					//日期的N日
					if (sn.getText().matches(TechMisc.REGEX_N_DAY_NAME)) {
						if (!BindStrToIndex.bindNodeToIndexProps(sn, cn))
							return false;
					} else {
						if (!BindNumOrDateToIndex1.bindNodeToIndexProps(sn,cn))
							return false;
					}
					break;
				case NUM:
					if (!BindNumOrDateToIndex1.bindNodeToIndexProps(sn, cn))
						return false;
					break;
				case CLASS:
					if (!PhraseParserPluginBindIndexToIndex
					        .bindNodeToIndexProps((ClassNodeFacade) sn, cn))
						return false;
					break;
				default:
					break;
				}
			}
		}
    	return true;
    }

	/**
     * @author: 	    吴永行 
     * @dateTime:	  2014-3-31 上午9:18:09
     * @description:   	
     * @param sb
     * @param cNode
     * 
     */
    private static void getIndexPropsText(StringBuffer sb, ClassNodeFacade cNode) {
	    List<PropNodeFacade> pNodes = cNode.getAllProps();
	    
	    StringBuffer sbProps = new StringBuffer();
	    StringBuffer sbIndexValue = new StringBuffer();
	    sbIndexValue.append(cNode.getText());
	    for (int i = 0; i < pNodes.size(); i++) {
	    	PropNodeFacade prop = pNodes.get(i);
	    	SemanticNode propValue = pNodes.get(i).getValue();
	    	if (propValue == null || propValue.getText() == null || propValue.getText().length() == 0) {
	    		continue;
	    	} else if (prop.isValueProp()) {
	    		//特定的指标
	    		if(PersonIndexShowContain.matcher(sbIndexValue.toString()).matches())
	    			sbIndexValue.append("包含");
	    		else if (propValue.getText().equals("是") || propValue.getText().equals("否"))
	    			sbIndexValue.append("为");
	    		else
	    			sbIndexValue.append("是");
	    		sbIndexValue.append(propValue.getText());
	    	} else {
	    		switch (propValue.getType()) {
	    		case DATE:
	    			datePresention(sbProps, propValue);
	    			break;
	    		case STR_VAL:
	    			sb.append(propValue.getText());
	    			break;
	    		default:
	    			sb.append(propValue.getText());
	    			break;
	    		}
	    		//sbProps.append("的");
	    	}
	    }
	    //change by wyh 2014.08.29
	    if(sb.length()!=0 || sbProps.length()!=0)
	    	sbProps.append("的");
	    
	    sb.append(sbProps.toString());
	    sb.append(sbIndexValue.toString());
    }
    
    private static String calculateFromResult(String result){
    	Pattern calculatePattern = Pattern.compile("(\\d+).*([/])(\\d+).*");
    	Matcher calculateMatcher = calculatePattern.matcher(result);
    	if(calculateMatcher.find()){
    		double num1 = Double.parseDouble(calculateMatcher.group(1));
    		char op = calculateMatcher.group(2).charAt(0);
    		double num2 = Double.parseDouble(calculateMatcher.group(3));
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
    
    /**
     * 删除重复的指标前缀修饰符，比如将 连续3天的融资余额、连续3天的融券余额 变成 连续3天的融资余额、融券余额
     * @param origIndexs 需要被消除重复前缀的原指标字符串
     * @return 消除后的字符串
     */
    private static String removeDuplicatePrefix(final String origIndexs, final String split) {
    	if( StringUtils.isNotEmpty(StringUtils.trim(origIndexs)) && StringUtils.contains(origIndexs, split) ) {
    		final String[] indexsStr = StringUtils.split(origIndexs, split);
    		if(indexsStr.length == 1) {
    			return origIndexs;
    		}
    		final String firstIndexStr = indexsStr[0]; //前缀必须在第一个元素中找，其他元素的前缀无法进行消除重复操作
    		final int matchedPrexPos = StringUtils.isNotEmpty(firstIndexStr)?StringUtils.indexOf(firstIndexStr, "的"):-1;
    		final String matchedPrex = ( matchedPrexPos > 0 &&  matchedPrexPos < firstIndexStr.length() -1 )?
    				StringUtils.substring(firstIndexStr, 0, matchedPrexPos):StringUtils.EMPTY; //获取前缀 比如 连续3天
    		if(StringUtils.isEmpty(matchedPrex)) {
    			return origIndexs;
    		}
    		final StringBuilder sb = new StringBuilder(firstIndexStr).append(split);
    		for(int i=1; i< indexsStr.length; i++) {
    			final String indexStr = indexsStr[i];
    			if(StringUtils.isBlank(indexStr)) {
    				continue;
    			}
    			final int indexPrexPos = StringUtils.indexOf(indexStr, "的");
    			if(indexPrexPos <= 0 || indexStr.length() ==1 || indexPrexPos == indexStr.length() -1  ) { //没找到前缀修饰符
    				sb.append(indexStr).append(split); //直接加入
    			}else {
    				final String indexPrex = StringUtils.substring(indexStr, 0, indexPrexPos);
    				if(StringUtils.equals(indexPrex, matchedPrex)){ //看看是否是同一个前缀
    					final String indexPost = StringUtils.substring(indexStr, indexPrexPos+1); //截取无修饰的指标名 比如 融券余额
    					sb.append(indexPost).append(split);
    				}
    			}
    		}
    		final String returnStr = sb.toString();
    		return returnStr.substring(0, returnStr.length()-1);
    	}
    	
    	return origIndexs;
	}
}