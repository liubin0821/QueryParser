package com.myhexin.qparser.util.freemarker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.myhexin.qparser.Param;
import com.myhexin.qparser.define.EnumDef.DescNodeType;
import com.myhexin.qparser.define.EnumDef.NodeType;
import com.myhexin.qparser.except.ExceptionUtil;
import com.myhexin.qparser.iterator.BoundaryInfos;
import com.myhexin.qparser.iterator.DescNode;
import com.myhexin.qparser.iterator.DescNodeNum;
import com.myhexin.qparser.iterator.SyntacticIteratorImpl;
import com.myhexin.qparser.iterator.SyntacticRepresentationIteratorImpl;
import com.myhexin.qparser.node.BoundaryNode;
import com.myhexin.qparser.node.BoundaryNode.IMPLICIT_PATTERN;
import com.myhexin.qparser.node.BoundaryNode.SyntacticPatternExtParseInfo;
import com.myhexin.qparser.node.FocusNode;
import com.myhexin.qparser.node.NestedSemanticNode;
import com.myhexin.qparser.node.SemanticNode;
import com.myhexin.qparser.node.StrNode;
import com.myhexin.qparser.onto.ClassNodeFacade;
import com.myhexin.qparser.phrase.PhraseInfo;
import com.myhexin.qparser.phrase.PhraseUtil;
import com.myhexin.qparser.phrase.SyntacticSemantic.Semantic.SemanticArgument;
import com.myhexin.qparser.phrase.SyntacticSemantic.Semantic.SemanticArgument.SemanArgType;
import com.myhexin.qparser.phrase.SyntacticSemantic.Semantic.SemanticArgument.ValueType;
import com.myhexin.qparser.phrase.SyntacticSemantic.Semantic.SemanticBind;
import com.myhexin.qparser.phrase.SyntacticSemantic.Semantic.SemanticBindTo;
import com.myhexin.qparser.phrase.SyntacticSemantic.Semantic.SemanticBindToArgument;
import com.myhexin.qparser.phrase.SyntacticSemantic.Semantic.SemanticBindToArgument.BindToType;
import com.myhexin.qparser.phrase.SyntacticSemantic.Semantic.SemanticBindToArgument.Source;
import com.myhexin.qparser.phrase.SyntacticSemantic.Semantic.SemanticPattern;
import com.myhexin.qparser.phrase.SyntacticSemantic.Syntactic.SyntacticPattern;
import com.myhexin.qparser.phrase.parsePlugins.ParsePluginsUtil;

public class CreateTemplate {
	private static org.slf4j.Logger logger_ = org.slf4j.LoggerFactory.getLogger(CreateTemplate.class.getName());

	
	private static TemplateResultBuilder templateBuilder = new TemplateResultBuilder(Param.TEMPLATES_PATH);
	//private static Configuration cfg_;
	//private static HashSet<String> existsTempleteSet ;
	
	/*static {
		// 初始化FreeMarker配置
		cfg_ = new Configuration(); // 创建一个Configuration实例
		existsTempleteSet = new HashSet<String>();
		
		//把模板文件加进去
		existsTempleteSet = new HashSet<String>();
		File templateFolder = new File(Param.TEMPLATES_PATH);
		if(templateFolder.exists() && templateFolder.isDirectory()) {
			String[] templateFiles = templateFolder.list(new FilenameFilter(){
				@Override
				public boolean accept(File dir, String name) {
					if(name.endsWith(".ftl")) {
						return true;
					}else
						return false;
				}});
			if(templateFiles!=null) {
				for(String fn : templateFiles) {
					existsTempleteSet.add(fn);
				}
			}
		}//把模板文件加进去 结束
				
		cfg_.setClassForTemplateLoading(Thread.currentThread().getClass(), Param.TEMPLATES_PATH);
	}*/
	
	public static String getJsonResult(ArrayList<SemanticNode> nodes) {
		List<String> results = getJsonResults(nodes);
		if (results != null && results.size() > 0)
			return results.get(0);
		else
			return null;
	}
	
