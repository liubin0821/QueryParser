/**
 * @author: 	    吴永行 
 * @dateTime:	  2014-5-12 下午8:23:45
 * @description:   	
 * 
 */
package com.myhexin.qparser.phrase.parsePlugins.bind;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import com.myhexin.qparser.define.EnumDef.Direction;
import com.myhexin.qparser.define.EnumDef.NodeType;
import com.myhexin.qparser.define.EnumDef.PropType;
import com.myhexin.qparser.define.EnumDef.Unit;
import com.myhexin.qparser.except.UnexpectedException;
import com.myhexin.qparser.iterator.BoundaryInfos;
import com.myhexin.qparser.iterator.SyntacticIteratorImpl;
import com.myhexin.qparser.node.BoundaryNode;
import com.myhexin.qparser.node.BoundaryNode.IMPLICIT_PATTERN;
import com.myhexin.qparser.node.DateNode;
import com.myhexin.qparser.node.FocusNode;
import com.myhexin.qparser.node.NumNode;
import com.myhexin.qparser.node.SemanticNode;
import com.myhexin.qparser.node.TechPeriodNode;
import com.myhexin.qparser.node.TimeNode;
import com.myhexin.qparser.onto.ClassNodeFacade;
import com.myhexin.qparser.onto.PropNodeFacade;
import com.myhexin.qparser.phrase.PhraseInfo;
import com.myhexin.qparser.phrase.SyntacticSemantic.Semantic.SemanticArgument;
import com.myhexin.qparser.phrase.SyntacticSemantic.Syntactic.SyntacticElement;
import com.myhexin.qparser.phrase.SyntacticSemantic.Syntactic.SyntacticElement.SyntElemType;
import com.myhexin.qparser.phrase.util.Consts;
import com.myhexin.qparser.tech.TechMisc;

@Component
public class BindNumOrDateToIndex1 {
	
	private static final String[] excludeBindForAnd = {"股本"};
	
	private static final String[] excludeBindForSpecialSyntactic= {"164"};
	
	private static final String[] excludeBindForSpecialIndex = {"机构净额合计"};

	// 这个isindex只是简单的判断
    private boolean isIndex(SemanticNode sn) {
        boolean rtn = false;
        if ((sn != null) && (sn.getType() == NodeType.FOCUS)) {
            FocusNode fn = (FocusNode) sn;
            if (fn.getIndex() != null) {
                rtn = true;
            }
        }
        return rtn;
    }
    
    // 判断是否为分隔符
	private boolean isSeparator(SemanticNode sn) {
		Pattern SEQUENCE_MAY_BREAK_TAG = Pattern.compile("^(\\s|,|;|\\.|。)$");
		boolean rtn = false;
		if (sn != null && SEQUENCE_MAY_BREAK_TAG.matcher(sn.getText()).matches()) {
			rtn = true;
		}
		return rtn;
	}

    /*
     * 判断是否为应该添加属性的指标
     * 
     * @param syntacticPatternId 匹配上的句式SyntacticPattern的id
     * @param syntacticElementSequence 参数SyntacticElement在列表中的sequence
     */
	private boolean isIndex(String syntacticPatternId, int syntacticElementSequence, SemanticNode sn) {
		if (BoundaryNode.getImplicitPattern(syntacticPatternId) == null) {
			// 处理非KEY_VALUE, STR_INSTANCE, FREE_VAR的情况
			// Element参数类型不为ARGUMENT，直接跳过绑定
			// Argument参数类型不为INDEX/INDEXLIST，直接跳过绑定
			SyntacticElement syntacticElement = PhraseInfo.getSyntacticPattern(syntacticPatternId).getSyntacticElement(syntacticElementSequence);
			if (syntacticElement == null || syntacticElement.getType() != SyntElemType.ARGUMENT||syntacticElement.isShouldIgnore())
				return false;
			
			SemanticArgument argument = syntacticElement.getArgument();
			if (argument != null && (argument.getType() == com.myhexin.qparser.phrase.SyntacticSemantic.Semantic.SemanticArgument.SemanArgType.INDEX || argument.getType() == com.myhexin.qparser.phrase.SyntacticSemantic.Semantic.SemanticArgument.SemanArgType.INDEXLIST))
				return true;
			else if (argument != null && argument.getType() == com.myhexin.qparser.phrase.SyntacticSemantic.Semantic.SemanticArgument.SemanArgType.ANY)
				return isIndex(sn);
			else 
				return false;
		} else if (syntacticPatternId.equals(IMPLICIT_PATTERN.KEY_VALUE.toString())) {
			// KEY_VALUE:Elem1:指标index,Elem2:值value
			if (syntacticElementSequence == 1) 
				return true;
			else
				return false;
		} else if (syntacticPatternId.equals(IMPLICIT_PATTERN.STR_INSTANCE.toString())) {
			// STR_INSTANCE:Elem1:指标index,Elem2:值value
			if (syntacticElementSequence == 1) 
				return isIndex(sn);
			else
				return false;
		} else if (syntacticPatternId.equals(IMPLICIT_PATTERN.FREE_VAR.toString())) {
			// FREE_VAR:Elem1:指标
			return true;
		} else {
			System.out.println("------");
			return false;
		}
	}
	
