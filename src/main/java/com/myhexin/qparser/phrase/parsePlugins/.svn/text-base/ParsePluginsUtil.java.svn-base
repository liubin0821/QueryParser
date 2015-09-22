/**
 * @author: 	    吴永行 
 * @dateTime:	  2013-12-6 下午5:43:34
 * @description:   	
 * 
 */
package com.myhexin.qparser.phrase.parsePlugins;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.regex.Matcher;

import com.myhexin.qparser.Query;
import com.myhexin.qparser.date.DateCompute;
import com.myhexin.qparser.date.DatePatterns;
import com.myhexin.qparser.date.DateRange;
import com.myhexin.qparser.define.EnumDef.NodeType;
import com.myhexin.qparser.except.NotSupportedException;
import com.myhexin.qparser.except.UnexpectedException;
import com.myhexin.qparser.node.DateNode;
import com.myhexin.qparser.node.FocusNode;
import com.myhexin.qparser.node.LogicNode;
import com.myhexin.qparser.node.NodeUtil;
import com.myhexin.qparser.node.NumNode;
import com.myhexin.qparser.node.SemanticNode;
import com.myhexin.qparser.node.StrNode;
import com.myhexin.qparser.node.TechPeriodNode;
import com.myhexin.qparser.node.UnknownNode;
import com.myhexin.qparser.number.NumParser;
import com.myhexin.qparser.number.NumRange;
import com.myhexin.qparser.onto.ClassNodeFacade;
import com.myhexin.qparser.onto.MemOnto;
import com.myhexin.qparser.onto.PropNodeFacade;
import com.myhexin.qparser.phrase.PhraseInfo;
import com.myhexin.qparser.phrase.PhraseInfo.WordsInfo;
import com.myhexin.qparser.phrase.SyntacticSemantic.Semantic.SemanticArgument.ValueType;
import com.myhexin.qparser.tech.TechMisc;

public class ParsePluginsUtil {

	public static org.slf4j.Logger logger_ = org.slf4j.LoggerFactory.getLogger(ParsePluginsUtil.class);
	
	public static FocusNode getFocusNode (String text, SemanticNode sNode)
			throws UnexpectedException {
		FocusNode node = null;
		boolean isknown = true;

		//index
		node = getIndexFocusNodeByString(text);

		if (node==null) {
			node = new FocusNode(text);
		} else {
			isknown = false;
		}
		// add keywords
		ArrayList<WordsInfo> words = PhraseInfo.getWordsInfo(text);
		if (words != null) {
			for (int i = 0; i < words.size(); i++) {
				isknown = false;
				String id = words.get(i).getId();
				node.addFocusItem(FocusNode.Type.KEYWORD, id);
			}
		} 
			
		// add str_val
		if (sNode.type == NodeType.STR_VAL && isknown == false) {
			FocusNode.FocusItem item = node.addFocusItem(FocusNode.Type.STRING, text);
			item.setStr((StrNode) sNode);
		}
		
		// add logic
		if (sNode.type == NodeType.LOGIC && isknown == false) {
			FocusNode.FocusItem item = node.addFocusItem(FocusNode.Type.LOGIC, text);
			item.setLogic((LogicNode) sNode);
		}
				
		if (isknown == true)
			return null;
		return node;
	}


	/**
     * @author: 	    吴永行 
     * @dateTime:	  2014-4-10 下午5:37:47
     * @description:   	
     * @param text
     * @param node
     * @param isknown
     * @return
     * @throws UnexpectedException
     * 
     */
    public static FocusNode getIndexFocusNodeByString(String text) throws UnexpectedException {
    	FocusNode node = new FocusNode(text);
    	boolean isknown = true;
    	
    	// add index & alias
    	Collection<ClassNodeFacade> collection = MemOnto.getOntoC(text, ClassNodeFacade.class, Query.Type.ALL);
    	if (collection == null || collection.isEmpty())
    		return null;
    	Iterator<ClassNodeFacade> iterator = collection.iterator();
    	while (iterator.hasNext()) {
    		ClassNodeFacade cn = iterator.next();
			if (cn != null) {
				isknown = false;
				if (!node.hasIndex())
					node.setIndex(cn);
				node.addFocusItem(FocusNode.Type.INDEX, cn.getText(), cn);
			}
    	}
		if (!isknown) 
			return node;
		
	    return null;
    }
	
