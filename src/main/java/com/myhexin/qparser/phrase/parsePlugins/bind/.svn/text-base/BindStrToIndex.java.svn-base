/**
 * @author: 	    吴永行 
 * @dateTime:	  2014-5-12 下午8:21:39
 * @description:   	
 * 
 */
package com.myhexin.qparser.phrase.parsePlugins.bind;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Component;

import com.myhexin.qparser.Query;
import com.myhexin.qparser.date.DatePatterns;
import com.myhexin.qparser.date.DateUtil;
import com.myhexin.qparser.define.EnumDef.Direction;
import com.myhexin.qparser.define.EnumDef.NodeType;
import com.myhexin.qparser.define.EnumDef.PropType;
import com.myhexin.qparser.except.NotSupportedException;
import com.myhexin.qparser.except.UnexpectedException;
import com.myhexin.qparser.node.BoundaryNode;
import com.myhexin.qparser.node.BoundaryNode.IMPLICIT_PATTERN;
import com.myhexin.qparser.node.DateNode;
import com.myhexin.qparser.node.Environment;
import com.myhexin.qparser.node.FocusNode;
import com.myhexin.qparser.node.SemanticNode;
import com.myhexin.qparser.node.StrNode;
import com.myhexin.qparser.node.TechPeriodNode;
import com.myhexin.qparser.onto.ClassNodeFacade;
import com.myhexin.qparser.onto.MemOnto;
import com.myhexin.qparser.onto.PropNodeFacade;
import com.myhexin.qparser.phrase.PhraseInfo;
import com.myhexin.qparser.phrase.SyntacticSemantic.Semantic.SemanticArgument;
import com.myhexin.qparser.phrase.SyntacticSemantic.Syntactic.SyntacticElement;
import com.myhexin.qparser.phrase.SyntacticSemantic.Syntactic.SyntacticElement.SyntElemType;
import com.myhexin.qparser.phrase.parsePlugins.bind.BindNumOrDateToIndex1.BreakType;
import com.myhexin.qparser.phrase.parsePlugins.bind.BindNumOrDateToIndex1.DirectionBreakType;
import com.myhexin.qparser.tech.TechMisc;

@Component
public class BindStrToIndex {
	