    /**
     * 绑定结束判断
     */
    private boolean isPropBindBreak(ArrayList<SemanticNode> list, int npos, Direction direction, BreakType breakType, BoundaryInfos boundaryInfos) {
        if(direction == Direction.LEFT && npos < 0 )
            return true;
        if(direction == Direction.RIGHT && npos >= list.size())
            return true;

        SemanticNode sn = list.get(npos);
        if(!isUsedInSyntactic(list, npos) && ";".equals(sn.getText())) {
        	return true;
        }

        if(breakType == BreakType.INDEX || breakType == BreakType.BOUNDARY) {
        	if (direction == Direction.LEFT) {
        		if (npos < boundaryInfos.bStart && isSeparator(sn) || npos == boundaryInfos.start)
        			return true;
        	} else if (direction == Direction.RIGHT) {
        		//往右的不能超过 BoundaryNode.end liuxiaofeng 2015/5/7
        		//if ((npos >= boundaryInfos.bEnd && isSeparator(sn)) || npos >= boundaryInfos.end)
        		//	return true;
        		if (npos >= boundaryInfos.bEnd)
        			return true;
        	}
        }
        if(breakType == BreakType.INDEX) {
        	//不能超过指标或者计算符号
            if(isIndex(sn)||isOperationSymbol(sn)) {
                return true;
            }
        }
		//不能通过分隔符来判断,因为分隔符在句式匹配的时候已经去掉了
		//一直往左直到前一个句式的末尾
		//一直往右知道前一个句式的开头
		if (breakType == BreakType.SEPARATOR) {
			if (direction == Direction.LEFT && sn.isBoundaryEndNode()) {
				return true;
			}
			if (direction == Direction.RIGHT && sn.isBoundaryStartNode()) {
				return true;
			}
		}
		return false;
    }

    private boolean isOperationSymbol(SemanticNode sn) {
		Pattern OPERATION_SYMBOL = Pattern.compile("^(\\+|\\-|\\*|/|加|减|乘|除)$");
		boolean rtn = false;
		if (sn != null && OPERATION_SYMBOL.matcher(sn.getText()).matches()) {
			rtn = true;
		}
		return rtn;
	
	}

	/**
     * 绑定一个指标的props
     *
     */
    private void bindIndexProps(ArrayList<SemanticNode> list, FocusNode fNode, int pos, Direction direction, BreakType breakType, BoundaryInfos boundaryInfos) {

    	//当前指标能绑定 绑定返回
    	//ClassNodeFacade index = fNode.getIndex();
    	if(fNode.getIndex()==null)
    		return;
    	
    	//fNode.setIndex(OntoFacadeUtil.copy(index));
    	ClassNodeFacade index = fNode.getIndex();
    	if(bindIndexProps(list, pos, direction, breakType, index, boundaryInfos)) {
    		return;
    	}else if(index.isBoundValueToThis()) {
    		return;
    	}
        
        //之前属性没有绑定其他信息, 时间绑定也能筛选能绑定的指标
        if(bindSomethingToIndex(index))
        	return;
        	
        //否则看看list里面其他指标是否能绑定, 能替换点当前指标
    	ArrayList<FocusNode.FocusItem> itemList = fNode.getFocusItemList();
		int j = 0,size = itemList.size();
		while (j < size) {
			FocusNode.FocusItem item = itemList.get(j++);
			if (item.getType() == FocusNode.Type.INDEX && !item.isCanDelete()) {
				ClassNodeFacade cn = item.getIndex();
				if(cn != index){
					if(bindIndexProps(list, pos, direction, breakType, cn,boundaryInfos)){
						fNode.setIndex(cn);
			        	return;
					}
				}
			}
		}
    }