    /**
     * 老系统指标
     * @param text
     * @deprecated
     * @return
     * @throws UnexpectedException
     */
  	public static FocusNode getFocusNodeOldIndex (String text)
  			throws UnexpectedException {
  		return getFocusNodeOldIndex(text, Query.Type.ALL);
  	}
	
	public static FocusNode getFocusNodeOldIndex (String text, Query.Type qType)
			throws UnexpectedException {
		FocusNode node = new FocusNode(text);
		boolean isknown = true;

		// add index & alias
    	Collection<ClassNodeFacade> collection = MemOnto.getOntoOld(text, ClassNodeFacade.class, qType);
    	if (collection == null || collection.isEmpty())
    		return null;
    	Iterator<ClassNodeFacade> iterator = collection.iterator();
    	while (iterator.hasNext()) {
    		ClassNodeFacade cn = iterator.next();
			if (cn != null) {
				isknown = false;
				if (!node.hasIndex())
					node.setIndex(cn);
				node.addFocusItem(FocusNode.Type.INDEX, cn.getText(), cn);
			}
    	}
		if (!isknown) 
			return node;
		
		return null;
	}
	
	// 根据字符串生成相应节点
	public static SemanticNode getSemanticNodeFromStr(String str, NodeType type, Query.Type qType) {
		if (type == NodeType.CLASS)
			return getIndexNodeFromStr(str, qType);
		else if (type == NodeType.DATE)
			return getDateNodeFromStr(str);
		else if (type == NodeType.NUM)
			return getNumNodeFromStr(str);
		else if (type == NodeType.STR_VAL)
			return getStrNodeFromStr(str);
		else 
			return new UnknownNode(str);
	}
	
	/**
	 *  根据字符串生成指标节点IndexNode->FocusNode
	 * @param str
	 * @return
	 * @deprecated
	 */
	public static SemanticNode getIndexNodeFromStr(String str) {
		return getIndexNodeFromStr(str, Query.Type.ALL);
	}
	
	// 根据字符串生成指标节点IndexNode->FocusNode
	public static SemanticNode getIndexNodeFromStr(String str, Query.Type type) {
		try {
        	FocusNode focusNode = new FocusNode(str);
        	String stringArray[] = str.split("\\|");
        	for (String temp : stringArray) {
	            Collection<ClassNodeFacade> collection = MemOnto.getOntoC(temp, ClassNodeFacade.class, type);
	            if (collection == null || collection.isEmpty())
	            	continue;
	        	Iterator<ClassNodeFacade> iterator = collection.iterator();
	        	while (iterator.hasNext()) {
					ClassNodeFacade classNodeFacade = iterator.next();

					if (classNodeFacade != null) {
						classNodeFacade = (ClassNodeFacade) NodeUtil.copyNode(classNodeFacade);
		            	if (!focusNode.hasIndex())
							focusNode.setIndex(classNodeFacade);
						focusNode.addFocusItem(FocusNode.Type.INDEX, temp, classNodeFacade);
		            }
	        	}
        	}
            return focusNode;
        } catch (UnexpectedException e) {
        	logger_.error(e.getMessage());
			return new UnknownNode(str);
        }
	}
    
    public static SemanticNode getConstantSemanticNodeFromStr(String str, ValueType valueType) {
		if (valueType == null || valueType == ValueType.UNDEFINED) {
			try {
				DateRange dateRange = DateCompute.getDateInfoFromStr(str, null);
				if (dateRange != null) {
					return getDateNodeFromStr(str);
				}
				NumRange numRange = NumParser.getNumRangeFromStr(str);
				if (numRange != null) {
					return getNumNodeFromStr(str);
				}
				return new UnknownNode(str);
			} catch (NotSupportedException e) {
				logger_.error(e.getMessage());
				return new UnknownNode(str);
			}
		} else if (valueType == ValueType.NUMBER || valueType == ValueType.PERCENTAGE) {
			return getNumNodeFromStr(str);
		} else if (valueType == ValueType.STRING) {
			return getStrNodeFromStr(str);
		} else if (valueType == ValueType.DATE) {
			return getDateNodeFromStr(str);
		}
		return new UnknownNode(str);
	}
	
