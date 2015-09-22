package com.myhexin.qparser.phrase.parsePlugins;

import java.util.ArrayList;
import java.util.Iterator;

import com.myhexin.qparser.Query;
import com.myhexin.qparser.date.DateCompute;
import com.myhexin.qparser.date.DateRange;
import com.myhexin.qparser.define.EnumDef.NodeType;
import com.myhexin.qparser.except.NotSupportedException;
import com.myhexin.qparser.except.UnexpectedException;
import com.myhexin.qparser.iterator.BoundaryInfos;
import com.myhexin.qparser.iterator.SyntacticIteratorImpl;
import com.myhexin.qparser.node.BoundaryNode;
import com.myhexin.qparser.node.Environment;
import com.myhexin.qparser.node.FocusNode;
import com.myhexin.qparser.node.SemanticNode;
import com.myhexin.qparser.node.UnknownNode;
import com.myhexin.qparser.number.NumParser;
import com.myhexin.qparser.number.NumRange;
import com.myhexin.qparser.onto.ClassNodeFacade;
import com.myhexin.qparser.onto.OntoFacadeUtil;
import com.myhexin.qparser.phrase.ParserAnnotation;
import com.myhexin.qparser.phrase.PhraseInfo;
import com.myhexin.qparser.phrase.SyntacticSemantic.Semantic.SemanticArgument;
import com.myhexin.qparser.phrase.SyntacticSemantic.Semantic.SemanticArgument.SemanArgType;
import com.myhexin.qparser.phrase.SyntacticSemantic.Semantic.SemanticArgument.ValueType;
import com.myhexin.qparser.phrase.SyntacticSemantic.Syntactic.SyntacticPattern;

public class PhraseParserPluginBuildFixedArgumentsAndSemanticProps extends PhraseParserPluginAbstract{
	
	public static org.slf4j.Logger logger_ = org.slf4j.LoggerFactory.getLogger(PhraseParserPluginBuildFixedArgumentsAndSemanticProps.class.getName());
	
    public PhraseParserPluginBuildFixedArgumentsAndSemanticProps() {
        super("Build_Fixed_Arguments_And_Semantic_Props");
    }
    
	public ArrayList<ArrayList<SemanticNode>> process(ParserAnnotation annotation) {
		
		Environment ENV = annotation.getEnv();
		ArrayList<SemanticNode> nodes = annotation.getNodes();
		return buildFixedArgumentsAndSemanticProps(nodes,ENV);
    }
    
    /**
     * 构建固定值类型的参数和语义属性
     * 1、构建固定值类型的参数
     * 2、构建语义属性
     */
    public ArrayList<ArrayList<SemanticNode>> buildFixedArgumentsAndSemanticProps(ArrayList<SemanticNode> nodes,Environment ENV) {
    	if (nodes == null || nodes.size() == 0)
    		return null;
    	
    	ArrayList<ArrayList<SemanticNode>> tlist = new ArrayList<ArrayList<SemanticNode>>(1);
        Iterator iterator = new SyntacticIteratorImpl(nodes);
        if (!iterator.hasNext()) {
			tlist.add(nodes);
			return tlist; // 没有句式
		}
        
    	while (iterator.hasNext()) {
    		BoundaryInfos boundaryInfos = (BoundaryInfos) iterator.next();
    		BoundaryNode bNode = (BoundaryNode) nodes.get(boundaryInfos.bStart);
    		String patternId = bNode.getSyntacticPatternId();
			if (BoundaryNode.getImplicitPattern(patternId) != null) {
				continue;
			} else {
				BoundaryNode.SyntacticPatternExtParseInfo extInfo = bNode.getSyntacticPatternExtParseInfo(true);
				SyntacticPattern syntPtn = PhraseInfo.getSyntacticPattern(patternId);
		        for (int j = 1; j < syntPtn.getFixedArgumentMax(); j++) {
		        	SemanticArgument arg = syntPtn.getFixedArgument(j).getArgument();
		        	SemanticNode fixedNode = getNodeForFixedArgument(arg, (Query.Type)ENV.get("qType",false));
	                if (fixedNode != null) {
	                    extInfo.fixedArgumentMap.put(j, fixedNode);
	                }
		        }
		        for (int j = 1; j < syntPtn.getSemanticPropsClassNodeMax(); j++) {
		        	try {
		        	ClassNodeFacade classNodeFacade = syntPtn.getSemanticPropsClassNode(j);
			        	if (classNodeFacade != null) {
			        			classNodeFacade = OntoFacadeUtil.copy(classNodeFacade); //.copy();
			        		FocusNode focusNode = new FocusNode();
			        		focusNode.setText(classNodeFacade.getText() );
			        		focusNode.type = NodeType.FOCUS;
			        		focusNode.setIndex(classNodeFacade);
			        		focusNode.addFocusItem(FocusNode.Type.INDEX, classNodeFacade.getText(), classNodeFacade);
			        		extInfo.semanticPropsMap.put(j, focusNode);
			        	}
		        	} catch (UnexpectedException e) {
		        		logger_.error(e.getMessage());
		        	}
		        }
			}
    	}
    	
		tlist.add(nodes);
		return tlist;
    }
    
    private SemanticNode getNodeForFixedArgument(SemanticArgument arg, Query.Type qType) {
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
}