    private final boolean bindSomethingToIndex(ClassNodeFacade index) {
	    
    	if(index.isBoundValueToThis())
    		return true;
    	
	    return false;
    }

	/**
     * @author: 	    吴永行 
     * @dateTime:	  2014-2-8 下午7:38:27
     * 
     */
    private boolean bindIndexProps(ArrayList<SemanticNode> list, int pos,
            Direction direction, BreakType breakType, ClassNodeFacade index, BoundaryInfos boundaryInfos) {
	    if(index == null)
            return false;
	    
	    boolean doBind = false;
        int inc = -1;
        if(direction == Direction.RIGHT) {
            inc = 1;
        }
        int npos = pos;
        SemanticNode sn = null;

        while(isPropBindBreak(list, npos, direction, breakType, boundaryInfos) == false) {
            sn = list.get(npos);
            if(bindNodeToIndexProps(sn, index,list,npos))
            	doBind=true;
            npos += inc;
        }

        if(direction == Direction.BOTH) {
            // 绑定另一个方向
            inc = 1;
            npos = pos + inc;
            while(isPropBindBreak(list, npos, direction, breakType, boundaryInfos) == false) {
                sn = list.get(npos);
                if(bindNodeToIndexProps(sn, index,list,npos))
                	doBind=true;
                npos += inc;
            }
        }
        return doBind;
    }



	/**
     * 单pattern梆定
     *
     * 遍历BoundaryNode中的ElementList来取得IndexNode的位置信息，为的是能够处理缺省指标的绑定。
     *
     * @param list SemanticNodeList
     * @param bNode boundary Node, Index们位置信息来源于此节点
     * @param direction 绑定方向
     * @param Boundary Node pos,因为BoundaryNode中的位置信息为相对位置，所以需要本参数
     * @param breakType 绑定结束边界类型
     */
    private void bindSinglePattern(ArrayList<SemanticNode> list, BoundaryNode bNode, Direction direction, BoundaryInfos boundaryInfos, BreakType breakType) {
    	BoundaryNode.SyntacticPatternExtParseInfo info = bNode.getSyntacticPatternExtParseInfo(false);
        if(info == null)
            return;

        // 绑定显式指标和默认指标
        bindNomalIndex(list, bNode, direction, boundaryInfos, breakType, info);

        if (breakType == BreakType.INDEX)
        	return ;
        
        // 对于语义固定值参数的处理
	    bindFixedArgumentIndex(list, direction, boundaryInfos, breakType, info);

        
        // 对于语义属性的处理
        bindSemanticPropsIndex(list, direction, boundaryInfos, breakType, info);
    }

	/**
     * @author: 	    吴永行 
     * @dateTime:	  2014-2-19 下午3:02:39
     * @description:  对于语义属性的处理
     * 
     */
    private final void bindSemanticPropsIndex(ArrayList<SemanticNode> list,
            Direction direction, BoundaryInfos boundaryInfos, BreakType breakType,
            BoundaryNode.SyntacticPatternExtParseInfo info) {
    	int boundaryPos = boundaryInfos.bStart;
	    for (int j = 1; j < info.semanticPropsMap.size()+1; j++) {
        	SemanticNode semanticProps = info.semanticPropsMap.get(j);
            if(!isIndex(semanticProps))
                continue;
            if(semanticProps != null && semanticProps.getType() == NodeType.FOCUS) {
                FocusNode fNode = (FocusNode)semanticProps;
                bindIndexProps(list, fNode, boundaryPos+1, direction, breakType, boundaryInfos);
            }
        }
    }