	// 根据字符串生成时间节点DateNode
	public static SemanticNode getDateNodeFromStr(String str) {
		try {
			DateNode dateNode = new DateNode(str);
			if (DatePatterns.SEQUENCE_SP.matcher(dateNode.getText()).matches()){
				dateNode.isSequence = true;
			}
			DateRange dateRange = DateCompute.getDateInfoFromStr(str, null);
			if (dateRange != null) {
				dateNode.setDateinfo(dateRange);
				return dateNode;
			} else {
				UnknownNode unknownNode = new UnknownNode(str);
				return unknownNode;
			}
		} catch (NotSupportedException e) {
			logger_.error(e.getMessage());
			return new UnknownNode(str);
		}
	}
	
	// 根据字符串生成数值节点NumNode
	public static SemanticNode getNumNodeFromStr(String str) {
		try {
			NumNode numNode = new NumNode(str);
			NumRange numRange = NumParser.getNumRangeFromStr(str);
			if (numRange != null) {
				numNode.setNuminfo(numRange);
				return numNode;
			} else {
				UnknownNode unknownNode = new UnknownNode(str);
				return unknownNode;
			}
		} catch (NotSupportedException e) {
			logger_.error(e.getMessage());
			return new UnknownNode(str);
		}
	}
	
	// 根据字符串生成字符串节点StrNode
	public static SemanticNode getStrNodeFromStr(String str) {
		StrNode strNode = new StrNode(str);
		return strNode;
	}
	
	/**
	 * 
	 * @author: 	    吴永行 
	 * @dateTime:	  2014-4-4 上午10:56:25
	 * @description:  这个方法是提取出来的,我觉得尽可能的好使用
	 *                原始方法在 PhraseParserPluginBindPropToIndex中
	 *                PropBindToIndex.getPossibleTechperiod 	
	 * @param sn
	 * @return
	 *
	 */
	public static ArrayList<SemanticNode> getPossibleTechperiod(SemanticNode sn) {
		ArrayList<SemanticNode> result = new ArrayList<SemanticNode>();
		try {
			switch (sn.getType()) {
			case UNKNOWN:
				if (sn.getText().matches(TechMisc.TECHPERIOD_REGEX)) {
					TechPeriodNode techPeriodNode = new TechPeriodNode(
					        String.format("%s%s", sn.getText(),
					                TechMisc.LINE_CHINESE));
					Collection<PropNodeFacade> collection = MemOnto.getOntoC(TechMisc.TECH_ANALY_PERIOD, PropNodeFacade.class, Query.Type.STOCK);
					if (collection != null && collection.isEmpty() == false)
						techPeriodNode.ofwhat.addAll(collection);
					techPeriodNode.oldNodes.add(sn);
					result.add(techPeriodNode);
					return result;
				}

			case DATE:
				if (DatePatterns.MUNITE.matcher(sn.getText()).matches()) {
					TechPeriodNode techPeriodNode = new TechPeriodNode(
					        String.format("%s", sn.getText()));
					Collection<PropNodeFacade> collection = MemOnto.getOntoC(TechMisc.TECH_ANALY_PERIOD, PropNodeFacade.class, Query.Type.STOCK);
					if (collection != null && collection.isEmpty() == false)
						techPeriodNode.ofwhat.addAll(collection);
					techPeriodNode.oldNodes.add(sn);
					result.add(techPeriodNode);
					return result;
				} else if (DatePatterns.REGEX_WEEK_MONTH.matcher(sn.getText())
				        .matches()) {

					Matcher matcher = DatePatterns.REGEX_WEEK_MONTH
					        .matcher(sn.getText());
					matcher.matches();
					// 分析周期改为周  而不是周线 
					TechPeriodNode techPeriodNode = new TechPeriodNode(
					        String.format("%s%s", matcher.group(2),
					                TechMisc.LINE_CHINESE));
					Collection<PropNodeFacade> collection = MemOnto.getOntoC(TechMisc.TECH_ANALY_PERIOD, PropNodeFacade.class, Query.Type.STOCK);
					if (collection != null && collection.isEmpty() == false)
						techPeriodNode.ofwhat.addAll(collection);
					DateNode dn = new DateNode(String.format("%s日",
					        matcher.group(1)));
					result.add(techPeriodNode);
					result.add(dn);
					return result;
				}
			default:
				break;
			}
		} catch (UnexpectedException e) {
			e.printStackTrace();
		}
		return result;
	}
}