    public static List<String> getJsonResults(List<SemanticNode> nodes) {
    	if (nodes == null || nodes.size() == 0)
    		return null;
    	String split = ","; // 多个句式之间，用“,”分隔，其最外层是and关系，用and.ftl表示
    	List<StringBuffer> buffers = new ArrayList<StringBuffer>();
		
        int matchSub = 0;
        int templateSub = 0;
        Iterator iterator = new SyntacticIteratorImpl(nodes);
        
        if (!iterator.hasNext())
			return new ArrayList<String>();
        
    	while (iterator.hasNext()) {
    		matchSub++;
    		BoundaryInfos boundaryInfos = (BoundaryInfos) iterator.next();
            List<String> strs = getJsonResults(nodes, boundaryInfos.bStart, boundaryInfos.bEnd, boundaryInfos.start);
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
                	if (k==0) {
                		buffer.append(str);	// 只返回的第一个
                		templateSub++;
                	}
                	else
                		;
                }
            }
            buffers.addAll(temps);
    	}
        
        List<String> strs = new ArrayList<String>();
        if (matchSub == 0) {
        	
        } else if (matchSub == 1 || templateSub == 1) {
        	for (StringBuffer buffer : buffers)
            	strs.add(buffer.toString());
        } else if (matchSub > 1 && templateSub > 1) {
        	for (StringBuffer buffer : buffers)
        		strs.add(createTemplate("and", buffer.toString()));
        }
        
        return strs;
    }

    private static List<String> getJsonResults(List<SemanticNode> nodes, int start, int end, int lastEnd) {
    	ArrayList<String> represents = new ArrayList<String>();
        StringBuffer newstr = new StringBuffer();
        BoundaryNode bNode = (BoundaryNode) nodes.get(start);
        BoundaryNode.SyntacticPatternExtParseInfo info = bNode.getSyntacticPatternExtParseInfo(false);
        String patternId = bNode.getSyntacticPatternId();
        try {
	        // YYY1：句式匹配上KEY_VALUE, STR_INSTANCE, FREE_VAR这三种情况
	        if (BoundaryNode.getImplicitPattern(patternId) != null) {
	        	newstr.append(getSyntacticElementsStringImplicit(patternId, null, info, nodes, start, end));
	        } else { // YYY2：句式匹配上了
		        SyntacticPattern syntPtn = PhraseInfo.getSyntacticPattern(patternId);
		        newstr.append(getSyntacticElementsStringNonImplicit(syntPtn, null, info, nodes, start, end));
	        }
        } catch (Exception e) {
        	logger_.error(ExceptionUtil.getStackTrace(e));
        }
        represents.add(newstr.toString());
    	return represents;
    }
    
    public static String createTemplate(List<SemanticNode> arguments, String semanticPatternId) {
    	// 第二步
		Map<String, Object> root = new HashMap<String, Object>();
		for (int i=0; i<arguments.size(); i++)
			root.put("arg"+(i+1), arguments.get(i));
		// 第三步
		String templateFileName = "default.ftl";
		if (semanticPatternId != null && !semanticPatternId.equals(""))
			templateFileName = semanticPatternId.toLowerCase()+".ftl";
		
		return templateBuilder.createTemplate(root, templateFileName);
		
		/*try {
			if(!existsTempleteSet.contains(templateFileName)){
				logger_.info("[FTL_WARNING]找不到模板:"+templateFileName);
				return Consts.STR_BLANK;
			}
			Template t = cfg_.getTemplate(templateFileName);
			Writer out = new StringWriter();
			t.process(root, out);
			return out.toString();
		} catch (TemplateException e) {
			//不严重的错误,所以用log.info
			logger_.info("[FTL_ERROR] " + ExceptionUtil.getStackTrace(e));
		} catch (IOException e) {
			//不严重的错误,所以用log.info
			//logger_.error("找不到模板:"+templateFileName);
			logger_.info("[FTL_ERROR] " + ExceptionUtil.getStackTrace(e));
		}
		return Consts.STR_BLANK;*/
    }
    
    public static String createTemplateByPatternId(ArrayList<ArrayList<SemanticNode>> arguments, String semanticPatternId) {
		Map<String, Object> root = new HashMap<String, Object>();
		for (int i=0; i<arguments.size(); i++)
			root.put("arg"+(i+1), arguments.get(i));
		
		String templateFileName = "default.ftl";
		if (semanticPatternId != null && !semanticPatternId.equals(""))
			templateFileName = semanticPatternId.toLowerCase()+".ftl";
		
		return templateBuilder.createTemplate(root, templateFileName);
		
		/*try {
			if(existsTempleteSet.contains(templateFileName)) return "";
			Template t = cfg_.getTemplate(templateFileName);
			Writer out = new StringWriter();
			t.process(root, out);
			return out.toString();
		} catch (TemplateException e) {
			logger_.error(ExceptionUtil.getStackTrace(e));
		} catch (IOException e) {
			existsTempleteSet.add(templateFileName);
			logger_.error("找不到模板:"+templateFileName);
		}
    	return "";*/
    }
    
    public static String createTemplate(String logic, String operands) {
		Map<String, Object> root = new HashMap<String, Object>();
		root.put("operands", operands);
		String templateFileName = logic+".ftl";
		//return createTemplate(templateFileName, root);
		return templateBuilder.createTemplate(root, templateFileName);
    }
    
    public static String createTemplate(String templateFileName, Map<String, Object> root) {
    	return templateBuilder.createTemplate(root, templateFileName);
    	
    	/*try {
    		if(existsTempleteSet.contains(templateFileName)) return "";
			Template t = cfg_.getTemplate(templateFileName);
			Writer out = new StringWriter();
			t.process(root, out);
			return out.toString();
		} catch (TemplateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			existsTempleteSet.add(templateFileName);
			logger_.error("找不到模板:"+templateFileName);
		}
		return null;*/
    }
    
    // 句式匹配上KEY_VALUE, STR_INSTANCE, FREE_VAR这三种情况
    private static String getSyntacticElementsStringImplicit(String patternId,
    		SemanticPattern pattern, SyntacticPatternExtParseInfo info,
			List<SemanticNode> nodes, int bStart, int bEnd) {
    	StringBuffer newstr = new StringBuffer();
    	if (patternId.equals(IMPLICIT_PATTERN.KEY_VALUE.toString())) {
    		List<SemanticNode> arguments = new ArrayList<SemanticNode>();
			ArrayList<Integer> elelist;
			boolean isKey = true;
			for (int j = 1; (elelist = info.getElementNodePosList(j)) != null; j++) {
				for (int pos : elelist) {
					if (pos != -1) {
						if (isKey == true) {
							arguments.add(nodes.get(bStart + pos));
							isKey = false;
						} else {
							if (isStrValInstance(nodes.get(bStart + pos))) {
								StrNode strNode = getStrValInstance(nodes.get(bStart + pos));
								if (strNode != null)
									arguments.add(strNode);
							} else {
								arguments.add(nodes.get(bStart + pos));
							}
						}
					}
				}
			}
			newstr.append(createTemplate(arguments, "key_value"));
		}
    	else if (patternId.equals(IMPLICIT_PATTERN.FREE_VAR.toString())) {
    		List<SemanticNode> arguments = new ArrayList<SemanticNode>();
    		ArrayList<Integer> elelist;
			for (int j = 1; (elelist = info.getElementNodePosList(j)) != null; j++) {
				for (int pos : elelist) {
					if (pos == -1)
						continue;
					SemanticNode sNode = nodes.get(bStart + pos);
					FocusNode fNode = sNode.type == NodeType.FOCUS ? (FocusNode)sNode : null;
					ClassNodeFacade cn = fNode != null ? fNode.getIndex() : null;
					if(cn==null || cn.isBoundToIndex() )
						continue;
					
					arguments.add(fNode);
				}
			}
			if (arguments.size() == 1)
				newstr.append(createTemplate(arguments, "free_var"));
    	}
		else if (patternId.equals(IMPLICIT_PATTERN.STR_INSTANCE.toString())) {
			List<SemanticNode> arguments = new ArrayList<SemanticNode>();
			ArrayList<Integer> elelist;
			boolean isKey = true;
			for (int j = 1; (elelist = info.getElementNodePosList(j)) != null; j++) {
				for (int pos : elelist) {
					if (pos == -1 && isKey == true) {
						SemanticNode defaultNode = info.absentDefalutIndexMap.get(j);
						arguments.add(defaultNode);
						isKey = false;
					} else if (pos != -1 && isKey == false) {
						if (nodes.get(bStart + pos) == null || nodes.get(bStart + pos).isBoundToIndex() )
							continue;
						
						StrNode strNode = getStrValInstance(nodes.get(bStart + pos));
						if (strNode != null)
							arguments.add(strNode);
					}
				}
			}
			if (arguments.size() == 2)
				newstr.append(createTemplate(arguments, "str_instance"));
		}
    	return newstr.toString();
    }
    
    // 句式匹配上了，非KEY_VALUE, STR_INSTANCE, FREE_VAR
    private static String getSyntacticElementsStringNonImplicit(SyntacticPattern syntPtn,
    		SemanticPattern pattern, SyntacticPatternExtParseInfo info,
			List<SemanticNode> nodes, int bStart, int bEnd) {
    	if(syntPtn.getSemanticBind().getId() != null)
    		return null;
    	StringBuffer newstr = new StringBuffer();
    	StringBuffer buffer = new StringBuffer();
    	SemanticBind sb = syntPtn.getSemanticBind();
        String chineseRepresentation = sb.getChineseRepresentation();
        SyntacticRepresentationIteratorImpl it = new SyntacticRepresentationIteratorImpl(chineseRepresentation);
        String logic = ""; // 在同一句式中目前只支持同一逻辑关系
        while(it.hasNext()){
        	DescNode dn = it.next();
        	if (dn.getType() == DescNodeType.NUM) {
        		DescNodeNum dnn = (DescNodeNum) dn;
        		int arg = dnn.getNum();
                SemanticBindTo mainBindTo = syntPtn.getSemanticBind().getSemanticBindTo(arg);
		        //递归调用得到语义的回显
                buffer.append(getRepresentBySemantic(syntPtn, mainBindTo, info, nodes, bStart));
        	} else if (dn.getType() == DescNodeType.LOGIC) {
        		logic = dn.getText();
        		buffer.append(",");
        	} else {
        		buffer.append(dn.getText());
        	}
        }
		
        if (logic.equals("&"))
        	newstr.append(createTemplate("and", buffer.toString()));
        else if (logic.equals("|"))
        	newstr.append(createTemplate("or", buffer.toString()));
        else if (logic.equals("!"))
        	newstr.append(createTemplate("not", buffer.toString()));
        else
        	newstr.append(buffer.toString());
		return newstr.toString();
	}

    private static String getRepresentBySemantic(SyntacticPattern syntPtn,
			SemanticBindTo bindTo, SyntacticPatternExtParseInfo info, List<SemanticNode> nodes, int start) {
		SemanticPattern semPtn = PhraseInfo.getSemanticPattern(bindTo.getBindToId()+"");
		if (semPtn == null)
			return null;
		// 语义属性
		/*
		int semanticPropsClassNodeId = bindTo.getSemanticPropsClassNodeId();
		if (semanticPropsClassNodeId != -1) {
			
		}
		*/
		ArrayList<ArrayList<SemanticNode>> arguments = new ArrayList<ArrayList<SemanticNode>>();
        String representation = semPtn.getChineseRepresentation();
        int last = 0;
        int idx = representation.indexOf("$");
        while (idx != -1 && idx < representation.length() - 1) {
            String before = representation.substring(last, idx).trim();
            last = idx;
            
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
            int arg = Integer.parseInt(txt); //得到语义中的$1的标号
            
            SemanticBindToArgument sad = bindTo.getSemanticBindToArgument(arg);
            //递归调用用 先计算 #1 对应的语义
            if (sad.getSource() == Source.ELEMENT) {
				if (sad.getBindToType() == BindToType.SEMANTIC) {
					// 嵌套语义逻辑还未处理
					String nestedSemantic = getRepresentBySemantic(syntPtn, syntPtn.getSemanticBind().getSemanticBindTo(sad.getElementId()), info, nodes,start);
					ArrayList<SemanticNode> nesteds = new ArrayList<SemanticNode>();
					nesteds.add(new NestedSemanticNode(nestedSemantic));
					arguments.add(nesteds);
				} else {
					SemanticArgument argument = semPtn.getSemanticArgument(arg, false);
					ArrayList<Integer> list;
					boolean matched = false;
	
					int i = sad.getElementId();
					list = info.getElementNodePosList(i);
					if (list != null) {
						if (list.size() == 1 && list.get(0) == -1) {
							ArrayList<SemanticNode> temp = getDefaultArgument(argument, info, i, nodes, before);
							arguments.add(temp);
						} else {
							Collections.sort(list);
							ArrayList<SemanticNode> temp = getPresentArgument(argument, nodes, start, list, before);
							arguments.add(temp);
						}
						matched = true;
					}
	
					// 增加的回写逻辑：当语义需要的字段无法从句式中的对应位置获取时，使用语义中的默认值
					// 这种回写只适合值的情况，指标类型必须匹配上
					if ((argument.getType() == SemanArgType.CONSTANT || argument.getType() == SemanArgType.CONSTANTLIST)
							&& matched == false) {
						ArrayList<SemanticNode> temp = getDefaultArgument(argument, info, 0, nodes, before);
						arguments.add(temp);
					} else if ((argument.getType() == SemanArgType.INDEX || argument.getType() == SemanArgType.INDEXLIST)
							&& matched == false) {
						// 需要增加处理，这种情况下句式配置是存在问题的
					}
				}
            } else {
            	ArrayList<SemanticNode> temp = getFixedArgument(info, sad.getElementId());
            	arguments.add(temp);
            }
			last += (txt.length() + 1);
			idx = representation.indexOf("$", idx + 1);
		}
        String result = createTemplateByPatternId(arguments, bindTo.getBindToId()+"");			
		return result.toString();
	}
	
	/*
	 * 获得设定为固定值的参数
	 */
	private static ArrayList<SemanticNode> getFixedArgument(BoundaryNode.SyntacticPatternExtParseInfo info, int fixedArgumentId) {
		SemanticNode fixedNode = info.fixedArgumentMap.get(fixedArgumentId);
		ArrayList<SemanticNode> fixedArgument = new ArrayList<SemanticNode>();
		fixedArgument.add(fixedNode);
		return fixedArgument;
	}

    /*
     * 获得句式匹配上的argument的默认信息
     * INDEX\INDEXLIST:回写默认的指标
     * CONSTANT:回写默认的值
     * ANY:回写默认的值
     * 
     */
    private static ArrayList<SemanticNode> getDefaultArgument(SemanticArgument argument, BoundaryNode.SyntacticPatternExtParseInfo info,
            int elemPos, List<SemanticNode> sNodes, String before) {
    	ArrayList<SemanticNode> defaultNodes = new ArrayList<SemanticNode>();
        switch (argument.getType()) {
            case INDEX:
            case INDEXLIST:
            	//if (argument.getDefaultIndex() != null) { //可能是通过时间指代产生的默认指标,   所以这个if判断是没有用的
        		SemanticNode defaultIndexNode = info.absentDefalutIndexMap.get(elemPos);
        		defaultNodes.add(defaultIndexNode);
        		return defaultNodes;
            	//}break;
            case CONSTANT:
            case CONSTANTLIST:
                if (argument.getDefaultValue() != null) {
                	String defaultValue = argument.getDefaultValue();
				ValueType valueType = argument.getFixedValueType();
                	SemanticNode defaultValueNode = ParsePluginsUtil.getConstantSemanticNodeFromStr(defaultValue, valueType);
                	defaultNodes.add(defaultValueNode);
                }
                break;
            case ANY:
            default:
                break;
        }
        return defaultNodes;
    }

    /*
     * 获得句式匹配上的argument的相关信息：text和相应的属性修饰
     * INDEX:获得指标的text和相应的属性
     * INDEXLIST\ANY:获得list中各个node的信息
     * CONSTANT:直接将text回写
     */
    private static ArrayList<SemanticNode> getPresentArgument(SemanticArgument argument, List<SemanticNode> sNodes, int start,
            List<Integer> poses, String before) {
        if (argument == null) {
            return null;
        }
        ArrayList<SemanticNode> presentNodes = new ArrayList<SemanticNode>();
        switch (argument.getType()) {
            case INDEX:
            	SemanticNode indexNode = PhraseUtil.getNodeByPosInfo(poses.get(0), start, sNodes);
            	presentNodes.add(indexNode);
            case INDEXLIST:
                List<SemanticNode> indexNodes = PhraseUtil.getNodesByPosInfo(poses, start, sNodes);
                presentNodes.addAll(indexNodes);
            case CONSTANT:
                SemanticNode constantNode = PhraseUtil.getNodeByPosInfo(poses.get(0), start, sNodes);
                presentNodes.add(constantNode);
            case CONSTANTLIST:
            	List<SemanticNode> constantNodes = PhraseUtil.getNodesByPosInfo(poses, start, sNodes);
            	presentNodes.addAll(constantNodes);
            case ANY:
            default:
                break;
        }
        return presentNodes;
    }
    
    private static boolean isStrValInstance(SemanticNode node) {
    	if (node == null || node.isCombined == true 
    			|| (node.type != NodeType.STR_VAL && node.type != NodeType.FOCUS))
    		return false;
    	StrNode strNode = null;
        if (node.type == NodeType.STR_VAL)
            strNode = (StrNode)node;
        else if (node.type == NodeType.FOCUS) {
        	FocusNode focusNode = (FocusNode) node;
        	if (focusNode.hasString())
        		strNode = focusNode.getString();
        }
        if(strNode!=null && strNode.hasDefaultIndex())
            return true;
        return false;
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
}