	/**
     * @author: 	    吴永行 
     * @dateTime:	  2014-2-19 下午3:00:05
     * @description:  对于语义固定值参数的处理
     */
    private final void bindFixedArgumentIndex(ArrayList<SemanticNode> list,
            Direction direction, BoundaryInfos boundaryInfos, BreakType breakType,
            BoundaryNode.SyntacticPatternExtParseInfo info) {
    	int boundaryPos = boundaryInfos.bStart;
	    for (int j = 1; j < info.fixedArgumentMap.size()+1; j++) {
        	SemanticNode fixedNode = info.fixedArgumentMap.get(j);
            if(!isIndex(fixedNode))
                continue;
            if(fixedNode != null && fixedNode.getType() == NodeType.FOCUS) {
                FocusNode fNode = (FocusNode)fixedNode;
            	List<SemanticNode> temDateNodeList = new ArrayList<SemanticNode>();
                if(list !=null && list.size() > 0) {
            		ClassNodeFacade cnode = fNode.getIndex();
                	for(SemanticNode sn : list) {
                		if(sn.getType() == NodeType.DATE  && sn.isBoundToIndex() && 
                				!ArrayUtils.contains(excludeBindForSpecialIndex, (cnode == null)?StringUtils.EMPTY : cnode.getText())) {
                				sn.setIsBoundToIndex(false); //假如是日期，优先绑定到fixedArgumentMap，如果前面已绑定，此处仍然绑定
                				temDateNodeList.add(sn);
                		}
                	}
                }
                bindIndexProps(list, fNode, boundaryPos+1, direction, breakType, boundaryInfos);
                //此处重新设置binding DateNode到指标节点，以防止计算分数出错。
                if( temDateNodeList.size() > 0) {
                	for(SemanticNode sn : temDateNodeList) {
                				sn.setIsBoundToIndex(true);
                	}
                }
            }
        }
    }
    
	/**
	 * 看看该日期是否已经绑定了其他的日期属性，存在则不需要重复绑定了。
	 * 
	 * @author huangmin
	 *
	 * @param propList
	 * @return
	 */
	private static boolean hasExistDateProp(List<PropNodeFacade> propList) {
		if(propList != null && propList.size()  > 0) {
			for (PropNodeFacade pn : propList) {
				SemanticNode snode = pn.getValue();
				if(snode != null && snode instanceof DateNode && StringUtils.isNotEmpty(snode.getText()) && !"n日".equals(pn.getText())) {
					return true;
				}
			}
		}
		
		return false;
	}

	/**
     * @author: 	    吴永行 
     * @dateTime:	  2014-2-19 下午2:51:44
     * @description:  绑定显式的正常指标 	
     * 
     */
    private final void bindNomalIndex(ArrayList<SemanticNode> list,
            BoundaryNode bNode, Direction direction, BoundaryInfos boundaryInfos,
            BreakType breakType, BoundaryNode.SyntacticPatternExtParseInfo info) {
    	int boundaryPos = boundaryInfos.bStart;
    	ArrayList<Integer> elelist;
    	int start = 0;
        for (int j = 1; (elelist = info.getElementNodePosList(j)) != null; j++) {
            for(int pos: elelist) {
                if(pos == -1) {
                	SemanticNode defaultNode = info.absentDefalutIndexMap.get(j);
                    if(!isIndex(bNode.syntacticPatternId, j, defaultNode))
                        continue;
                    if(defaultNode != null && defaultNode.getType() == NodeType.FOCUS) {
        	            FocusNode fNode = (FocusNode)defaultNode;
        	            bindIndexProps(list, fNode, boundaryPos+start+1, direction, breakType, boundaryInfos);
                    }
                    //continue;
                } else {
                	start = pos;
	                SemanticNode sn = list.get(boundaryPos + pos);
	                if(!isIndex(bNode.syntacticPatternId, j, sn))
	                    continue;
	                if(sn.getType() != NodeType.FOCUS)
	                    continue;
	                FocusNode fNode = (FocusNode)sn;
	                // skip self node
	                if(direction == Direction.LEFT) {
	                    pos -=1;
	                }  else {
	                    pos +=1;
	                }
	        		bindIndexProps(list, fNode, boundaryPos + pos, direction, breakType, boundaryInfos);
                }
            }
        }
    }