	// 这个isindex只是简单的判断
    private boolean isIndex(SemanticNode sn) {
        boolean rtn = false;
        if ((sn != null) && (sn.type == NodeType.FOCUS)) {
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
    private boolean isPropBindBreak(ArrayList<SemanticNode> list, int npos, Direction direction, BreakType breakType) {
        if(direction == Direction.LEFT && npos < 0 )
            return true;
        if(direction == Direction.RIGHT && npos >= list.size())
            return true;

        SemanticNode sn = list.get(npos);

        if(breakType == BreakType.INDEX || breakType == BreakType.BOUNDARY) {
            if(sn.type == NodeType.BOUNDARY) {
                BoundaryNode bNode = (BoundaryNode)sn;
                if(direction == Direction.LEFT) {
                    if(bNode.isStart())
                        return true;
                } else {
                	if(bNode.isEnd())
                        return true;
                }
            }
        }
        if(breakType == BreakType.INDEX) {
            if(isIndex(sn)) {
                return true;
            }
        }
        if (breakType == BreakType.SEPARATOR) {
        	if(isSeparator(sn)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 绑定一个指标的props
     *
     */
    private void bindIndexProps(Environment ENV,ArrayList<SemanticNode> list, FocusNode fNode, int pos, Direction direction, BreakType breakType) {

    	//当前指标能绑定 绑定返回
    	ClassNodeFacade index = fNode.getIndex();
    	if(index==null)
    		return;
    	
        if(bindIndexProps(ENV,list, pos, direction, breakType, index, fNode) || index.isBoundValueToThis())
        	return;
        
        //否则看看list里面其他指标是否能绑定, 能替换点当前指标
    	ArrayList<FocusNode.FocusItem> itemList = fNode.getFocusItemList();
		int j = 0,size = itemList.size();
		while (j < size) {
			FocusNode.FocusItem item = itemList.get(j++);
			if (item.getType() == FocusNode.Type.INDEX && !item.isCanDelete()) {
				ClassNodeFacade cn = item.getIndex();
				if(cn != index){
					if(bindIndexProps(ENV,list, pos, direction, breakType, cn, fNode)){
						fNode.setIndex(cn);
			        	return;
					}
				}
			}
		}
    	
    }

	/**
     * @author: 	    吴永行 
	 * @param fNode 
     * @dateTime:	  2014-2-8 下午7:38:27
     * 
     */
    private boolean bindIndexProps(Environment ENV,ArrayList<SemanticNode> list, int pos,
            Direction direction, BreakType breakType, ClassNodeFacade index, FocusNode fNode) {
	    if(index == null)
            return false;
	    
	    boolean doBind = false;
        int inc = -1;
        if(direction == Direction.RIGHT) {
            inc = 1;
        }
        int npos = pos;
        SemanticNode sn = null;
        SemanticNode tsn = null;

        while(isPropBindBreak(list, npos, direction, breakType) == false) {
            //临时方案
        	sn=getPossibleTechperiod(ENV,list,npos, direction, breakType);
        	tsn = list.get(npos);
        	if(sn != null){
				list.set(npos, sn);
        		if(bindNodeToIndexProps(sn, index,list,npos, fNode)) {
	        		//设置原来节点为 被绑定了
					setOldNodeIsBoundToIndex(sn);
					doBind = true;
        		}
        	}
        	
//        	sn = list.get(npos);
            if(bindNodeToIndexProps(tsn, index,list,npos, fNode))
            	doBind=true;
            npos += inc;
        }

        if(direction == Direction.BOTH) {
            // 绑定另一个方向
            inc = 1;
            npos = pos + inc;
            while(isPropBindBreak(list, npos, direction, breakType) == false) {
                //临时方案
            	sn=getPossibleTechperiod(ENV,list,npos, direction, breakType);
				if (sn != null && bindNodeToIndexProps(sn, index, list, npos, fNode)) {
					setOldNodeIsBoundToIndex(sn);
					doBind = true;
					list.set(npos, sn);
				}
                if(bindNodeToIndexProps(tsn, index,list,npos, fNode))
                	doBind=true;
                npos += inc;
            }
        }
        return doBind;
    }

	/**
     * @author: 	    吴永行 
     * @dateTime:	  2014-3-25 下午6:55:31
     * @description:   	
     * @param sn
     * 
     */
    private void setOldNodeIsBoundToIndex(SemanticNode sn) {
	    for (SemanticNode oldNode : ((TechPeriodNode) sn).oldNodes) {
	    	oldNode.setIsBoundToIndex(true);
	    	//oldNode.setBoundToIndexProp(pn);
	    	if(oldNode.type == NodeType.UNKNOWN)
	    		oldNode.type = NodeType.COMBINED;
	    }
    }
    
    /**
     * 解决分析周期识别问题，例句如下：
     * a.周macd金叉。【done】
     * b.macd月线金叉。【done】
     * c.5周线金叉10周线。【doing】
     * d.60分钟macd金叉。【doing】
     */
    private TechPeriodNode getPossibleTechperiod(Environment ENV,
    		ArrayList<SemanticNode> nodes,int pos, Direction direction, BreakType breakType){
		SemanticNode sn = nodes.get(pos);
		try {
			switch (sn.getType()) {
			case UNKNOWN:
	            if (sn.getText().matches(TechMisc.TECHPERIOD_REGEX)) {             	
	                TechPeriodNode techPeriodNode = new TechPeriodNode(String.format("%s%s", sn.getText(),TechMisc.LINE_CHINESE));
	                Collection collection = MemOnto.getOntoC(TechMisc.TECH_ANALY_PERIOD, PropNodeFacade.class, (Query.Type) ENV.get("qType",false));
	                if (collection != null && collection.isEmpty() == false)
		                techPeriodNode.ofwhat.addAll(collection);      
	                techPeriodNode.oldNodes.add(sn);
	                return techPeriodNode;
	            }
	            
			case DATE:
			case TIME:
	            if (DatePatterns.MUNITE.matcher(sn.getText()).matches()) {
	                TechPeriodNode techPeriodNode = new TechPeriodNode(String.format("%s", sn.getText()));
	                techPeriodNode.setIsBoundToIndex(sn.isBoundToIndex());
	                Collection collection = MemOnto.getOntoC(TechMisc.TECH_ANALY_PERIOD, PropNodeFacade.class, (Query.Type) ENV.get("qType",false));
	                if (collection != null && collection.isEmpty() == false)
		                techPeriodNode.ofwhat.addAll(collection);
	                techPeriodNode.oldNodes.add(sn);
	                //60分钟K线图
	                if(++pos<nodes.size() && TechMisc.K_LINE_WORDS.matcher(nodes.get(pos).getText()).matches()){
	                	techPeriodNode.setText(techPeriodNode.getText() + nodes.get(pos).getText());
	                	techPeriodNode.oldNodes.add(nodes.get(pos));
	                }
	                return techPeriodNode;
	            } else if (DatePatterns.REGEX_WEEK_MONTH.matcher(sn.getText()).matches()) {
	                for (int p = pos + 1; p < nodes.size(); p++) {
	                	 if(isPropBindBreak(nodes, p, direction, breakType))
		                    	break;
	                	 
	                    SemanticNode rightNode = nodes.get(p);
	                    if (rightNode.type == NodeType.UNKNOWN || rightNode.type == NodeType.BOUNDARY) {
	                        continue;
	                    } else if (rightNode.type == NodeType.FOCUS) {
	                        FocusNode fn = (FocusNode) rightNode;
	                        ClassNodeFacade cn = fn.getIndex();
	                        if (cn != null && cn.getText().matches(TechMisc.MA_TECH_INST_NAME)) {
	                        	//分析周期的逻辑是不正确的
	                        //if (cn != null && cn.hasProp("分析周期")) {
	                            Matcher matcher = DatePatterns.REGEX_WEEK_MONTH.matcher(sn.getText());
	                            matcher.matches();
	                            // 分析周期改为周  而不是周线 
	                            TechPeriodNode techPeriodNode = new TechPeriodNode(String.format("%s%s",
                                        matcher.group(2),TechMisc.LINE_CHINESE));
	                            Collection collection = MemOnto.getOntoC(TechMisc.TECH_ANALY_PERIOD, PropNodeFacade.class, (Query.Type) ENV.get("qType",false));
	        	                if (collection != null && collection.isEmpty() == false)
	        		                techPeriodNode.ofwhat.addAll(collection);
	        	                String nday = String.format("%s日", matcher.group(1));
	                            DateNode dn = null;
                                try {
                                	dn = DateUtil.getDateNodeFromStr("近"+nday, null);
                                } catch (NotSupportedException e) {	                                
	                                e.printStackTrace();
                                }
                                dn.setText(nday);
                                nodes.set(pos, dn);
                                
                                if(!cn.hasProp("n日"))
                                	nodes.set(pos, techPeriodNode);
                                
	                            return techPeriodNode;
	                        }
	                        continue;
	                    } else {
	                        break;
	                    }
	                }
	            }	        				
			default:
				break;
			}
		} catch (UnexpectedException e) {           
            e.printStackTrace();
        }    	
    	return null;
    	
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
    private void bindSinglePattern(Environment ENV,ArrayList<SemanticNode> list, BoundaryNode bNode, Direction direction, int boundaryPos, BreakType breakType) {
        BoundaryNode.SyntacticPatternExtParseInfo info = bNode.getSyntacticPatternExtParseInfo(false);
        if(info == null)
            return;

        // 绑定显式指标和缺省指标
        bindNomalIndex(ENV,list, bNode, direction, boundaryPos, breakType, info);

        if (breakType == BreakType.INDEX)
        	return ;
        
        // 对于语义固定值参数的处理
        bindFixedArgumentIndex(ENV,list, direction, boundaryPos, breakType, info);
        
        // 对于语义属性的处理
        bindSemanticPropsIndex(ENV,list, direction, boundaryPos, breakType, info);
    }

	/**
     * @author: 	    吴永行 
     * @dateTime:	  2014-2-19 下午3:02:39
     * @description:  对于语义属性的处理
     * 
     */
    private final void bindSemanticPropsIndex(Environment ENV,ArrayList<SemanticNode> list,
            Direction direction, int boundaryPos, BreakType breakType,
            BoundaryNode.SyntacticPatternExtParseInfo info) {
	    for (int j = 1; j < info.semanticPropsMap.size()+1; j++) {
        	SemanticNode semanticProps = info.semanticPropsMap.get(j);
            if(!isIndex(semanticProps))
                continue;
            if(semanticProps != null && semanticProps.type == NodeType.FOCUS) {
                FocusNode fNode = (FocusNode)semanticProps;
                bindIndexProps(ENV,list, fNode, boundaryPos+1, direction, breakType);
            }
        }
    }

	/**
     * @author: 	    吴永行 
     * @dateTime:	  2014-2-19 下午3:00:05
     * @description:  对于语义固定值参数的处理
     */
    private final void bindFixedArgumentIndex(Environment ENV,ArrayList<SemanticNode> list,
            Direction direction, int boundaryPos, BreakType breakType,
            BoundaryNode.SyntacticPatternExtParseInfo info) {
	    for (int j = 1; j < info.fixedArgumentMap.size()+1; j++) {
        	SemanticNode fixedNode = info.fixedArgumentMap.get(j);
            if(!isIndex(fixedNode))
                continue;
            if(fixedNode != null && fixedNode.type == NodeType.FOCUS) {
                FocusNode fNode = (FocusNode)fixedNode;
                bindIndexProps(ENV,list, fNode, boundaryPos+1, direction, breakType);
            }
        }
    }

	/**
     * @author: 	    吴永行 
     * @dateTime:	  2014-2-19 下午2:51:44
     * @description:  绑定显式的正常指标 	
     * 
     */
    private final void bindNomalIndex(Environment ENV,
    		ArrayList<SemanticNode> list,
            BoundaryNode bNode, Direction direction, int boundaryPos,
            BreakType breakType, BoundaryNode.SyntacticPatternExtParseInfo info) {
    	ArrayList<Integer> elelist;
    	int start = 0;
        for (int j = 1; (elelist = info.getElementNodePosList(j)) != null; j++) {
            for(int pos: elelist) {
                if(pos == -1) {
                	SemanticNode defaultNode = info.absentDefalutIndexMap.get(j);
                    if(!isIndex(bNode.syntacticPatternId, j, defaultNode))
                        continue;
                    if(defaultNode != null && defaultNode.type == NodeType.FOCUS) {
        	            FocusNode fNode = (FocusNode)defaultNode;
        	            bindIndexProps(ENV,list, fNode, boundaryPos+start+1, direction, breakType);
                    }
                    //continue;
                } else {
                	start = pos;
	                SemanticNode sn = list.get(boundaryPos + pos);
	                if(!isIndex(bNode.syntacticPatternId, j, sn))
	                    continue;
	                if(sn.type != NodeType.FOCUS)
	                    continue;
	                FocusNode fNode = (FocusNode)sn;
	                // skip self node
	                if(direction == Direction.LEFT)
	                    pos -=1;
	                else
	                    pos +=1;
	                bindIndexProps(ENV,list, fNode, boundaryPos + pos, direction, breakType);
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
    public void bindToSingleDerection(Environment ENV,ArrayList<SemanticNode> list, Direction direction, BreakType breakType) throws UnexpectedException {
        for (int i = 0; i < list.size(); i++) {
            SemanticNode sn = list.get(i);
            if(sn.type != NodeType.BOUNDARY)
                continue;

            BoundaryNode bNode = (BoundaryNode) sn;
            if(!bNode.isStart())
                continue;
            bindSinglePattern(ENV,list, bNode, direction, i, breakType);
        }
    }

    public static void bindNodeToProp(SemanticNode sn, PropNodeFacade pn, SemanticNode pnParent) {
    	if (sn.type == NodeType.DATE && ((DateNode) sn).isCombined)
    		return;
        pn.setValue(sn);
        sn.setIsBoundToIndex( true);
        sn.setBoundToIndexProp(pnParent, pn);
    }

    private boolean bindNodeToIndexProps(SemanticNode sn, ClassNodeFacade index, ArrayList<SemanticNode> list, int npos, FocusNode fNode) {
		if ( sn.type == NodeType.BOUNDARY || index == null 
			  || isNotBoundedIndex(sn) || isUsedInSyntactic(list, npos,index) || excludeDate(sn, fNode, list))
            return false;
		

        return bindNodeToIndexProps(sn, index);
    }

	/**
     * @author: 	    吴永行 
     * @dateTime:	  2014-4-2 下午1:30:56
     * @description:   	
     * @param sn
     * @param index
     * @return
     * 
     */
    public static boolean bindNodeToIndexProps(SemanticNode sn, ClassNodeFacade index) {
	    List<PropNodeFacade> propList = null;
        switch (sn.getType()) {
		case STR_VAL:
			if(bindStrNodeToProps(sn, index.getClassifiedProps(PropType.STR), (StrNode) sn, index)){ 
				index.setIsBoundValueToThis(true);
				return true;
			}
			return false;
		case FOCUS:
        	if(((FocusNode)sn).hasString())
	        	if(bindStrNodeToProps(sn, index.getClassifiedProps(PropType.STR), ((FocusNode)sn).getString(), index)){ 
	        		index.setIsBoundValueToThis(true);
	        		return true;   
	        	}
        	return false;
		case DATE:
        	//临时处理 均线的 n日    不是时间频率
        	if(sn.getText().matches(TechMisc.REGEX_N_DAY_NAME) && ((DateNode)sn).getFrequencyInfo() == null ){     		
        		for(PropNodeFacade pn: index.getClassifiedProps(PropType.DATE)){
        			if(pn.isNDate()){
        				if (pn.getValue() !=null)//已经绑定 
        					continue;
        				
        				bindNodeToProp(sn, pn, index);
        				index.setIsBoundValueToThis(true);
                        return true;
        			}
        		}
        	}
        	return false;
		case TECH_PERIOD:
        	propList = index.getClassifiedProps(PropType.TECH_PERIOD);
        	if(propList==null) return false;
            TechPeriodNode techPeriodNode = (TechPeriodNode) sn;
            for (PropNodeFacade pn : propList) {
            	if (pn.isValueProp())
    				continue;
            	if (pn.getValue() !=null)//已经绑定 
					continue;
            	
                if (pn.getText().matches(TechMisc.TECH_ANALY_PERIOD)) {
                    for (SemanticNode ofwhat : techPeriodNode.ofwhat) {
                        if (ofwhat.type == NodeType.PROP && pn.isTextEqual((PropNodeFacade) ofwhat)) {
                            bindNodeToProp(sn, pn, index);
                            index.setIsBoundValueToThis(true);
                            return true;
                        }
                    }
                }
            }      
            return false;
		case CONSIST_PERIOD:
			propList = index.getClassifiedProps(PropType.CONSIST_PERIOD);
			if (propList == null)
				return false;
			for (PropNodeFacade pn : propList) {
				if (pn.isValueProp())
					continue;
				if (pn.getValue() != null)//已经绑定 
					continue;

				if (pn.getText().matches(TechMisc.TECH_CONSIST_PERIOD)) {
					bindNodeToProp(sn, pn, index);
					index.setIsBoundValueToThis(true);
					return true;
				}
			}
			return false;
		default:
			return false;
		}
    }
    
    private final boolean isNotBoundedIndex(SemanticNode sn) { 	
		switch (sn.getType()) {
		case FOCUS:
			return !((FocusNode) sn).hasIndex() && sn.isBoundToIndex();
		default:
			return sn.isBoundToIndex();
		}	    
    }

	/**
	 * @author: 	    吴永行 
	 * @param index 
	 * @dateTime:	  2014-1-8 上午11:06:26
	 * @description:  该节点前面有一个BOUNDARY start 后面有一个BOUNDARY end
	 * 
	 */
	private boolean isUsedInSyntactic(ArrayList<SemanticNode> list, int npos, ClassNodeFacade index) {
		SemanticNode sn = list.get(npos);
		//指标被使用了 也要考虑绑定
		if (sn.type == NodeType.FOCUS && ((FocusNode) sn).hasIndex())
			return false;

		//前面找不到BOUNDARY start 不在同一个句式内
		int i = npos - 1;
		while (i >= 0 && list.get(i).type != NodeType.BOUNDARY)
			i--;
		if (i < 0 || !((BoundaryNode) list.get(i)).isStart()) {
			return false;
		}

		//STR_INSTANCE的 只有一个STR,不考虑句式
		BoundaryNode bn = (BoundaryNode) list.get(i);
		String patternId = bn.getSyntacticPatternId();
		if (patternId != null && (patternId.equals(IMPLICIT_PATTERN.STR_INSTANCE.toString()) )) {
			//默认指标
			for(SemanticNode cn : bn.getSyntacticPatternExtParseInfo(false).absentDefalutIndexMap.values())
				if(cn.getText().equals(index.getText()))
					return true;
			return false;
		}

		if (!sn.isBoundToSynt)
			return false;


		return true;
	}

	/**
	 * @author: 	    吴永行 
	 * @dateTime:	  2013-12-31 上午11:25:53
	 * @description:   	
	 * 
	 */
	private static boolean bindStrNodeToProps(SemanticNode sn,
			List<PropNodeFacade> propList, StrNode stringNode, SemanticNode pnParent) {
		
		if(stringNode == null || propList == null) return false;
		if(stringNode.isBoundToIndex()) return false;//已经被绑定
		
		for (PropNodeFacade pn : propList) {
			if (pn.isValueProp())
				continue;
    		if (pn.getValue() !=null) 
				continue;
			
		    if (pn.isStrProp()) {
		    	
		    	for(String st : stringNode.subType)
		    		if(pn.getSubType().contains(st)){
		    			bindFocuStrNodeToPro(sn, pn, pnParent);
		                return true;
		    		}
		    }
		}
		return false;
	}

    private static final void bindFocuStrNodeToPro(SemanticNode sn, PropNodeFacade pn, SemanticNode pnParent) {
	    if (sn.type==NodeType.FOCUS) {
	    	sn.setIsBoundToIndex(true);
	    	sn.setBoundToIndexProp(pnParent, pn);
	    	
	    	bindNodeToProp(((FocusNode)sn).getString(), pn, pnParent);
	    	return ;
	    }
	    bindNodeToProp(sn, pn, pnParent);
    }
    
    /**
     * 排除那些不是属于均线指标的日期属性。
     * 
     * @param sn
     * @param fNode
     * @param list
     * @return
     */
	private boolean excludeDate(SemanticNode sn, FocusNode fNode,
			ArrayList<SemanticNode> list) {
		if(sn instanceof DateNode || sn instanceof TechPeriodNode) {
			int datePos = list.indexOf(sn);
			int indexPos = list.indexOf(fNode);
			//该指标节点存在于absentDefalutIndexMap或者fixedArgumentMap中，则以该boundarynode的位置作为起始或者结束节点
			if(indexPos == -1) {
		        for (int i = 0; i < list.size(); i++) {
		            SemanticNode tsn = list.get(i);
		            if(tsn.type != NodeType.BOUNDARY  || ((BoundaryNode)tsn).isEnd()) {
		                continue;
		            }
		            BoundaryNode bNode = (BoundaryNode) tsn; //是边界节点的开始节点
		            BoundaryNode.SyntacticPatternExtParseInfo info = bNode.getSyntacticPatternExtParseInfo(false);
		            Map<Integer, SemanticNode> absentDefalutIndexMap = info.absentDefalutIndexMap;
		            Map<Integer, SemanticNode> fixedArgumentMap = info.fixedArgumentMap;
		            Map<Integer, SemanticNode> semanticPropsMap = info.semanticPropsMap;
		            if(isFoundIndex(absentDefalutIndexMap,fNode) || isFoundIndex(fixedArgumentMap,fNode) || isFoundIndex(semanticPropsMap,fNode)) {
		            	indexPos = list.indexOf(bNode);
		            	break;
		            }
		        }
			}
			
			if(indexPos > 0 && (datePos > indexPos + 1 || indexPos > datePos + 1)) { //要绑定的时间节点在被绑定的指标节点前面或者后面，且用分隔符分隔，则不绑定
				int endPos = datePos;
				int startPos = indexPos;
				if(indexPos > datePos) {
					endPos = indexPos;
					startPos = datePos;
				}
				for(int i = endPos - 1; i > startPos; i--) {
					SemanticNode tsn = list.get(i);
					if(isSeparator(tsn)) {
						return true;
					}
				}
			}
		}
		
		return false;
	}
	
	/**
	 * 看看这些这些map是否含有需要寻找的指标。
	 * 
	 * @author huangmin
	 *
	 * @param boundMap
	 * @param fNode 
	 * @return
	 */
    private boolean isFoundIndex(Map<Integer, SemanticNode> boundMap, FocusNode fNode) {
        if(boundMap != null && boundMap.size() > 0) {
        	for(Map.Entry<Integer, SemanticNode> entry : boundMap.entrySet()) {
        		SemanticNode snode = entry.getValue();
        		if(snode != null && snode.type == NodeType.FOCUS  && ((FocusNode)snode).hasIndex()) {
        			FocusNode tfNode = (FocusNode)snode;
        			if(tfNode == fNode) { //找到该指标
        				return true;
        			}
        		}
        	}
        }
        
        return false;
	}

	/**
     * prop的绑定
     * 
     * @param list
     * @throws UnexpectedException
     */
    public void bind(Environment ENV,ArrayList<SemanticNode> list) throws UnexpectedException {
    	
    	for(DirectionBreakType db : BindNumOrDateToIndex1.dbList ) {
    		bindToSingleDerection(ENV,list, db.d, db.type);
    		/*String item = "[BindStrToIndex]" + db.d + "-" + db.type;
            String s = BindingInfoUtil.getBindingInfo(item, list);
            System.out.println(s);*/
    	}
    	
        /*bindToSingleDerection(ENV,list, Direction.LEFT, BreakType.INDEX);
        bindToSingleDerection(ENV,list, Direction.RIGHT, BreakType.INDEX);
        bindToSingleDerection(ENV,list, Direction.LEFT, BreakType.BOUNDARY);
        bindToSingleDerection(ENV,list, Direction.RIGHT, BreakType.BOUNDARY);
        bindToSingleDerection(ENV,list, Direction.LEFT, BreakType.SEPARATOR);
        bindToSingleDerection(ENV,list, Direction.RIGHT, BreakType.SEPARATOR);
        bindToSingleDerection(ENV,list, Direction.LEFT, BreakType.NONE);
        bindToSingleDerection(ENV,list, Direction.RIGHT, BreakType.NONE);*/
    }

    /*private enum BreakType {
		INDEX, BOUNDARY, SEPARATOR, NONE
    }*/
}