    /**
     * 单一方向的绑定 整句绑定
     * 
     * @param list
     * @param direction
     * @throws UnexpectedException
     */
    public void bindToSingleDerection(ArrayList<SemanticNode> list, Direction direction, BreakType breakType) throws UnexpectedException {
    	Iterator<BoundaryInfos> iterator = new SyntacticIteratorImpl(list);
        if (!iterator.hasNext()) {
			return;
		}
    	while (iterator.hasNext()) {
    		BoundaryInfos boundaryInfos = iterator.next();
    		BoundaryNode bNode = (BoundaryNode) list.get(boundaryInfos.bStart);
    		BoundaryNode.SyntacticPatternExtParseInfo extInfo = bNode.getSyntacticPatternExtParseInfo(false).copy();
    		bNode.setSyntacticPatternExtParseInfo(extInfo);
    		bindSinglePattern(list, bNode, direction, boundaryInfos, breakType);
    	}
    }

    public static void bindNodeToProp(SemanticNode sn, PropNodeFacade pn, SemanticNode pnParent) {
    	if (sn.getType() == NodeType.DATE && ((DateNode) sn).isCombined())
    		return;
        pn.setValue(sn);
        sn.setIsBoundToIndex(true);
        sn.setBoundToIndexProp(pnParent, pn);
    }

    private boolean bindNodeToIndexProps(SemanticNode sn, ClassNodeFacade index, ArrayList<SemanticNode> list, int npos) {
		boolean isBoundary = (sn.getType() == NodeType.BOUNDARY );
		boolean isNotBoundayIndex =  isNotBoundedIndex(sn);
		boolean isUsedInSyntactic = isUsedInSyntactic(list, npos);
		boolean isSpecicalSyntactic = isSpecicalSyntactic(sn, list); //已经绑定一些特殊的句式，就不能再绑定其他指标了

		//changed by huangmin  for 两个指标节点之间用"和，且"连接的话，假如其中一个已经绑定时间，那么另一个也需要绑定时间
    	if ( isBoundary || index == null  || (isNotBoundayIndex && !needBindToIndexForAnd(index, list)) || isUsedInSyntactic || isSpecicalSyntactic) {
            return false;
    	}
    	//changed end

    	//TODO 这段代码!!
    	if(index.getText().contains("逐笔")||index.getText().contains("一笔")){//TODO 逐笔需求:逐笔主动买单量(大于)1000手超过两次
    		if(npos > 0){
    			SemanticNode node = list.get(npos-1);
    			if(node.isFocusNode()){
    				//List<String> compList = Arrays.asList(SemanticCondCompiler.COMP_ARR);
    				if(Consts.compSignList.contains(node.getText())){
    					sn.setText(node.getText() + sn.getText());
    					node.setIsBoundToIndex(true);
    				}
    			}
    		}
    	}
    	
        return bindNodeToIndexProps(sn, index);
    }

	/**
     * @author: 	    吴永行 
     * @dateTime:	  2014-4-2 下午1:40:53
     * @description:   	
     * @param sn
     * @param index
     * @return
     * 
     */
    public static boolean bindNodeToIndexProps(SemanticNode sn, ClassNodeFacade index) {
	    List<PropNodeFacade> propList = null;
        switch (sn.getType()) {
        //测试添加
        case NUM:
        	propList = index.getClassifiedProps(PropType.LONG,PropType.DOUBLE);
        	if(propList==null) return false;
        	for(PropNodeFacade pn: propList) { 
        		if (pn.isValueProp())
    				continue;
        		if (pn.getValue() !=null )//已经绑定 
        		{
        			NumNode node1 = (NumNode)pn.getValue();
        			NumNode node2 = (NumNode)sn;
            		
            		//已经绑定过,而且是一样的Num,返回true
            		if(node1.getNuminfo()!=null && node2.getNuminfo()!=null && node1.getText()!=null && node1.getText().equals(node2.getText()) && node1.getNuminfo().equals(node2.getNuminfo())) {
            			return true;
            		}else{
        				continue;
        			}
        		}
        		//只有一个直接绑定
        		else if(propList.size()==1) {
        			bindNodeToProp(sn, pn, index);
        			index.setIsBoundValueToThis( true);
        			return true;
        		}
        		//有多个需要绑定单位
        		else if(pn.getUnits().contains(((NumNode)sn).getUnit())
        				|| ((NumNode)sn).getUnit() == Unit.UNKNOWN
        				|| (pn.getUnits()==null || pn.getUnits().size()==0 || (pn.getUnits().size()==1 && pn.getUnits().get(0)==Unit.UNKNOWN))){ // 3、单位可能有多个
        			bindNodeToProp(sn, pn, index);   
        			index.setIsBoundValueToThis ( true);
        			return true;
        		}
        	}
        	return false;
		case DATE:
        	propList = index.getClassifiedProps(PropType.DATE);
        	if(propList==null ) return false;
        	//n日在str绑定哪儿处理
        	if(sn.getText().matches(TechMisc.REGEX_N_DAY_NAME) && index.hasNDayPrpop()) return false;
        	
        	
        	//System.out.println("propList.size=" + propList.size());
            for(PropNodeFacade pn: propList) {
            	// 日期的值属性不绑定
            	if (pn.isValueProp())
    				continue;
            	if (pn.getValue() !=null )//已经绑定 
        		{
					try {
						DateNode node1 = (DateNode) pn.getValue();
						DateNode node2 = (DateNode) sn;

						//已经绑定过,而且是一样的日期,返回true
						if (node1.getDateinfo() != null && node2.getDateinfo() != null && node1.getText() != null
								&& node1.getText().equals(node2.getText())
								&& node1.getDateinfo().equals(node2.getDateinfo())) {
							/*System.out.println("BindNumOrDateToIndex1 : already bind :" + pn.toString());
							System.out.println("\t\t:" + index.toString());
							System.out.println("\t\t:" + sn.toString());*/
							return true;
						} else {
							continue;
						}
					} catch (Exception e) {
						continue;
					}
        		}
				else if(pn.isDateProp()) {
					SemanticNode boundIndex = sn.getBoundToIndex();
					SemanticNode boundIndexProp = sn.getBoundToIndexProp();
					String boundIndexPropName = StringUtils.EMPTY;
					if(boundIndexProp instanceof PropNodeFacade) {
						boundIndexPropName = ((PropNodeFacade)boundIndexProp).getText();
					}
					boolean isSameProp = isSameProp(boundIndexPropName, pn);
					if(index != null && index != boundIndex && !hasExistDateProp(index.getAllProps()) && isSameProp ) {
	                    bindNodeToProp(sn, pn, index);
	                    index.setIsBoundValueToThis( true);
					}
                    
                    //[lxf]这里不直接退出,再看有没有其他时间属性,也一起绑定
                    //比如量比,有属性交易日期和区间,交易日期和区间都应该绑上这个时间
                    //return true;
                }
            }      
			return false;
		case TIME:
			propList = index.getClassifiedProps(PropType.DATE);
			if (propList == null)
				return false;
			//n日在str绑定哪儿处理
			if (sn.getText().matches(TechMisc.REGEX_N_DAY_NAME) && index.hasNDayPrpop())
				return false;

			//System.out.println("propList.size=" + propList.size());
			for (PropNodeFacade pn : propList) {
				// 日期的值属性不绑定
				if (pn.isValueProp())
					continue;
				if (pn.getValue() != null) {//已经绑定{
					try {
						TimeNode node1 = (TimeNode) pn.getValue();
						TimeNode node2 = (TimeNode) sn;

						//已经绑定过,而且是一样的日期,返回true
						if (node1.getText() != null && node1.getText().equals(node2.getText())
								&& node1.getTimeRange().equals(node2.getTimeRange())) {

							return true;
						} else {
							continue;
						}
					} catch (Exception e) {
						continue;
					}
				} else if (pn.isDateProp()) {
					bindNodeToProp(sn, pn, index);
					index.setIsBoundValueToThis(true);
				}
			}
			return false;
		default:
			return false;
		}
    }
    
    /**
     * 日期已经绑定到了一些特殊的句式上，则不能再绑定其他的指标。
     * 
     * @param sn
     * @param list
     * @return
     */
	private boolean isSpecicalSyntactic(SemanticNode sn,
			ArrayList<SemanticNode> list) {
		if(sn.getType() != NodeType.DATE) {
			return false;
		}
		SemanticNode boundIndex = sn.getBoundToIndex();
		int boundIndexPos = -1;
		if(boundIndex != null && boundIndex instanceof ClassNodeFacade) {
			for(SemanticNode snode : list) {
				if(snode instanceof FocusNode) {
					FocusNode fnode = (FocusNode)snode;
					if(boundIndex == fnode.getIndex()) {
						boundIndexPos = list.indexOf(snode);
						break;
					}
				}
			}
		}
    	Iterator<BoundaryInfos> iterator = new SyntacticIteratorImpl(list);
        if (!iterator.hasNext()) {
			return false;
		}
    	while (iterator.hasNext()) {
    		BoundaryInfos boundaryInfos = iterator.next();
    		if(boundaryInfos.bStart < boundIndexPos && boundaryInfos.bEnd > boundIndexPos ) { //找到该指标所在的句式
        		BoundaryNode bNode = (BoundaryNode) list.get(boundaryInfos.bStart);
        		String syntacticId = bNode.getSyntacticPatternId();
        		if(ArrayUtils.contains(excludeBindForSpecialSyntactic, syntacticId)) {
        			return true;
        		}
    		}
    	}
    	
    	return false;
	}

	/**
	 * 
	 * @param boundIndexPropName
	 * @param pn
	 * @return
	 */
    private static boolean isSameProp(String boundIndexPropName,
			PropNodeFacade pn) {
    	if(StringUtils.isEmpty(boundIndexPropName)) {
    		return true;
    	}else if( "n日".equals(boundIndexPropName) && StringUtils.equals(pn.getText(), boundIndexPropName) ) {
    		return true;
    	}else if( !"n日".equals(boundIndexPropName) &&  !"n日".equals(pn.getText()) ) {
    		return true;
    	}else {
    		return false;
    	}
	}

	private final boolean isNotBoundedIndex(SemanticNode sn) { 	
		switch (sn.getType()) {
		case FOCUS:
			return !((FocusNode) sn).hasIndex() && sn.isBoundToIndex(); //focusNode没有指标,并且没有东西绑定到它
		default:
			return sn.isBoundToIndex();
		}	    
    }
	/**
	 * @author: 	    吴永行 
	 * @dateTime:	  2014-1-8 上午11:06:26
	 * @description:  该节点前面有一个BOUNDARY start 后面有一个BOUNDARY end
	 * 
	 */
	public static boolean isUsedInSyntactic(ArrayList<SemanticNode> list, int npos) {
		SemanticNode sn = list.get(npos);
		//指标被使用了 也要考虑绑定
		if (sn.getType() == NodeType.FOCUS && ((FocusNode) sn).hasIndex())
			return false;

		//前面找不到BOUNDARY start 不在同一个句式内
		int i = npos - 1;
		while (i >= 0 && list.get(i).getType() != NodeType.BOUNDARY)
			i--;
		if (i < 0 || !((BoundaryNode) list.get(i)).isStart()) {
			return false;
		}

		//STR_INSTANCE的 只有一个STR,不考虑句式
		String patternId = ((BoundaryNode) list.get(i)).getSyntacticPatternId();
		if (patternId != null
		        && (patternId.equals(IMPLICIT_PATTERN.STR_INSTANCE.toString())
		        )) {
			return false;
		}

		if (!sn.isBoundToSynt())
			return false;


		return true;
	}
	
	static class DirectionBreakType {
		public Direction d;
		public BreakType type;
		
		private DirectionBreakType(Direction d, BreakType type) {
			this.d = d;
			this.type = type;
		}
	}
	
	public final static List<DirectionBreakType> dbList = new ArrayList<DirectionBreakType>();
	static {
		dbList.add(new DirectionBreakType(Direction.LEFT, BreakType.INDEX));
		dbList.add(new DirectionBreakType(Direction.RIGHT, BreakType.INDEX));
		dbList.add(new DirectionBreakType(Direction.LEFT, BreakType.BOUNDARY));
		dbList.add(new DirectionBreakType(Direction.RIGHT, BreakType.BOUNDARY));
		dbList.add(new DirectionBreakType(Direction.LEFT, BreakType.SEPARATOR));
		dbList.add(new DirectionBreakType(Direction.RIGHT, BreakType.SEPARATOR));
		dbList.add(new DirectionBreakType(Direction.LEFT, BreakType.NONE));
		dbList.add(new DirectionBreakType(Direction.RIGHT, BreakType.NONE));
	}
	

    /**
     * prop的绑定
     * 
     * @param list
     * @throws UnexpectedException
     */
    public void bind(ArrayList<SemanticNode> list) throws UnexpectedException {
    	
		//    	for(DirectionBreakType db : dbList) {
		//    		bindToSingleDerection(list, db.d, db.type);
		//    		/*String item = db.d + "-" + db.type;
		//            String s = BindingInfoUtil.getBindingInfo(item, list);
		//            System.out.println(s);*/
		//    	}
    	//System.out.println("##################");
		bindToSingleDerection(list, Direction.LEFT, BreakType.INDEX);
        bindToSingleDerection(list, Direction.RIGHT, BreakType.INDEX);
        bindToSingleDerection(list, Direction.LEFT, BreakType.BOUNDARY);
        bindToSingleDerection(list, Direction.RIGHT, BreakType.BOUNDARY);
        bindToSingleDerection(list, Direction.LEFT, BreakType.SEPARATOR);
        bindToSingleDerection(list, Direction.RIGHT, BreakType.SEPARATOR);
        bindToSingleDerection(list, Direction.LEFT, BreakType.NONE);
		bindToSingleDerection(list, Direction.RIGHT, BreakType.NONE);
    }

    public enum BreakType {
		INDEX, BOUNDARY, SEPARATOR, NONE
    }
    
    /**
     * 如果两个指标之间用 "和，且" 连接，且其中一个指标已经绑定，那么另一个指标也需要绑定。
     * 
     * @param index 
     * 					当前需要绑定的指标节点
     * @param list 
     * 					节点列表
     * @return 
     * 				   true：需要绑定，false:不需要绑定
     */
	private boolean needBindToIndexForAnd(ClassNodeFacade index,
			ArrayList<SemanticNode> list) {
		int currentPos = -1;
		if( index !=null && list !=null && list.size() > 0) {
			for(int i = 0; i<list.size(); i++) {
				SemanticNode sn = list.get(i);
				if(sn instanceof FocusNode) {
					FocusNode fnode = (FocusNode)sn;
					ClassNodeFacade cnFacade = fnode.getIndex();
					if(index == cnFacade) { //假如是同一个对象，则获取该对象的下标
						currentPos = i;
						break;
					}
				}
			}

			if(isAnd(list,currentPos )) {
				return true;
			} else {
				return false;
			}
		}
		
		return false;
	}

	/**
	 * 判断和，且的关系，如果有和，且，则即使日期绑定到其他指标，也应该要绑定当前指标。
	 * ;
	 * @author huangmin
	 *
	 * @param list
	 * @param indexPos 
	 * @return
	 */
	private boolean isAnd(ArrayList<SemanticNode> list, int indexPos) {
		if(list == null || list.size() == 0 || indexPos < 1) {
			return false;
		}
		int bStart=0; //当前句式的开始
		int preBEnd = 0; //前面一个句式的结束
		for(int i = indexPos - 1; i>=0; i--) {
			SemanticNode sn = list.get(i);
			if(sn instanceof BoundaryNode && ((BoundaryNode)sn).isStart()) {
				bStart = i;
			}
			if(sn instanceof BoundaryNode && ((BoundaryNode)sn).isEnd()) {
				preBEnd = i;
				break;
			}
		}
		if(preBEnd == 0 || preBEnd + 1 == bStart ) {
			return false;
		}
		SemanticNode fIndexNode = list.get(bStart + 1);
		for(int j =bStart - 1; j>preBEnd; j--) {
			SemanticNode unode = list.get(j);
			String nodeText = unode.getText();
			if(("且".equals(nodeText) || "和".equals(nodeText) || "、".equals(nodeText)) && !ArrayUtils.contains(excludeBindForAnd, fIndexNode.getText())) {
				return true;
			}
		}
		
		return false;
	}
}